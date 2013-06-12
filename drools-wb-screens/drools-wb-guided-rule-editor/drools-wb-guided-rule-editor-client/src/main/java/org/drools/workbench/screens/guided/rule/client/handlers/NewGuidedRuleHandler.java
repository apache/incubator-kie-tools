package org.drools.workbench.screens.guided.rule.client.handlers;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.models.commons.shared.rule.RuleModel;
import org.drools.workbench.screens.guided.rule.client.resources.GuidedRuleEditorResources;
import org.drools.workbench.screens.guided.rule.client.resources.i18n.Constants;
import org.drools.workbench.screens.guided.rule.client.type.GuidedRuleDRLResourceType;
import org.drools.workbench.screens.guided.rule.client.type.GuidedRuleDSLRResourceType;
import org.drools.workbench.screens.guided.rule.service.GuidedRuleEditorService;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.commons.data.Pair;
import org.kie.workbench.common.services.shared.context.Package;
import org.kie.workbench.common.widgets.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.workbench.common.widgets.client.handlers.DefaultNewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.client.widget.BusyIndicatorView;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.type.ClientResourceType;

/**
 * Handler for the creation of new Guided Rules
 */
@ApplicationScoped
public class NewGuidedRuleHandler extends DefaultNewResourceHandler {

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Caller<GuidedRuleEditorService> service;

    @Inject
    private GuidedRuleDRLResourceType resourceTypeDRL;

    @Inject
    private GuidedRuleDSLRResourceType resourceTypeDSLR;

    @Inject
    private BusyIndicatorView busyIndicatorView;

    private CheckBox useDSLCheckbox = new CheckBox( Constants.INSTANCE.UseDSL() );

    @PostConstruct
    private void setupExtensions() {
        extensions.add( new Pair<String, CheckBox>( Constants.INSTANCE.UseDSL(),
                                                    useDSLCheckbox ) );
    }

    @Override
    public String getDescription() {
        return Constants.INSTANCE.NewGuidedRuleDescription();
    }

    @Override
    public IsWidget getIcon() {
        return new Image( GuidedRuleEditorResources.INSTANCE.images().guidedRuleIcon() );
    }

    @Override
    public void create( final Package pkg,
                        final String baseFileName,
                        final NewResourcePresenter presenter ) {
        final RuleModel ruleModel = new RuleModel();
        final boolean useDSL = useDSLCheckbox.getValue();
        final ClientResourceType resourceType = ( useDSL ? resourceTypeDSLR : resourceTypeDRL );
        ruleModel.name = baseFileName;

        busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Saving() );
        service.call( getSuccessCallback( presenter ),
                      new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).create( pkg.getPackageMainResourcesPath(),
                                                                                              buildFileName( resourceType,
                                                                                                             baseFileName ),
                                                                                              ruleModel,
                                                                                              "" );
    }

}
