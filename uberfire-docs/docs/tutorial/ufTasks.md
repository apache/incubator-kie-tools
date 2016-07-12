
#UF Tasks
In order to understand how Uberfire works, let's create a simple task manager that will look like this:

![UF tasks](ufTasksFinal.png)

On the left side is a "Projects" section, where the user can create Projects.

Each project has a group of folders (displayed in the main window), and each folder has a group of tasks.

A task can be created in the text field "New Task" and can be marked as *Done* with the checkbox left of the task description.

##Uberfire Design Guidelines

Before you can start writing code, it's important to familiarize yourself with the [MVP Pattern](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93presenter) to better understand this tutorial.


###MVP Pattern
MVP (Model-View-Presenter) like any pattern is open to different interpretation, so here is how MVP is used in this tutorial:

- Model is POJO;

- View is a passive interface that displays data, but this data cannot be the model. So it’s limited to primitive types and regular platform objects (e.g. String). Every user action should be routed to the presenter;
- Presenter is where all business logic should live; it acts upon the model and the view.

![MVP](mvp.png)

##Cleaning the Archetype
Uberfire Archetype contains some useful code samples that are not necessary in our app, so let's do some cleanup first.

Please delete these files: SharedSample.java, HelloWorldScreen.java, HelloWorldScreen.ui.xml and  MainPerspective.java.

##Creating project structure

Uberfire interfaces are made of some fundamental building blocks: Widgets, Layout Panels, Screens, Workbench Panels, Menu Bars, Tool Bars, and Perspectives. Layout Panels can contain Widgets and other Layout Panels; Perspectives contains Workbench Panels, an optional Menu Bar, and an optional Tool Bar.

Perspectives split up the screen into multiple resizeable regions, and end users can drag and drop Panels between these regions to customize their workspace.

For now, we will create two Uberfire Screens (Project and Tasks) and one perspective to hold these screens.

