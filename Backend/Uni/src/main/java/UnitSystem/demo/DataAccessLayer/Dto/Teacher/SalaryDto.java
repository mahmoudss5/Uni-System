package UnitSystem.demo.DataAccessLayer.Dto.Teacher;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class SalaryDto {
   private String  teacherName;
   private String plainTextSalary;
   private String encryptedSalary;
   private String decryptedSalary;
}
