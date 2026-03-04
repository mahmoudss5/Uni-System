package UnitSystem.demo.DataAccessLayer.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@Service
@Builder
@Table(name = "courses")
@RequiredArgsConstructor
@AllArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_name", unique = true)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_dep", nullable = false)
    private Department department;

    @Column(name = "credits", unique = true)
    private int Credits;

    @Column(name = "course_description")
    private String description;

    @Column(name = "capacity", unique = true)
    private int Capacity;

    @Column(name = "start_date")
    private LocalDate StartDate;

    @Column(name = "end_date")
    private LocalDate EndDate;

    @Column(name = "course_code", unique = true)
    private String courseCode;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;


    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EnrolledCourse> courseEnrollments ;


    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Announcement>announcements = new HashSet<>();


}
