package debrecen.university.pti.kovtamas.display.utils.display;

import debrecen.university.pti.kovtamas.display.utils.Modules;
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

    public static DisplayVo load(Modules module, final Locale locale) throws DisplayLoadException {
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

    public static DisplayVo load(Modules module) throws DisplayLoadException {
        return load(module, null);
    }

}
