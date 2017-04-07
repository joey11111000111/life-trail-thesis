package debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.task;

import debrecen.university.pti.kovtamas.todo.service.vo.TaskVo;

public class TaskNode {

    static public final TaskNode NO_PARENT = null;

    private final TaskNode parent;
    private final TaskVo vo;

    public TaskNode(TaskNode parentNode, TaskVo currentVo) {
        this.parent = parentNode;
        this.vo = currentVo;
    }

    public TaskNode getParent() {
        return parent;
    }

    public TaskVo getVo() {
        return vo;
    }

}
