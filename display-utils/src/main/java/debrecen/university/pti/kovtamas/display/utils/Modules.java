package debrecen.university.pti.kovtamas.display.utils;

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
