package org.drools.workbench.screens.testscenario.client.handlers;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.core.client.Callback;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.drools.workbench.screens.testscenario.client.resources.images.TestScenarioAltedImages;
import org.drools.workbench.screens.testscenario.client.type.TestScenarioResourceType;
import org.drools.workbench.screens.testscenario.service.ScenarioTestEditorService;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.model.Package;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.widgets.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.workbench.common.widgets.client.handlers.DefaultNewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.client.widget.BusyIndicatorView;
import org.uberfire.commons.data.Pair;
import org.uberfire.workbench.type.ResourceTypeDefinition;

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
        return TestScenarioAltedImages.INSTANCE.typeTestScenario();
    }

    @Override
    public ResourceTypeDefinition getResourceType() {
        return resourceType;
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
                                                                                        buildFileName( baseFileName,
                                                                                                       resourceType ),
                                                                                        new Scenario( pkg.getPackageName(),
                                                                                                      baseFileName ),
                                                                                        "" );
    }

    @Override
    public void acceptContext( final ProjectContext context,
                               final Callback<Boolean, Void> callback ) {
        if ( context == null ) {
            callback.onSuccess( false );
        } else {
            final Package pkg = context.getActivePackage();
            boolean accept = ( pkg == null ? false : pkg.getPackageTestResourcesPath() != null );
            callback.onSuccess( accept );
        }
    }
}
