package debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TaskDisplayState {

    public static class Builder {

        private Integer indentWidth;
        private Boolean completed;
        private String priorityColorStyle;
        private Collection<String> selectableCategories;
        private String selectedCategory;
        private String taskDef;
        private LocalDate deadline;

        public Builder() {
        }

        public Builder indentWidth(Integer indentWidth) {
            this.indentWidth = indentWidth;
            return this;
        }

        public Builder completed(Boolean completed) {
            this.completed = completed;
            return this;
        }

        public Builder priorityColorStyle(String priorityColorStyle) {
            this.priorityColorStyle = priorityColorStyle;
            return this;
        }

        public Builder selectableCategories(Collection<String> selectableCategories) {
            this.selectableCategories = selectableCategories;
            return this;
        }

        public Builder selectedCategory(String selectedCategory) {
            this.selectedCategory = selectedCategory;
            return this;
        }

        public Builder taskDef(String taskDef) {
            this.taskDef = taskDef;
            return this;
        }

        public Builder deadline(LocalDate deadline) {
            this.deadline = deadline;
            return this;
        }

        public TaskDisplayState build() {
            return new TaskDisplayState(indentWidth, completed, priorityColorStyle,
                    selectableCategories, selectedCategory, taskDef, deadline);
        }

    }

    public static Builder builder() {
        return new Builder();
    }

    private Integer indentWidth;
    private Boolean completed;
    private String priorityColorStyle;
    private List<String> selectableCategories;
    private String selectedCategory;
    private String taskDef;
    private LocalDate deadline;

    private TaskDisplayState(Integer indentWidth, Boolean completed, String priorityColorStyle,
            Collection<String> selectableCategories, String selectedCategory,
            String taskDef, LocalDate deadline) {

        this.indentWidth = indentWidth;
        this.completed = completed;
        this.priorityColorStyle = priorityColorStyle;
        this.selectableCategories = new ArrayList<>(selectableCategories);
        this.selectedCategory = selectedCategory;
        this.taskDef = taskDef;
        this.deadline = deadline;
    }

    public Integer getIndentWidth() {
        return indentWidth;
    }

    public void setIndentWidth(Integer indentWidth) {
        this.indentWidth = indentWidth;
    }

    public Boolean isCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public String getPriorityColorStyle() {
        return priorityColorStyle;
    }

    public void setPriorityColorStyle(String priorityColorStyle) {
        this.priorityColorStyle = priorityColorStyle;
    }

    public List<String> getSelectableCategories() {
        return selectableCategories;
    }

    public void setSelectableCategories(List<String> selectableCategories) {
        this.selectableCategories = selectableCategories;
    }

    public String getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(String selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public String getTaskDef() {
        return taskDef;
    }

    public void setTaskDef(String taskDef) {
        this.taskDef = taskDef;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

}
