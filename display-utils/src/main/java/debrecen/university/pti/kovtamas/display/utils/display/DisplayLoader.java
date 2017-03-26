package debrecen.university.pti.kovtamas.display.utils.display;

import debrecen.university.pti.kovtamas.display.utils.locale.Localizer;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public final class DisplayLoader {

    private static final Localizer LOCALIZER;

    public static enum FxmlFiles {
        MENU_FXML("/fxml/main-menu.fxml"),
        TODO_FXML("/fxml/todo.fxml"),
        TASK_FXML("/fxml/task.fxml");

        private final URL fxmlUrl;

        private FxmlFiles(String path) {
            fxmlUrl = FxmlFiles.class.getResource(path);
            if (fxmlUrl == null) {
                String message = "Failed to create URL for fxml from path: " + path;
                throw new RuntimeException(message);
            }
        }

        public URL getFxmlUrl() {
            return fxmlUrl;
        }
    }

    static {
        LOCALIZER = Localizer.getInstance();
    }

    private DisplayLoader() {
    }

    public static DisplayVo loadFxmlWithResource(final FxmlFiles fxml, final Localizer.ResourcePaths resourcePath) throws DisplayLoadException {
        if (fxml == null) {
            throw new IllegalArgumentException("Cannot load display. No display specified, fxml is null.");
        }

        FXMLLoader fxmlLoader = createConfiguredFxmlLoader(resourcePath);
        InputStream fxmlStream = openStreamFromUrl(fxml.getFxmlUrl());

        Parent rootViewComponent = loadFxmlFromStream(fxmlLoader, fxmlStream);
        Object controller = fxmlLoader.getController();

        return new DisplayVo(rootViewComponent, controller);
    }

    private static FXMLLoader createConfiguredFxmlLoader(Localizer.ResourcePaths resourcePath) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        if (resourcePath != null) {
            fxmlLoader.setResources(ResourceBundle.getBundle(resourcePath.getPath(), LOCALIZER.getCurrentLocale()));
        }
        return fxmlLoader;
    }

    private static InputStream openStreamFromUrl(URL url) throws DisplayLoadException {
        InputStream fxmlStream = null;
        try {
            fxmlStream = url.openStream();
        } catch (IOException ioe) {
            throw new DisplayLoadException("Failed to open stream for task from URL!", ioe);
        }

        return fxmlStream;
    }

    private static Parent loadFxmlFromStream(FXMLLoader fxmlLoader, InputStream fxmlStream) throws DisplayLoadException {
        Parent rootComponent = null;
        try {
            rootComponent = fxmlLoader.load(fxmlStream);
        } catch (IOException ioe) {
            throw new DisplayLoadException("Could not load task fxml!", ioe);
        }

        if (rootComponent == null) {
            throw new DisplayLoadException("Parent loaded from fxml is null!");
        }

        return rootComponent;
    }

}
