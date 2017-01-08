package debrecen.university.pti.kovtamas.data.impl.inmemory.todo;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
class TodoEntityKey implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;

}
