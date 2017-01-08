package debrecen.university.pti.kovtamas.data.impl.todo.exceptions;

public class TaskNotFoundException extends Exception {

    public TaskNotFoundException() {
    }

    public TaskNotFoundException(String message) {
        super(message);
    }

    public TaskNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
