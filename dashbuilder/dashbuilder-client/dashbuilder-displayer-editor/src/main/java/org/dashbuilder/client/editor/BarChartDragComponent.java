/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.client.editor;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.dashbuilder.client.editor.resources.i18n.Constants;
import org.dashbuilder.displayer.DisplayerType;
import org.dashbuilder.displayer.client.PerspectiveCoordinator;
import org.dashbuilder.displayer.client.widgets.DisplayerViewer;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.mvp.PlaceManager;

@Dependent
public class BarChartDragComponent extends DisplayerDragComponent {

    @Inject
    public BarChartDragComponent(SyncBeanManager beanManager,
                                 DisplayerViewer viewer,
                                 PlaceManager placeManager,
                                 PerspectiveCoordinator perspectiveCoordinator) {

        super(beanManager, viewer, placeManager, perspectiveCoordinator);
    }

    @Override
    public DisplayerType getDisplayerType() {
        return DisplayerType.BARCHART;
    }

    @Override
    public String getDragComponentIconClass() {
        return "fa fa-bar-chart";
    }

    @Override
    public String getDragComponentTitle() {
        return Constants.INSTANCE.drag_component_name_barchart();
    }
}
