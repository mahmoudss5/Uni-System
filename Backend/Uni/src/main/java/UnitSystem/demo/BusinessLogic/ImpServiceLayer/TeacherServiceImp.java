package UnitSystem.demo.BusinessLogic.ImpServiceLayer;

import UnitSystem.demo.BusinessLogic.InterfaceServiceLayer.TeacherService;
import UnitSystem.demo.DataAccessLayer.Dto.Announcement.AnnouncementResponse;
import UnitSystem.demo.DataAccessLayer.Dto.Course.CourseResponse;
import UnitSystem.demo.DataAccessLayer.Dto.Teacher.TeacherRequest;
import UnitSystem.demo.DataAccessLayer.Dto.Teacher.TeacherResponse;
import UnitSystem.demo.DataAccessLayer.Dto.UpcomingEvent.UpcomingEventResponse;
import UnitSystem.demo.DataAccessLayer.Dto.UserDetails.TeacherDetailsResponse;
import UnitSystem.demo.DataAccessLayer.Dto.UserDetails.UserDetailsRequest;
import UnitSystem.demo.DataAccessLayer.Entities.Announcement;
import UnitSystem.demo.DataAccessLayer.Entities.Course;
import UnitSystem.demo.DataAccessLayer.Entities.Role;
import UnitSystem.demo.DataAccessLayer.Entities.Teacher;
import UnitSystem.demo.DataAccessLayer.Entities.UpcomingEvent;
import UnitSystem.demo.DataAccessLayer.Entities.User;
import UnitSystem.demo.DataAccessLayer.Repositories.AnnouncementRepository;
import UnitSystem.demo.DataAccessLayer.Repositories.RoleRepository;
import UnitSystem.demo.DataAccessLayer.Repositories.TeacherRepository;
import UnitSystem.demo.DataAccessLayer.Repositories.UpcomingEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherServiceImp implements TeacherService {

        private final TeacherRepository teacherRepository;
        private final RoleRepository roleRepository;
        private final PasswordEncoder passwordEncoder;
        private final AnnouncementRepository announcementRepository;
        private final UpcomingEventRepository upcomingEventRepository;

        private TeacherResponse mapToTeacherResponse(Teacher teacher) {
                return TeacherResponse.builder()
                                .id(teacher.getId())
                                .userName(teacher.getUserName())
                                .email(teacher.getEmail())
                                .active(teacher.getActive())
                                .createdAt(teacher.getCreatedAt())
                                .officeLocation(teacher.getOfficeLocation())
                                .salary(teacher.getSalary())
                                .roles(teacher.getRoles().stream()
                                                .map(Role::getName)
                                                .collect(Collectors.toSet()))
                                .coursesCount(teacher.getCourses() != null ? teacher.getCourses().size() : 0)
                                .build();
        }

        private Teacher mapToTeacher(TeacherRequest teacherRequest) {
                Set<Role> roles = teacherRequest.getRoles().stream()
                                .map(roleName -> roleRepository.findByName(roleName)
                                                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
                                .collect(Collectors.toSet());

                return Teacher.builder()
                                .userName(teacherRequest.getUserName())
                                .email(teacherRequest.getEmail())
                                .password(passwordEncoder.encode(teacherRequest.getPassword()))
                                .active(teacherRequest.getActive() != null ? teacherRequest.getActive() : true)
                                .officeLocation(teacherRequest.getOfficeLocation())
                                .salary(teacherRequest.getSalary())
                                .roles(roles)
                                .build();
        }

        @Override
        public List<TeacherResponse> getAllTeachers() {
                return teacherRepository.findAll().stream()
                                .map(this::mapToTeacherResponse)
                                .toList();
        }

        @Override
        @Cacheable(value = "teachersCache", key = "'teacherById:' + #teacherId")
        public TeacherResponse getTeacherById(Long teacherId) {
                return teacherRepository.findById(teacherId)
                                .map(this::mapToTeacherResponse)
                                .orElse(null);
        }

        @Override
        @Cacheable(value = "teachersCache", key = "'teacherByUserName:' + #userName")
        public TeacherResponse getTeacherByUserName(String userName) {
                Teacher teacher = teacherRepository.findByUserName(userName);
                return teacher != null ? mapToTeacherResponse(teacher) : null;
        }

        @Override
        @CacheEvict(value = "teachersCache", allEntries = true)
        public TeacherResponse createTeacher(TeacherRequest teacherRequest) {
                Teacher teacher = mapToTeacher(teacherRequest);
                teacherRepository.save(teacher);
                return mapToTeacherResponse(teacher);
        }

        @Override
        @CacheEvict(value = "teachersCache", allEntries = true)
        public TeacherResponse updateTeacher(Long teacherId, TeacherRequest teacherRequest) {
                Teacher existingTeacher = teacherRepository.findById(teacherId)
                                .orElseThrow(() -> new RuntimeException("Teacher not found"));

                existingTeacher.setUserName(teacherRequest.getUserName());
                existingTeacher.setEmail(teacherRequest.getEmail());
                if (teacherRequest.getPassword() != null && !teacherRequest.getPassword().isEmpty()) {
                        existingTeacher.setPassword(passwordEncoder.encode(teacherRequest.getPassword()));
                }
                existingTeacher.setActive(teacherRequest.getActive());
                existingTeacher.setOfficeLocation(teacherRequest.getOfficeLocation());
                existingTeacher.setSalary(teacherRequest.getSalary());

                if (teacherRequest.getRoles() != null) {
                        Set<Role> roles = teacherRequest.getRoles().stream()
                                        .map(roleName -> roleRepository.findByName(roleName)
                                                        .orElseThrow(() -> new RuntimeException(
                                                                        "Role not found: " + roleName)))
                                        .collect(Collectors.toSet());
                        existingTeacher.setRoles(roles);
                }

                teacherRepository.save(existingTeacher);
                return mapToTeacherResponse(existingTeacher);
        }

        @Override
        public void saveUserASTeacher(User user) {
                Teacher teacher = (Teacher) user;
                teacherRepository.save(teacher);
        }

        @Override
        @CacheEvict(value = "teachersCache", allEntries = true)
        public void deleteTeacher(Long teacherId) {
                teacherRepository.deleteById(teacherId);
        }

        private CourseResponse mapToCourseResponse(Course course) {
                return CourseResponse.builder()
                                .id(course.getId())
                                .name(course.getName())
                                .description(course.getDescription())
                                .departmentName(course.getDepartment() != null ? course.getDepartment().getName()
                                                : null)
                                .teacherUserName(course.getTeacher() != null ? course.getTeacher().getUserName() : null)
                                .creditHours(course.getCredits())
                                .maxStudents(course.getCapacity())
                                .enrolledStudents(course.getCourseEnrollments() != null
                                                ? course.getCourseEnrollments().size()
                                                : 0)
                                .build();
        }

        private AnnouncementResponse mapToAnnouncementResponse(Announcement announcement) {
                return AnnouncementResponse.builder()
                                .id(announcement.getId())
                                .title(announcement.getTitle())
                                .content(announcement.getDescription())
                                .courseId(announcement.getCourse() != null ? announcement.getCourse().getId() : null)
                                .createdDate(announcement.getCreatedAt())
                                .build();
        }

        private UpcomingEventResponse mapToUpcomingEventResponse(UpcomingEvent event) {
                return UpcomingEventResponse.builder()
                                .id(event.getId())
                                .title(event.getTitle())
                                .subtitle(event.getSubtitle())
                                .eventDate(event.getEventDate())
                                .type(event.getType().name())
                                .userId(event.getUser() != null ? event.getUser().getId() : null)
                                .userName(event.getUser() != null ? event.getUser().getUserName() : null)
                                .createdAt(event.getCreatedAt())
                                .build();
        } 


        @Override
        @Cacheable(value = "teachersCache", key = "'teacherDetails:' + #userDetailsRequest.userId")
        public TeacherDetailsResponse getTeacherDetails(UserDetailsRequest userDetailsRequest) {
                Teacher teacher = teacherRepository.findById(userDetailsRequest.getUserId())
                                .orElseThrow(
                                                () -> new RuntimeException("Teacher not found with ID: "
                                                                + userDetailsRequest.getUserId()));

                Set<CourseResponse> courses = teacher.getCourses() != null
                                ? teacher.getCourses().stream()
                                                .map(this::mapToCourseResponse)
                                                .collect(Collectors.toSet())
                                : Collections.emptySet();

                // Get all announcements from teacher's courses
                List<AnnouncementResponse> announcements = teacher.getCourses() != null
                                ? teacher.getCourses().stream()
                                                .flatMap(course -> announcementRepository.findByCourseId(course.getId()).stream())
                                                .map(this::mapToAnnouncementResponse)
                                                .collect(Collectors.toList())
                                : Collections.emptyList();

                // Get upcoming events for the teacher
                List<UpcomingEventResponse> upcomingEvents = upcomingEventRepository.findByUserId(teacher.getId())
                                .stream()
                                .map(this::mapToUpcomingEventResponse)
                                .collect(Collectors.toList());

                // Calculate total number of students across all courses
                Long numberOfStudents = teacher.getCourses() != null
                                ? teacher.getCourses().stream()
                                                .mapToLong(course -> course.getCourseEnrollments() != null
                                                                ? course.getCourseEnrollments().size() : 0)
                                                .sum()
                                : 0L;

                // Get department from teacher's courses (first course's department)
                String department = teacher.getCourses() != null && !teacher.getCourses().isEmpty()
                                ? teacher.getCourses().iterator().next().getDepartment().getName()
                                : null;

                return TeacherDetailsResponse.builder()
                                .teacherId(teacher.getId())
                                .name(teacher.getUserName())
                                .email(teacher.getEmail())
                                .department(department)
                                .salary(teacher.getSalary())
                                .roles(teacher.getRoles().stream()
                                                .map(Role::getName)
                                                .collect(Collectors.toSet()))
                                .courses(courses)
                                .announcements(announcements)
                                .upcomingEvents(upcomingEvents)
                                .coursesCount(courses.size())
                                .numberOfStudents(numberOfStudents)
                                .build();
        }
}
