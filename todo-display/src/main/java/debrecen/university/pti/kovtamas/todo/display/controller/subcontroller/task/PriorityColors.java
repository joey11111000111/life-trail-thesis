package debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.task;

import debrecen.university.pti.kovtamas.todo.service.vo.Priority;
import java.util.HashMap;
import java.util.Map;

public enum PriorityColors {
    NONE_COLOR("-fx-fill: radial-gradient(radius 180%, burlywood,"
            + "derive(rgb(100,100,60), -30%), derive(rgb(100,100,60), 30%));"),
    LOW_COLOR("-fx-fill: radial-gradient(radius 180%, silver,"
            + "derive(cadetblue, -30%), derive(cadetblue, 30%));"),
    MEDIUM_COLOR("-fx-fill: radial-gradient(radius 180%, burlywood,"
            + "derive(blue, -30%), derive(blue, 30%));"),
    HIGH_COLOR("-fx-fill: radial-gradient(radius 180%, burlywood,"
            + "derive(red, -30%), derive(red, 30%));");

    static private final Map<Priority, PriorityColors> priorityMapping;
    private final String colorStyle;

    static {
        priorityMapping = new HashMap<>();
        priorityMapping.put(Priority.NONE, NONE_COLOR);
        priorityMapping.put(Priority.LOW, LOW_COLOR);
        priorityMapping.put(Priority.MEDIUM, MEDIUM_COLOR);
        priorityMapping.put(Priority.HIGH, HIGH_COLOR);
    }

    static public String getColorStyleOfPriority(Priority priority) {
        return ofPriority(priority).getColorStyle();
    }

    static public PriorityColors ofPriority(Priority priority) {
        return priorityMapping.get(priority);
    }

    private PriorityColors(String colorStyle) {
        this.colorStyle = colorStyle;
    }

    public Priority toPriority() {
        return priorityMapping.entrySet()
                .stream()
                .filter(priEntry -> priEntry.getValue() == this)
                .findAny()
                .get()
                .getKey();
    }

    public String getColorStyle() {
        return colorStyle;
    }

}
