package org.kie.workbench.common.screens.explorer.client.widgets.dropdown;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Dropdown;
import com.github.gwtbootstrap.client.ui.InputAddOn;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.Tooltip;
import com.github.gwtbootstrap.client.ui.base.ListItem;
import com.github.gwtbootstrap.client.ui.base.UnorderedList;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HorizontalPanel;
import org.kie.workbench.common.screens.explorer.client.resources.ProjectExplorerResources;
import org.kie.workbench.common.screens.explorer.client.resources.i18n.ProjectExplorerConstants;

public class CustomDropdown extends Dropdown {

    private boolean orderedUp = false;

    private final Map<String, NavLink> downContentMap =
            new TreeMap<String, NavLink>( new Comparator<String>() {
                @Override
                public int compare( String o1, String o2 ) {
                    // Cannot use compareToIgnoreCase because this will lead to entries 'disappearing',
                    // such as when you add 'aA' after 'aa' (aa disappears).
                    return o1.compareTo( o2 );
                }
            } );

    private final Map<String, NavLink> upContentMap =
            new TreeMap<String, NavLink>( new Comparator<String>() {
                @Override
                public int compare( String o1, String o2 ) {
                    return o2.compareTo( o1 );
                }
            } );

    private final UnorderedList content = new UnorderedList();

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
            public void onKeyUp( final KeyUpEvent keyUpEvent ) {
                filter( searchBox.getText() );
            }
        } );
    }};

    private final InputAddOn search = new InputAddOn() {{
        setAppendIcon( IconType.SEARCH );
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
        add( new Anchor( new SafeHtml() {
            @Override
            public String asString() {
                return "<i class=\"icon-chevron-up pull-left\"></i><i class=\"icon-chevron-up pull-right\"></i>";
            }
        } ) );
    }};

    public CustomDropdown() {
        super();

        orderTt.setWidget( orderButton );

        controls.add( search );
        controls.add( orderTt );

        super.add( new ListItem( controls ) );
        super.add( new ListItem( content ) );
        super.add( footer );
        content.addStyleName( "dropdown-menu" );
        content.addStyleName( ProjectExplorerResources.INSTANCE.CSS().scrollMenu() );
    }

    private void filter( final String filter ) {
        content.clear();
        if ( filter != null && !filter.trim().isEmpty() ) {
            for ( final Map.Entry<String, NavLink> entry : orderedUp ? upContentMap.entrySet() : downContentMap.entrySet() ) {
                if ( entry.getKey().startsWith( filter.trim() ) ) {
                    content.add( entry.getValue() );
                }
            }
        } else {
            for ( final Map.Entry<String, NavLink> entry : orderedUp ? upContentMap.entrySet() : downContentMap.entrySet() ) {
                content.add( entry.getValue() );
            }
        }
    }

    public void add( final NavLink item ) {
        downContentMap.put( item.getText().trim(), item );
        upContentMap.put( item.getText().trim(), item );
        filter( null );
    }

    public void clear() {
        downContentMap.clear();
        upContentMap.clear();
        content.clear();
    }

    @Override
    public void setText( final String text ) {
        searchBox.setText( "" );
        super.setText( text );
        Scheduler.get().scheduleDeferred( new Command() {
            @Override
            public void execute() {
                if ( getAbsoluteLeft() == 0 ) {
                    return;
                }

                if ( getAbsoluteLeft() < 220 ) {
                    setRightDropdown( false );
                } else {
                    setRightDropdown( true );
                }
            }
        } );
    }
}
