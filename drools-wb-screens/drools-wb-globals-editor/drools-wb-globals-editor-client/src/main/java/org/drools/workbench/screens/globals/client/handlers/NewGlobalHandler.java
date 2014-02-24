package org.drools.workbench.screens.globals.client.handlers;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.screens.globals.client.resources.GlobalsEditorResources;
import org.drools.workbench.screens.globals.client.resources.i18n.GlobalsEditorConstants;
import org.drools.workbench.screens.globals.client.type.GlobalResourceType;
import org.drools.workbench.screens.globals.model.GlobalsModel;
import org.drools.workbench.screens.globals.service.GlobalsEditorService;
import org.guvnor.common.services.project.model.Package;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.widgets.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.workbench.common.widgets.client.handlers.DefaultNewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.client.widget.BusyIndicatorView;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.workbench.type.ResourceTypeDefinition;

/**
 * Handler for the creation of new DRL Text Rules
 */
@ApplicationScoped
public class NewGlobalHandler extends DefaultNewResourceHandler {

    @Inject
    private Caller<GlobalsEditorService> globalsService;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private GlobalResourceType resourceType;

    @Inject
    private BusyIndicatorView busyIndicatorView;

    @Override
    public String getDescription() {
        return GlobalsEditorConstants.INSTANCE.newGlobalDescription();
    }

    @Override
    public IsWidget getIcon() {
        return new Image( GlobalsEditorResources.INSTANCE.images().typeGlobalVariable() );
    }

    @Override
    public ResourceTypeDefinition getResourceType() {
        return resourceType;
    }

    @Override
    public void create( final Package pkg,
                        final String baseFileName,
                        final NewResourcePresenter presenter ) {
        final GlobalsModel model = new GlobalsModel();
        busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Saving() );
        globalsService.call( getSuccessCallback( presenter ),
                             new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).create( pkg.getPackageMainResourcesPath(),
                                                                                                     buildFileName( baseFileName,
                                                                                                                    resourceType ),
                                                                                                     model,
                                                                                                     "" );
    }

}
