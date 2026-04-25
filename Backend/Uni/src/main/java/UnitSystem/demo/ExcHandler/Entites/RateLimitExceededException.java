package UnitSystem.demo.ExcHandler.Entites;

public class RateLimitExceededException extends RuntimeException {
    public RateLimitExceededException(String message) {
        super(message);
    }
}
