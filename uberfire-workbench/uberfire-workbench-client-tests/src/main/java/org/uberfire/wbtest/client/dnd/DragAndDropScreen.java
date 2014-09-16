package org.uberfire.wbtest.client.dnd;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.wbtest.client.api.AbstractTestScreenActivity;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;

@Dependent
@Named("org.uberfire.wbtest.client.dnd.DragAndDropScreen")
public class DragAndDropScreen extends AbstractTestScreenActivity {

    private final Label label = new Label();
    private String debugId;

    @Inject
    public DragAndDropScreen( PlaceManager placeManager ) {
        super( placeManager );
    }

    @Override
    public void onStartup( PlaceRequest place ) {
        super.onStartup( place );
        debugId = place.getParameter( "debugId", "default" );
        label.setText( debugId );
        label.getElement().setId( "DragAndDropScreen-" + debugId );
    }

    @Override
    public String getTitle() {
        return "DnD-" + debugId;
    }

    @Override
    public IsWidget getWidget() {
        return label;
    }

}
