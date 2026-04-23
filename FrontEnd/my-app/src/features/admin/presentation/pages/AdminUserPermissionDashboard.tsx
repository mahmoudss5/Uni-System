import { useMemo, useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import FilterBar from "../components/FilterBar";
import UserTable from "../components/UserTable";
import PermissionModal from "../components/PermissionModal";
import type { User, UserPermission, UserPermissionRequest, UserTypeFilter } from "../../domain/interfaces";
import { userService } from "../../infrastructure/services/UserService";
import { permissionService } from "../../infrastructure/services/PermissionService";
import { getRole } from "../../../../Services/userService";
function hasRole(user: User, targetRole: "STUDENT" | "TEACHER"): boolean {
    return user.roles.some((role) => role.name.toUpperCase().includes(targetRole));
}

export default function AdminUserPermissionDashboard() {
    const queryClient = useQueryClient();
    const [selectedFilter, setSelectedFilter] = useState<UserTypeFilter>("all");
    const [selectedUser, setSelectedUser] = useState<User | null>(null);
    const [optimisticUserPermissions, setOptimisticUserPermissions] = useState<UserPermission[] | null>(null);
    const [successMessage, setSuccessMessage] = useState<string | null>(null);
    const role = getRole();
    if(role !== "admin") {
        return (
            <div className="min-h-full flex items-center justify-center bg-slate-100 p-8">
                <div className="rounded-xl border border-red-200 bg-red-50 p-4 text-sm text-red-700">
                    You do not have permission to access this page.
                </div>
            </div>
        );
    }
    const usersQuery = useQuery({
        queryKey: ["admin-users"],
        queryFn: () => userService.getAllUsers(),
    });

    const permissionsQuery = useQuery({
        queryKey: ["all-permissions"],
        queryFn: () => permissionService.getAllPermissions(),
    });

    const userPermissionsQuery = useQuery({
        queryKey: ["user-permissions", selectedUser?.id],
        queryFn: () => permissionService.getUserPermissions(selectedUser!.id),
        enabled: Boolean(selectedUser),
        initialData: selectedUser?.userPermissions,
    });

    const updatePermissionMutation = useMutation({
        mutationFn: (payload: UserPermissionRequest) => permissionService.upsertUserPermission(payload),
        onSuccess: (_, variables) => {
            const action = variables.granted ? "granted" : "revoked";
            setSuccessMessage(`Permission ${action} successfully.`);
        },
        onSettled: async () => {
            if (selectedUser) {
                await queryClient.invalidateQueries({
                    queryKey: ["user-permissions", selectedUser.id],
                });
                await queryClient.invalidateQueries({ queryKey: ["admin-users"] });
            }
            setTimeout(() => setSuccessMessage(null), 2000);
        },
    });

    const filteredUsers = useMemo(() => {
        const users = usersQuery.data ?? [];
        if (selectedFilter === "students") return users.filter((user) => hasRole(user, "STUDENT"));
        if (selectedFilter === "teachers") return users.filter((user) => hasRole(user, "TEACHER"));
        return users;
    }, [selectedFilter, usersQuery.data]);

    const effectiveUserPermissions = optimisticUserPermissions ?? userPermissionsQuery.data ?? [];

    const handleSelectUser = (user: User) => {
        setSelectedUser(user);
        setOptimisticUserPermissions(null);
        setSuccessMessage(null);
    };

    const handleCloseModal = () => {
        setSelectedUser(null);
        setOptimisticUserPermissions(null);
        setSuccessMessage(null);
    };

    const handleTogglePermission = (permissionId: number, nextGranted: boolean) => {
        if (!selectedUser) return;

        const basePermissions = optimisticUserPermissions ?? userPermissionsQuery.data ?? [];
        const existing = basePermissions.find((permission) => permission.permissionId === permissionId);

        const nextPermissions = existing
            ? basePermissions.map((permission) =>
                  permission.permissionId === permissionId ? { ...permission, granted: nextGranted } : permission
              )
            : [
                  ...basePermissions,
                  {
                      userId: selectedUser.id,
                      permissionId,
                      permissionName:
                          permissionsQuery.data?.find((permission) => permission.id === permissionId)?.name ??
                          `Permission ${permissionId}`,
                      granted: nextGranted,
                  },
              ];

        setOptimisticUserPermissions(nextPermissions);

        updatePermissionMutation.mutate(
            {
                userId: selectedUser.id,
                permissionId,
                granted: nextGranted,
            },
            {
                onError: () => {
                    setOptimisticUserPermissions(userPermissionsQuery.data ?? []);
                },
            }
        );
    };
  

    return (
        <section className="min-h-full bg-slate-100 p-8">
            <div className="mx-auto max-w-7xl space-y-6">
                <header className="space-y-2">
                    <h1 className="text-2xl font-bold text-slate-800">Admin Dashboard</h1>
                    <p className="text-sm text-slate-600">
                        Manage users and inspect or modify their permissions from a single workspace.
                    </p>
                </header>

                <FilterBar selectedFilter={selectedFilter} onFilterChange={setSelectedFilter} />

                {usersQuery.isLoading ? (
                    <div className="rounded-xl border border-slate-200 bg-white p-10 text-center text-slate-500">
                        Loading users...
                    </div>
                ) : usersQuery.isError ? (
                    <div className="rounded-xl border border-red-200 bg-red-50 p-4 text-sm text-red-700">
                        Failed to load users. Please refresh and try again.
                    </div>
                ) : (
                    <UserTable users={filteredUsers} onSelectUser={handleSelectUser} />
                )}

                {selectedUser && (
                    <PermissionModal
                        user={selectedUser}
                        permissions={permissionsQuery.data ?? []}
                        userPermissions={effectiveUserPermissions}
                        isLoading={userPermissionsQuery.isLoading || permissionsQuery.isLoading}
                        isSaving={updatePermissionMutation.isPending}
                        errorMessage={
                            userPermissionsQuery.isError || permissionsQuery.isError || updatePermissionMutation.isError
                                ? "Unable to update permissions right now."
                                : null
                        }
                        successMessage={successMessage}
                        onTogglePermission={handleTogglePermission}
                        onClose={handleCloseModal}
                    />
                )}
            </div>
        </section>
    );
}
