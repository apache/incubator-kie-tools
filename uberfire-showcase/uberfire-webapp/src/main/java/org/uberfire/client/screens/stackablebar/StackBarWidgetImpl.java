/*
 * Copyright 2013 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.client.screens.stackablebar;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ButtonGroup;
import com.github.gwtbootstrap.client.ui.Dropdown;
import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.NavSearch;
import com.github.gwtbootstrap.client.ui.base.ListItem;
import com.github.gwtbootstrap.client.ui.base.UnorderedList;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.enterprise.event.Event;
import org.uberfire.client.mvp.AbstractWorkbenchScreenActivity;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.screens.stackablebar.events.CloseBarEvent;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.events.ClosePlaceEvent;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.EnabledStateChangeListener;
import org.uberfire.workbench.model.menu.MenuGroup;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuItemCommand;
import org.uberfire.workbench.model.menu.MenuSearchItem;
import org.uberfire.workbench.model.menu.Menus;

/**
 *
 * @author salaboy
 */
public class StackBarWidgetImpl extends Composite implements StackBarWidget{
    
    
    private Event<ClosePlaceEvent> closeEvent;
    
    private ActivityManager activityManager;
    
    private Event<NotificationEvent> notification;
    
    private Event<CloseBarEvent> closeBarEvent;

    private Long id;
    
    public FlowPanel stackBar = new FlowPanel();
    
    private UnorderedList title;
    
    public FlowPanel contextualMenues = new FlowPanel();
    
    public FlowPanel contentPanel = new FlowPanel();
    
    public FlowPanel placesPanel = new FlowPanel();
    
    public List<String> places = new ArrayList<String>();
    
    public String currentPlace;

    private ButtonGroup barSystemButtons;
    
    private Button navButton;
    private Button collapseButton;
    private Button closeButton;
    
    private boolean alwaysOpened = false;
    
    private int maxPlaces = 1;
    
    public StackBarWidgetImpl(final Long id, final String place){
        this(id, place, 1, false);
    }
    
    
    public StackBarWidgetImpl(final Long id, final String place, int maxPlaces, boolean alwaysOpened) {
        this.places.add(place);
        currentPlace = place;
        this.alwaysOpened = alwaysOpened;
        this.maxPlaces = maxPlaces;
        this.id = id;
        title = new UnorderedList();
        
        initWidget(stackBar);
        stackBar.setStyleName("subnav affix-top");
        title.setStyleName("nav nav-pills");
        barSystemButtons = new ButtonGroup();
        
        
        if(!alwaysOpened){
            collapseButton = new Button(); 
            collapseButton.setIcon(IconType.ARROW_LEFT);
            collapseButton.setSize(ButtonSize.MINI);
            collapseButton.addClickHandler(new ClickHandler() {

                private boolean opened = true;
                @Override
                public void onClick(ClickEvent event) {
                    if(!opened){
                        opened  = true;
                        collapseButton.setIcon(IconType.ARROW_LEFT);
                        showContent(currentPlace);
                    }else{

                        collapseButton.setIcon(IconType.ARROW_DOWN);
                        hideContent(currentPlace);
                        opened = false;
                    }

                }


            });
            barSystemButtons.add(collapseButton);
        }
        if(maxPlaces > 1){
            navButton = new Button(); 
            navButton.setIcon(IconType.TH_LIST);
            navButton.setSize(ButtonSize.MINI);
            navButton.setVisible(false);
            navButton.addClickHandler(new ClickHandler() {
                private boolean opened = true;
                @Override
                public void onClick(ClickEvent event) {
                    if(!opened){
                        opened  = true;

                        showPlaces();
                    }else{


                        hidePlaces();
                        opened = false;
                    }

                }


            });
            barSystemButtons.add(navButton);
        }
        
        closeButton = new Button();
        closeButton.setSize(ButtonSize.MINI);
        closeButton.setIcon(IconType.REMOVE);
        closeButton.addClickHandler(new ClickHandler() {
            
            @Override
            public void onClick(ClickEvent event) {
                stackBar.clear();
                stackBar.setStyleName("");
                hideContent(currentPlace);
                closeBarEvent.fire(new CloseBarEvent(id));
                notification.fire(new NotificationEvent("Bar "+id+" closed!"));
            }

            
        });
        barSystemButtons.add(closeButton);
  
        contextualMenues.add(barSystemButtons);

        
        stackBar.add(title);
        
        stackBar.add(placesPanel);
        
        
        stackBar.add(contentPanel);
        
        initializeBar(place);
       
        
    }
    
