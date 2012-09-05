package org.uberfire.client.editors.home;


import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

import javax.enterprise.context.Dependent;

@Dependent
@WorkbenchScreen(identifier = "notifications")
public class NotificationsScreen {

    private final VerticalPanel widgets = new VerticalPanel();

    public NotificationsScreen() {
        addMessage("Toni finished the Welcome Screen", "Didn't take him that long.");
        addMessage("Michael is on PTO September 4th", "");
        addMessage("Jervis finished the Gadgets", "It is now possible to write your own Gadgets to ÜberFire!");
        addMessage("Porcelli is on parental leave, nobody knows when he is back", "Emails might reach him, but better leave him alone");
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Notifications";
    }

    @WorkbenchPartView
    public IsWidget getView() {


        return widgets;
    }

    private void addMessage(String title, String content) {
        InfoCube infoCube = new InfoCube();
        infoCube.setTitle(title);
        infoCube.setContent(content);
        widgets.add(infoCube);
    }
}
