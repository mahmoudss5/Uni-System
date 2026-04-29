package UnitSystem.demo.ExcHandler.Entites;

public class AccountDeactivated extends RuntimeException {
    public AccountDeactivated(String message) {
        super(message);
    }
}
