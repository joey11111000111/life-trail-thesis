package debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.task;

import debrecen.university.pti.kovtamas.todo.service.vo.TaskVo;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TaskTreeSynchronizer {

    private final List<ValueChangeRecord<TaskVo>> allTaskChanges;
    private TaskVo rootTask;

    static public class ValueChangeRecord<T> {

        private final T fromValue;
        private final T toValue;

        public ValueChangeRecord(T fromValue, T toValue) {
            this.fromValue = fromValue;
            this.toValue = toValue;
        }

        public T getFromValue() {
            return fromValue;
        }

        public T getToValue() {
            return toValue;
        }

    }

    public TaskTreeSynchronizer() {
        allTaskChanges = new ArrayList<>();
        rootTask = null;
    }

    public List<ValueChangeRecord<TaskVo>> getAllTaskChanges() {
        return allTaskChanges;
    }

    public TaskVo getSynchronizedRootTask() {
        if (rootTask == null) {
            throw new IllegalStateException("No synchronization has happened, cannot return synchronized root task!");
        }

        return rootTask;
    }

    private void findAndSaveRootTask(TaskNode taskNode) {
        TaskNode rootNode = taskNode;
        while (rootNode.hasParent()) {
            rootNode = rootNode.getParent();
        }

        rootTask = rootNode.getVo();
    }

    public void synchronizeChanges(TaskNode oldNode, TaskNode newNode) {
        clear();
        saveTaskChange(oldNode.getVo(), newNode.getVo());   // Other parts of the code only save recursive changes
        findAndSaveRootTask(newNode);
        applyInheritedValueChanges(oldNode, newNode);
        applyCompletionChanges(oldNode, newNode);
    }

    private void clear() {
        allTaskChanges.clear();
        rootTask = null;
    }

    private void applyInheritedValueChanges(TaskNode oldNode, TaskNode newNode) {
        if (areThereInheritedValueChanges(oldNode, newNode)) {
            applyInheritedValueChangesUpwards(newNode);
            applyInheritedValueChangesDownwards(newNode.getVo());
        }
    }

    private void applyInheritedValueChangesUpwards(TaskNode newNode) {
        TaskVo newVo = newNode.getVo();
        TaskNode currentNode = newNode;
        while (currentNode.hasParent()) {
            currentNode = currentNode.getParent();
            applyInheritedValueChangesFromTo(newVo, currentNode.getVo());
        }
    }

    private void applyInheritedValueChangesFromTo(TaskVo source, TaskVo target) {
        target.setCategory(source.getCategory());
        target.setPriority(source.getPriority());
        target.setRepeating(source.isRepeating());
        target.setDeadline(source.getDeadline());

        saveTaskChange(target, source);
    }

    private void applyInheritedValueChangesDownwards(TaskVo newVo) {
        if (newVo.hasSubTasks()) {
            newVo.getSubTasks().forEach(subTask -> {
                applyInheritedValueChangesFromTo(newVo, subTask);
                applyInheritedValueChangesDownwards(subTask);
            });
        }
    }

    private void applyCompletionChanges(TaskNode oldNode, TaskNode newNode) {
        TaskVo oldVo = oldNode.getVo();
        TaskVo newVo = newNode.getVo();
        if (oldVo.isCompleted() && !newVo.isCompleted()) {
            applyCompletionChangesUpwards(newNode);
        }
        if (!oldVo.isCompleted() && newVo.isCompleted()) {
            applyCompletionChangesDownwards(newNode.getVo());
        }
    }

    private void applyCompletionChangesUpwards(TaskNode newNode) {
        TaskNode currentNode = newNode;
        while (currentNode.hasParent()) {
            currentNode = currentNode.getParent();
            changeCompletionState(currentNode.getVo(), false);
        }
    }

    private void applyCompletionChangesDownwards(TaskVo newVo) {
        if (newVo.hasSubTasks()) {
            newVo.getSubTasks().forEach(subTask -> {
                changeCompletionState(subTask, true);
                applyCompletionChangesDownwards(subTask);
            });
        }
    }

    private void changeCompletionState(TaskVo vo, boolean completionState) {
        TaskVo fromVo = TaskVo.deepCopy(vo);
        vo.setCompleted(completionState);
        TaskVo toVo = TaskVo.deepCopy(vo);

        saveTaskChange(fromVo, toVo);
    }

    private boolean areThereInheritedValueChanges(TaskNode oldNode, TaskNode newNode) {
        TaskVo oldVo = oldNode.getVo();
        TaskVo newVo = newNode.getVo();
        return !Objects.equals(oldVo.getCategory(), newVo.getCategory())
                || !Objects.equals(oldVo.getDeadline(), newVo.getDeadline())
                || !Objects.equals(oldVo.getPriority(), newVo.getPriority())
                || !Objects.equals(oldVo.isRepeating(), newVo.isRepeating());
    }

    private void saveTaskChange(TaskVo fromVo, TaskVo toVo) {
        allTaskChanges.add(new ValueChangeRecord<>(fromVo, toVo));
    }

}
