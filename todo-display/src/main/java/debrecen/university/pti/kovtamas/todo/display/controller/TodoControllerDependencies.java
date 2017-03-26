package debrecen.university.pti.kovtamas.todo.display.controller;

import debrecen.university.pti.kovtamas.display.utils.VoidNoArgMethod;
import debrecen.university.pti.kovtamas.display.utils.locale.Localizer;
import debrecen.university.pti.kovtamas.todo.service.api.TodoService;
import java.util.function.Consumer;

public class TodoControllerDependencies {

    public static class Builder {

        private TodoService service;
        private VoidNoArgMethod backToMenuMethod;
        private Consumer<Localizer.SupportedLanguages> switchLanguageMethod;

        private Builder() {
        }

        public Builder service(TodoService service) {
            this.service = service;
            return this;
        }

        public Builder backToMenuMethod(VoidNoArgMethod backToMenuMethod) {
            this.backToMenuMethod = backToMenuMethod;
            return this;
        }

        public Builder switchLanguageMethod(Consumer<Localizer.SupportedLanguages> switchLanguageMethod) {
            this.switchLanguageMethod = switchLanguageMethod;
            return this;
        }

        public TodoControllerDependencies build() {
            if (!areAllFieldsSet()) {
                throw new IllegalStateException("Not all fields are set! Cannot create "
                        + "TodoControllerDependencies instance!");
            }

            return newInstance();
        }

        private TodoControllerDependencies newInstance() {
            return new TodoControllerDependencies(service, backToMenuMethod, switchLanguageMethod);
        }

        private boolean areAllFieldsSet() {
            return service != null && backToMenuMethod != null && switchLanguageMethod != null;
        }

    }

    public static Builder builder() {
        return new Builder();
    }

    private final TodoService service;
    private final VoidNoArgMethod backToMenuMethod;
    private final Consumer<Localizer.SupportedLanguages> switchLanguageMethod;

    private TodoControllerDependencies(TodoService service, VoidNoArgMethod backToMenuMethod,
            Consumer<Localizer.SupportedLanguages> switchLanguageMethod) {
        this.service = service;
        this.backToMenuMethod = backToMenuMethod;
        this.switchLanguageMethod = switchLanguageMethod;
    }

    public TodoService getService() {
        return service;
    }

    public VoidNoArgMethod getBackToMenuMethod() {
        return backToMenuMethod;
    }

    public Consumer<Localizer.SupportedLanguages> getSwitchLanguageMethod() {
        return switchLanguageMethod;
    }

}
