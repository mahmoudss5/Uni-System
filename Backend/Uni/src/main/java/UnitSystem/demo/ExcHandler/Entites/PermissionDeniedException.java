package UnitSystem.demo.ExcHandler.Entites;

public class PermissionDeniedException extends RuntimeException {

    public PermissionDeniedException(String message) {
        super(message);
    }
}
