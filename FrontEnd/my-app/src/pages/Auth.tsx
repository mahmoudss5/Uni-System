import { Outlet } from "react-router-dom";
export default function Auth() {
    return (
        <div className="h-screen w-full flex ">

            <div className="w-1/2 flex items-center justify-center flex-col gap-4 "style={{ backgroundColor: 'rgb(12, 61, 126)' }}>
                <div className="rounded-full p-6 shadow-lg" style={{ backgroundColor: 'rgb(12, 61, 126)' }}>
                    <div className="text-6xl font-bold text-white tracking-wide animate-bounce font-sans ">
                        HU
                    </div>
                </div>
                <h1 className="text-4xl font-bold text-white tracking-wide animate-bounce font-sans ">
                    Helwan University System</h1>

            </div>

            <div className="w-1/2 flex items-center justify-center flex-col gap-4 ">
                <Outlet />
            </div>

        </div>
    )
}