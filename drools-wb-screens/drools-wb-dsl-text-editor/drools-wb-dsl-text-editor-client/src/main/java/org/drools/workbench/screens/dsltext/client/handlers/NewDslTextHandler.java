package org.drools.workbench.screens.dsltext.client.handlers;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.screens.dsltext.client.resources.DSLTextEditorResources;
import org.drools.workbench.screens.dsltext.client.resources.i18n.DSLTextEditorConstants;
import org.drools.workbench.screens.dsltext.client.resources.images.DSLTextEditorImageResources;
import org.drools.workbench.screens.dsltext.client.type.DSLResourceType;
import org.drools.workbench.screens.dsltext.service.DSLTextEditorService;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.workbench.common.services.shared.context.Package;
import org.kie.workbench.common.widgets.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.workbench.common.widgets.client.handlers.DefaultNewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.client.widget.BusyIndicatorView;
import org.uberfire.client.mvp.PlaceManager;

/**
 * Handler for the creation of new DSL definitions
 */
@ApplicationScoped
public class NewDslTextHandler extends DefaultNewResourceHandler {

    @Inject
    private Caller<DSLTextEditorService> dslTextService;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private DSLResourceType resourceType;

    @Inject
    private BusyIndicatorView busyIndicatorView;

    @Override
    public String getDescription() {
        return DSLTextEditorConstants.INSTANCE.NewDslTextDescription();
    }

    @Override
    public IsWidget getIcon() {
        return new Image( DSLTextEditorResources.INSTANCE.images().DSLIcon() );
    }

    @Override
    public void create( final Package pkg,
                        final String baseFileName,
                        final NewResourcePresenter presenter ) {
        busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Saving() );
        dslTextService.call( getSuccessCallback( presenter ),
                             new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).create( pkg.getPackageMainResourcesPath(),
                                                                                                     buildFileName( resourceType,
                                                                                                                    baseFileName ),
                                                                                                     "",
                                                                                                     "" );
    }

}
