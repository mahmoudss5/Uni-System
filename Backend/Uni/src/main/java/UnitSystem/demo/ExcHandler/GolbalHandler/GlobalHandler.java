package UnitSystem.demo.ExcHandler.GolbalHandler;

import UnitSystem.demo.ExcHandler.Entites.AuthError;
import UnitSystem.demo.ExcHandler.Entites.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalHandler {

   @ExceptionHandler(AuthError.class)
    public ResponseEntity<ErrorResponse> authError(AuthError e){

         ErrorResponse errorResponse = new ErrorResponse(
                401,
                "Unauthorized",
                e.getMessage(),
                java.time.LocalDateTime.now()
         );
         return ResponseEntity.status(401).body(errorResponse);

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse(
                500,
                "Internal Server Error",
                e.getMessage(),
                java.time.LocalDateTime.now()
        );
        return ResponseEntity.status(500).body(errorResponse);
    }
}
