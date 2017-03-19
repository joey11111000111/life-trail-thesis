package debrecen.university.pti.kovtamas.display.utils.locale;

import debrecen.university.pti.kovtamas.display.utils.Modules;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;

public class LocaleManager {

    private static final LocaleManager LOCALE_MANAGER_INSTANCE;

    private Locale locale;
    private ResourceBundle menuMessages;
    private ResourceBundle todoMessages;
    private int prevQuoteIndex;

    static {
        LOCALE_MANAGER_INSTANCE = new LocaleManager();
    }

    private LocaleManager() {
        locale = Locale.ENGLISH;
        loadLocalizedMessages();
        prevQuoteIndex = -1;
    }

    public static LocaleManager getInstance() {
        return LOCALE_MANAGER_INSTANCE;
    }

    public Locale getLocale() {
        return locale;
    }

    public boolean setLanguage(final String lang) {
        if (lang == null || lang.isEmpty()) {
            throw new IllegalArgumentException("Language string must not be null or empty!");
        }

        if (lang.toLowerCase().equals(locale.getLanguage())) {
            return false;
        }

        Locale newLocale = new Locale(lang);
        if (locale == null) {
            throw new IllegalArgumentException("Invalid language code!");
        }

        locale = newLocale;
        loadLocalizedMessages();
        return true;
    }

    public String localize(String key, Modules module) {
        ResourceBundle messages;
        switch (module) {
            case MENU:
                messages = menuMessages;
                break;
            case TODO:
                messages = todoMessages;
                break;
            default:
                throw new UnsupportedOperationException("Localization for module '" + module.name() + "' is not implemented yet!");
        }

        return messages.getString(key);
    }

    public String getNextMotivationalQuote() {
        StringBuilder keyBuilder = new StringBuilder("motivation_");
        Random rnd = new Random();

        // Get next random quote index, which is different from the previous index
        int currentQuoteIndex;
        final int quoteIndexBound = 10;
        do {
            currentQuoteIndex = rnd.nextInt(quoteIndexBound);
        } while (currentQuoteIndex == prevQuoteIndex);

        prevQuoteIndex = currentQuoteIndex;
        keyBuilder.append(currentQuoteIndex);
        return todoMessages.getString(keyBuilder.toString());
    }

    private void loadLocalizedMessages() {
        menuMessages = ResourceBundle.getBundle(Modules.MENU.getResPath(), locale);
        todoMessages = ResourceBundle.getBundle(Modules.TODO.getResPath(), locale);
    }

}
