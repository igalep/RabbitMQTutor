package tutor1_1;

import java.io.Serializable;

/**
 * Created by epshtein.
 * Date: 2019-06-02
 */
public class Student implements Serializable {
    private long id;
    private String name;

    public Student(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
