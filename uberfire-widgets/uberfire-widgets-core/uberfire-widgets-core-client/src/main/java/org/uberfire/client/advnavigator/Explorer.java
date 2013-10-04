package org.uberfire.client.advnavigator;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.github.gwtbootstrap.client.ui.Dropdown;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.NavList;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.resources.NavigatorResources;
import org.uberfire.mvp.ParameterizedCommand;

@Dependent
public class Explorer extends Composite {

    public static enum Mode {
        REGULAR, EXPANDED, COLLAPSED
    }

    @Inject
    @Named("TreeNav")
    private Navigator navigator;

    private final FlowPanel container = new FlowPanel();
    private NavigatorOptions options = NavigatorOptions.DEFAULT;
    private ParameterizedCommand<Path> fileActionCommand = null;
    private Mode mode = Mode.REGULAR;

    @PostConstruct
    public void init() {
        initWidget( container );
        setStyleName( NavigatorResources.INSTANCE.css().container() );
        setupHeader();
    }

    private void setupHeader() {
        container.clear();
        if ( !mode.equals( Mode.REGULAR ) ) {
            final Element element = DOM.createElement( "i" );
            element.getStyle().setFloat( Style.Float.RIGHT );
            element.getStyle().setPaddingTop( 5, Style.Unit.PX );
            element.getStyle().setPaddingRight( 10, Style.Unit.PX );
            DOM.sinkEvents( (com.google.gwt.user.client.Element) element, Event.ONCLICK );
            DOM.setEventListener( (com.google.gwt.user.client.Element) element, new EventListener() {
                public void onBrowserEvent( Event event ) {
                    if ( element.getClassName().equals( "icon-expand-alt" ) ) {
                        element.setClassName( "icon-collapse-alt" );
                        onExpandNavigator();
                    } else {
                        element.setClassName( "icon-expand-alt" );
                        onCollapseNavigator();
                    }
                }
            } );

            if ( mode.equals( Mode.COLLAPSED ) ) {
                element.setClassName( "icon-expand-alt" );
            } else {
                element.setClassName( "icon-collapse-alt" );
            }

            container.getElement().appendChild( element );
        }

        container.add( new NavigatorBreadcrumbs( NavigatorBreadcrumbs.Mode.HEADER ) {{

            final Dropdown groups = new Dropdown( "financial" ) {{
                add( new NavList() {{
                    add( new NavLink( "financial" ) );
                    add( new NavLink( "demo" ) );
                }} );
            }};

            final Dropdown repos = new Dropdown( "uf-playground" ) {{
                add( new NavList() {{
                    add( new NavLink( "uf-playground" ) );
                }} );
            }};

            final Dropdown projects = new Dropdown( "mortgages" ) {{
                add( new NavList() {{
                    add( new NavLink( "mortgages" ) );
                }} );
            }};

            build( groups, repos, projects );
        }} );
    }

    public void setMode( final Mode mode ) {
        this.mode = mode;
        setupHeader();
    }

    public void loadContent( final Path path ) {
        if ( path != null ) {
            navigator.loadContent( path );
        }
    }

    private void onCollapseNavigator() {
        if ( navigator.isAttached() ) {
            container.remove( navigator );
        }
        mode = Mode.COLLAPSED;
    }

    private void onExpandNavigator() {
        if ( !navigator.isAttached() ) {
            container.add( navigator );
        }
        mode = Mode.EXPANDED;
    }
}