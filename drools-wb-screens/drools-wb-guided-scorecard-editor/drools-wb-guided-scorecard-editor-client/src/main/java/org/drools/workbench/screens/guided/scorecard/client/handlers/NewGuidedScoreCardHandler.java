package org.drools.workbench.screens.guided.scorecard.client.handlers;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.models.guided.scorecard.shared.ScoreCardModel;
import org.drools.workbench.screens.guided.scorecard.client.resources.i18n.Constants;
import org.drools.workbench.screens.guided.scorecard.client.resources.images.ImageResources;
import org.drools.workbench.screens.guided.scorecard.client.type.GuidedScoreCardResourceType;
import org.drools.workbench.screens.guided.scorecard.service.GuidedScoreCardEditorService;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.workbench.common.widgets.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.workbench.common.widgets.client.handlers.DefaultNewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.client.widget.BusyIndicatorView;
import org.uberfire.backend.vfs.Path;

/**
 * Handler for the creation of new Guided Score Cards
 */
@ApplicationScoped
public class NewGuidedScoreCardHandler extends DefaultNewResourceHandler {

    @Inject
    private Caller<GuidedScoreCardEditorService> scoreCardService;

    @Inject
    private GuidedScoreCardResourceType resourceType;

    @Inject
    private BusyIndicatorView busyIndicatorView;

    @Override
    public String getDescription() {
        return Constants.INSTANCE.newGuidedScoreCardDescription();
    }

    @Override
    public IsWidget getIcon() {
        return new Image( ImageResources.INSTANCE.scoreCardIcon() );
    }

    @Override
    public void create( final Path contextPath,
                        final String baseFileName,
                        final NewResourcePresenter presenter ) {
        final ScoreCardModel model = new ScoreCardModel();
        model.setName( baseFileName );
        busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Saving() );
        scoreCardService.call( getSuccessCallback( presenter ),
                               new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).create( contextPath,
                                                                                                       buildFileName( resourceType,
                                                                                                                      baseFileName ),
                                                                                                       model,
                                                                                                       "" );
    }

}
