package debrecen.university.pti.kovtamas.data.impl.todo.exceptions;

public class UnsuccessfulDatabaseOperation extends RuntimeException {

    public UnsuccessfulDatabaseOperation() {
    }

    public UnsuccessfulDatabaseOperation(String message) {
        super(message);
    }

    public UnsuccessfulDatabaseOperation(String message, Throwable cause) {
        super(message, cause);
    }

}
