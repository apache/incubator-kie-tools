package dummy;

public class Dummy {

    private String name, surname;
    private int age;

    public Dummy(String name, String surname, int age) {
        this.name = name;
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public int getAge() {
        return age;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("name:").append(name).append(" surname:").append(surname).append(" age:").append(age);
        return sb.toString();
    }
}