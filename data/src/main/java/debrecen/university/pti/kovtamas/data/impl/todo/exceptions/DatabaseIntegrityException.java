package debrecen.university.pti.kovtamas.data.impl.todo.exceptions;

public class DatabaseIntegrityException extends RuntimeException {

    public DatabaseIntegrityException() {
    }

    public DatabaseIntegrityException(String message) {
        super(message);
    }

}
