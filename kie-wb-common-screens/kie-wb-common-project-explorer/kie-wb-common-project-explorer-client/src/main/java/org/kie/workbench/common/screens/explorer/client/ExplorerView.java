package org.kie.workbench.common.screens.explorer.client;

import org.kie.workbench.common.screens.explorer.model.ExplorerContent;
import org.uberfire.client.mvp.UberView;

/**
 * Explorer View definition
 */
public interface ExplorerView extends
                              UberView<ExplorerPresenter> {

    void setContent( ExplorerContent content );

}
