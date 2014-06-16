package org.kie.workbench.common.screens.explorer.client.widgets.dropdown;

import java.util.Map;
import java.util.TreeMap;

import com.github.gwtbootstrap.client.ui.Dropdown;
import com.github.gwtbootstrap.client.ui.InputAddOn;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.TextBox;
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
import org.kie.workbench.common.screens.explorer.client.resources.ProjectExplorerResources;

public class CustomDropdown extends Dropdown {

    private final Map<String, NavLink> contentMap = new TreeMap<String, NavLink>();
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
        super.add( new ListItem( search ) );
        super.add( new ListItem( content ) );
        super.add( footer );
        content.addStyleName( "dropdown-menu" );
        content.addStyleName( ProjectExplorerResources.INSTANCE.CSS().scrollMenu() );
    }

    private void filter( final String filter ) {
        content.clear();
        if ( filter != null && !filter.trim().isEmpty() ) {
            for ( final Map.Entry<String, NavLink> entry : contentMap.entrySet() ) {
                if ( entry.getKey().startsWith( filter.trim() ) ) {
                    content.add( entry.getValue() );
                }
            }
        } else {
            for ( final NavLink value : contentMap.values() ) {
                content.add( value );
            }
        }
    }

    public void add( final NavLink item ) {
        contentMap.put( item.getText().trim(), item );
        content.add( item );
    }

    public void clear() {
        contentMap.clear();
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
