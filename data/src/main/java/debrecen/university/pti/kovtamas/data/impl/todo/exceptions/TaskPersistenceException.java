package debrecen.university.pti.kovtamas.data.impl.todo.exceptions;

public class TaskPersistenceException extends Exception {

    public TaskPersistenceException() {
    }

    public TaskPersistenceException(String message) {
        super(message);
    }

    public TaskPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

}
