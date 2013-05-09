package org.kie.guvnor.workitems.client.handlers;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.commons.ui.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.guvnor.commons.ui.client.handlers.DefaultNewResourceHandler;
import org.kie.guvnor.commons.ui.client.handlers.NewResourcePresenter;
import org.kie.guvnor.commons.ui.client.resources.i18n.CommonConstants;
import org.kie.guvnor.commons.ui.client.widget.BusyIndicatorView;
import org.kie.guvnor.workitems.client.resources.i18n.WorkItemsEditorConstants;
import org.kie.guvnor.workitems.client.resources.images.WorkItemsEditorImageResources;
import org.kie.guvnor.workitems.client.type.WorkItemsResourceType;
import org.kie.guvnor.workitems.service.WorkItemsEditorService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;

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
        return new Image( WorkItemsEditorImageResources.INSTANCE.workitemImage() );
    }

    @Override
    public void create( final Path contextPath,
                        final String baseFileName,
                        final NewResourcePresenter presenter ) {
        busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Saving() );
        workItemsEditorService.call( getSuccessCallback( presenter ),
                                     new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).create( contextPath,
                                                                                                             buildFileName( resourceType,
                                                                                                                            baseFileName ),
                                                                                                             "",
                                                                                                             "" );
    }

}
