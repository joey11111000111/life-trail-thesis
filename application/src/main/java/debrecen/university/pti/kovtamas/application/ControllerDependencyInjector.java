package debrecen.university.pti.kovtamas.application;

import debrecen.university.pti.kovtamas.application.controller.MenuController;
import debrecen.university.pti.kovtamas.display.utils.Modules;
import debrecen.university.pti.kovtamas.display.utils.locale.Localizer;
import debrecen.university.pti.kovtamas.todo.display.controller.TodoController;
import debrecen.university.pti.kovtamas.todo.display.controller.TodoControllerDependencies;
import debrecen.university.pti.kovtamas.todo.service.impl.TodoServiceImpl;
import java.util.function.Consumer;

public class ControllerDependencyInjector {

    public static class Builder {

        private Consumer<Localizer.SupportedLanguages> switchLanguageMethod;
        private Consumer<Modules> switchSceneMethod;

        private Builder() {
        }

        public ControllerDependencyInjector build() {
            if (!areAllFieldsSet()) {
                throw new IllegalStateException("Not all fields are set, cannot build instance!");
            }

            return newInstance();
        }

        public Builder switchLanguageMethod(Consumer<Localizer.SupportedLanguages> switchLanguageMethod) {
            this.switchLanguageMethod = switchLanguageMethod;
            return this;
        }

        public Builder switchSceneMethod(Consumer<Modules> switchSceneMethod) {
            this.switchSceneMethod = switchSceneMethod;
            return this;
        }

        private ControllerDependencyInjector newInstance() {
            return new ControllerDependencyInjector(switchLanguageMethod, switchSceneMethod);
        }

        private boolean areAllFieldsSet() {
            return switchLanguageMethod != null && switchSceneMethod != null;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private final Consumer<Localizer.SupportedLanguages> switchLanguageMethod;
    private final Consumer<Modules> switchSceneMethod;

    private ControllerDependencyInjector(Consumer<Localizer.SupportedLanguages> switchLanguageMethod,
            Consumer<Modules> switchSceneMethod) {
        this.switchLanguageMethod = switchLanguageMethod;
        this.switchSceneMethod = switchSceneMethod;
    }

    public TodoController injectTodoControllerDependenciesInto(TodoController todoController) {
        TodoControllerDependencies todoDependencies = buildTodoControllerDependencies();
        todoController.startUp(todoDependencies);
        return todoController;
    }

    public MenuController injectMenuControllerDependenciesInto(MenuController menuController) {
        menuController.startup(
                () -> switchSceneMethod.accept(Modules.TODO),
                switchLanguageMethod
        );
        return menuController;
    }

    private TodoControllerDependencies buildTodoControllerDependencies() {
        return TodoControllerDependencies.builder()
                .service(new TodoServiceImpl())
                .backToMenuMethod(() -> switchSceneMethod.accept(Modules.MENU))
                .switchLanguageMethod(switchLanguageMethod)
                .build();
    }

}
