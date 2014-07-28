package org.uberfire.client.screens;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.ShowcaseEntryPoint.DumpLayout;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.util.Layouts;
import org.uberfire.shared.Mood;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;

@Dependent
@Templated
@WorkbenchScreen(identifier="MoodScreen")
public class MoodScreen extends Composite {

    @Inject
    @DataField
    private TextBox moodTextBox;

    @Inject Event<Mood> moodEvent;

    @WorkbenchPartTitle
    public String getScreenTitle() {
        return "Change Mood";
    }

    @EventHandler("moodTextBox")
    private void onKeyDown(KeyDownEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            moodEvent.fire(new Mood(moodTextBox.getText()));
            moodTextBox.setText("");
        }
    }

    public void dumpHierarchy(@Observes DumpLayout e) {
        System.out.println("Containment hierarchy of MoodScreen textbox:");
        System.out.println(Layouts.getContainmentHierarchy( moodTextBox ));
    }
}
