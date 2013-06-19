package org.drools.workbench.screens.testscenario.client.handler;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.core.client.Callback;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.testscenario.client.TestScenarioResourceType;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.drools.workbench.screens.testscenario.client.resources.images.TestScenarioAltedImages;
import org.drools.workbench.screens.testscenario.service.ScenarioTestEditorService;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.commons.data.Pair;
import org.kie.workbench.common.services.shared.context.Package;
import org.kie.workbench.common.widgets.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.workbench.common.widgets.client.handlers.DefaultNewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.client.widget.BusyIndicatorView;
import org.uberfire.backend.vfs.Path;

@ApplicationScoped
public class NewTestScenarioHandler
        extends DefaultNewResourceHandler {

    @Inject
    private BusyIndicatorView busyIndicatorView;

    @Inject
    private Caller<ScenarioTestEditorService> service;

    @Inject
    private TestScenarioResourceType resourceType;

    @Override
    public String getDescription() {
        return TestScenarioConstants.INSTANCE.NewTestScenarioDescription();
    }

    @Override
    public IsWidget getIcon() {
        return TestScenarioAltedImages.INSTANCE.TestScenario();
    }

    @Override
    public List<Pair<String, ? extends IsWidget>> getExtensions() {
        final Package activePackage = context.getActivePackage();
        this.pathLabel.setPath( ( activePackage == null ? null : activePackage.getPackageTestResourcesPath() ) );
        return this.extensions;
    }

    @Override
    public void create( final Package pkg,
                        final String baseFileName,
                        final NewResourcePresenter presenter ) {
        busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Saving() );

        service.call(
                getSuccessCallback( presenter ),
                new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).create( pkg.getPackageTestResourcesPath(),
                                                                                        buildFileName( resourceType,
                                                                                                       baseFileName ),
                                                                                        new Scenario(),
                                                                                        "" );
    }

    @Override
    public void acceptPath( final Path path,
                            final Callback<Boolean, Void> callback ) {
        if ( path == null ) {
            callback.onSuccess( false );
        } else {
            projectService.call( new RemoteCallback<Package>() {
                @Override
                public void callback( final Package pkg ) {
                    boolean accept = ( pkg == null ? false : pkg.getPackageTestResourcesPath() != null );
                    callback.onSuccess( accept );
                }
            } ).resolvePackage( path );
        }
    }
}
