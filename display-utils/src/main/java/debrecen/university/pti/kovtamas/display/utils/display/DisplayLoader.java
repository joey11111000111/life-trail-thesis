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

        URL appUrl = getResourceUrl(fxmlPath);
        InputStream openedStream = openStreamFromUrl(appUrl);

        FXMLLoader fxmlLoader = new FXMLLoader();
        if (resPath != null && locale != null) {
            fxmlLoader.setResources(ResourceBundle.getBundle(resPath, locale));
        }

        Parent root = loadFxml(fxmlLoader, openedStream);

        Scene scene = new Scene(root);
        Object controller = fxmlLoader.getController();

        return new DisplayVo(scene, controller);
    }

    public static DisplayVo load(Modules module) throws DisplayLoadException {
        return load(module, null);
    }

    public static Object loadTaskDisplay() throws DisplayLoadException {
        String fxmlPath = "/fxml/task.fxml";

        URL url = getResourceUrl(fxmlPath);
        InputStream openedStream = openStreamFromUrl(url);
        FXMLLoader fxmlLoader = new FXMLLoader();
        loadFxml(fxmlLoader, openedStream);

        return fxmlLoader.getController();
    }

    private static URL getResourceUrl(String path) throws DisplayLoadException {
        URL url = DisplayLoader.class.getResource(path);
        if (url == null) {
            throw new DisplayLoadException("Could not get url for task root!");
        }

        return url;
    }

    private static InputStream openStreamFromUrl(URL url) throws DisplayLoadException {
        InputStream openedStream = null;
        try {
            openedStream = url.openStream();
        } catch (IOException ioe) {
            throw new DisplayLoadException("Failed to open stream for task from URL!", ioe);
        }

        return openedStream;
    }

    private static Parent loadFxml(FXMLLoader fxmlLoader, InputStream openedStream) throws DisplayLoadException {
        Parent root = null;
        try {
            root = fxmlLoader.load(openedStream);
        } catch (IOException ioe) {
            throw new DisplayLoadException("Could not load task fxml!", ioe);
        }

        if (root == null) {
            throw new DisplayLoadException("Parent loaded from fxml is null!");
        }

        return root;
    }

}
