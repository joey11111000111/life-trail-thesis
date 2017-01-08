package debrecen.university.pti.kovtamas.data.impl.todo;

import com.sleepycat.bind.serial.ClassCatalog;
import com.sleepycat.bind.serial.SerialSerialBinding;
import com.sleepycat.collections.StoredValueSet;
import debrecen.university.pti.kovtamas.data.entity.todo.TodoEntity;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

class BerkeleyView {

    private StoredValueSet<TodoEntity> todoEntitySet;

    public BerkeleyView(BerkeleyDatabase db) {
        ClassCatalog catalog = db.getConfig().getJavaCatalog();

        todoEntitySet = new StoredValueSet<>(db.getTodoEntityDb(),
                new TodoEntityBinding(catalog, TodoEntityKey.class, TodoEntityValue.class), true);
    }

    private class TodoEntityBinding extends SerialSerialBinding<TodoEntityKey, TodoEntityValue, TodoEntity> {

        public TodoEntityBinding(ClassCatalog classCatalog, Class<TodoEntityKey> keyClass,
                Class<TodoEntityValue> dataClass) {
            super(classCatalog, keyClass, dataClass);
        }

        @Override
        public TodoEntity entryToObject(TodoEntityKey key, TodoEntityValue data) {
            return TodoEntity.builder()
                    .id(key.getId())
                    .taskDef(data.getTaskDef())
                    .priority(data.getPriority())
                    .deadline(data.getDeadline())
                    .category(data.getCategory())
                    .subTaskIds(data.getSubTaskIds())
                    .repeating(data.isRepeating())
                    .build();
        }

        @Override
        public TodoEntityKey objectToKey(TodoEntity entity) {
            return new TodoEntityKey(entity.getId());
        }

        @Override
        public TodoEntityValue objectToData(TodoEntity entity) {
            return TodoEntityValue.builder()
                    .taskDef(entity.getTaskDef())
                    .priority(entity.getPriority())
                    .deadline(entity.getDeadline())
                    .category(entity.getCategory())
                    .subTaskIds(entity.getSubTaskIds())
                    .repeating(entity.isRepeating())
                    .build();
        }
    }

    public void add(TodoEntity entity) {
        todoEntitySet.add(entity);
    }

    public void addAll(Collection<TodoEntity> entityCollection) {
        todoEntitySet.addAll(todoEntitySet);
    }

    public Set<TodoEntity> getAllEntities() {
        return new HashSet<>(todoEntitySet);
    }

}
