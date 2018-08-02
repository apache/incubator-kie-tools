package dummy;

import java.io.Serializable;

public class Dummy implements Serializable {

    private String name;

    public Dummy(String name) {
        this.name = name;
    }

    public String getName(){return name;}

    public String toString(){
        return "name:" + name + " \n";
    }
}
