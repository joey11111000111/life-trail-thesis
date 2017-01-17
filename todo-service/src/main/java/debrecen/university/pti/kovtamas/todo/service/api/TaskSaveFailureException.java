package debrecen.university.pti.kovtamas.todo.service.api;

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
