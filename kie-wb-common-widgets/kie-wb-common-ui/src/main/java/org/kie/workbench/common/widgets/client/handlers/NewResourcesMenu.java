package org.kie.workbench.common.widgets.client.handlers;

import com.google.gwt.core.client.Callback;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.IOCBeanManager;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.Command;
import org.uberfire.client.workbench.widgets.events.PathChangeEvent;
import org.uberfire.client.workbench.widgets.menu.MenuFactory;
import org.uberfire.client.workbench.widgets.menu.MenuItem;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A menu to create New Resources
 */
@ApplicationScoped
public class NewResourcesMenu {

    @Inject
    private IOCBeanManager iocBeanManager;

    @Inject
    private NewResourcePresenter newResourcePresenter;

    private final List<MenuItem>                    items               = new ArrayList<MenuItem>();
    private final Map<NewResourceHandler, MenuItem> newResourceHandlers = new HashMap<NewResourceHandler, MenuItem>();

    @PostConstruct
    public void setup() {
        final Collection<IOCBeanDef<NewResourceHandler>> handlerBeans = iocBeanManager.lookupBeans( NewResourceHandler.class );
        if ( handlerBeans.size() > 0 ) {
            for ( IOCBeanDef<NewResourceHandler> handlerBean : handlerBeans ) {
                final NewResourceHandler activeHandler = handlerBean.getInstance();
                final String description = activeHandler.getDescription();
                final MenuItem menuItem = MenuFactory.newSimpleItem( description ).respondsWith( new Command() {
                    @Override
                    public void execute() {
                        newResourcePresenter.show( activeHandler );
                    }
                } ).endMenu().build().getItems().get( 0 );
                newResourceHandlers.put( activeHandler,
                                         menuItem );
                items.add( menuItem );
            }
        }

        //Sort MenuItems by caption
        Collections.sort( items,
                          new Comparator<MenuItem>() {
            @Override
            public int compare( final MenuItem o1,
                                final MenuItem o2 ) {
                return o1.getCaption().compareToIgnoreCase( o2.getCaption() );
            }
        } );

    }

    public List<MenuItem> getMenuItems() {
        return items;
    }

    public void selectedPathChanged( @Observes final PathChangeEvent event ) {
        enableNewResourceHandlers( event.getPath() );
    }

    private void enableNewResourceHandlers( final Path path ) {
        for ( Map.Entry<NewResourceHandler, MenuItem> e : this.newResourceHandlers.entrySet() ) {
            final NewResourceHandler handler = e.getKey();
            final MenuItem menuItem = e.getValue();
            handler.acceptPath( path, new Callback<Boolean, Void>() {
                @Override
                public void onFailure( Void reason ) {
                    // Nothing to do there right now.
                }

                @Override
                public void onSuccess( final Boolean result ) {
                    if ( result != null ) {
                        menuItem.setEnabled( result );
                    }
                }
            } );

        }
    }

}
