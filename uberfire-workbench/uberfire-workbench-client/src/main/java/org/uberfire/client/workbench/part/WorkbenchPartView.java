package org.uberfire.client.workbench.part;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import org.uberfire.client.mvp.UberView;

public interface WorkbenchPartView extends UberView<WorkbenchPartPresenter>,
                                           RequiresResize {

    WorkbenchPartPresenter getPresenter();

    void setWrappedWidget(IsWidget widget);
}
