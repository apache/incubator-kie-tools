package org.drools.workbench.screens.guided.dtable.client.handlers;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.resources.GuidedDecisionTableResources;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.drools.workbench.screens.guided.dtable.client.type.GuidedDTableResourceType;
import org.drools.workbench.screens.guided.dtable.client.wizard.NewGuidedDecisionTableAssetWizardContext;
import org.drools.workbench.screens.guided.dtable.client.wizard.NewGuidedDecisionTableWizard;
import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableEditorService;
import org.guvnor.common.services.project.model.Package;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.kie.workbench.common.widgets.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleFactory;
import org.kie.workbench.common.widgets.client.handlers.DefaultNewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.client.widget.BusyIndicatorView;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.wizards.WizardPresenter;
import org.uberfire.commons.data.Pair;
import org.uberfire.workbench.type.ResourceTypeDefinition;

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
    private GuidedDTableResourceType resourceType;

    @Inject
    private GuidedDecisionTableOptions options;

    @Inject
    private WizardPresenter wizardPresenter;

    @Inject
    private NewGuidedDecisionTableWizard wizard;

    @Inject
    private BusyIndicatorView busyIndicatorView;

    @Inject
    private AsyncPackageDataModelOracleFactory oracleFactory;

    private AsyncPackageDataModelOracle oracle;

    private NewResourcePresenter newResourcePresenter;

    @PostConstruct
    private void setupExtensions() {
        extensions.add( new Pair<String, GuidedDecisionTableOptions>( GuidedDecisionTableConstants.INSTANCE.Options(),
                                                                      options ) );
    }

    @Override
    public String getDescription() {
        return GuidedDecisionTableConstants.INSTANCE.NewGuidedDecisionTableDescription();
    }

    @Override
    public IsWidget getIcon() {
        return new Image( GuidedDecisionTableResources.INSTANCE.images().typeGuidedDecisionTable() );
    }

    @Override
    public ResourceTypeDefinition getResourceType() {
        return resourceType;
    }

    @Override
    public void create( final Package pkg,
                        final String baseFileName,
                        final NewResourcePresenter presenter ) {
        this.newResourcePresenter = presenter;
        if ( !options.isUsingWizard() ) {
            createEmptyDecisionTable( baseFileName,
                                      pkg.getPackageMainResourcesPath(),
                                      options.getTableFormat() );
        } else {
            createDecisionTableWithWizard( baseFileName,
                                           pkg.getPackageMainResourcesPath(),
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
        service.call( new RemoteCallback<PackageDataModelOracleBaselinePayload>() {

            @Override
            public void callback( final PackageDataModelOracleBaselinePayload dataModel ) {
                newResourcePresenter.complete();
                oracle = oracleFactory.makeAsyncPackageDataModelOracle( contextPath,
                                                                        dataModel );
                final NewGuidedDecisionTableAssetWizardContext context = new NewGuidedDecisionTableAssetWizardContext( baseFileName,
                                                                                                                       contextPath,
                                                                                                                       tableFormat );
                wizard.setContent( context,
                                   oracle,
                                   NewGuidedDecisionTableHandler.this );
                wizardPresenter.start( wizard );
            }
        } ).loadDataModel( contextPath );
    }

    public void save( final String baseFileName,
                      final Path contextPath,
                      final GuidedDecisionTable52 model,
                      final Command postSaveCommand ) {
        oracleFactory.destroy( oracle );
        busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Saving() );
        service.call( getSuccessCallback( newResourcePresenter,
                                          postSaveCommand ),
                      new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).create( contextPath,
                                                                                              buildFileName( baseFileName,
                                                                                                             resourceType ),
                                                                                              model,
                                                                                              "" );
    }

}
