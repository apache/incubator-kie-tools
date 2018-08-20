package dummy;

import java.io.Serializable;

public class DummyOverride implements Serializable {

    private String surname;

    public DummyOverride(String surname) {
        this.surname = surname;
    }
}
