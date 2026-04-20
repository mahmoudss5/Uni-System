package UnitSystem.demo.DataAccessLayer.Repositories;

import UnitSystem.demo.DataAccessLayer.Entities.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
        Teacher findByUserName(String userName);

        @Query(value = "SELECT salary FROM teachers WHERE user_id = :teacherId", nativeQuery = true)
        String getRawEncryptedSalary(@Param("teacherId") Long teacherId);

}
