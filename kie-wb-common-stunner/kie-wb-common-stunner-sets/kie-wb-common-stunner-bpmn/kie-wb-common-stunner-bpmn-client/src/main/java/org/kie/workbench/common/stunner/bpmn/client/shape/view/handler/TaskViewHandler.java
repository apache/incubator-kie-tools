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

import org.kie.workbench.common.stunner.bpmn.definition.BaseTask;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeViewHandler;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGViewUtils;

public class TaskViewHandler implements ShapeViewHandler<BaseTask, SVGShapeView<?>> {

    private static final String MULTIPLE_INSTANCE_ICON = "taskMultipleInstanceIcon";

    @Override
    public void handle(BaseTask task, SVGShapeView<?> view) {
        if (task instanceof UserTask) {
            final boolean multipleInstance = ((UserTask) task).getExecutionSet().getIsMultipleInstance().getValue();
            final double fillApha = multipleInstance ? 1 : 0;
            final double fillStroke = multipleInstance ? 1 : 0;
            SVGViewUtils.getPrimitive(view, MULTIPLE_INSTANCE_ICON)
                    .ifPresent(p -> p.get().setFillAlpha(fillApha).setStrokeAlpha(fillStroke));
        }
    }
}
