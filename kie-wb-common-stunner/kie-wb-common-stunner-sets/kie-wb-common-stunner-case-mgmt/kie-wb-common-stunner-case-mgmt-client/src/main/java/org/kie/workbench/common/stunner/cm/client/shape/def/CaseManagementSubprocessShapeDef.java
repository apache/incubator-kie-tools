/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.cm.client.shape.def;

import java.util.Optional;
import java.util.function.BiConsumer;

import org.kie.workbench.common.stunner.bpmn.definition.BaseSubprocess;
import org.kie.workbench.common.stunner.cm.client.shape.view.StageView;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeDef;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public final class CaseManagementSubprocessShapeDef
        implements StageShapeDef<BaseSubprocess, StageView> {

    public static final double DROP_AREA_HEIGHT = 508d;
    public static final double WIDTH = 136d;
    public static final double HEIGHT = 48d;

    @Override
    public Optional<BiConsumer<View<BaseSubprocess>, StageView>> sizeHandler() {
        return Optional.of(newSizeHandlerBuilder()
                                   .width(this::getWidth)
                                   .height(this::getHeight)
                                   .build()::handle);
    }

    @Override
    public double getDropAreaWidth(BaseSubprocess element) {
        return WIDTH;
    }

    @Override
    public double getDropAreaHeight(BaseSubprocess element) {
        return DROP_AREA_HEIGHT;
    }

    @Override
    public double getWidth(BaseSubprocess element) {
        return WIDTH;
    }

    @Override
    public double getHeight(BaseSubprocess element) {
        return HEIGHT;
    }

    @Override
    public double getVOffset(final BaseSubprocess element) {
        return 20.0;
    }

    @Override
    public Class<? extends ShapeDef> getType() {
        return CaseManagementSubprocessShapeDef.class;
    }
}
