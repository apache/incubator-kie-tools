package guvnor.feature.dtables;

// $HASH(ee15bb4bdabae23dd989493b198c753e) (added manually)
public class Person {
    
    private String sex;
    
    private int age;
    
    private String name;
    
    private int value;
    
    private int dummy;

    public Person() {
    }

    public Person(int value, String name, int dummy, String sex, int age) {
        this.value = value;
        this.name = name;
        this.dummy = dummy;
        this.sex = sex;
        this.age = age;
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




}
