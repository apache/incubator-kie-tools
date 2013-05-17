package org.drools.workbench.screens.guided.dtable.client.handlers;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.resources.Resources;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.Constants;
import org.drools.workbench.screens.guided.dtable.client.type.GuidedDTableResourceType;
import org.drools.workbench.screens.guided.dtable.client.wizard.NewGuidedDecisionTableAssetWizardContext;
import org.drools.workbench.screens.guided.dtable.client.wizard.NewGuidedDecisionTableWizard;
import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableEditorService;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.commons.data.Pair;
import org.kie.workbench.common.widgets.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.workbench.common.widgets.client.handlers.DefaultNewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.client.widget.BusyIndicatorView;
import org.kie.workbench.common.services.datamodel.oracle.PackageDataModelOracle;
import org.kie.workbench.common.services.datamodel.service.DataModelService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.wizards.WizardPresenter;

/**
 * Handler for the creation of new Guided Decision Tables
 */
@ApplicationScoped
public class NewGuidedDecisionTableHandler extends DefaultNewResourceHandler {

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Caller<GuidedDecisionTableEditorService> service;

    @Inject
    private Caller<DataModelService> dmoService;

    @Inject
    private GuidedDTableResourceType resourceType;

    @Inject
    private GuidedDecisionTableOptions options;

    @Inject
    private WizardPresenter wizardPresenter;

    @Inject
    private NewGuidedDecisionTableWizard wizard;

    @Inject
    private BusyIndicatorView busyIndicatorView;

    private NewResourcePresenter newResourcePresenter;

    @PostConstruct
    private void setupExtensions() {
        extensions.add( new Pair<String, GuidedDecisionTableOptions>( Constants.INSTANCE.Options(),
                                                                      options ) );
    }

    @Override
    public String getDescription() {
        return Constants.INSTANCE.NewGuidedDecisionTableDescription();
    }

    @Override
    public IsWidget getIcon() {
        return new Image( Resources.INSTANCE.images().guidedDecisionTableIcon() );
    }

    @Override
    public void create( final Path contextPath,
                        final String baseFileName,
                        final NewResourcePresenter presenter ) {
        this.newResourcePresenter = presenter;
        if ( !options.isUsingWizard() ) {
            createEmptyDecisionTable( baseFileName,
                                      contextPath,
                                      options.getTableFormat() );
        } else {
            createDecisionTableWithWizard( baseFileName,
                                           contextPath,
                                           options.getTableFormat() );
        }
    }

    private void createEmptyDecisionTable( final String baseFileName,
                                           final Path contextPath,
                                           final GuidedDecisionTable52.TableFormat tableFormat ) {
        final GuidedDecisionTable52 model = new GuidedDecisionTable52();
        model.setTableFormat( tableFormat );
        model.setTableName( baseFileName );
        save( baseFileName,
              contextPath,
              model,
              null );
    }

    private void createDecisionTableWithWizard( final String baseFileName,
                                                final Path contextPath,
                                                final GuidedDecisionTable52.TableFormat tableFormat ) {
        dmoService.call( new RemoteCallback<PackageDataModelOracle>() {

            @Override
            public void callback( final PackageDataModelOracle oracle ) {
                newResourcePresenter.complete();
                final NewGuidedDecisionTableAssetWizardContext context = new NewGuidedDecisionTableAssetWizardContext( baseFileName,
                                                                                                                       contextPath,
                                                                                                                       tableFormat );
                wizard.setContent( context,
                                   oracle,
                                   NewGuidedDecisionTableHandler.this );
                wizardPresenter.start( wizard );
            }
        } ).getDataModel( contextPath );

    }

    public void save( final String baseFileName,
                      final Path contextPath,
                      final GuidedDecisionTable52 model,
                      final Command postSaveCommand ) {
        busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Saving() );
        service.call( getSuccessCallback( newResourcePresenter,
                                          postSaveCommand ),
                      new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).create( contextPath,
                                                                                              buildFileName( resourceType,
                                                                                                             baseFileName ),
                                                                                              model,
                                                                                              "" );
    }

}
