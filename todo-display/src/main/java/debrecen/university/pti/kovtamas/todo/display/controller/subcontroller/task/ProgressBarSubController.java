package debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.task;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import lombok.Builder;

public class ProgressBarSubController {

    @Builder
    public static class ProgressRatio {

        private final Integer completedCount;
        private final Integer activeCount;

        public double getRatio() {
            double allCount = completedCount + activeCount;

            // Prevent division with 0
            if (allCount == 0) {
                return 0.0;
            }

            return completedCount / allCount;
        }

        @Override
        public String toString() {
            return "ProgressRatio:\tCompleted: " + completedCount + "\tActive: " + activeCount + "\tRatio: " + getRatio();
        }

    }

    private final VBox containerComponent;
    private final Rectangle indicator;
    private final double minHeight;

    public ProgressBarSubController(VBox containerComponent, Rectangle indicator) {
        this.containerComponent = containerComponent;
        this.indicator = indicator;
        minHeight = 30.0;
        indicator.setHeight(minHeight);
    }

    public void ratioChangedAction(ProgressRatio fromRatio, ProgressRatio toRatio) {
        DoubleProperty indicatorHeightProperty = indicator.heightProperty();
        indicatorHeightProperty.unbind();
        DoubleBinding binding = createIndicatorHeightBinding(toRatio.getRatio());
        indicatorHeightProperty.bind(binding);
        // fromRatio is not needed here, but this method must be compatible with the
        // accept(T fromValue, T toValue) method of ValueChangeAction<T> interface
    }

    private DoubleBinding createIndicatorHeightBinding(double ratio) {
        return new DoubleBinding() {
            {
                super.bind(containerComponent.heightProperty(), indicator.heightProperty());
            }

            @Override
            protected double computeValue() {
                System.out.println("width: " + containerComponent.widthProperty().getValue());
                double containerHeight = containerComponent.heightProperty().getValue();
                double calculatedIndicatorHeight = containerHeight * ratio;
                return Math.max(calculatedIndicatorHeight, minHeight);
            }
        };
    }

}
