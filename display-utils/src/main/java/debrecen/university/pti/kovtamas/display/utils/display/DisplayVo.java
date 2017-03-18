package debrecen.university.pti.kovtamas.display.utils.display;

import javafx.scene.Scene;

public class DisplayVo {

    private Scene displayScene;
    private Object controller;

    public DisplayVo() {
    }

    public DisplayVo(Scene displayScene, Object controller) {
        this.displayScene = displayScene;
        this.controller = controller;
    }

    public Scene getDisplayScene() {
        return displayScene;
    }

    public void setDisplayScene(Scene displayScene) {
        this.displayScene = displayScene;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

}
