/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.explorer.client.widgets.dropdown;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.AnchorButton;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.DropDown;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.InputGroup;
import org.gwtbootstrap3.client.ui.InputGroupAddon;
import org.gwtbootstrap3.client.ui.ListItem;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.Tooltip;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Styles;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.kie.workbench.common.screens.explorer.client.resources.ProjectExplorerResources;
import org.kie.workbench.common.screens.explorer.client.resources.i18n.ProjectExplorerConstants;

public class CustomDropdown extends Composite {

    private boolean orderedUp = false;

    private final Map<String, AnchorListItem> downContentMap =
            new TreeMap<String, AnchorListItem>( new Comparator<String>() {
                @Override
                public int compare( String o1,
                                    String o2 ) {
                    // Cannot use compareToIgnoreCase because this will lead to entries 'disappearing',
                    // such as when you add 'aA' after 'aa' (aa disappears).
                    return o1.compareTo( o2 );
                }
            } );

    private final Map<String, AnchorListItem> upContentMap =
            new TreeMap<String, AnchorListItem>( new Comparator<String>() {
                @Override
                public int compare( String o1,
                                    String o2 ) {
                    return o2.compareTo( o1 );
                }
            } );

    private final DropDown dropDown = new DropDown();
    private final AnchorButton anchor = new AnchorButton( ButtonType.LINK ) {{
        setDataToggle( Toggle.DROPDOWN );
        setToggleCaret( true );
        getElement().getStyle().setFontSize( 16, Style.Unit.PX );
    }};

    private final DropDownMenu content = new DropDownMenu();

    private final TextBox searchBox = new TextBox() {{
        setPlaceholder( "Search..." );
        addClickHandler( new ClickHandler() {
            @Override
            public void onClick( final ClickEvent clickEvent ) {
                clickEvent.stopPropagation();
            }
        } );
        addKeyUpHandler( new KeyUpHandler() {
            @Override
            public void onKeyUp( final KeyUpEvent event ) {
                filter( searchBox.getText() );
            }
        } );
    }};

    private final InputGroup search = new InputGroup() {{
        add( new InputGroupAddon() {{
            setIcon( IconType.SEARCH );
        }} );
        add( searchBox );
    }};

    private final HorizontalPanel controls = new HorizontalPanel();

    private final Tooltip orderTt = new Tooltip( ProjectExplorerConstants.INSTANCE.sort() );

    private final Button orderButton = new Button() {{
        orderedUp = false; //redundant, just in case
        setIcon( IconType.ARROW_UP );
        addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                if ( orderButton.equals( event.getSource() ) ) {
                    event.stopPropagation();
                    orderedUp = !orderedUp;
                    filter( null );
                    setIcon( orderedUp ? IconType.ARROW_DOWN : IconType.ARROW_UP );
                }
            }
        } );
    }};

    private final ListItem footer = new ListItem() {{
        addStyleName( "disabled" );
        add( new Anchor() {{
            add( new Icon( IconType.CHEVRON_UP ) {{
                addStyleName( Styles.PULL_LEFT );
            }} );
            add( new Icon( IconType.CHEVRON_UP ) {{
                addStyleName( Styles.PULL_RIGHT );
            }} );
        }} );
    }};

    public CustomDropdown() {
        initWidget( dropDown );

        orderTt.setWidget( orderButton );

        controls.add( search );
        controls.add( orderTt );

        content.add( controls );
        content.add( footer );

        dropDown.add( anchor );
        dropDown.add( content );
    }

    private void filter( final String filter ) {
        if ( content.getWidgetCount() - 2 > 0 ) {
            final Widget[] clean = new Widget[ content.getWidgetCount() - 2 ];
            int index = -1;
            for ( int i = 1; i < ( content.getWidgetCount() - 1 ); i++ ) {
                clean[ ++index ] = content.getWidget( i );
            }

            for ( final Widget widget : clean ) {
                widget.removeFromParent();
            }
        }
        content.add( footer );

        if ( filter != null && !filter.trim().isEmpty() ) {
            for ( final Map.Entry<String, AnchorListItem> entry : orderedUp ? upContentMap.entrySet() : downContentMap.entrySet() ) {
                if ( entry.getKey().startsWith( filter.trim() ) ) {
                    content.insert( entry.getValue(), content.getWidgetCount() - 1 );
                }
            }
        } else {
            for ( final Map.Entry<String, AnchorListItem> entry : orderedUp ? upContentMap.entrySet() : downContentMap.entrySet() ) {
                content.insert( entry.getValue(), content.getWidgetCount() - 1 );
            }
        }
    }

    public void add( final AnchorListItem item ) {
        downContentMap.put( item.getText().trim(), item );
        upContentMap.put( item.getText().trim(), item );
        filter( null );
    }

    public void clear() {
        downContentMap.clear();
        upContentMap.clear();
        filter( null );
    }

    public void setText( final String text ) {
        searchBox.setText( "" );
        anchor.setText( text );
        Scheduler.get().scheduleDeferred( new Command() {
            @Override
            public void execute() {
                if ( getAbsoluteLeft() == 0 ) {
                    return;
                }

                if ( getAbsoluteLeft() < 220 ) {
                    content.removeStyleName( Styles.PULL_RIGHT );
                } else {
                    content.addStyleName( Styles.PULL_RIGHT );
                }
            }
        } );
    }

    public void setEnableTriggerWidget( final boolean enabled ) {
//        anchor.setEnabled( enabled );
    }
}
