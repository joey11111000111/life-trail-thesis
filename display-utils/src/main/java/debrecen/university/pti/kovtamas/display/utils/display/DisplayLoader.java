package debrecen.university.pti.kovtamas.display.utils.display;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public final class DisplayLoader {

    private DisplayLoader() {
    }

    public enum Modules {
        MENU("/fxml/main-menu.fxml", "i18n.menu-localization"),
        TODO("/fxml/todo.fxml", "i18n.todo-localization"),
        JOURNAL(null, null);

        private final String fxmlPath;
        private final String resPath;

        Modules(final String fxmlPath, final String resPath) {
            this.fxmlPath = fxmlPath;
            this.resPath = resPath;
        }

        public String getFxmlPath() {
            return fxmlPath;
        }

        public String getResPath() {
            return resPath;
        }

    }

    public static DisplayVo fromFxml(Modules module, final Locale locale) throws DisplayLoadException {
        final String fxmlPath = module.getFxmlPath();
        final String resPath = module.getResPath();
        URL appUrl = DisplayLoader.class.getResource(fxmlPath);
        if (appUrl == null) {
            throw new DisplayLoadException("Could not get url for app root!");
        }

        InputStream openedStream = null;
        try {
            openedStream = appUrl.openStream();
        } catch (IOException ioe) {
            throw new DisplayLoadException("Failed to open stream from URL!", ioe);
        }

        FXMLLoader fxmlLoader = new FXMLLoader();
        if (resPath != null && locale != null) {
            fxmlLoader.setResources(ResourceBundle.getBundle(resPath, locale));
        }

        Parent root = null;
        try {
            root = fxmlLoader.load(openedStream);
        } catch (IOException ioe) {
            throw new DisplayLoadException("Could not load fxml!", ioe);
        }

        Scene scene = new Scene(root);
        Object controller = fxmlLoader.getController();

        return new DisplayVo(scene, controller);
    }

    public static DisplayVo fromFxml(Modules module) throws DisplayLoadException {
        return fromFxml(module, null);
    }

}
