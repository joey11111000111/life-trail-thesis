package debrecen.university.pti.kovtamas.todo.display.controller.subcontroller;

import debrecen.university.pti.kovtamas.display.utils.Modules;
import debrecen.university.pti.kovtamas.display.utils.locale.Localizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.animation.FadeTransition;
import javafx.scene.text.Text;
import javafx.util.Duration;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MotivationSubController {

    private Text quoteDisplayText;
    private final Localizer localizer;
    private List<String> allLocalizedQuotes;
    private int currentQuoteIndex;
    private Thread quoteChangerThread;

    public MotivationSubController(Text quoteDisplayText) {
        this.quoteDisplayText = quoteDisplayText;
        localizer = Localizer.getInstance();
        allLocalizedQuotes = collectLocalizedQuotes();
        currentQuoteIndex = -1;
        quoteChangerThread = null;
    }

    public void startMotivationTextChanger() {
        final long quoteOnScreenMillis = (long) Duration.minutes(1.5).toMillis();

        Runnable endlessQuoteChanger = () -> {
            while (true) {
                createQuoteChangerEffect().play();

                try {
                    Thread.sleep(quoteOnScreenMillis);
                } catch (InterruptedException ie) {
                    log.info("Quote changer thread was stopped.", ie);
                    return;
                }
            }
        };

        quoteChangerThread = new Thread(endlessQuoteChanger);
        quoteChangerThread.setDaemon(true);
        quoteChangerThread.start();

    }

    public void stopMotivationTextChanger() {
        if (quoteChangerThread != null) {
            quoteChangerThread.interrupt();
            quoteChangerThread = null;
        }
    }

    public void languageChangedAction() {
        stopMotivationTextChanger();
        allLocalizedQuotes = collectLocalizedQuotes();
        startMotivationTextChanger();
    }

    public void setQuoteDisplayText(@NonNull Text newQuoteDisplayText) {
        stopMotivationTextChanger();
        this.quoteDisplayText = newQuoteDisplayText;
        startMotivationTextChanger();
    }

    private FadeTransition createQuoteChangerEffect() {
        final Duration fadeEffectDuration = Duration.seconds(3);
        final int visible = 1;
        final int transparent = 0;

        FadeTransition quoteChangerEffect = new FadeTransition(fadeEffectDuration, quoteDisplayText);
        quoteChangerEffect.setFromValue(visible);
        quoteChangerEffect.setToValue(transparent);
        quoteChangerEffect.setOnFinished((event) -> {
            quoteDisplayText.setText(getNextQuote());

            FadeTransition fadeInEffect = new FadeTransition(fadeEffectDuration, quoteDisplayText);
            fadeInEffect.setFromValue(transparent);
            fadeInEffect.setToValue(visible);
            fadeInEffect.play();
        });

        return quoteChangerEffect;
    }

    private List<String> collectLocalizedQuotes() {
        List<String> localizedQuotes = new ArrayList<>();
        StringBuilder localizationKeyBuilder = new StringBuilder("motivation_");
        Modules moduleOfQuote = Modules.TODO;

        final int quoteCount = 10;
        for (int i = 0; i < quoteCount; i++) {
            String keyPostfix = Integer.toString(i);
            localizationKeyBuilder.append(keyPostfix);

            String quoteKey = localizationKeyBuilder.toString();
            localizedQuotes.add(localizer.localize(quoteKey, moduleOfQuote));

            deletePostfix(localizationKeyBuilder, keyPostfix);
        }

        return localizedQuotes;
    }

    private void deletePostfix(StringBuilder builder, String postfix) {
        int builderLength = builder.length();
        int startIndex = builderLength - postfix.length();
        builder.delete(startIndex, builderLength);
    }

    private String getNextQuote() {
        Random rnd = new Random();
        final int upperBound = allLocalizedQuotes.size();

        int quoteIndex;
        do {
            quoteIndex = rnd.nextInt(upperBound);
        } while (quoteIndex == currentQuoteIndex);

        currentQuoteIndex = quoteIndex;
        return allLocalizedQuotes.get(quoteIndex);
    }

}
