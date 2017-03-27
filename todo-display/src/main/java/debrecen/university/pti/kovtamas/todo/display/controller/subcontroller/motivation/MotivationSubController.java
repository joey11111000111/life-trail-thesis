package debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.motivation;

import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MotivationSubController {

    private Text quoteDisplayText;
    private QuoteChanger quoteChanger;

    public MotivationSubController(HBox quoteContainer, Text quoteDisplayText) {
        initFields(quoteDisplayText);
        makeTextWidthDynamic(quoteContainer);
    }

    public void startMotivationTextChanger() {
        quoteChanger.startMotivationTextChanger();
    }

    public void stopMotivationTextChanger() {
        quoteChanger.stopMotivationTextChanger();
    }

    private void initFields(Text quoteDisplayText) {
        this.quoteDisplayText = quoteDisplayText;
        this.quoteChanger = new QuoteChanger(quoteDisplayText, new QuoteSupplier());
    }

    private void makeTextWidthDynamic(HBox quoteContainer) {
        quoteContainer.widthProperty().addListener((observable, oldWidth, newWidth) -> {
            quoteDisplayText.setWrappingWidth(newWidth.doubleValue());
        });
    }

}
