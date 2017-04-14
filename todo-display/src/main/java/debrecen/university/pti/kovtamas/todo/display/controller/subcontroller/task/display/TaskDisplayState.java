package debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.task.display;

import debrecen.university.pti.kovtamas.todo.service.vo.CategoryVo;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskDisplayState {

    public static class Builder {

        private Integer indentWidth;
        private Boolean completed;
        private PriorityColors priorityColor;
        private List<CategoryVo> selectableCategories;
        private CategoryVo selectedCategory;
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

        public Builder priorityColor(PriorityColors priorityColorStyle) {
            this.priorityColor = priorityColorStyle;
            return this;
        }

        public Builder selectableCategories(List<CategoryVo> selectableCategories) {
            this.selectableCategories = selectableCategories;
            return this;
        }

        public Builder selectedCategory(CategoryVo selectedCategory) {
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
            return new TaskDisplayState(indentWidth, completed, priorityColor,
                    selectableCategories, selectedCategory, taskDef, deadline);
        }

    }

    public static Builder builder() {
        return new Builder();
    }

    private Integer indentWidth;
    private Boolean completed;
    private PriorityColors priorityColor;
    private List<CategoryVo> selectableCategories;
    private CategoryVo selectedCategory;
    private String taskDef;
    private LocalDate deadline;

    private TaskDisplayState(Integer indentWidth, Boolean completed, PriorityColors priorityColor,
            List<CategoryVo> selectableCategories, CategoryVo selectedCategory,
            String taskDef, LocalDate deadline) {

        this.indentWidth = indentWidth;
        this.completed = completed;
        this.priorityColor = priorityColor;
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

    public PriorityColors getPriorityColor() {
        return priorityColor;
    }

    public void setPriorityColor(PriorityColors priorityColor) {
        this.priorityColor = priorityColor;
    }

    public List<CategoryVo> getSelectableCategories() {
        return selectableCategories;
    }

    public void setSelectableCategories(List<CategoryVo> selectableCategories) {
        this.selectableCategories = selectableCategories;
    }

    public CategoryVo getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(CategoryVo selectedCategory) {
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
