package org.drools.workbench.screens.guided.template.client.handlers;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.models.guided.template.shared.TemplateModel;
import org.drools.workbench.screens.guided.template.client.resources.GuidedTemplateEditorResources;
import org.drools.workbench.screens.guided.template.client.resources.i18n.Constants;
import org.drools.workbench.screens.guided.template.client.type.GuidedRuleTemplateResourceType;
import org.drools.workbench.screens.guided.template.service.GuidedRuleTemplateEditorService;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.workbench.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.workbench.widgets.common.client.handlers.DefaultNewResourceHandler;
import org.kie.workbench.widgets.common.client.handlers.NewResourcePresenter;
import org.kie.workbench.widgets.common.client.resources.i18n.CommonConstants;
import org.kie.workbench.widgets.common.client.widget.BusyIndicatorView;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;

/**
 * Handler for the creation of new Guided Templates
 */
@ApplicationScoped
public class NewGuidedRuleTemplateHandler extends DefaultNewResourceHandler {

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Caller<GuidedRuleTemplateEditorService> service;

    @Inject
    private GuidedRuleTemplateResourceType resourceType;

    @Inject
    private BusyIndicatorView busyIndicatorView;

    @Override
    public String getDescription() {
        return Constants.INSTANCE.NewGuidedRuleTemplateDescription();
    }

    @Override
    public IsWidget getIcon() {
        return new Image( GuidedTemplateEditorResources.INSTANCE.images().guidedRuleTemplateIcon() );
    }

    @Override
    public void create( final Path contextPath,
                        final String baseFileName,
                        final NewResourcePresenter presenter ) {
        final TemplateModel templateModel = new TemplateModel();
        templateModel.name = baseFileName;
        busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Saving() );
        service.call( getSuccessCallback( presenter ),
                      new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).create( contextPath,
                                                                                              buildFileName( resourceType,
                                                                                                             baseFileName ),
                                                                                              templateModel,
                                                                                              "" );
    }

}
