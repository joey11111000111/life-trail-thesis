package debrecen.university.pti.kovtamas.data.impl.todo.exceptions;

public class TaskRemovalException extends Exception {

    public TaskRemovalException() {
    }

    public TaskRemovalException(String message) {
        super(message);
    }

    public TaskRemovalException(String message, Throwable cause) {
        super(message, cause);
    }

}
