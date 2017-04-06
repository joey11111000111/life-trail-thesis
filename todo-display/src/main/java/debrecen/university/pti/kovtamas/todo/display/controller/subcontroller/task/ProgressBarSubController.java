package debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.task;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

public class ProgressBarSubController {

    private final VBox containerComponent;
    private final Rectangle indicator;
    private final double minHeight;

    public ProgressBarSubController(VBox containerComponent, Rectangle indicator) {
        this.containerComponent = containerComponent;
        this.indicator = indicator;
        minHeight = 30.0;
//        indicator.setHeight(minHeight);
    }

    public void setRatio(ProgressRatio progressRatio) {
        DoubleProperty indicatorHeightProperty = indicator.heightProperty();
        indicatorHeightProperty.unbind();
        DoubleBinding binding = createIndicatorHeightBinding(progressRatio.getRatio());
        indicatorHeightProperty.bind(binding);
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
