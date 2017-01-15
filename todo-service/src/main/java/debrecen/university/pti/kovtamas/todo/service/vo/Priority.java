package debrecen.university.pti.kovtamas.todo.service.vo;

public enum Priority {
    NONE(0), LOW(1), MEDIUM(2), HIGH(3);

    private final int value;

    Priority(int value) {
        this.value = value;
    }

    int intValue() {
        return value;
    }

    public static Priority ofInteger(int intValue) {
        switch (intValue) {
            case 0:
                return NONE;
            case 1:
                return LOW;
            case 2:
                return MEDIUM;
            case 3:
                return HIGH;
            default:
                throw new IllegalArgumentException("The given priority value is out of range: " + intValue);
        }
    }

}
