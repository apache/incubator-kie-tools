package guvnor.feature.dtables;

// $HASH(ee15bb4bdabae23dd989493b198c753e) (added manually)
public class Person {
    
    private String sex;
    
    private int age;
    
    private String name;
    
    private int value;
    
    private int dummy;

    private String helloMsg;

    private boolean married;

    public Person() {
    }

    public Person(String sex, int age, String name, int value, int dummy, String helloMsg, boolean married) {
        this.sex = sex;
        this.age = age;
        this.name = name;
        this.value = value;
        this.dummy = dummy;
        this.helloMsg = helloMsg;
        this.married = married;
    }

    public String getSex() {
        return this.sex;
    }

    public void setSex( String sex ) {
        this.sex = sex;
    }
    
    public int getAge() {
        return this.age;
    }

    public void setAge(int age ) {
        this.age = age;
    }
    
    public String getName() {
        return this.name;
    }

    public void setName(String name ) {
        this.name = name;
    }
    
    public int getValue() {
        return this.value;
    }

    public void setValue(int value ) {
        this.value = value;
    }
    
    public int getDummy() {
        return this.dummy;
    }

    public void setDummy(int dummy ) {
        this.dummy = dummy;
    }

    public String getHelloMsg() {
        return helloMsg;
    }

    public void setHelloMsg(String helloMsg) {
        this.helloMsg = helloMsg;
    }

    public boolean isMarried() {
        return married;
    }

    public void setMarried(boolean married) {
        this.married = married;
    }
}
