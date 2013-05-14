package org.drools.workbench.screens.factmodel.client.handlers;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.screens.factmodel.client.resources.i18n.Constants;
import org.drools.workbench.screens.factmodel.client.resources.images.ImageResources;
import org.drools.workbench.screens.factmodel.client.type.FactModelResourceType;
import org.drools.workbench.screens.factmodel.model.FactModels;
import org.drools.workbench.screens.factmodel.service.FactModelService;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.workbench.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.workbench.widgets.common.client.handlers.DefaultNewResourceHandler;
import org.kie.workbench.widgets.common.client.handlers.NewResourcePresenter;
import org.kie.workbench.widgets.common.client.resources.i18n.CommonConstants;
import org.kie.workbench.widgets.common.client.widget.BusyIndicatorView;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;

/**
 * Handler for the creation of new Fact Models
 */
@ApplicationScoped
public class NewFactModelHandler extends DefaultNewResourceHandler {

    @Inject
    private Caller<FactModelService> factModelService;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private FactModelResourceType resourceType;

    @Inject
    private BusyIndicatorView busyIndicatorView;

    @Override
    public String getDescription() {
        return Constants.INSTANCE.newFactModelDescription();
    }

    @Override
    public IsWidget getIcon() {
        return new Image( ImageResources.INSTANCE.factModelIcon() );
    }

    @Override
    public void create( final Path contextPath,
                        final String baseFileName,
                        final NewResourcePresenter presenter ) {
        final FactModels factModel = new FactModels();
        busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Saving() );
        factModelService.call( getSuccessCallback( presenter ),
                               new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).create( contextPath,
                                                                                                       buildFileName( resourceType,
                                                                                                                      baseFileName ),
                                                                                                       factModel,
                                                                                                       "" );
    }

}
