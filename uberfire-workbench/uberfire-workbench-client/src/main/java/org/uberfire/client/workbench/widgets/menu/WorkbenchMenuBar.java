package org.uberfire.client.workbench.widgets.menu;

import org.uberfire.mvp.Command;

/**
 *
 */
public interface WorkbenchMenuBar extends HasMenus {

    void clear();

    void expand();

    void collapse();

    void addCollapseHandler( Command command );

    void addExpandHandler( Command command );
}
