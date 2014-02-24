package org.drools.workbench.screens.workitems.client.handlers;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.screens.workitems.client.resources.WorkItemsEditorResources;
import org.drools.workbench.screens.workitems.client.resources.i18n.WorkItemsEditorConstants;
import org.drools.workbench.screens.workitems.client.type.WorkItemsResourceType;
import org.drools.workbench.screens.workitems.service.WorkItemsEditorService;
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
 * Handler for the creation of new Work Item definitions
 */
@ApplicationScoped
public class NewWorkItemHandler extends DefaultNewResourceHandler {

    @Inject
    private Caller<WorkItemsEditorService> workItemsEditorService;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private WorkItemsResourceType resourceType;

    @Inject
    private BusyIndicatorView busyIndicatorView;

    @Override
    public String getDescription() {
        return WorkItemsEditorConstants.INSTANCE.NewWorkItemDescription();
    }

    @Override
    public IsWidget getIcon() {
        return new Image( WorkItemsEditorResources.INSTANCE.images().typeWorkItem() );
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
        workItemsEditorService.call( getSuccessCallback( presenter ),
                                     new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).create( pkg.getPackageMainResourcesPath(),
                                                                                                             buildFileName( baseFileName,
                                                                                                                            resourceType ),
                                                                                                             "",
                                                                                                             "" );
    }

}
