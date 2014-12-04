package org.uberfire.ext.perspective.editor.client.side;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.AccordionGroup;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.perspective.editor.client.api.ExternalPerspectiveEditorComponent;
import org.uberfire.ext.perspective.editor.client.panels.dnd.DragGridElement;
import org.uberfire.ext.perspective.editor.client.util.DragType;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.workbench.events.NotificationEvent;

@ApplicationScoped
@WorkbenchScreen(identifier = "PerspectiveEditorSidePresenter")
public class PerspectiveEditorSidePresenter {

    public interface View extends UberView<PerspectiveEditorSidePresenter> {

        void setupMenu( AccordionGroup... accordionsGroup );
    }

    @Inject
    private View view;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private PerspectiveEditorSidePresenterHelper helper;

    @Inject
    private Instance<ExternalPerspectiveEditorComponent> externalComponents;

    @Inject
    private Event<NotificationEvent> ufNotification;

    @PostConstruct
    public void init() {
    }

    @AfterInitialization
    public void loadContent() {

    }

    @OnOpen
    public void onOpen() {
        AccordionGroup gridSystem = generateGridSystem();
        AccordionGroup components = generateComponent();
        view.setupMenu( gridSystem, components );

    }

    private AccordionGroup generateComponent() {
        AccordionGroup accordion = new AccordionGroup();
        accordion.setHeading( "Components" );
        accordion.setIcon( IconType.FOLDER_OPEN );
        accordion.add( new DragGridElement( DragType.SCREEN, DragType.SCREEN.label(), ufNotification ) );
        accordion.add( new DragGridElement( DragType.HTML, DragType.HTML.label(), ufNotification ) );
        generateExternalComponents( accordion );
        return accordion;
    }

    private void generateExternalComponents( AccordionGroup accordion ) {
        for ( ExternalPerspectiveEditorComponent externalPerspectiveEditorComponent : helper.lookupExternalComponents() ) {
            accordion.add( new DragGridElement( DragType.EXTERNAL, externalPerspectiveEditorComponent.getPlaceName(), externalPerspectiveEditorComponent ) );
        }
    }

    private AccordionGroup generateGridSystem() {
        AccordionGroup accordion = new AccordionGroup();
        accordion.setHeading( "Grid System" );
        accordion.setIcon( IconType.TH );
        accordion.setDefaultOpen( true );
        accordion.add( new DragGridElement( DragType.GRID, "12", ufNotification ) );
        accordion.add( new DragGridElement( DragType.GRID, "6 6", ufNotification ) );
        accordion.add( new DragGridElement( DragType.GRID, "4 4 4", ufNotification ) );
        return accordion;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Perspective Builder";
    }

    @WorkbenchPartView
    public UberView<PerspectiveEditorSidePresenter> getView() {
        return view;
    }

}
