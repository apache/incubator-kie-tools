package org.kie.workbench.common.widgets.client.menu;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.common.services.shared.file.CopyService;
import org.guvnor.common.services.shared.file.DeleteService;
import org.guvnor.common.services.shared.file.RenameService;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.widgets.client.resources.i18n.ToolsMenuConstants;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;

@ApplicationScoped
public class RepositoryMenu {

    @Inject
    private PlaceManager placeManager;

    @Inject
    protected Caller<ProjectService> projectService;

    @Inject
    protected Caller<RenameService> renameService;

    @Inject
    protected Caller<DeleteService> deleteService;

    @Inject
    protected Caller<CopyService> copyService;

    @Inject
    protected ProjectContext context;

    private MenuItem categoriesEditor = MenuFactory.newSimpleItem(ToolsMenuConstants.INSTANCE.CategoriesEditor()).respondsWith(
            new Command() {
                @Override
                public void execute() {
                    placeManager.goTo("CategoryManager");
                }
            }).endMenu().build().getItems().get(0);

    public List<MenuItem> getMenuItems() {
        ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();

        menuItems.add(categoriesEditor);

        return menuItems;
    }

}
