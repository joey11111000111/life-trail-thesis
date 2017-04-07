package debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.task.display;

import debrecen.university.pti.kovtamas.todo.display.controller.TaskRowController;
import debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.task.TaskNode;
import debrecen.university.pti.kovtamas.todo.service.vo.TaskVo;
import lombok.NonNull;

public class TaskRepresentations {

    private final TaskRowController rowController;
    private final TaskNode taskNode;

    public TaskRepresentations(@NonNull final TaskRowController rowController, @NonNull final TaskNode taskNode) {
        this.rowController = rowController;
        this.taskNode = taskNode;
    }

    public TaskRowController getRowController() {
        return rowController;
    }

    public TaskNode getTaskNode() {
        return taskNode;
    }

    public TaskVo getUnupdatedDetachedVo() {
        return TaskVo.deepCopy(taskNode.getVo());
    }

    public TaskNode getUnupdatedNode() {
        TaskNode parentNode = taskNode.getParent();
        TaskVo nodeVo = taskNode.getVo();
        return new TaskNode(parentNode, TaskVo.deepCopy(nodeVo));
    }

    public TaskNode getUpdatedNode() {
        updateNodeVo();
        return taskNode;
    }

    public TaskVo getUpdatedVo() {
        updateNodeVo();
        return taskNode.getVo();
    }

    private void updateNodeVo() {
        TaskDisplayState displayState = rowController.getTaskStateDetached();
        TaskVo vo = taskNode.getVo();
        vo.setTaskDef(displayState.getTaskDef());
        vo.setPriority(displayState.getPriorityColor().toPriority());
        vo.setDeadline(displayState.getDeadline());
        vo.setCompleted(displayState.isCompleted());
        vo.setCategory(displayState.getSelectedCategory());
    }

}
