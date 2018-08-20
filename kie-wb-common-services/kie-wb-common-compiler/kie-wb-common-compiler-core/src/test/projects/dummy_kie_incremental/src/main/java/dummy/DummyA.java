package dummy;

import java.io.Serializable;

public class DummyA extends Dummy implements Serializable {

    private String surname;

    public DummyA(String name, String surname) {
        super(name);
        this.surname = surname;
    }

    public String toString() {
        return super.toString() + "surname:" + surname + " \n";
    }
}
