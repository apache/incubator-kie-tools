package org.kie.workbench.common.widgets.client.menu;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.workbench.common.widgets.client.resources.i18n.ToolsMenuConstants;
import org.kie.guvnor.project.service.ProjectService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.Command;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.events.PathChangeEvent;
import org.uberfire.client.workbench.widgets.menu.MenuFactory;
import org.uberfire.client.workbench.widgets.menu.MenuItem;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ToolsMenu {


    @Inject
    private PlaceManager placeManager;

    @Inject
    protected Caller<ProjectService> projectService;

    private MenuItem projectScreen = MenuFactory.newSimpleItem(ToolsMenuConstants.INSTANCE.ProjectEditor()).respondsWith(
            new Command() {
                @Override
                public void execute() {
                    placeManager.goTo("projectScreen");
                }
            }).endMenu().build().getItems().get(0);

    private MenuItem dataModelerScreen = MenuFactory.newSimpleItem("Data Modeller").respondsWith(
            new Command() {
                @Override
                public void execute() {
                    placeManager.goTo("dataModelerScreen");
                }
            }).endMenu().build().getItems().get(0);

    public List<MenuItem> getMenuItems() {
        ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();

        menuItems.add(projectScreen);
        menuItems.add(dataModelerScreen);

        return menuItems;
    }

    public void selectedPathChanged(@Observes final PathChangeEvent event) {
        projectService.call(new RemoteCallback<Path>() {
            @Override
            public void callback(Path path) {
                projectScreen.setEnabled(path != null);
                dataModelerScreen.setEnabled(path != null);
            }
        }).resolveProject(event.getPath());
    }
}
