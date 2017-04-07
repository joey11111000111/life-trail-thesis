package debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.task;

import debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.task.display.TaskDisplayer;
import debrecen.university.pti.kovtamas.display.utils.load.DisplayLoadException;
import debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.category.CategoryVo;
import debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.category.LogicalCategoryNames.LogicalCategories;
import debrecen.university.pti.kovtamas.todo.service.api.TaskDeletionException;
import debrecen.university.pti.kovtamas.todo.service.api.TaskSaveFailureException;
import debrecen.university.pti.kovtamas.todo.service.api.TodoService;
import debrecen.university.pti.kovtamas.todo.service.vo.TaskVo;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TaskSubController {

    private final TodoService service;
    private TaskDisplayer taskDisplayer;
    private Map<LogicalCategories, Supplier<List<TaskVo>>> logicalCategoryTaskQueries;
    private CategoryVo selectedCategory;
    private TodayProgressDisplayer progressDisplayer;
    private TaskTreeSynchronizer taskTreeSynchronizer;

    static public class Builder {

        private TodoService service;
        private VBox taskBox;
        private VBox progressContainer;
        private Rectangle progressIndicator;

        public Builder service(TodoService service) {
            this.service = service;
            return this;
        }

        public Builder taskBox(VBox taskBox) {
            this.taskBox = taskBox;
            return this;
        }

        public Builder progressContainer(VBox progressContainer) {
            this.progressContainer = progressContainer;
            return this;
        }

        public Builder progressIndicator(Rectangle progressIndicator) {
            this.progressIndicator = progressIndicator;
            return this;
        }

        public TaskSubController build() {
            if (hasNullField()) {
                throw new IllegalStateException("All fields of the builder must be initialized before the build!");
            }

            return new TaskSubController(service, taskBox, progressContainer, progressIndicator);
        }

        private boolean hasNullField() {
            return service == null || taskBox == null || progressContainer == null || progressIndicator == null;
        }
    }

    static public Builder builder() {
        return new Builder();
    }

    private TaskSubController(@NonNull final TodoService service, @NonNull final VBox taskBox,
            VBox progressContainer, Rectangle progressIndicator) {
        this.service = service;
        this.selectedCategory = null;
        this.taskTreeSynchronizer = new TaskTreeSynchronizer();
        initTaskDisplayer(taskBox);
        initProgressDisplayer(progressContainer, progressIndicator);
        initLogicalCategoryTaskQueries();
        setupTaskChangeAction();
    }

    private void initTaskDisplayer(final VBox taskBox) {
        taskDisplayer = new TaskDisplayer(taskBox, service.getCustomCategories());
    }

    private void initProgressDisplayer(VBox progressContainer, Rectangle progressIndicator) {
        ProgressBarSubController progressBarSubController
                = new ProgressBarSubController(progressContainer, progressIndicator);

        List<TaskVo> todayTasks = service.getTodayTasks();
        ProgressRatio initialRatio = ProgressRatio.fromTodayTasks(todayTasks);

        this.progressDisplayer = new TodayProgressDisplayer(progressBarSubController, initialRatio);
    }

    private void initLogicalCategoryTaskQueries() {
        logicalCategoryTaskQueries = new HashMap<>();
        logicalCategoryTaskQueries.put(LogicalCategories.TODAY, service::getTodayTasks);
        logicalCategoryTaskQueries.put(LogicalCategories.TOMORROW, service::getTomorrowTasks);
        logicalCategoryTaskQueries.put(LogicalCategories.THIS_WEEK, service::getOneWeekTasks);
        logicalCategoryTaskQueries.put(LogicalCategories.UNCATEGORIZED, service::getUncategorizedTasks);
        logicalCategoryTaskQueries.put(LogicalCategories.COMPLETED, service::getCompletedTasks);
    }

    private void setupTaskChangeAction() {
        taskDisplayer.registerTaskStateChangeAction((oldNode, newNode) -> {
            try {
                saveChangesAndUpdateProgressBar(oldNode, newNode);
                switchToCategory(selectedCategory);
            } catch (TaskSaveFailureException tsfe) {
                log.warn("Could not save changes of task with id: " + newNode.getVo().getId(), tsfe);
            }
        });
    }

    private void saveChangesAndUpdateProgressBar(TaskNode oldNode, TaskNode newNode) throws TaskSaveFailureException {
        taskTreeSynchronizer.synchronizeChanges(oldNode, newNode);
        TaskVo rootTask = taskTreeSynchronizer.getSynchronizedRootTask();

        service.save(rootTask);

        log.info("Saved changes of task with id: " + newNode.getVo().getId());
        progressDisplayer.multipleTasksChanged(taskTreeSynchronizer.getAllTaskChanges());
//        progressDisplayer.taskChanged(oldNode.getVo(), newNode.getVo());
    }

    public void newCategoryAddedAction(String newCategory) {
        taskDisplayer.newCategoryAddedAction(newCategory);
    }

    public void categoryRemovedAction(String removedCategory) {
        taskDisplayer.categoryRemovedAction(removedCategory);
    }

    public void selectedCategoryChangedAction(CategoryVo fromCategoryVo, CategoryVo toCategoryVo) {
        switchToCategory(toCategoryVo);
    }

    public void switchToCategory(@NonNull final CategoryVo categoryVo) {
        taskDisplayer.clear();
        List<TaskVo> tasksOfCategory = getCategoryTasks(categoryVo);
        try {
            taskDisplayer.displayAllTasks(tasksOfCategory);
        } catch (DisplayLoadException dle) {
            handleTaskRowCreationException(dle);
        }

        selectedCategory = categoryVo;
    }

    public void toggleDisableForSelectedRow() {
        taskDisplayer.toggleDisableForSelectedRow();
    }

    public void addNewTask() {
        if (selectedCategory == null) {
            return;
        }

        TaskVo parent = taskDisplayer.getSelectedTask();
        if (parent == null) {
            addNewTopLevelTask();
        } else {
            addNewSubTask(parent);
        }
    }

    private void addNewTopLevelTask() {
        TaskVo newTask = setupNewTaskState(service.newMinimalTaskVo());
        updateTaskAndReloadCategory(newTask);
        progressDisplayer.newTaskAdded(newTask);
    }

    private TaskVo setupNewTaskState(TaskVo newMinimalTask) {
        if (selectedCategory.isLogical()) {
            // A minimal taskVo is perfectly set up by its nature for every logical category except for the TOMORROW
            if (selectedCategory.getLogicalCategory() == LogicalCategories.TOMORROW) {
                newMinimalTask.setDeadline(LocalDate.now().plusDays(1));
            }
        } else {
            newMinimalTask.setCategory(selectedCategory.getCustomCategoryName());
        }

        return newMinimalTask;
    }

    private void addNewSubTask(TaskVo parent) {
        service.addNewMinimalSubTaskTo(parent);
        updateTaskAndReloadCategory(parent);
        progressDisplayer.newTaskAdded(parent);
    }

    private void updateTaskAndReloadCategory(TaskVo task) {
        try {
            service.save(task);
            switchToCategory(selectedCategory);
        } catch (TaskSaveFailureException tsfe) {
            log.warn("Failed to save new minimal sub task!", tsfe);
        }
    }

    public void removeSelectedTask() {
        if (!taskDisplayer.hasSelectedTask()) {
            return;
        }

        TaskVo selectedTask = taskDisplayer.getSelectedTask();
        TaskVo rootTask = taskDisplayer.getRootOfSelectedTaskTree();

        try {
            service.deleteTaskFromTaskTree(selectedTask, rootTask);
            switchToCategory(selectedCategory);
        } catch (TaskDeletionException tde) {
            log.warn("Failed to delete selected task!", tde);
        }

        progressDisplayer.taskRemoved(selectedTask);
    }

    private List<TaskVo> getCategoryTasks(CategoryVo categoryVo) {
        if (categoryVo.isLogical()) {
            return getLogicalCategoryTasks(categoryVo.getLogicalCategory());
        }

        return getCustomCategoryTasks(categoryVo.getCustomCategoryName());
    }

    private List<TaskVo> getCustomCategoryTasks(String categoryName) {
        return service.getActiveByCategory(categoryName);
    }

    private List<TaskVo> getLogicalCategoryTasks(LogicalCategories logicalCategory) {
        return logicalCategoryTaskQueries.get(logicalCategory).get();
    }

    private void handleTaskRowCreationException(DisplayLoadException dle) {
        String errorMessage = "Failed to load fxml resource for displaying tasks";
        log.error(errorMessage, dle);
        taskDisplayer.displayErrorMessage(errorMessage);
    }

}
