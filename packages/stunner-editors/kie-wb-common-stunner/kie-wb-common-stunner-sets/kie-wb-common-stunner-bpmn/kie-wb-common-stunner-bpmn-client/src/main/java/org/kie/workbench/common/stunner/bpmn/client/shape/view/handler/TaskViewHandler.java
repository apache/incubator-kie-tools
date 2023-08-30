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

import org.kie.workbench.common.stunner.bpmn.definition.BaseTask;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeViewHandler;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGViewUtils;

public class TaskViewHandler implements ShapeViewHandler<BaseTask, SVGShapeView<?>> {

    static final String MULTIPLE_INSTANCE_ICON_PARALLEL = "taskMultipleInstanceIconParallel";
    static final String MULTIPLE_INSTANCE_ICON_SEQUENTIAL = "taskMultipleInstanceIconSequential";

    @Override
    public void handle(BaseTask task, SVGShapeView<?> view) {
        if (task instanceof UserTask) {
            final UserTask userTask = (UserTask) task;
            final boolean multipleInstance = userTask.getExecutionSet().getIsMultipleInstance().getValue();
            final boolean sequential = userTask.getExecutionSet().getMultipleInstanceExecutionMode().isSequential();

            SVGViewUtils.setFillAndStroke(view, MULTIPLE_INSTANCE_ICON_PARALLEL, 0, 0);
            SVGViewUtils.setFillAndStroke(view, MULTIPLE_INSTANCE_ICON_SEQUENTIAL, 0, 0);

            if (multipleInstance) {
                if (sequential) {
                    SVGViewUtils.setFillAndStroke(view, MULTIPLE_INSTANCE_ICON_SEQUENTIAL, 1, 1);
                } else {
                    SVGViewUtils.setFillAndStroke(view, MULTIPLE_INSTANCE_ICON_PARALLEL, 1, 1);
                }
            }
        }
    }
}
