package debrecen.university.pti.kovtamas.display.utils.locale;

import debrecen.university.pti.kovtamas.display.utils.Modules;
import debrecen.university.pti.kovtamas.display.utils.ValueChangeAction;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.NonNull;

public class Localizer {

    public static enum SupportedLanguages {
        HUNGARIAN(new Locale("hu")),
        ENGLISH(new Locale("en")),
        SPANISH(new Locale("es"));

        private final Locale locale;

        private SupportedLanguages(Locale locale) {
            this.locale = locale;
        }

        public Locale getLocale() {
            return locale;
        }
    }

    public static enum ResourcePaths {
        MENU_RES_PATH("i18n.menu-localization"),
        TODO_RES_PATH("i18n.todo-localization");

        private final String resourcePath;

        private ResourcePaths(String resourcePath) {
            this.resourcePath = resourcePath;
        }

        public String getPath() {
            return resourcePath;
        }
    }

    private static final Localizer LOCALIZER_INSTANCE;

    private final ObjectProperty<SupportedLanguages> languageProperty;
    private final Map<Modules, ResourceBundle> resourcesForModules;

    static {
        LOCALIZER_INSTANCE = new Localizer();
    }

    public static Localizer getInstance() {
        return LOCALIZER_INSTANCE;
    }

    private Localizer() {
        resourcesForModules = new HashMap<>();
        languageProperty = new SimpleObjectProperty<>(SupportedLanguages.ENGLISH);
        loadLocalizedMessagesWithLocale(languageProperty.getValue().getLocale());
    }

    public Locale getCurrentLocale() {
        return languageProperty.getValue().getLocale();
    }

    public boolean setLanguage(@NonNull final SupportedLanguages language) {
        if (isTheSameAsCurrentLanguage(language)) {
            return false;
        }

        loadLocalizedMessagesWithLocale(language.getLocale());
        languageProperty.setValue(language);

        return true;
    }

    private boolean isTheSameAsCurrentLanguage(SupportedLanguages language) {
        return language == languageProperty.getValue();
    }

    public String localize(String key, Modules module) {
        ResourceBundle messages = resourcesForModules.get(module);
        return messages.getString(key);
    }

    public void registerLanguageChangeAction(ValueChangeAction<SupportedLanguages> action) {
        languageProperty.addListener((observable, fromValue, toValue) -> {
            action.accept(fromValue, toValue);
        });
    }

    private void loadLocalizedMessagesWithLocale(Locale locale) {
        ResourceBundle menuMessages = ResourceBundle.getBundle(ResourcePaths.MENU_RES_PATH.getPath(), locale);
        ResourceBundle todoMessages = ResourceBundle.getBundle(ResourcePaths.TODO_RES_PATH.getPath(), locale);
        resourcesForModules.put(Modules.MENU, menuMessages);
        resourcesForModules.put(Modules.TODO, todoMessages);
    }

}
