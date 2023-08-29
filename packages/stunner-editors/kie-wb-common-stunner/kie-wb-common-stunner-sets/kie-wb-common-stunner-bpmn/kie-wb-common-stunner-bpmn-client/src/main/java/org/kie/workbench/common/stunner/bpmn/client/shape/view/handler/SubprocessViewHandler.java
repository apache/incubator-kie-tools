/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.bpmn.client.shape.view.handler;

import org.kie.workbench.common.stunner.bpmn.definition.BaseSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.MultipleInstanceSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeViewHandler;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGViewUtils;

public class SubprocessViewHandler implements ShapeViewHandler<BaseSubprocess, SVGShapeView<?>> {

    static final String REUSABLE_SUB_PROCESS_NORMAL_BOUNDING_BOX = "subProcessReusableNormalBoundingBox";
    static final String REUSABLE_SUB_PROCESS_NORMAL_REUSABLE_ICON = "subProcessReusableNormalReusableIcon";
    static final String REUSABLE_SUB_PROCESS_MI_BOUNDING_BOX = "subProcessReusableMIBoundingBox";
    static final String REUSABLE_SUB_PROCESS_MI_REUSABLE_ICON = "subProcessReusableMIReusableIcon";
    static final String REUSABLE_SUB_PROCESS_MI_MULTIPLE_INSTANCE_ICON_PARALLEL = "subProcessReusableMIIMultipleInstanceIconParallel";
    static final String REUSABLE_SUB_PROCESS_MI_MULTIPLE_INSTANCE_ICON_SEQUENTIAL = "subProcessReusableMIIMultipleInstanceIconSequential";

    static final String MULTIPLE_INSTANCE_SUB_PROCESS_ICON_PARALLEL = "multipleInstanceIconParallel";
    static final String MULTIPLE_INSTANCE_SUB_PROCESS_ICON_SEQUENTIAL = "multipleInstanceIconSequential";

    @Override
    public void handle(BaseSubprocess subprocess, SVGShapeView<?> view) {
        if (subprocess instanceof ReusableSubprocess) {
            final ReusableSubprocess reusableSubprocess = (ReusableSubprocess) subprocess;
            final boolean multipleInstance = reusableSubprocess.getExecutionSet().getIsMultipleInstance().getValue();
            final boolean sequential = reusableSubprocess.getExecutionSet().getMultipleInstanceExecutionMode().isSequential();
            final double normalFillApha = multipleInstance ? 0 : 1;
            final double normalFillStroke = multipleInstance ? 0 : 1;
            final double miFillAlpha = multipleInstance ? 1 : 0;
            final double miFillStroke = multipleInstance ? 1 : 0;

            SVGViewUtils.setFillAndStroke(view, REUSABLE_SUB_PROCESS_NORMAL_BOUNDING_BOX, normalFillApha, normalFillStroke);
            SVGViewUtils.setFillAndStroke(view, REUSABLE_SUB_PROCESS_NORMAL_REUSABLE_ICON, normalFillApha, normalFillStroke);

            SVGViewUtils.setFillAndStroke(view, REUSABLE_SUB_PROCESS_MI_BOUNDING_BOX, miFillAlpha, miFillStroke);
            SVGViewUtils.setFillAndStroke(view, REUSABLE_SUB_PROCESS_MI_REUSABLE_ICON, miFillAlpha, miFillStroke);

            SVGViewUtils.setFillAndStroke(view, REUSABLE_SUB_PROCESS_MI_MULTIPLE_INSTANCE_ICON_PARALLEL, 0, 0);
            SVGViewUtils.setFillAndStroke(view, REUSABLE_SUB_PROCESS_MI_MULTIPLE_INSTANCE_ICON_SEQUENTIAL, 0, 0);

            if (multipleInstance) {
                if (sequential) {
                    SVGViewUtils.setFillAndStroke(view, REUSABLE_SUB_PROCESS_MI_MULTIPLE_INSTANCE_ICON_SEQUENTIAL, 1, 1);
                } else {
                    SVGViewUtils.setFillAndStroke(view, REUSABLE_SUB_PROCESS_MI_MULTIPLE_INSTANCE_ICON_PARALLEL, 1, 1);
                }
            }
        } else if (subprocess instanceof MultipleInstanceSubprocess) {
            final boolean sequential = ((MultipleInstanceSubprocess) subprocess).getExecutionSet().getMultipleInstanceExecutionMode().isSequential();
            if (sequential) {
                SVGViewUtils.setFillAndStroke(view, MULTIPLE_INSTANCE_SUB_PROCESS_ICON_SEQUENTIAL, 1, 1);
                SVGViewUtils.setFillAndStroke(view, MULTIPLE_INSTANCE_SUB_PROCESS_ICON_PARALLEL, 0, 0);
            } else {
                SVGViewUtils.setFillAndStroke(view, MULTIPLE_INSTANCE_SUB_PROCESS_ICON_PARALLEL, 1, 1);
                SVGViewUtils.setFillAndStroke(view, MULTIPLE_INSTANCE_SUB_PROCESS_ICON_SEQUENTIAL, 0, 0);
            }
        }
    }
}
