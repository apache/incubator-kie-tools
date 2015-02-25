package org.drools.workbench.screens.testscenario.client;

import java.util.Set;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.metadata.client.KieEditorView;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.model.menu.MenuItem;

public interface ScenarioEditorView
        extends IsWidget,
                KieEditorView {

    interface Presenter {

        void onRunScenario();

        void onRedraw();

        void onRunAllScenarios();

    }

    void setPresenter(Presenter presenter);

    MenuItem getRunScenarioMenuItem();

    MenuItem getRunAllScenariosMenuItem();

    void initKSessionSelector(final ObservablePath path,
                              final Scenario scenario);

    void showAuditView(Set<String> log);

    void renderFixtures(Path path, AsyncPackageDataModelOracle oracle, Scenario scenario);

}
