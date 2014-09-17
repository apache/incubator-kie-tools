package org.uberfire.wbtest.client.dnd;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.util.Layouts;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.wbtest.client.api.AbstractTestScreenActivity;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;

@Dependent
@Named("org.uberfire.wbtest.client.dnd.DragAndDropScreen")
public class DragAndDropScreen extends AbstractTestScreenActivity {

    private final FlowPanel panel = new FlowPanel();
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

        label.setText( "DnD screen " + debugId );
        label.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                System.out.println( Layouts.getContainmentHierarchy( label ) );
            }
        } );

        panel.getElement().setId( "DragAndDropScreen-" + debugId );
        panel.add( label );
    }

    @Override
    public String getTitle() {
        return "DnD-" + debugId;
    }

    @Override
    public IsWidget getWidget() {
        return panel;
    }

}
