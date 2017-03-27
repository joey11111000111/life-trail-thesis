package debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.motivation;

import javafx.animation.FadeTransition;
import javafx.scene.text.Text;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QuoteChanger {

    private final Text motivationText;
    private final QuoteSupplier quoteSupplier;

    private final long quoteOnScreenMillis;

    private Thread quoteChangerThread;
    private boolean isQuoteChangerStopped;

    public QuoteChanger(Text motivationText, QuoteSupplier quoteSupplier) {
        this.motivationText = motivationText;
        this.quoteSupplier = quoteSupplier;
        quoteOnScreenMillis = java.time.Duration.ofSeconds(12).toMillis();
//        quoteOnScreenMillis = java.time.Duration.ofMinutes(1).toMillis();
        quoteChangerThread = null;
        isQuoteChangerStopped = false;
    }

    public void startMotivationTextChanger() {
        isQuoteChangerStopped = false;
        Runnable endlessQuoteChanger = createQuoteChangerRunnable();
        quoteChangerThread = new Thread(endlessQuoteChanger);
        quoteChangerThread.setDaemon(true);
        quoteChangerThread.start();
    }

    private Runnable createQuoteChangerRunnable() {
        return () -> {
            while (!isQuoteChangerStopped) {
                quoteChangeAction();
            }
        };
    }

    private void quoteChangeAction() {
        createQuoteChangerEffect().play();

        try {
            Thread.sleep(quoteOnScreenMillis);
        } catch (InterruptedException ie) {
            log.info("Quote changer thread was interrupted.", ie);
            stopMotivationTextChanger();
        }
    }

    private FadeTransition createQuoteChangerEffect() {
        final Duration fadeEffectDuration = Duration.seconds(3);

        FadeTransition quoteChangerEffect = new FadeTransition(fadeEffectDuration, motivationText);
        quoteChangerEffect.setFromValue(1);
        quoteChangerEffect.setToValue(0);
        quoteChangerEffect.setOnFinished((event) -> {
            fadeInNewQuote(fadeEffectDuration);
        });

        return quoteChangerEffect;
    }

    private void fadeInNewQuote(Duration effectDuration) {
        motivationText.setText(quoteSupplier.nextQuote());

        FadeTransition fadeInEffect = new FadeTransition(effectDuration, motivationText);
        fadeInEffect.setFromValue(0);
        fadeInEffect.setToValue(1);
        fadeInEffect.play();
    }

    public void stopMotivationTextChanger() {
        isQuoteChangerStopped = true;
    }

}
