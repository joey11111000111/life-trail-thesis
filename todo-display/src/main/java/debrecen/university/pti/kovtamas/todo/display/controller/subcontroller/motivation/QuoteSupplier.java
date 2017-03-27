package debrecen.university.pti.kovtamas.todo.display.controller.subcontroller.motivation;

import debrecen.university.pti.kovtamas.display.utils.Modules;
import debrecen.university.pti.kovtamas.display.utils.locale.Localizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuoteSupplier {

    private final List<String> allQuotes;
    private final Localizer localizer;
    private final Random rnd;
    private int currentQuoteIndex;

    public QuoteSupplier() {
        localizer = Localizer.getInstance();
        allQuotes = collectLocalizedQuotes();
        rnd = new Random();
        currentQuoteIndex = -1;
    }

    public String nextQuote() {
        int nextQuoteIndex = nextQuoteIndex();
        currentQuoteIndex = nextQuoteIndex;
        return allQuotes.get(nextQuoteIndex);
    }

    private int nextQuoteIndex() {
        int quoteIndex;
        do {
            quoteIndex = generateRandomQuoteIndex();
        } while (quoteIndex == currentQuoteIndex);

        return quoteIndex;
    }

    private int generateRandomQuoteIndex() {
        return rnd.nextInt(allQuotes.size());
    }

    private List<String> collectLocalizedQuotes() {
        final int quoteCount = 10;
        List<String> localizedQuotes = new ArrayList<>(quoteCount);

        for (int i = 0; i < quoteCount; i++) {
            localizedQuotes.add(getQuote(i));
        }

        return localizedQuotes;
    }

    private String getQuote(int index) {
        String key = "motivation_" + index;
        return localizer.localize(key, Modules.TODO);
    }

}
