package UnitSystem.demo.ExcHandler.Entites;

import java.util.List;

public class MissingPrerequisitesException extends RuntimeException {
    private List<String> missingPrerequisites;
    public MissingPrerequisitesException(String message, List<String> missingPrerequisites) {

        super(message);
        this.missingPrerequisites = missingPrerequisites;
    }

    public List<String> getMissingPrerequisites() {
        return missingPrerequisites;
    }
}
