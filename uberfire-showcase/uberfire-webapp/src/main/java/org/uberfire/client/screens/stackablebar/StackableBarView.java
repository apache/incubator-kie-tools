package org.uberfire.client.screens.stackablebar;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CheckBox;
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
    @UiField
    public CheckBox alwaysOpenCheckBox;
    
    @UiField
    public TextBox maxBarsTextBox;
    
    @UiField
    public TextBox maxPlacesTextBox;
    
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
    
    @UiField
    public Button createContainerButton;

    interface ViewBinder
            extends
            UiBinder<Widget, StackableBarView> {
    }

    @Override
    public void init(final StackableBarPresenter presenter) {
        initWidget(uiBinder.createAndBindUi(this));
        this.presenter = presenter;
        
    }
    
    
    @UiHandler("createContainerButton")
    public void onClickCreateContainerButton( final ClickEvent event ) {
        String maxBars = maxBarsTextBox.getText();
        if(maxBarsTextBox.getText().equals("")){
            maxBars = "1";
        }
        mainPanel.clear();
        stackBarContainer.clear();
        stackBarContainer.setMaxBars(Integer.parseInt(maxBars));
        
        mainPanel.add(((StackBarContainerImpl)stackBarContainer));
        
        stackBarContainer.refresh();
    }
    
    @UiHandler("addButton")
    public void onClickAddButton( final ClickEvent event ) {
        Long id = null;
        try {
            id =  new Long(idTextBox.getText());
        }catch(NumberFormatException e){
            
        }
        if(id == null){
            String maxPlaces = maxPlacesTextBox.getText();
            if(maxPlaces.equals("")){
                maxPlaces = "1";
            }
            stackBarContainer.addNewBar(placeTextBox.getText(), Integer.parseInt(maxPlaces), alwaysOpenCheckBox.getValue());
        }else{
            stackBarContainer.addPlaceToBar(id, placeTextBox.getText());
        }

        stackBarContainer.refresh();
    
    }
    
}
