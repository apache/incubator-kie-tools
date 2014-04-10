package org.drools.workbench.screens.testscenario.client;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.testscenario.service.ScenarioTestEditorService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.kie.workbench.common.services.shared.rulename.RuleNamesService;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.widget.HasBusyIndicator;
import org.uberfire.backend.vfs.Path;

public interface ScenarioEditorView
        extends IsWidget,
                HasBusyIndicator {

    void showCanNotSaveReadOnly();

    void setContent( final Path path,
                     final boolean isReadOnly,
                     final Scenario scenario,
                     final AsyncPackageDataModelOracle oracle,
                     final Caller<RuleNamesService> ruleNameService,
                     final Caller<ScenarioTestEditorService> service );

    void showSaveSuccessful();

    String getTitle( final String fileName,
                     final String version );

    Metadata getMetadata();

    void resetMetadataDirty();

    void handleNoSuchFileException();

}
