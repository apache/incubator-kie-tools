package org.drools.workbench.screens.guided.template.client.handlers;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.models.guided.template.shared.TemplateModel;
import org.drools.workbench.screens.guided.template.client.resources.GuidedTemplateEditorResources;
import org.drools.workbench.screens.guided.template.client.resources.i18n.GuidedTemplateEditorConstants;
import org.drools.workbench.screens.guided.template.client.type.GuidedRuleTemplateResourceType;
import org.drools.workbench.screens.guided.template.service.GuidedRuleTemplateEditorService;
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
        return GuidedTemplateEditorConstants.INSTANCE.NewGuidedRuleTemplateDescription();
    }

    @Override
    public IsWidget getIcon() {
        return new Image( GuidedTemplateEditorResources.INSTANCE.images().typeGuidedRuleTemplate() );
    }

    @Override
    public ResourceTypeDefinition getResourceType() {
        return resourceType;
    }

    @Override
    public void create( final Package pkg,
                        final String baseFileName,
                        final NewResourcePresenter presenter ) {
        final TemplateModel templateModel = new TemplateModel();
        templateModel.name = baseFileName;
        busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Saving() );
        service.call( getSuccessCallback( presenter ),
                      new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).create( pkg.getPackageMainResourcesPath(),
                                                                                              buildFileName( baseFileName,
                                                                                                             resourceType ),
                                                                                              templateModel,
                                                                                              "" );
    }

}
