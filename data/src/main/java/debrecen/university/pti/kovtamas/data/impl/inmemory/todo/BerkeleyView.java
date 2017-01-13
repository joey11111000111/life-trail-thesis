package debrecen.university.pti.kovtamas.data.impl.inmemory.todo;

import com.sleepycat.bind.serial.ClassCatalog;
import com.sleepycat.bind.serial.SerialSerialBinding;
import com.sleepycat.collections.StoredValueSet;
import debrecen.university.pti.kovtamas.data.entity.todo.TaskEntity;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

class BerkeleyView {

    private BerkeleyDatabase db;
    private StoredValueSet<TaskEntity> todoEntitySet;

    public BerkeleyView(BerkeleyDatabase db) {
        this.db = db;
        ClassCatalog catalog = db.getConfig().getJavaCatalog();

        todoEntitySet = new StoredValueSet<>(db.getTodoEntityDb(),
                new TodoEntityBinding(catalog, TodoEntityKey.class, TodoEntityValue.class), true);
    }

    private class TodoEntityBinding extends SerialSerialBinding<TodoEntityKey, TodoEntityValue, TaskEntity> {

        public TodoEntityBinding(ClassCatalog classCatalog, Class<TodoEntityKey> keyClass,
                Class<TodoEntityValue> dataClass) {
            super(classCatalog, keyClass, dataClass);
        }

        @Override
        public TaskEntity entryToObject(TodoEntityKey key, TodoEntityValue data) {
            return TaskEntity.builder()
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
        public TodoEntityKey objectToKey(TaskEntity entity) {
            return new TodoEntityKey(entity.getId());
        }

        @Override
        public TodoEntityValue objectToData(TaskEntity entity) {
            return TodoEntityValue.builder()
                    .taskDef(entity.getTaskDef())
                    .priority(entity.getPriority())
                    .deadline(entity.getDeadline())
                    .category(entity.getCategory())
                    .subTaskIds(entity.getSubTaskIds())
                    //                    .repeating(entity.getRepeating())
                    .build();
        }
    }

    public void close() {
        db.close();
    }

    public void add(TaskEntity entity) {
        todoEntitySet.add(entity);
    }

    public void addAll(Collection<TaskEntity> entityCollection) {
        todoEntitySet.addAll(todoEntitySet);
    }

    public Set<TaskEntity> getAllEntities() {
        return new HashSet<>(todoEntitySet);
    }

}
