package org.drools.workbench.screens.testscenario.client;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.testscenario.service.ScenarioTestEditorService;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.metadata.client.KieEditorView;
import org.uberfire.backend.vfs.ObservablePath;

public interface ScenarioEditorView
        extends IsWidget,
        KieEditorView {

    void setContent( final ObservablePath path,
                     final boolean isReadOnly,
                     final Scenario scenario,
                     final Overview overview,
                     final AsyncPackageDataModelOracle oracle,
                     final Caller<ScenarioTestEditorService> service );

    void showSaveSuccessful();

    void handleNoSuchFileException();

}
