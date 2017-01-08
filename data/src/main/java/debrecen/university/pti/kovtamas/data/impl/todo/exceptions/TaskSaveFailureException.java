package debrecen.university.pti.kovtamas.data.impl.todo.exceptions;

public class TaskSaveFailureException extends Exception {

    public TaskSaveFailureException() {
    }

    public TaskSaveFailureException(String message) {
        super(message);
    }

    public TaskSaveFailureException(String message, Throwable cause) {
        super(message, cause);
    }

}
