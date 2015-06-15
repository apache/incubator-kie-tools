#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.client;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import ${package}.client.resources.AppResource;
import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.views.pfly.menu.MainBrand;
import org.uberfire.client.workbench.events.ApplicationReadyEvent;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBar;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.menu.Menus;

import static org.uberfire.workbench.model.menu.MenuFactory.*;

@EntryPoint
public class ShowcaseEntryPoint {

    @Inject
    private WorkbenchMenuBar menubar;

    @Inject
    private PlaceManager placeManager;

    @PostConstruct
    public void startApp() {
        AppResource.INSTANCE.CSS().ensureInjected();
        hideLoadingPopup();
    }

    private void setupMenu( @Observes final ApplicationReadyEvent event ) {
        final Menus menus =
                newTopLevelMenu( "Home" )
                        .respondsWith( new Command() {
                            @Override
                            public void execute() {
                                placeManager.goTo( new DefaultPlaceRequest( "MainPerspective" ) );
                            }
                        } )
                        .endMenu()
                        .build();

        menubar.addMenus( menus );
    }

    @Produces
    @ApplicationScoped
    public MainBrand createBrandLogo() {
        return new MainBrand() {
            @Override
            public Widget asWidget() {
                return new Image( AppResource.INSTANCE.images().ufBrandLogo() );
            }
        };
    }

    //Fade out the "Loading application" pop-up
    private void hideLoadingPopup() {
        final Element e = RootPanel.get( "loading" ).getElement();

        new Animation() {

            @Override
            protected void onUpdate( double progress ) {
                e.getStyle().setOpacity( 1.0 - progress );
            }

            @Override
            protected void onComplete() {
                e.getStyle().setVisibility( Style.Visibility.HIDDEN );
            }
        }.run( 500 );
    }
}