    public void refresh(){
        showContent(currentPlace);
        showPlaces();
    }
    
    private void initializeBar(String place){
        currentPlace = place;
        title.clear();
        ListItem item = new ListItem();
        item.add(new Anchor(id + " - "+currentPlace));
        title.add(item);
        
        ListItem li = new ListItem();
        li.add(contextualMenues);
        li.setStyleName("pull-right");

        title.add(li);
    }
    
    
    private void showPlaces(){
        placesPanel.clear();
        if(this.places.size() > 1){
            FlowPanel internalPanel = new FlowPanel();
            internalPanel.setStyleName("span12");
            FlowPanel externalPanel = new FlowPanel();
            externalPanel.setStyleName("row-fluid");

            UnorderedList ul = new UnorderedList();
            ul.setStyleName("nav nav-tabs nav-stacked");
            

            for(final String place : places){
                if(!currentPlace.equals(place)){
                    Anchor changeButton = new Anchor(place);
                    changeButton.addClickHandler(new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent event) {
                            initializeBar(place);
                            showPlaces();
                            showContent(place);
                        }
                    });
                    Anchor closePlaceButton = new Anchor(" x");
                    closePlaceButton.addClickHandler(new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent event) {
                            closeEvent.fire(new ClosePlaceEvent( new DefaultPlaceRequest(place) ) );
                            removePlace(place);
                            showPlaces();

                        }
                    });
                    ListItem li = new ListItem();
                    FlowPanel flowPanel = new FlowPanel();
                    flowPanel.add(changeButton);
                    flowPanel.add(closePlaceButton);
                    li.add(flowPanel);
                    ul.add(li);
                }
            }
            internalPanel.add(ul);
            externalPanel.add(internalPanel);
            placesPanel.add(externalPanel);
        }
    }
    
    private void hidePlaces(){
        placesPanel.clear();
    }
   
    
    @Override
    public void addPlace(String place){
       if(this.places.size() >= maxPlaces){
           notification.fire(new NotificationEvent("This bar is full, remove places or add the place to a new bar"));
           return;
       } 
       this.places.add(place);
       if(this.places.size() > 1){
           navButton.setVisible(true);
       }
    }
    
    @Override
    public void removePlace(String place){
       this.places.remove(place);
       if(!this.places.isEmpty()){
        currentPlace = this.places.get(0);
        showContent(currentPlace);
        
        if(this.places.size() == 1){
           navButton.setVisible(false);
           hidePlaces();
        }else{    
           showPlaces();
        }
       }
       
    }
    
    private void showContent(String place) {
        contentPanel.clear();
        contextualMenues.clear();
        Set<Activity> activities = activityManager.getActivities(new DefaultPlaceRequest(place));
        AbstractWorkbenchScreenActivity activity = ((AbstractWorkbenchScreenActivity)activities.iterator().next());
        IsWidget widget = activity.getWidget();
        
        
        Menus menus = activity.getMenus();
        UnorderedList menu = new UnorderedList();
        menu.setStyleName("nav nav-pills");
        if(menus != null && menus.getItems() != null && !menus.getItems().isEmpty()){
            List<Widget> makeMenuItems = makeMenuItems(menus.getItems());
            for(Widget w : makeMenuItems){
               menu.add(w);
            }
        }
        
        
        ListItem systemBarButtons = new ListItem(barSystemButtons);
        
        menu.add(systemBarButtons);
        contextualMenues.add(menu);
        FlowPanel externalPanel = new FlowPanel();
        externalPanel.setStyleName("row-fluid");
        FlowPanel internalPanel = new FlowPanel();
        internalPanel.setStyleName("span12");
        internalPanel.add(widget);
        externalPanel.add(internalPanel);
        contentPanel.add(externalPanel);
    }
    
    private void hideContent(String place) {
        closeEvent.fire(new ClosePlaceEvent( new DefaultPlaceRequest(place) ) );
        
        contextualMenues.clear();
        contextualMenues.add(barSystemButtons);
        contentPanel.clear();
    }

    public void setCloseEvent(Event<ClosePlaceEvent> closeEvent) {
        this.closeEvent = closeEvent;
    }

    public void setActivityManager(ActivityManager activityManager) {
        this.activityManager = activityManager;
    }

    public void setNotification(Event<NotificationEvent> notification) {
        this.notification = notification;
    }

    public void setCloseBarEvent(Event<CloseBarEvent> closeBarEvent) {
        this.closeBarEvent = closeBarEvent;
    }
    
    @Override
    public void setAlwaysOpen(boolean open) {
        this.alwaysOpened = open;
    }

    @Override
    public boolean getAlwaysOpen() {
        return this.alwaysOpened;
    }

    @Override
    public void setMaxPlaces(int maxPlaces) {
        this.maxPlaces = maxPlaces;
    }

    @Override
    public int getMaxPlaces() {
        return this.maxPlaces;
    }

    
    
    // STOLEN FROM UF MENUBARIMPL -> Is there any way to reuse that code? 
     //Recursively converts a Presenter Menu item to a GWT MenuItem
    private Widget makeMenuItem( final MenuItem item ) {
        if ( item instanceof MenuItemCommand ) {
            final MenuItemCommand cmdItem = (MenuItemCommand) item;
            final NavLink gwtItem = new NavLink( cmdItem.getCaption() ) {{
                setDisabled( !item.isEnabled() );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( final ClickEvent event ) {
                        cmdItem.getCommand().execute();
                    }
                } );
            }};
            item.addEnabledStateChangeListener( new EnabledStateChangeListener() {
                @Override
                public void enabledStateChanged( final boolean enabled ) {
                    gwtItem.setDisabled( !enabled );
                }
            } );

            return gwtItem;

        }
        if ( item instanceof MenuSearchItem ) {
            final MenuSearchItem searchItem = (MenuSearchItem) item;
            final NavSearch gwtItem = new NavSearch() {{
                setPlaceholder( searchItem.getCaption() );
                addSubmitHandler( new Form.SubmitHandler() {
                    @Override
                    public void onSubmit( final Form.SubmitEvent event ) {
                        event.cancel();
                        searchItem.getCommand().execute( getTextBox().getText() );
                    }
                } );
            }};

            return gwtItem;

        } else if ( item instanceof MenuGroup ) {
            final MenuGroup groups = (MenuGroup) item;
            final Dropdown gwtItem = new Dropdown( groups.getCaption() );
            for ( final Widget _item : makeMenuItems( groups.getItems() ) ) {
                gwtItem.add( _item );
            }
            return gwtItem;
        }

        final NavLink gwtItem = new NavLink( item.getCaption() ) {{
            setDisabled( !item.isEnabled() );
        }};
        item.addEnabledStateChangeListener( new EnabledStateChangeListener() {
            @Override
            public void enabledStateChanged( final boolean enabled ) {
                gwtItem.setDisabled( !enabled );
            }
        } );

        return gwtItem;
    }

    private List<Widget> makeMenuItems( final List<MenuItem> items ) {
        final List<Widget> gwtItems = new ArrayList<Widget>();
        for ( final MenuItem item : items ) {
            final Widget gwtItem = makeMenuItem( item );
            gwtItems.add( gwtItem );
        }
        return gwtItems;
    }
    
    
    
    
    
}
