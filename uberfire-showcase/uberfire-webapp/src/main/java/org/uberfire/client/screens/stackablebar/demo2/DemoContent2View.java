package org.uberfire.client.screens.stackablebar.demo2;

import com.github.gwtbootstrap.client.ui.Label;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import org.uberfire.workbench.events.NotificationEvent;


@Dependent
public class DemoContent2View extends Composite implements DemoContent2Presenter.View {

    
    private DemoContent2Presenter presenter;
    
    @Inject
    private Event<NotificationEvent> notification;
    
    public static ViewBinder uiBinder = GWT.create(ViewBinder.class);

    interface ViewBinder
            extends
            UiBinder<Widget, DemoContent2View> {
    }
    
    @UiField
    public HTMLPanel mainPanel;

    @Override
    public void displayNotification( String text ) {
        notification.fire( new NotificationEvent( text ) );
    }
    
    @Override
    public void init(final DemoContent2Presenter presenter) {
        this.presenter = presenter;
        initWidget(uiBinder.createAndBindUi(this));
        FlowPanel flowPanel = new FlowPanel();
        flowPanel.setStyleName("row-fluid");
        FlowPanel span12 = new FlowPanel();
        span12.setStyleName("span12");
        Label label = new Label("CONTENT 2");
        span12.add(label);
        flowPanel.add(span12);
        mainPanel.add(flowPanel);
        
    }

}
