package debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.task;

import debrecen.university.pti.kovtamas.todo.service.vo.Priority;

public enum PriorityColors {
    NONE_COLOR("-fx-fill: radial-gradient(radius 180%, burlywood,"
            + "derive(rgb(100,100,60), -30%), derive(rgb(100,100,60), 30%));"),
    LOW_COLOR("-fx-fill: radial-gradient(radius 180%, silver,"
            + "derive(cadetblue, -30%), derive(cadetblue, 30%));"),
    MEDIUM_COLOR("-fx-fill: radial-gradient(radius 180%, burlywood,"
            + "derive(blue, -30%), derive(blue, 30%));"),
    HIGH_COLOR("-fx-fill: radial-gradient(radius 180%, burlywood,"
            + "derive(red, -30%), derive(red, 30%));");

    public static String getColorStyleOfPriority(Priority priority) {
        return ofPriority(priority).getColorStyle();
    }

    public static PriorityColors ofPriority(Priority priority) {
        switch (priority) {
            case NONE:
                return NONE_COLOR;
            case LOW:
                return LOW_COLOR;
            case MEDIUM:
                return MEDIUM_COLOR;
            case HIGH:
                return HIGH_COLOR;
            default:
                throw new UnsupportedOperationException("Priority " + priority.name()
                        + "is not supported yet in PriorityColors!");

        }
    }

    private final String colorStyle;

    private PriorityColors(String colorStyle) {
        this.colorStyle = colorStyle;
    }

    public String getColorStyle() {
        return colorStyle;
    }

}
