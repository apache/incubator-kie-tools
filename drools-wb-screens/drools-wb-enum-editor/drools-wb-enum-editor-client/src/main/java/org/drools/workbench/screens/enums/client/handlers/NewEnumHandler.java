package org.drools.workbench.screens.enums.client.handlers;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.screens.enums.client.resources.EnumEditorResources;
import org.drools.workbench.screens.enums.client.resources.i18n.EnumEditorConstants;
import org.drools.workbench.screens.enums.client.type.EnumResourceType;
import org.drools.workbench.screens.enums.service.EnumService;
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
 * Handler for the creation of new Enumerations
 */
@ApplicationScoped
public class NewEnumHandler extends DefaultNewResourceHandler {

    @Inject
    private Caller<EnumService> enumService;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private EnumResourceType resourceType;

    @Inject
    private BusyIndicatorView busyIndicatorView;

    @Override
    public String getDescription() {
        return EnumEditorConstants.INSTANCE.newEnumDescription();
    }

    @Override
    public IsWidget getIcon() {
        return new Image( EnumEditorResources.INSTANCE.images().typeEnumeration() );
    }

    @Override
    public ResourceTypeDefinition getResourceType() {
        return resourceType;
    }

    @Override
    public void create( final Package pkg,
                        final String baseFileName,
                        final NewResourcePresenter presenter ) {
        busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Saving() );
        enumService.call( getSuccessCallback( presenter ),
                          new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).create( pkg.getPackageMainResourcesPath(),
                                                                                                  buildFileName( baseFileName,
                                                                                                                 resourceType ),
                                                                                                  "",
                                                                                                  "" );
    }

}