###Creating Projects Screen
Following the MVP pattern, each Uberfire Screen will be a Presenter plus a View. Our views will be built using [Errai UI](https://docs.jboss.org/author/display/ERRAI/Errai+UI). This means we will also have an .html file associated with each screen.

Inside the package org.uberfire.client.screens, create this new source file:

- ProjectsPresenter.java

```
package org.uberfire.client.screens;

import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElement;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
@WorkbenchScreen( identifier = "ProjectsPresenter" )
public class ProjectsPresenter {

    public interface View extends UberElement<ProjectsPresenter> {
    }

    @Inject
    private View view;

    @WorkbenchPartTitle
    public String getTitle() {
        return "Projects";
    }

    @WorkbenchPartView
    public UberElement<ProjectsPresenter> getView() {
        return view;
    }

}
```
The presenter itself is a CDI bean with one injected field (the view). But whether or not we’re familiar with CDI, we’re seeing a bunch of Uberfire annotations for the first time. Let’s examine some of them:

**@WorkbenchScreen**
Tells Uberfire that the class defines a Screen in the application. Each screen has an identifier.

**@WorkbenchPartTitle**
Denotes the method that returns the Screen’s title. Every Screen must have a @WorkbenchPartTitle method.

**@WorkbenchPartView**
Denotes the method that returns the Panel’s view. The view can be any class that extends GWT’s Widget class or implements GWT’s IsWidget interface. In this example, we’re returning a CDI bean that implements UberElement<ProjectsPresenter>, which is the specific view, for this presenter (following MVP pattern).

Every Screen must have a @WorkbenchPartView method returning a com.google.gwt.user.client.ui.IsWidget or **preferably** org.jboss.errai.common.client.api.IsElement, extend Widget, or implement IsWidget.

Let's define our view (inside org.uberfire.client.screens package):

- ProjectsView.java

```
package org.uberfire.client.screens;

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jboss.errai.ui.client.local.api.IsElement;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
@Templated
public class ProjectsView implements ProjectsPresenter.View, IsElement {

    private ProjectsPresenter presenter;

    @Override
    public void init( ProjectsPresenter presenter ) {
        this.presenter = presenter;
    }

}
```
- ProjectsView.html

```
<div>
    <label>Project View</label>
</div>
```
For now, this view only has a label with the text "Project View". "Div view" is the property that will be referenced by the presenter as the root element of this template.

###Creating Tasks Screen
Our second screen is the Task Screen. Let's create it (inside org.uberfire.client.screens package):

- TasksPresenter.java

```
package org.uberfire.client.screens;

import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElement;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
@WorkbenchScreen( identifier = "TasksPresenter" )
public class TasksPresenter {

    public interface View extends UberElement<TasksPresenter> {
    }

    @Inject
    private View view;

    @WorkbenchPartTitle
    public String getTitle() {
        return "Tasks";
    }

    @WorkbenchPartView
    public UberElement<TasksPresenter> getView() {
        return view;
    }
}
```
- TasksView.java

```
package org.uberfire.client.screens;

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jboss.errai.ui.client.local.api.IsElement;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
@Templated
public class TasksView implements TasksPresenter.View, IsElement {

    private TasksPresenter presenter;

    @Override
    public void init( TasksPresenter presenter ) {
        this.presenter = presenter;
    }

}
```
- TasksView.html

```
<div>
    <label>Tasks View</label>
</div>
```

###Creating Tasks Perspective
Now we have two Uberfire Screens, but nowhere to put them. Remember, the Uberfire Workbench UI is arranged as Workbench → Perspective → Workbench Panel → Screen. Perspectives dictate the position and size of Workbench Panels. Besides the explicit positioning approach, we can also define perspectives using Errai UI templates.

We need to define a Perspective inside org.uberfire.client.perspectives:

- TasksPerspective.java

```
package org.uberfire.client.perspectives;

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.SimpleWorkbenchPanelPresenter;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

@ApplicationScoped
@WorkbenchPerspective(identifier = "TasksPerspective", isDefault = true)
public class TasksPerspective {

    @Perspective
    public PerspectiveDefinition buildPerspective() {
        final PerspectiveDefinitionImpl perspective =
                new PerspectiveDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );
        perspective.setName( "TasksPerspective" );

        final PanelDefinition west = new PanelDefinitionImpl( SimpleWorkbenchPanelPresenter.class.getName() );
        west.addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "ProjectsPresenter" ) ) );
        west.setWidth( 350 );
        perspective.getRoot().insertChild( CompassPosition.WEST, west );

        perspective.getRoot().addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "TasksPresenter" ) ) );

        return perspective;
    }
}
```
Once again, we’re encountering some new annotations:

**@WorkbenchPerspective**
Tells UberFire that the class defines a perspective.

**@Perspective**
Tells Uberfire that this method returns the PerspectiveDefinition that governs the perspective’s layout and default contents. Every @WorkbenchPerspective class needs a method annotated with @Perspective.

In this definition, we’ll add a new panel on the left-hand side (WEST) and populate it with ProjectsPresenter by default. The perspective root panel (main window) will be populated with TasksPresenter.


###Modifying application entry point



Inside the package org.uberfire.client, modify the setup method as follows to create a menu item in the "TaskPerspective" we just defined before (instead of the "MainPerspective"):

- ShowcaseEntryPoint.java


```
private void setupMenu( @Observes final ApplicationReadyEvent event ) {
    final Menus menus =
            newTopLevelMenu( "Home" )
                    .respondsWith( new Command() {
                        @Override
                        public void execute() {
                            placeManager.goTo( new DefaultPlaceRequest( "TasksPerspective" ) );
                        }
                    } )
                    .endMenu()
                    .build();

    menubar.addMenus( menus );
}
```




##Time to see it work!
We’ve come a long way since we started with that empty directory. Let’s reward all the hard work by starting our app and seeing it do something!

If you are using a IDE, stop the server, build and restart or if you are using command line interface:

```
$ mvn clean install
$ mvn clean gwt:run
```
Eventually, the GWT Development Mode GUI will pop up. Wait for the "Calculating…" button to change to "Launch in Default Browser," then press that button.

That should be the result of our work:

![App Structure IDE](appStructureIDE.png)

And our app running:

![App Structure](appStructure.png)


##Projects Screen
Now that we have the basic infrastructure required for our project, it's time to put some functionality in the Project Screen. Let's begin with the ProjectsPresenter.

###ProjectsPresenter
Here’s what ProjectsPresenter.java might look like:
```
package org.uberfire.client.screens;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;

@ApplicationScoped
@WorkbenchScreen(identifier = "ProjectsPresenter")
public class ProjectsPresenter {

    public interface View extends UberView<ProjectsPresenter> {

        void clearProjects();

        void addProject( String projectName,
                         boolean selected );
    }

    @Inject
    private View view;

    @WorkbenchPartTitle
    public String getTitle() {
        return "Projects";
    }

    @WorkbenchPartView
    public UberView<ProjectsPresenter> getView() {
        return view;
    }

    public void newProject() {
        // TODO
    }

    public void selectProject( String projectName ) {
        // TODO
    }
}
```

###ProjectsView
Our view has two components: a [Bootstrap](http://getbootstrap.com/components/#list-group) List Group to list our projects and a button to create new ones.

Here’s what ProjectsView.html looks like:
```
<div>
    <ul class="list-group" data-field="projects-list">
    </ul>
    <button type="button" class="btn btn-primary test" data-field="new-project">
        <i class="fa fa-plus"></i> New Project
    </button>
</div>
```
And the owner class for the above template might look like this:
```
package org.uberfire.client.screens;

import com.google.gwt.user.client.Event;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.UnorderedList;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.SinkNative;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import static org.jboss.errai.common.client.dom.DOMUtil.removeAllChildren;

@Dependent
@Templated
public class ProjectsView implements ProjectsPresenter.View {

    @Inject
    @DataField( "projects-view" )
    Div view;

    @Inject
    @DataField( "new-project" )
    Button newProject;

    @Inject
    @DataField( "projects-list" )
    UnorderedList projectsList;

    private ProjectsPresenter presenter;

    @Override
    public void init( ProjectsPresenter presenter ) {
        this.presenter = presenter;
    }

    @SinkNative( Event.ONCLICK )
    @EventHandler( "new-project" )
    public void newProject( final Event event ) {
        presenter.newProject();
    }

    @Override
    public void addProject( final String projectName,
                            final boolean active ) {
        //TODO
    }

    @Override
    public void clearProjects() {
        removeAllChildren( projectsList );
    }


    @Override
    public HTMLElement getElement() {
        return view;
    }

}
```
Three @DataField attributes to bind the template with the view Java class, a click handler for the "New Project" button, a method to add a project and a method to clear projects.
This method should receive as parameters the projectName and a boolean representing if the project is active on the screen.

##Time to see it work!
Let’s see the results of all our hard work: start the app and we should see the New Project button.

![New Project Clicked](newProjectClicked.png)

##New Project Screen
The next step of our project is to provide a real implementation for the New Project button. To achieve this, let's create these classes on org.uberfire.client.screens.popup package:

**NewProjectPresenter.java**
```
package org.uberfire.client.screens.popup;

import org.uberfire.client.screens.ProjectsPresenter;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class NewProjectPresenter {

    private ProjectsPresenter projectsPresenter;

    public interface View {

        void show();

        void hide();

        void init( NewProjectPresenter presenter );
    }

    @Inject
    private View view;

    @PostConstruct
    public void setup() {
        view.init( this );
    }

    public void show( ProjectsPresenter projectsPresenter ) {
        this.projectsPresenter = projectsPresenter;
        view.show();
    }

    public void newProject( String projectName ) {
        projectsPresenter.createNewProject( projectName );
        view.hide();
    }

    public void close() {
        view.hide();
    }
}
```
The method show(projectsPresenter) will open the modal dialog on the view. The method newProject(projectName) will create a new project on projectsPresenter and hide the view.

**NewProjectView.java**
```
package org.uberfire.client.screens.popup;

import com.google.gwt.core.client.GWT;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class NewProjectView implements NewProjectPresenter.View {

    private NewProjectPresenter presenter;

    private Modal modal;

    @Inject
    NewProjectContentView contentView;

    @Override
    public void init( NewProjectPresenter presenter ) {
        this.presenter = presenter;
        contentView.init( projectName -> presenter.newProject( projectName ),
                          () -> presenter.close() );
        createModal();
    }

    private void createModal() {
        this.modal = GWT.create( Modal.class );
        final ModalBody body = GWT.create( ModalBody.class );
        body.add( ElementWrapperWidget.getWidget( contentView.getElement() ) );
        modal.add( body );
    }

    @Override
    public void show() {
        modal.show();
    }

    @Override
    public void hide() {
        contentView.clearContent();
        modal.hide();
    }

}
```
The NewProjectView is a simple wrapper for an org.gwtbootstrap3.client.ui.Modal. The content of the modal will be implemented by NewProjectContentView. Let's create this class inside popup package:
```
package org.uberfire.client.screens.popup;

import com.google.gwt.user.client.Event;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Input;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.SinkNative;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
@Templated
public class NewProjectContentView implements IsElement {

    @Inject
    @DataField( "project-name" )
    Input projectNameTextBox;

    @Inject
    @DataField( "ok-button" )
    Button okButton;

    @Inject
    @DataField( "cancel-button" )
    Button cancelButton;

    private ParameterizedCommand<String> addProject;
    private Command cancel;

    public void init( ParameterizedCommand<String> addProject, Command cancel ) {
        this.addProject = addProject;
        this.cancel = cancel;
    }

    public void clearContent() {
        projectNameTextBox.setValue( "" );
    }

    @SinkNative( Event.ONCLICK )
    @EventHandler( "ok-button" )
    public void addProject( Event event ) {
        addProject.execute( projectNameTextBox.getValue() );
    }

    @SinkNative( Event.ONCLICK )
    @EventHandler( "cancel-button" )
    public void cancel( Event event ) {
        cancel.execute();
    }

}
```
And also, create NewProjectContentView.html:
```
<div>
    <form data-field="new-project-modal">
        <fieldset>
            <legend>New Project</legend>
            <div class="form-group">
                <label class="control-label col-md-3">Name</label>
                <div class="col-md-9">
                    <input type="text" class="form-control" data-field="project-name"
                           placeholder="Project name">
                    <span data-field="project-name-help" class="help-block"></span>
                </div>
            </div>
        </fieldset>
    </form>
    <div class="modal-footer">
        <button type="button" class="btn btn-danger" data-field="cancel-button">Cancel</button>
        <button type="button" class="btn btn-primary" data-field="ok-button">OK</button>
    </div>
</div>
```
It is important to take a look that NewProjectView interacts with NewProjectContentView through two important Uberfire classes: [ParameterizedCommand<T>](https://github.com/uberfire/uberfire/blob/master/uberfire-api/src/main/java/org/uberfire/mvp/ParameterizedCommand.java) and [Command](https://github.com/uberfire/uberfire/blob/master/uberfire-api/src/main/java/org/uberfire/mvp/Command.java).
```
public void init( ParameterizedCommand<String> addProject, Command cancel ){...}
```
This commands will delegate the **OK** and **Cancel** actions for NewProjectPresenter.

We also have to change **ProjectsPresenter.java** in order to open the popup and receive the name of the new project created. Add this snippet to our class:
```
import com.google.gwt.user.client.Window;
import org.uberfire.client.screens.popup.NewProjectPresenter;

[...]

    @Inject
    private NewProjectPresenter newProjectPresenter;

    public void newProject() {
        newProjectPresenter.show( this );
    }

    public void createNewProject( String projectName ) {
        Window.alert( "project created: " + projectName );
    }
```

##Time to see it work!
Let’s see the results of all our hard work: start the app and watch it open the popup dialog displaying the project created message!

Refresh your browser and click on the New Project button.

![new project](newProject.png)

##Adding a project to the projects list
Our next task is to add a project to the projects list. First of all, let's create the Project model class. In package org.uberfire.shared.model, create this class:

**Project.java**
```
package org.uberfire.shared.model;

public class Project {

    private final String name;
    private boolean selected;

    public Project( String name ) {
        this.name = name;
        this.selected = false;
    }

    public String getName() {
        return name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected( boolean selected ) {
        this.selected = selected;
    }
}
```
Next, when the method createNewProject(project) is called, let's create a new model for that project and update the view. Add the following code to ProjectsPresenter.java:

**ProjectsPresenter.java**
```
import org.uberfire.shared.model.Project;
import java.util.ArrayList;
import java.util.List;

[...]

    private List<Project> projects = new ArrayList<Project>();

    public void createNewProject( String projectName ) {
        projects.add( new Project( projectName ) );
        updateView();
    }

    private void updateView() {
        view.clearProjects();
        for ( Project project : projects ) {
            view.addProject( project.getName(), project.isSelected() );
        }
    }
```
Now it's time to place this new projects in our view. In order to do that let's create an Errai UI template in package widgets called ProjectItem.java:

```
package org.uberfire.client.screens.widgets;

import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.ListItem;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.mvp.Command;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
@Templated
public class ProjectItem implements IsElement {

    @Inject
    @DataField
    ListItem project;

    public void init( String projectName, boolean active, Command onClick ) {
        if ( active ) {
            project.setClassName( "list-group-item active" );
        }
        project.setTextContent( projectName );
        project.setOnclick( e -> onClick.execute() );
        project.setTextContent( projectName );
    }

}
```
And its html template, called ProjectItem.html
```
<li data-field="project" class="list-group-item">

</li>
```
Let's create this widget in ProjectsView. Add the following code to ProjectsView.java:
```
@Inject
ManagedInstance<ProjectItem> projects;

@Override
public void addProject( final String projectName,
                        final boolean active ) {

    ProjectItem projectItem = projects.get();
    projectItem.init( projectName,
                      active,
                      () -> presenter.selectProject( projectName ) );
    projectsList.appendChild( projectItem.getElement() );
}
```
ManagedInstance is an Errai class that allows the application to dynamically obtain instances of beans (our widgets). ManagedInstances have their life-cycle automatically controlled by Errai. So no need to worry to destroy your CDI instances. ;)

We create ProjectItem instances via projects.get(), and provide a name, a boolean telling if the project is active and an onClick command, that will be used in the future in order to select projects.

##Time to see it work!

Refresh the browser, and add two projects to our list.

![two projects](twoProjects.png)


##Creating tasks list
It's time to create our task list. When our user creates or selects a project in the projects list, we should display the folders and tasks associated with that project.

In order to do that, let's create inside org.uberfire.shared.events a model class called ProjectSelectedEvent, to allow communication between our screens:

**ProjectSelectedEvent.java**
```
package org.uberfire.shared.events;

public class ProjectSelectedEvent {

    private final String name;

    public ProjectSelectedEvent( String name ) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
```

And also add this code on **ProjectsPresenter.java**:
```
import org.uberfire.shared.events.ProjectSelectedEvent;
import javax.enterprise.event.Event;

[...]

    @Inject
    private Event<ProjectSelectedEvent> projectSelectedEvent;

    public void selectProject( String projectName ) {
        setActiveProject( projectName );
        projectSelectedEvent.fire( new ProjectSelectedEvent( projectName ) );
    }

    private void setActiveProject( String projectName ) {
        for ( Project project : projects ) {
            if ( projectName.equalsIgnoreCase( project.getName() ) ) {
                project.setSelected( true );
            } else {
                project.setSelected( false );
            }
        }
        updateView();
    }
```
There are two important calls that happen inside the selectProject(projectName) method:

- setActiveProject(projectName): marks a specific project as active and updates the view;
- projectSelectEvent.fire(...): fires a CDI event telling TasksPresenter.java that a project was selected

Now, let's listen to this CDI event on **TaskPresenter.java**. Add the following method:
```
import com.google.gwt.user.client.Window;
import org.uberfire.shared.events.ProjectSelectedEvent;
import javax.enterprise.event.Observes;

[...]

    public void projectSelected( @Observes ProjectSelectedEvent projectSelectedEvent ) {
        Window.alert( projectSelectedEvent.getName() );
    }
```

##Time to see it work!

Refresh the browser, create two projects and click on one of them.

![project selected](projectSelected.png)


##New folder
Our next step is to create a new folder button. A folder is an aggregator of tasks. This button is only displayed after a project is selected. Let's edit the view:

**TasksView.html**
```
<div>
    <div class="list-group" data-field="tasks"></div>
    <button type="button" class="btn btn-primary" data-field="new-folder">
        <i class="fa fa-plus"></i> New Folder
    </button>
</div>
```
Errai UI templates, can have their own CSS definitions. Let's create one for TasksView.html? Create TaskView.css file inside screens package:
```
[data-field='projects-view'] [data-field='new-project'] {
  float: right;
  margin-right: 5px
}
```
Update your **TaskPresenter.java** with the following code:
```
package org.uberfire.client.screens;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.screens.popup.NewFolderPresenter;
import org.uberfire.shared.events.ProjectSelectedEvent;

@ApplicationScoped
@WorkbenchScreen(identifier = "TasksPresenter")
public class TasksPresenter {

    public interface View extends UberView<TasksPresenter> {

        void activateNewFolder();

        void clearTasks();

        void newFolder( String name,
                        Integer size,
                        List<String> tasks );
    }

    @Inject
    private View view;

    @Inject
    private NewFolderPresenter newFolderPresenter;

    private String currentSelectedProject;

    @WorkbenchPartTitle
    public String getTitle() {
        return "Tasks";
    }

    @WorkbenchPartView
    public UberView<TasksPresenter> getView() {
        return view;
    }

    public void projectSelected( @Observes ProjectSelectedEvent projectSelectedEvent ) {
        this.currentSelectedProject = projectSelectedEvent.getName();
        selectFolder();
    }

    private void selectFolder() {
        view.activateNewFolder();
        updateView( null );
    }

    public void showNewFolder() {
        newFolderPresenter.show( this );
    }

    private void updateView( String folderName ) {
        view.clearTasks();
        if ( folderName != null ) {
            view.newFolder( folderName, 0, new ArrayList<String>() );
        }
    }

    public void newFolder( String folderName ) {
        updateView( folderName );
    }
}
```
We're going to use a popup structure, like the **NewProject** structure. Let's create it inside package org.uberfire.client.screens.popup:

**NewFolderPresenter.java**
```
package org.uberfire.client.screens.popup;

import org.uberfire.client.screens.TasksPresenter;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class NewFolderPresenter {

    public interface View {

        void show();

        void hide();

        void init( NewFolderPresenter presenter );
    }

    @Inject
    private View view;

    private TasksPresenter tasksPresenter;

    @PostConstruct
    public void setup() {
        view.init( this );
    }

    public void show( TasksPresenter tasksPresenter ) {
        this.tasksPresenter = tasksPresenter;
        view.show();
    }

    public void newFolder( String folderName ) {
        tasksPresenter.newFolder( folderName );
        view.hide();
    }

    public void close() {
        view.hide();
    }
}

```
**NewFolderView.java**
```
package org.uberfire.client.screens.popup;

import com.google.gwt.core.client.GWT;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import javax.inject.Inject;

public class NewFolderView implements NewFolderPresenter.View {

    private NewFolderPresenter presenter;

    private Modal modal;

    @Inject
    NewFolderContentView contentView;


    @Override
    public void init( NewFolderPresenter presenter ) {
        this.presenter = presenter;
        contentView.init( folderName -> presenter.newFolder( folderName ),
                          () -> presenter.close() );
        createModal();
    }

    private void createModal() {
        this.modal = GWT.create( Modal.class );
        final ModalBody body = GWT.create( ModalBody.class );
        body.add( ElementWrapperWidget.getWidget( contentView.getElement() ) );
        modal.add( body );
    }

    @Override
    public void show() {
        modal.show();
    }

    @Override
    public void hide() {
        modal.hide();
        contentView.clearContent();
    }

}
```
**NewFolderContentView.java**
```

package org.uberfire.client.screens.popup;

import com.google.gwt.user.client.Event;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Input;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.SinkNative;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
@Templated
public class NewFolderContentView implements IsElement {

    @Inject
    @DataField( "folder-name" )
    Input folderNameTextBox;

    @Inject
    @DataField( "ok-button" )
    Button okButton;

    @Inject
    @DataField( "cancel-button" )
    Button cancelButton;

    private ParameterizedCommand<String> addFolder;
    private Command cancel;

    public void init( ParameterizedCommand<String> addFolder, Command cancel ) {
        this.addFolder = addFolder;
        this.cancel = cancel;
    }


    public void clearContent() {
        folderNameTextBox.setValue( "" );
    }

    @SinkNative( Event.ONCLICK )
    @EventHandler( "ok-button" )
    public void addFolder( Event event ) {
        addFolder.execute( folderNameTextBox.getValue() );
    }

    @EventHandler( "cancel-button" )
    public void cancel( Event event ) {
        cancel.execute();
    }

}

```
NewFolderContentView.html
```
<div>
    <form data-field="new-folder-modal">
        <fieldset>
            <legend>New Folder</legend>
            <div class="form-group">
                <label class="control-label col-md-3">Name</label>
                <div class="col-md-9">
                    <input type="text" class="form-control" data-field="folder-name"
                           placeholder="Folder name">
                    <span data-field="project-name-help" class="help-block"></span>
                </div>
            </div>
        </fieldset>
    </form>
    <div class="modal-footer">
        <button type="button" class="btn btn-danger" data-field="cancel-button">Cancel</button>
        <button type="button" class="btn btn-primary" data-field="ok-button">OK</button>
    </div>
</div>
```
####Time to add some tasks
It's time to add some tasks to our project. At this point, you're probably very comfortable with basic Uberfire concepts, so we'll just quickly run through the necessary changes to implement the tasks support in our project.
Let's begin by creating a model class (package org.uberfire.shared.model):

**Folder.java**

```
package org.uberfire.shared.model;

import java.util.ArrayList;
import java.util.List;

public class Folder {

    private final String name;

    private List<String> tasks = new ArrayList<String>();

    public Folder( String name ) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<String> getTasks() {
        return tasks;
    }

    public void addTask( String task ) {
        tasks.add( task );
    }

    public void removeTask( String taskText ) {
        tasks.remove( taskText );
    }
}
```
Now update the **TasksPresenter.java** in order to support task creation.
```
package org.uberfire.client.screens;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.screens.popup.NewFolderPresenter;
import org.uberfire.shared.events.ProjectSelectedEvent;
import org.uberfire.shared.model.Folder;

@ApplicationScoped
@WorkbenchScreen(identifier = "TasksPresenter")
public class TasksPresenter {

    public interface View extends UberView<TasksPresenter> {

        void activateNewFolder();

        void clearTasks();

        void newFolder( String name,
                        Integer size,
                        List<String> strings );
    }

    @Inject
    private View view;

    @Inject
    private NewFolderPresenter newFolderPresenter;

    private String currentSelectedProject;

    private Map<String, List<Folder>> foldersPerProject = new HashMap<String, List<Folder>>();

    @WorkbenchPartTitle
    public String getTitle() {
        return "Tasks";
    }

    @WorkbenchPartView
    public UberView<TasksPresenter> getView() {
        return view;
    }

    public void projectSelected( @Observes ProjectSelectedEvent projectSelectedEvent ) {
        this.currentSelectedProject = projectSelectedEvent.getName();
        selectFolder();
    }

    private void selectFolder() {
        view.activateNewFolder();
        updateView();
    }

    public void showNewFolder() {
        newFolderPresenter.show( this );
    }

    public void createTask( String folderName,
                            String task ) {

        Folder folder = getFolder( folderName );
        if ( folder != null ) {
            folder.addTask( task );
        }
        updateView();
    }

    private Folder getFolder( String folderName ) {
        for ( final Folder folder : getFolders() ) {
            if ( folder.getName().equalsIgnoreCase( folderName ) ) {
                return folder;
            }
        }
        return null;
    }

    public void doneTask( String folderName,
                          String taskText ) {
        Folder folder = getFolder( folderName );
        if ( folder != null ) {
            folder.removeTask( taskText );
        }
        updateView();
    }

    private List<Folder> getFolders() {
        List<Folder> folders = foldersPerProject.get( currentSelectedProject );
        if ( folders == null ) {
            folders = new ArrayList<Folder>();
        }
        return folders;
    }

    private void updateView() {
        view.clearTasks();
        for ( final Folder folder : getFolders() ) {
            view.newFolder( folder.getName(), folder.getTasks().size(), folder.getTasks() );
        }
    }

    public void newFolder( String folderName ) {
        List<Folder> folders = getFolders();
        folders.add( new Folder( folderName ) );
        foldersPerProject.put( currentSelectedProject, folders );
        updateView();
    }
}
```
Let's highlight some important pieces of code

- Map< String, List < Folder > > foldersPerProject: keeps an in memory map of folders for each project selected;
- createTask(folderName,task): add a task for a specific folder. This is triggered by addTasks text box;
- doneTask(folderName,taskText): after a task is marked as done, remove it from folder;
- newFolder(folderName): create a new folder and add it in persistence structure (folderPerProject).


Let's update the **TasksView.java** to support task creation.
```
package org.uberfire.client.screens;

import com.google.gwt.event.dom.client.ClickEvent;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Document;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.screens.widgets.FolderItem;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.List;

import static org.jboss.errai.common.client.dom.DOMUtil.removeAllChildren;

@Dependent
@Templated
public class TasksView implements TasksPresenter.View, IsElement {

    private TasksPresenter presenter;

    @Inject
    Document document;

    @Inject
    @DataField( "view" )
    Div view;

    @Inject
    @DataField( "new-folder" )
    Button newFolder;

    @Inject
    @DataField( "tasks" )
    Div tasks;

    @Inject
    ManagedInstance<FolderItem> folders;

    @Override
    public void init( final TasksPresenter presenter ) {
        this.presenter = presenter;
        this.newFolder.setDisabled( true );
    }

    @Override
    public void activateNewFolder() {
        newFolder.setDisabled( false );
    }

    @Override
    public void clearTasks() {
        removeAllChildren( tasks );
    }

    @Override
    public void newFolder( String folderName, Integer numberOfTasks, List<String> tasksList ) {
        FolderItem folderItem = createFolder( folderName, String.valueOf( numberOfTasks ) );
        createTasks( folderName, tasksList, folderItem );

        tasks.appendChild( folderItem.getElement() );
    }

    private void createTasks( String folderName, List<String> tasksList, FolderItem folderItem ) {
        for ( String task : tasksList ) {
            folderItem.createTask( task,
                                   () -> presenter.doneTask( folderName, task ) );
        }
    }

    private FolderItem createFolder( String folderName, String numberOfTasks ) {
        FolderItem folderItem = folders.get();
        folderItem.init( folderName,
                         numberOfTasks,
                         newTaskText -> presenter.createTask( folderName, newTaskText ) );
        return folderItem;
    }


    @EventHandler( "new-folder" )
    public void newFolderClick( ClickEvent event ) {
        presenter.showNewFolder();
    }
}
```
Pay attention to the method newFolder(folderName, numberOfTasks, tasksList), because here is where we create folder items and their tasks.

We also have to create at widgets package a FolderItem template in order to display our folders:
***FolderItem.java***
```
package org.uberfire.client.screens.widgets;

import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.common.client.dom.*;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
@Templated
public class FolderItem implements IsElement {

    @Inject
    @DataField
    UnorderedList folder;

    @Inject
    @DataField
    Span folderName;

    @Inject
    @DataField
    Span numberOfTasks;

    @Inject
    @DataField
    Div tasksList;

    @Inject
    @DataField
    Form newTaskForm;

    @Inject
    @DataField
    Input taskName;

    @Inject
    ManagedInstance<TaskItem> tasks;

    public void init( String folderName, String numberOfTasks, ParameterizedCommand<String> newTask ) {
        this.folderName.setTextContent( folderName );
        this.numberOfTasks.setTextContent( numberOfTasks );
        newTaskForm.setOnsubmit( e -> {
            e.preventDefault();
            newTask.execute( taskName.getValue() );
        } );
    }

    public void createTask( String taskTitle, Command doneCommand ) {
        TaskItem taskItem = tasks.get();
        taskItem.init( taskTitle, doneCommand );
        tasksList.appendChild( taskItem.getElement() );
    }
}
```

***FolderItem.html***
```
<ul data-field="folder" class="list-group">
    <li class="list-group-item list-group-item-info">
        <span data-field="folderName"></span>
        <span data-field="numberOfTasks" class="badge"></span>
    </li>
    <div data-field="tasksList">

    </div>
    <li class="list-group-item">
        <form data-field="newTaskForm">
            <div class="input-group">
                <input data-field="taskName" type="text" class="form-control" placeholder="New task...">
            </div>
        </form>
    </li>
</ul>
```
A Folder Item, has Tasks Items:


*TaskItem.java
```
package org.uberfire.client.screens.widgets;

import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Input;
import org.jboss.errai.common.client.dom.ListItem;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.mvp.Command;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
@Templated
public class TaskItem implements IsElement {

    @Inject
    @DataField
    ListItem task;

    @Inject
    @DataField
    Input done;

    @Inject
    @DataField
    Span taskName;

    public void init( String taskTitle, Command doneCommand ) {
        taskName.setTextContent( taskTitle );
        done.setOnclick( e -> doneCommand.execute() );
    }

}
```

***TaskItem.html***

```
<li data-field="task" class="list-group-item">
    <label class="checkbox-inline">
        <input data-field="done" type="checkbox">
        <span data-field="taskName"></span>
    </label>
</li>

```

##Time to see it work!

Stop the server, run the app, login, create two projects and click in one of them. Create a new folder and add some tasks to it.

![uftasks final](ufTasksFinal.png)

##Let's improve your app

In order to get in touch with some important Uberfire concepts like adding more Perspectives, using PlaceManagers, etc, let's improve your tasks app and build a basic dashboard on it.

This dashboard will count the number of tasks created and done by project and will look like this:

![dashboard](dashboardFinal.png)

Because we'll be writing new perspectives and screens, Uberfire needs to generate some new code; stop the server and let's get back to work.

###Dashboard Perspective
Create this class on package org.uberfire.client.perspectives:

```
package org.uberfire.client.perspectives;

import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@WorkbenchPerspective(identifier = "DashboardPerspective")
public class DashboardPerspective {

    @Perspective
    public PerspectiveDefinition buildPerspective() {
        final PerspectiveDefinitionImpl perspective =
                new PerspectiveDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );
        perspective.setName( "DashboardPerspective" );

        perspective.getRoot().addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "DashboardPresenter" ) ));

        return perspective;
    }
}
```
This class has one presenter (DashboardPresenter) and note that it is a little bit different from TaskPerspective, because it is not a default perspective. You can have only one default perspective that will be automatically loaded on startup.

###DashboardPresenter
On org.uberfire.client.screens package:

```
package org.uberfire.client.screens;

import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.client.mvp.UberView;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.shared.events.TaskCreated;
import org.uberfire.shared.events.TaskDone;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
@WorkbenchScreen(identifier = "DashboardPresenter")
public class DashboardPresenter {

    public interface View extends UberElement<DashboardPresenter> {

        void addProject( String project,
                         String tasksCreated,
                         String tasksDone );

        void clear();
    }

    @Inject
    private View view;

    private Map<String, ProjectTasksCounter> projectTasksCounter = new HashMap<String, ProjectTasksCounter>();

    @WorkbenchPartTitle
    public String getTitle() {
        return "Dashboard";
    }

    @WorkbenchPartView
    public UberElement<DashboardPresenter> getView() {
        return view;
    }

    @OnOpen
    public void onOpen() {
        updateView();
    }

    private void updateView() {
        view.clear();
        for ( String project : projectTasksCounter.keySet() ) {
            ProjectTasksCounter projectTasksCounter = this.projectTasksCounter.get( project );
            view.addProject( project, projectTasksCounter.getTasksCreated(), projectTasksCounter.getTasksDone() );
        }
    }

    public void taskCreated( @Observes TaskCreated taskCreated ) {
        ProjectTasksCounter projectTasksCounter = getProjectTasksCounter( taskCreated.getProject() );
        projectTasksCounter.taskCreated();
    }

    public void taskDone( @Observes TaskDone taskDone ) {
        ProjectTasksCounter projectTasksCounter = getProjectTasksCounter( taskDone.getProject() );
        projectTasksCounter.taskDone();
    }

    public ProjectTasksCounter getProjectTasksCounter( String projectName ) {
        ProjectTasksCounter projectTasksCounter = this.projectTasksCounter.get( projectName );
        if ( projectTasksCounter == null ) {
            projectTasksCounter = new ProjectTasksCounter();
            this.projectTasksCounter.put( projectName, projectTasksCounter );
        }
        return projectTasksCounter;
    }

    private class ProjectTasksCounter {

        int tasksDone;
        int tasksCreated;

        public void taskDone() {
            tasksDone++;
            tasksCreated--;
        }

        public void taskCreated() {
            tasksCreated++;
        }

        public String getTasksDone() {
            return String.valueOf( tasksDone );
        }

        public String getTasksCreated() {
            return String.valueOf( tasksCreated );
        }
    }
}
```
This presenter has a Map and a utility class to keep track of tasks created and done. But how do we keep track of task changes? Let's pay a close attention to these methods:
```
    public void taskCreated( @Observes TaskCreated taskCreated ) {
        ProjectTasksCounter projectTasksCounter = getProjectTasksCounter( taskCreated.getProject() );
        projectTasksCounter.taskCreated();
    }

    public void taskDone( @Observes TaskDone taskDone ) {
        ProjectTasksCounter projectTasksCounter = getProjectTasksCounter( taskDone.getProject() );
        projectTasksCounter.taskDone();
    }
```
There are two observer methods listening for task events. Let's create these event classes.

On org.uberfire.shared.events package:
```
package org.uberfire.shared.events;

public class TaskCreated {

    private final String project;
    private final String folder;
    private final String task;

    public TaskCreated( String project,
                        String folder,
                        String task ) {

        this.project = project;
        this.folder = folder;
        this.task = task;
    }

    public String getProject() {
        return project;
    }
}
```
And an event class for TaskDone:
```
package org.uberfire.shared.events;

public class TaskDone {

    private final String project;
    private final String folder;
    private final String task;

    public TaskDone( String project,
                     String folder,
                     String task ) {

        this.project = project;
        this.folder = folder;
        this.task = task;
    }

    public String getProject() {
        return project;
    }
}
```
Remember that the TaskPresenter creates and marks tasks as done, so let's change these methods to fire a CDI event:
```
import org.uberfire.shared.events.TaskCreated;
import org.uberfire.shared.events.TaskDone;
import javax.enterprise.event.Event;

[...]

    @Inject
    private Event<TaskCreated> taskCreatedEvent;

    @Inject
    private Event<TaskDone> taskDoneEvent;

    public void doneTask( String folderName,
                          String taskText ) {
        Folder folder = getFolder( folderName );
        if ( folder != null ) {
            folder.removeTask( taskText );
        }
        taskDoneEvent.fire( new TaskDone(currentSelectedProject,folderName, taskText) );
        updateView();
    }

    public void createTask( String folderName,
                            String task ) {

        Folder folder = getFolder( folderName );
        if ( folder != null ) {
            folder.addTask( task );
        }
        taskCreatedEvent.fire( new TaskCreated(currentSelectedProject,folderName, task) );
        updateView();
    }
```

###DashboardView
Now it's time to create our view classes (org.uberfire.client.screens package):

**DashboardView.java**
```
package org.uberfire.client.screens;

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.screens.widgets.DashboardItem;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import static org.jboss.errai.common.client.dom.DOMUtil.removeAllChildren;

@Dependent
@Templated
public class DashboardView implements DashboardPresenter.View, IsElement {

    private DashboardPresenter presenter;

    @Inject
    @DataField( "projects" )
    Div projects;

    @Inject
    ManagedInstance<DashboardItem> dashboardItems;

    @Override
    public void init( DashboardPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void addProject( final String project,
                            final String tasksCreated,
                            final String tasksDone ) {
        DashboardItem dashboardItem = dashboardItems.get();
        dashboardItem.init( project,
                            String.valueOf( tasksCreated ),
                            String.valueOf( tasksDone ) );

        projects.appendChild( dashboardItem.getElement() );
    }

    @Override
    public void clear() {
        removeAllChildren( projects );
    }

}
```
**DashboardView.html**
```
<div class="list-group" data-field="projects"></div>
```
Our dashboard creates DashboardItem templates. Let's create them in package org.uberfire.client.screens.widgets.

**DashboardItem.java**
```
package org.uberfire.client.screens.widgets;

import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.dom.UnorderedList;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jboss.errai.ui.client.local.api.IsElement;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
@Templated
public class DashboardItem implements IsElement {

    @Inject
    @DataField( "dashboard-item" )
    UnorderedList dashboardItem;

    @Inject
    @DataField( "project-name" )
    Span projectName;

    @Inject
    @DataField( "todo" )
    Span todo;


    @Inject
    @DataField( "done" )
    Span done;


    public void init( String projectName, String todo, String done ) {

        this.projectName.setTextContent( projectName.toUpperCase() );
        this.todo.setTextContent( todo );
        this.done.setTextContent( done );

    }

}
```
**DashboardItem.html**
```
<ul class="list-group" data-field="dashboard-item">

    <li class="list-group-item-info">
        <span data-field="project-name"></span>
    </li>
    <li class="list-group-item-warning">
        <span> TODO </span>
        <span data-field="todo" class="badge"></span>
    </li>
    <li class="list-group-item-success">
        <span> DONE </span>
        <span data-field="done" class="badge"></span>
    </li>

</ul>
```

###Create Perspective Menu
Now it's time to put this work on our app menu. Open ShowcaseEntryPoint.java and update method setupMenu(event):

```
    private void setupMenu( @Observes final ApplicationReadyEvent event ) {
        final Menus menus =
                newTopLevelMenu( "UF Tasks" )
                        .respondsWith(
                                () -> placeManager.goTo( new DefaultPlaceRequest( "TasksPerspective" ) ) )
                        .endMenu()
                        .newTopLevelMenu( "Dashboard" )
                        .respondsWith(
                                () -> placeManager.goTo( new DefaultPlaceRequest( "DashboardPerspective" ) ) )
                        .endMenu()
                        .build();

        menubar.addMenus( menus );
    }
```
Pay attention to placeManager.goTo( new DefaultPlaceRequest( "DashboardPerspective" ) ) call.
This method is a Workbench-centric abstraction over the browser's history mechanism. It allows the application to initiate navigation
to any displayable thing: a WorkbenchPerspective, a  WorkbenchScreen, a WorkbenchPopup, a WorkbenchEditor, a WorkbenchPart within a screen or editor, or the editor associated with a VFS file.

##Time to see it work!

Start the server, refresh the browser, create two projects and click in one of them. Create a new folder and add some tasks for it and mark some of them as done. Click on Dashboard menu:

![dashboard final](dashboardFinal.png)

##Next
We hope you liked this tutorial. If you find any issues or want to contribute to it, feel free to send a pull request for [Uberfire Docs](https://github.com/uberfire/uberfire-docs) and [Uberfire Tutorial](https://github.com/uberfire/uberfire-tutorial) projects.

Now, let's deploy this app in Tomcat and Wildfly.
