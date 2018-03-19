# Defining New Components

# Property Readers and Writers

## Custom Elements

In order to add custom elements we first define the element as a field in an enum-like class 
(a class with several public static final fields: singleton values) called `CustomElement`.


```java
public class CustomElement<T> {
  ...
  public static final ElementDefinition<String> description = 
    new StringElement(
            "customDescription", // element name
            ""                   // default value when missing
    );
}
```


The reason why custom elements are defined as singleton values, is that
this way we have a complete list of such custom elements in a single place,
that is, the `CustomElement` class.

Each field can `get()` or `set(value)` of a custom element as a child of a given
element. For instance, consider a `UserTask task`.

We can get and set it using the following syntax:

```java
       CustomInput.description.of(task).set(description);
       String description = CustomInput.description.of(task).get();
```


Converter classes **MUST NOT** use these objects directly, 
but rather they should wrap it inside a *Reader, *Writer 
object
 
e.g., let's consider `customDescription` in UserTask

UserTaskPropertyWriter:

```java
public class UserTaskPropertyWriter {
   UserTask task;
   ...
   public void setDescription(String description) {
       CustomInput.description.of(task).set(description);
   }
```

UserTaskPropertyReader:

```java
public class UserTaskPropertyReader {
   UserTask task;
   ...
   public String getDescription() {
        return CustomInput.description.of(task).get();
   }
```

then converters for a UserTask are able to read/write from a task as
follows:

```java
    UserTask task = ...;
    UserTaskPropertyWriter p = propertyWriterFactory.of(task);
    p.setDescription("my description");

    UserTask task = ...;
    UserTaskPropertyReader p = propertyReaderFactory.of(task);
    String description = p.getDecsription();
```



