package org.kie.uberfire.plugin.client.editor;

import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

public class PluginsCommonMenu {

    public Menus build( final Command saveCommand,
                        Command deleteCommand ) {
        return MenuFactory.newTopLevelMenu( "Save" )
                .respondsWith( saveCommand )
                .endMenu()
                .newTopLevelMenu( "Delete" )
                .respondsWith( deleteCommand )
                .endMenu().build();
    }
}
