/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.client.workbench.panels.impl;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;

/**
 * Exactly like {@link SimpleWorkbenchPanelPresenter} but has drag and drop enabled by default.
 */
@Dependent
public class SimpleDnDWorkbenchPanelPresenter extends AbstractDockingWorkbenchPanelPresenter<SimpleDnDWorkbenchPanelPresenter> {

    private PlaceManager placeManager;

    @Inject
    public SimpleDnDWorkbenchPanelPresenter(final SimpleDnDWorkbenchPanelView view,
                                            final PerspectiveManager perspectiveManager,
                                            final PlaceManager placeManager) {
        super(view,
              perspectiveManager);
        this.placeManager = placeManager;
        view.enableDnd();
    }

    @Override
    protected SimpleDnDWorkbenchPanelPresenter asPresenterType() {
        return this;
    }

    @Override
    public void addPart(WorkbenchPartPresenter part) {
        SinglePartPanelHelper h = createSinglePartPanelHelper();
        if (h.hasNoParts()) {
            super.addPart(part);
        } else {
            h.closeFirstPartAndAddNewOne(() -> super.addPart(part));
        }
    }

    @Override
    public void addPart(WorkbenchPartPresenter part,
                        String contextId) {
        SinglePartPanelHelper h = createSinglePartPanelHelper();
        if (h.hasNoParts()) {
            super.addPart(part,
                          contextId);
        } else {
            h.closeFirstPartAndAddNewOne(() -> super.addPart(part,
                                                             contextId));
        }
    }

    SinglePartPanelHelper createSinglePartPanelHelper() {
        return new SinglePartPanelHelper(getPanelView().getParts(),
                                         placeManager);
    }
}
