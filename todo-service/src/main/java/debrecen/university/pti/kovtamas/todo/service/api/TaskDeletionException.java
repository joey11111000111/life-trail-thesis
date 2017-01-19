package debrecen.university.pti.kovtamas.todo.service.api;

public class TaskDeletionException extends Exception {

    public TaskDeletionException() {
    }

    public TaskDeletionException(String message) {
        super(message);
    }

    public TaskDeletionException(String message, Throwable cause) {
        super(message, cause);
    }

}
