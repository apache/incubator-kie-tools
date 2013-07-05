package org.uberfire.client.screens.stackablebar;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.workbench.events.ClosePlaceEvent;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
public class StackableBarView extends Composite implements StackableBarPresenter.View {

    private StackableBarPresenter presenter;
    @UiField
    public FlowPanel mainPanel;
    @UiField
    public TextBox placeTextBox;
    @UiField
    public TextBox idTextBox;
    @Inject
    private Event<ClosePlaceEvent> closedEvent;
    @Inject
    private Event<NotificationEvent> notification;
    
    @Inject
    private ActivityManager activityManager;
    @Inject
    public StackBarContainer stackBarContainer;
    
    private static ViewBinder uiBinder = GWT.create(ViewBinder.class);
    
    
    
    @UiField
    public Button addButton;

    interface ViewBinder
            extends
            UiBinder<Widget, StackableBarView> {
    }

    @Override
    public void init(final StackableBarPresenter presenter) {
        initWidget(uiBinder.createAndBindUi(this));
        this.presenter = presenter;
        
        stackBarContainer.setMaxBars(5);
        
        mainPanel.add(((StackBarContainerImpl)stackBarContainer));
    }
    
    @UiHandler("addButton")
    public void onClickNotificationButton( final ClickEvent event ) {
        Long id = null;
        try {
            id =  new Long(idTextBox.getText());
        }catch(NumberFormatException e){
            
        }
        if(id == null){
            stackBarContainer.addNewBar(placeTextBox.getText());
        }else{
            stackBarContainer.addPlaceToBar(id, placeTextBox.getText());
        }

        stackBarContainer.refresh();
    
    }
    
}
