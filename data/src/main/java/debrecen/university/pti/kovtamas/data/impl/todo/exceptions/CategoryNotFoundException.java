package debrecen.university.pti.kovtamas.data.impl.todo.exceptions;

public class CategoryNotFoundException extends Exception {

    public CategoryNotFoundException(String message) {
        super(message);
    }

    public CategoryNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
