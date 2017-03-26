package debrecen.university.pti.kovtamas.application;

import debrecen.university.pti.kovtamas.display.utils.Modules;
import debrecen.university.pti.kovtamas.display.utils.ValueChangeAction;
import java.util.HashMap;
import java.util.Map;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import lombok.NonNull;

public class ViewBuffer {

    private final Map<Modules, Scene> sceneBuffer;
    private final Map<Modules, Object> controllerBuffer;
    private final Map<Modules, String> titleBuffer;
    private final ObjectProperty<Modules> activeModuleProperty;

    public ViewBuffer(@NonNull final Modules activeModule) {
        activeModuleProperty = new SimpleObjectProperty<>(activeModule);
        sceneBuffer = new HashMap<>();
        controllerBuffer = new HashMap<>();
        titleBuffer = new HashMap<>();
    }

    public void registerActiveModuleChangeAction(ValueChangeAction<Modules> action) {
        activeModuleProperty.addListener((observable, fromModule, toModule) -> {
            if (fromModule != toModule) {
                action.accept(fromModule, toModule);
            }
        });
    }

    public void deactivateBeforeShutdown() {
        activeModuleProperty.setValue(null);
    }

    public Scene getScene(@NonNull final Modules module) {
        return sceneBuffer.get(module);
    }

    public Object getController(@NonNull final Modules module) {
        return controllerBuffer.get(module);
    }

    public void putScene(@NonNull final Modules module, @NonNull final Scene scene) {
        sceneBuffer.put(module, scene);
    }

    public void putController(@NonNull final Modules module, @NonNull final Object controller) {
        controllerBuffer.put(module, controller);
    }

    public void setActiveModule(@NonNull final Modules module) {
        activeModuleProperty.setValue(module);
    }

    public Scene getActiveScene() {
        return sceneBuffer.get(activeModuleProperty.getValue());
    }

    public Modules getActiveModule() {
        return activeModuleProperty.getValue();
    }

    public void putTitle(@NonNull final Modules module, @NonNull final String title) {
        titleBuffer.put(module, title);
    }

    public String getTitle(@NonNull final Modules module) {
        return titleBuffer.get(module);
    }

}
