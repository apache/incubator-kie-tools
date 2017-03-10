#Improving your first App
In this session, we will create some basic Uberfire components aiming to give you an idea of how Uberfire works. For now, don’t pay too much attention to new terms and concepts presented here, it’s time to just have fun.
The Uberfire Architeture and details of how everything glues together will be presented in the [Tutorial](../tutorial/tutorial.md) section.

## Feeling Uberfire
Let’s change our App so we can get a better feel for how Uberfire workbench perspectives and panels fit together.

We’ll create two screens backed by a simple model class to demonstrate how you’d typically separate model from view in an UberFire application and how screens communicate in a decoupled way.

### Creating our model
The data model in an UberFire app is typically represented by Plain Old Java Objects, (POJOs). This leaves you the flexibility to use them in other frameworks that like POJOs such as JPA, JAXB, Errai Data Binding, and much more by adorning them with annotations. For now, our extremely simple data model will just be an unadorned POJO.


The model class will be called Mood, and it will represent how the current user is feeling at the moment. Place it on org.uberfire.shared package of your web app.
```
package org.uberfire.shared;

public class Mood {

    private final String text;

    public Mood( String text ) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return text;
    }
}
```
### Creating MoodScreen, a Templated Widget

For MoodScreen, let’s use the Errai UI Template system. This approach is similar to GWT UiBinder, but it lets you create the template in a plain HTML 5 file rather than a specialized UiBinder XML file.

Create a HTML file named MoodScreen.html inside Java package org.uberfire.client.screens with this content:


```
<form data-field="moodForm">
    <div class="input-group">
        <input data-field="moodTextBox" type="text" placeholder="How do you feel?">
    </div>
</form>
```
Create a Java class "MoodScreen.java" in the package org.uberfire.client.screens. This file will be used as a client-side template for the new MoodScreen widget. Here’s what that looks like:
```
package org.uberfire.client.screens;

import org.jboss.errai.common.client.dom.Form;
import org.jboss.errai.common.client.dom.Input;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.shared.Mood;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

@Dependent
@Templated
@WorkbenchScreen( identifier = "MoodScreen" )
public class MoodScreen implements IsElement {

    @Inject
    @DataField
    Form moodForm;

    @Inject
    @DataField
    Input moodTextBox;

    @Inject
    Event<Mood> moodEvent;

    @WorkbenchPartTitle
    public String getScreenTitle() {
        return "Change Mood";
    }


    @PostConstruct
    public void init() {
        moodForm.setOnsubmit( e -> {
            e.preventDefault();
            moodEvent.fire( new Mood( moodTextBox.getValue() ) );
            moodTextBox.setValue( "" );
        } );
    }

    @WorkbenchPartView
    public IsElement getView() {
        return this;
    }

}
```
MoodScreen is very similar to HelloWorldScreen. The structurals differences are related to our choice to use just an Errai UI Template instead of a full MVP (Model View Presenter) structure. See more about Errai UI templates in [this guide](https://docs.jboss.org/author/display/ERRAI/Errai+UI).

### Creating MoodListenerScreen
Create a HTML file named MoodListenerScreen.html inside Java package org.uberfire.client.screens with this content:
```
<div data-field="view">
    <input data-field="moodTextBox" type="text"
           placeholder="I understand that you are feeling...">
</div>
```
And create MoodListenerScreen.java, inside org.uberfire.cliente.screens:
```
package org.uberfire.client.screens;

import org.jboss.errai.common.client.dom.Input;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
@Templated
@WorkbenchScreen( identifier = "MoodListenerScreen" )
public class MoodListenerScreen implements IsElement {

    @Inject
    @DataField
    Input moodTextBox;

    @WorkbenchPartTitle
    public String getScreenTitle() {
        return "MoodListenerScreen";
    }

    @WorkbenchPartView
    public IsElement getView() {
        return this;
    }

}
```

### Giving MoodScreen, a perspective
Let's create our first perspective, using Uberfire Templated Perspectives.

First, we need to create the perspective Errai UI template, named "MoodPerspective.html" on org.uberfire.client.perspectives package:
```
<div class="fluid-container uf-perspective-container">
    <div class="fluid-row uf-perspective-row-12">
        <div class="col-md-6 uf-perspective-col">
            <span><b>Our MoodScreen</b></span>
            <div data-field="moodScreen"></div>
        </div>
        <div class="col-md-6 uf-perspective-col">
            <span><b>Mood Listener</b></span>
            <div data-field="moodListener"></div>
        </div>
    </div>
</div>
```

Now, let's create the Perspective class MoodPerspective on org.uberfire.client.perspectives package:

```
package org.uberfire.client.perspectives;

import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.annotations.WorkbenchPanel;
import org.uberfire.client.annotations.WorkbenchPerspective;

@Templated
@WorkbenchPerspective(identifier = "MoodPerspective")
public class MoodPerspective implements IsElement {

    @Inject
    @DataField
    @WorkbenchPanel(parts = "MoodScreen")
    Div moodScreen;

    @Inject
    @DataField
    @WorkbenchPanel(parts = "MoodListenerScreen")
    Div moodListener;
}
```
### Adding MoodPerspective
Moving on, let’s add MoodPerspective to the menu bar of our app.
We need to update org.uberfire.client.ShowcaseEntryPoint and replace setupMenu method to that:
```
    private void setupMenu( @Observes final ApplicationReadyEvent event ) {
        final Menus menus =
                newTopLevelMenu( "Home" )
                        .respondsWith(
                                () -> placeManager.goTo( "MainPerspective" ) )
                        .endMenu()
                        .newTopLevelMenu( "Mood Perspective" )
                        .respondsWith(
                                () -> placeManager.goTo( "MoodPerspective" ) )
                        .endMenu()
                        .build();

        menubar.addMenus( menus );
    }
 ```
### Check your work
It's time to check your classes and package created. See an example here:

![Project Structure](5minStructure.png)
### See it work!!!
How about seeing our changes?
```
cd demo-showcase/demo-webapp
mvn clean install
mvn clean gwt:run
```
Click on MoodPerspective menu:
![hello world](moodPerspective.png)

### Let's make the screens communicate
Did you notice the CDI event raised by MoodScreen? If no, take a look at moodForm.setOnsubmit(..) call at init() method.

Now let’s do something in response to the event we fire in MoodListenerScreen when the user presses Enter. To do this we’ll add a CDI observer method at MoodListenerScreen:
```
public void onMoodChange( @Observes Mood mood ) {
        moodTextBox.setValue( "You are feeling " + mood.getText() );
}
```
Build and run your App again (mvn gwt: clean gwt:compile gwt:run), write a text on "How do  you fell" textbox and press enter to see screens communicating:

![hello world](moodPerspective.png)


### A taste of Uberfire lifecycle events
Uberfire supports a lot of workbench events, let's see how they work.

Edit MoodPerspective.java and add these two methods, run the app again and change perspectives to see the events happening.

```
    @OnOpen
    public void onOpen() {
        Window.alert( "On Open" );
    }

    @OnClose
    public void OnClose() {
        Window.alert( "On Close" );
    }
```
Build (mvn clean install) and run your App again (mvn clean gwt:run) and change perspectives to see the events being triggered.
