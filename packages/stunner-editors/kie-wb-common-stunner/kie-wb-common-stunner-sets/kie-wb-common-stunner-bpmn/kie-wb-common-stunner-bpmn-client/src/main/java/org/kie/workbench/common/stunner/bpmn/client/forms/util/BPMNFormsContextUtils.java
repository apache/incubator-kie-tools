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

package org.kie.workbench.common.stunner.bpmn.client.forms.util;

import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.stunner.bpmn.definition.BaseUserTask;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class BPMNFormsContextUtils {

    public static Object getModel(final FormRenderingContext context) {
        Object model = null;
        if (context != null) {
            if (context.getModel() != null) {
                model = context.getModel();
            } else if (context.getParentContext() != null) {
                model = context.getParentContext().getModel();
            }
        }
        return model;
    }

    @SuppressWarnings("unchecked")
    public static boolean isFormGenerationSupported(final Element<?> element) {
        return null != element.asNode() &&
                element.getContent() instanceof View &&
                ((Element<View<?>>) element).getContent().getDefinition() instanceof BaseUserTask;
    }
}
