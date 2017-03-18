package debrecen.university.pti.kovtamas.display.utils.locale;

import java.util.Locale;

public class LocaleManager {

    private Locale locale;

    public LocaleManager() {
        locale = Locale.ENGLISH;
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
        return true;
    }

}
