# Beans API

## Introduction

The Preferences Beans API provides you a way to create POJOs and, by adding some configuration annotations, transform them into managed preference objects which allow users to manage these preferences at run-time.

## API

To transform a POJO in a preference, you should:

* Annotate it with `@WorkbenchPreference`;
* Annotate its properties with `@Property`;
* Extend `BasePreference<YourPreferencePOJO>`;
* Optionally override the `defaultValue` method to define the preference default value.

Each of those annotations have some attributes that can be customized:

* `@WorkbenchPreference`:
    * `root`: Optional boolean attribute. It defines if a preference is a root preference, which means it will appear in the first level of the hierarchic structure printed in the configuration screen (a future feature).
    * `bundleKey`: Optional (but recommended to be customized) string attribute. It defines a bundle key that will be used to internationalize the property's label wherever its necessary. It's expected that the Errai's TranslationService will have access to the key translation.

* `@Property`:
    * `formType`: Optional PropertyFormType (an enum) attribute. Defines the type of field to be used in a default provided form. The default is `TEXT`, and the other possible types are `BOOLEAN`, `NATURAL_NUMBER`, `SECRET_TEXT` and `COLOR`. You should only specify this if you are using the default form for preferences edition, otherwise it will have no effect.
    * `inherited`: Optional boolean attribute. If false, the property will be defined specifically for this preference. If true, it will share its value with another inherited properties of the same type.
    * `bundleKey`: Optional (but recommended to be customized) string attribute. It defines a bundle key that will be used to internationalize the property's label wherever its necessary. It's expected that the Errai's TranslationService will have access to the key translation.

Observations:
* It is expected that the `private` properties have one setter and one getter method.
* To override the method `defaultValue`, you must only set the desired values in its parameter and return it, like in the example below.

## How it works

For example, you can have this preference:

```
import org.uberfire.ext.preferences.shared.PropertyFormType;
import org.uberfire.ext.preferences.shared.annotations.Property;
import org.uberfire.ext.preferences.shared.annotations.WorkbenchPreference;
import org.uberfire.ext.preferences.shared.bean.BasePreference;

@WorkbenchPreference(bundleKey = "MyPreference.Label")
public class MyPreference implements BasePreference<MyPreference> {

    @Property(bundleKey = "MyPreference.Text")
    String text;

    @Property(formType = PropertyFormType.BOOLEAN, bundleKey = "MyPreference.SendReports")
    boolean sendReports;

    @Property(formType = PropertyFormType.COLOR, bundleKey = "MyPreference.BackgroundColor")
    String backgroundColor;

    @Property(formType = PropertyFormType.NATURAL_NUMBER, bundleKey = "MyPreference.Age")
    int age;

    @Property(formType = PropertyFormType.SECRET_TEXT, bundleKey = "MyPreference.Password")
    String password;

    @Property(bundleKey = "MyPreference.MyInnerPreference")
    MyInnerPreference myInnerPreference;

    @Property(inherited = true, bundleKey = "MyPreference.MyInheritedPreference")
    MyInheritedPreference myInheritedPreference;

    @Override
    public MyPreference defaultValue( final MyPreference defaultValue ) {
        defaultValue.text = "text";
        defaultValue.sendReports = true;
        defaultValue.backgroundColor = "ABCDEF";
        defaultValue.age = 27;
        defaultValue.password = "password";
        defaultValue.myInnerPreference.text = "text";
        defaultValue.myInheritedPreference.text = "text";
        defaultValue.myInheritedPreference.myInnerPreference2.text = "text";
        defaultValue.myInheritedPreference.myInnerPreference2.myInheritedPreference2.text = "text";

        return defaultValue;
    }
}
```

To read and modify this preference, you just have to inject it and use it as it follows:

### Server-side

```
public class MyServerBean {

    @Inject
    private MyPreference myPreference;

    public void load() {
        // Loads the preference content from the file system
        myPreference.load();

        myPreference.text = "text";
        myPreference.sendReports = true;
        myPreference.backgroundColor = "ABCDEF";
        myPreference.age = 27;
        myPreference.password = "password";
        myPreference.myInnerPreference.text = "text";
        myPreference.myInheritedPreference.text = "text";
        myPreference.myInheritedPreference.myInnerPreference2.text = "text";
        myPreference.myInheritedPreference.myInnerPreference2.myInheritedPreference2.text = "text";

        // Saves the modified preference content.
        myPreference.save();
    }
}
```

### Client-side

```
public class MyClientBean {

    @Inject
    private MyPreference myPreference;

    myPreference.load( myLoadedPreference -> {
        myLoadedPreference.text = "text";
        myLoadedPreference.sendReports = true;
        myLoadedPreference.backgroundColor = "ABCDEF";
        myLoadedPreference.age = 27;
        myLoadedPreference.password = "password";
        myLoadedPreference.myInnerPreference.text = "text";
        myLoadedPreference.myInheritedPreference.text = "text";
        myLoadedPreference.myInheritedPreference.myInnerPreference2.text = "text";
        myLoadedPreference.myInheritedPreference.myInnerPreference2.myInheritedPreference2.text = "text";

        // Saves the modified preference content.
        myLoadedPreference.save();
    }, exception -> {
        throw new RuntimeException( exception );
    } );
}
```
