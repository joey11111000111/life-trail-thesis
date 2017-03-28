package debrecen.university.pti.kovtamas.display.utils.load;

import javafx.scene.Parent;
import javafx.scene.Scene;

public class DisplayVo {

    private Parent rootComponent;
    private Object controller;

    public DisplayVo() {
    }

    public DisplayVo(Parent rootComponent, Object controller) {
        this.rootComponent = rootComponent;
        this.controller = controller;
    }

    public Parent getRootComponent() {
        return rootComponent;
    }

    public void setRootComponent(Parent rootComponent) {
        this.rootComponent = rootComponent;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public Scene createScene() {
        return new Scene(rootComponent);
    }

}
