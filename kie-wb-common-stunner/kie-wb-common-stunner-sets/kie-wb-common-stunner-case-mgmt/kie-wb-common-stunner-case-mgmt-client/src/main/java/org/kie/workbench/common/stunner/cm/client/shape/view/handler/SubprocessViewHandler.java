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

package org.kie.workbench.common.stunner.cm.client.shape.view.handler;

import org.kie.workbench.common.stunner.cm.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeViewHandler;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGViewUtils;

public class SubprocessViewHandler implements ShapeViewHandler<ReusableSubprocess, SVGShapeView<?>> {

    static final String MULTIPLE_INSTANCE_SUBPROCESS_ICON_PARALLEL = "subprocessReusableMIIMultipleInstanceIconParallel";
    static final String MULTIPLE_INSTANCE_SUBPROCESS_ICON_SEQUENTIAL = "subprocessReusableMIIMultipleInstanceIconSequential";

    static final String MULTIPLE_INSTANCE_SUBCASE_ICON_PARALLEL = "subcaseReusableMIIMultipleInstanceIconParallel";
    static final String MULTIPLE_INSTANCE_SUBCASE_ICON_SEQUENTIAL = "subcaseReusableMIIMultipleInstanceIconSequential";

    @Override
    public void handle(ReusableSubprocess reusableSubprocess, SVGShapeView<?> view) {
        final boolean multipleInstance = reusableSubprocess.getExecutionSet().getIsMultipleInstance().getValue();
        final boolean sequential = reusableSubprocess.getExecutionSet().getMultipleInstanceExecutionMode().isSequential();
        final boolean isCase = reusableSubprocess.getExecutionSet().getIsCase().getValue();

        setFillAndStroke(view, multipleInstance, sequential,
                         isCase ? MULTIPLE_INSTANCE_SUBCASE_ICON_PARALLEL : MULTIPLE_INSTANCE_SUBPROCESS_ICON_PARALLEL,
                         isCase ? MULTIPLE_INSTANCE_SUBCASE_ICON_SEQUENTIAL : MULTIPLE_INSTANCE_SUBPROCESS_ICON_SEQUENTIAL);
    }

    private void setFillAndStroke(final SVGShapeView<?> view, final boolean isMultipleInstance, final boolean isSequential,
                                  final String parallelId, String sequentialId) {
        SVGViewUtils.setFillAndStroke(view, parallelId, 0, 0);
        SVGViewUtils.setFillAndStroke(view, sequentialId, 0, 0);

        if (isMultipleInstance) {
            if (isSequential) {
                SVGViewUtils.setFillAndStroke(view, sequentialId, 1, 1);
            } else {
                SVGViewUtils.setFillAndStroke(view, parallelId, 1, 1);
            }
        }
    }
}
