/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.shape.view.handler;

import org.kie.workbench.common.stunner.bpmn.definition.BaseSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeViewHandler;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGViewUtils;

public class SubprocessViewHandler implements ShapeViewHandler<BaseSubprocess, SVGShapeView<?>> {

    private static final String SUB_PROCESS_NORMAL_BOUNDING_BOX = "subProcessReusableNormalBoundingBox";
    private static final String SUB_PROCESS_NORMAL_REUSABLE_ICON = "subProcessReusableNormalReusableIcon";

    private static final String SUB_PROCESS_MI_BOUNDING_BOX = "subProcessReusableMIBoundingBox";
    private static final String SUB_PROCESS_MI_REUSABLE_ICON = "subProcessReusableMIReusableIcon";
    private static final String SUB_PROCESS_MI_MULTIPLE_INSTANCE_ICON = "subProcessReusableMIIMultipleInstanceIcon";

    @Override
    public void handle(BaseSubprocess subprocess, SVGShapeView<?> view) {
        if (subprocess instanceof ReusableSubprocess) {
            final boolean multipleInstance = ((ReusableSubprocess) subprocess).getExecutionSet().getIsMultipleInstance().getValue();
            final double normalFillApha = multipleInstance ? 0 : 1;
            final double normalFillStroke = multipleInstance ? 0 : 1;
            final double miFillAlpha = multipleInstance ? 1 : 0;
            final double miFillStroke = multipleInstance ? 1 : 0;

            SVGViewUtils.getPrimitive(view, SUB_PROCESS_NORMAL_BOUNDING_BOX)
                    .ifPresent(p -> p.get().setFillAlpha(normalFillApha).setStrokeAlpha(normalFillStroke));
            SVGViewUtils.getPrimitive(view, SUB_PROCESS_NORMAL_REUSABLE_ICON)
                    .ifPresent(p -> p.get().setFillAlpha(normalFillApha).setStrokeAlpha(normalFillStroke));

            SVGViewUtils.getPrimitive(view, SUB_PROCESS_MI_BOUNDING_BOX)
                    .ifPresent(p -> p.get().setFillAlpha(miFillAlpha).setStrokeAlpha(miFillStroke));
            SVGViewUtils.getPrimitive(view, SUB_PROCESS_MI_REUSABLE_ICON)
                    .ifPresent(p -> p.get().setFillAlpha(miFillAlpha).setStrokeAlpha(miFillStroke));
            SVGViewUtils.getPrimitive(view, SUB_PROCESS_MI_MULTIPLE_INSTANCE_ICON)
                    .ifPresent(p -> p.get().setFillAlpha(miFillAlpha).setStrokeAlpha(miFillStroke));
        }
    }
}
