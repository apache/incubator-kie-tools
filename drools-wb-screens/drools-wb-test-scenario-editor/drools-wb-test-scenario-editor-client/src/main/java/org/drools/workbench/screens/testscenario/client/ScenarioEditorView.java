package org.drools.workbench.screens.testscenario.client;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.testscenario.service.ScenarioTestEditorService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.common.client.api.Caller;
import org.kie.uberfire.client.common.HasBusyIndicator;
import org.kie.workbench.common.services.shared.rulename.RuleNamesService;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.editor.GuvnorEditorView;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;

public interface ScenarioEditorView
        extends IsWidget,
                GuvnorEditorView {

    void setContent( final ObservablePath path,
                     final boolean isReadOnly,
                     final Scenario scenario,
                     final Overview overview,
                     final AsyncPackageDataModelOracle oracle,
                     final Caller<ScenarioTestEditorService> service );

    void showSaveSuccessful();

    String getTitle( final String fileName,
                     final String version );

    void handleNoSuchFileException();

}
