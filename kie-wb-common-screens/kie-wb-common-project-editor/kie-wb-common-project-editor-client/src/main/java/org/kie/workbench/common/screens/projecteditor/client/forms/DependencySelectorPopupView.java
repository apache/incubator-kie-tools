package org.kie.workbench.common.screens.projecteditor.client.forms;

import org.uberfire.client.mvp.UberView;

public interface DependencySelectorPopupView extends UberView<DependencySelectorPresenter> {

    void show();

    void hide();
}
