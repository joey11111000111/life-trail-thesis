package debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.task.display;

import debrecen.university.pti.kovtamas.todo.display.controller.TaskRowController;
import debrecen.university.pti.kovtamas.todo.service.vo.TaskVo;
import lombok.NonNull;

public class TaskRepresentations {

    private final TaskRowController rowController;
    private final TaskVo vo;

    public TaskRepresentations(@NonNull final TaskRowController rowController, @NonNull final TaskVo vo) {
        this.rowController = rowController;
        this.vo = vo;
    }

    public void updateVo() {
        TaskDisplayState displayState = rowController.getTaskStateDetached();
        vo.setTaskDef(displayState.getTaskDef());
        vo.setPriority(displayState.getPriorityColor().toPriority());
        vo.setDeadline(displayState.getDeadline());
        vo.setCompleted(displayState.isCompleted());
        vo.setCategory(displayState.getSelectedCategory());
    }

    public TaskRowController getRowController() {
        return rowController;
    }

    public TaskVo getVo() {
        return vo;
    }

}
