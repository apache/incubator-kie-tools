package org.kie.workbench.screens.explorer.client;

import org.kie.workbench.screens.explorer.model.ExplorerContent;
import org.uberfire.client.mvp.UberView;

/**
 * Explorer View definition
 */
public interface ExplorerView extends
                              UberView<ExplorerPresenter> {

    void setContent( ExplorerContent content );

}
