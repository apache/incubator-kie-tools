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

package org.kie.workbench.common.stunner.sw.client.shapes;

import com.ait.lienzo.client.core.event.NodeMouseEnterHandler;
import com.ait.lienzo.client.core.event.NodeMouseExitHandler;
import org.kie.j2cl.tools.di.ui.translation.client.TranslationService;
import org.kie.workbench.common.stunner.core.client.shape.impl.AbstractShape;
import org.kie.workbench.common.stunner.core.client.shape.impl.NodeShapeImpl;
import org.kie.workbench.common.stunner.sw.definition.State;

import static org.kie.workbench.common.stunner.core.client.shape.ShapeState.HIGHLIGHT;
import static org.kie.workbench.common.stunner.core.client.shape.ShapeState.NONE;
import static org.kie.workbench.common.stunner.core.client.shape.ShapeState.SELECTED;

public abstract class ServerlessWorkflowShape<V extends ServerlessWorkflowShapeView> extends NodeShapeImpl<State, V> implements HasTranslation {

    private final TranslationService translationService;

    public ServerlessWorkflowShape(final AbstractShape shape, final TranslationService translationService) {
        super(shape);
        this.translationService = translationService;
    }

    public NodeMouseExitHandler getExitHandler() {
        return event -> {
            if (getShapeView().getShapeState() == SELECTED) {
                return;
            }
            getShapeView().applyState(NONE);
        };
    }

    public NodeMouseEnterHandler getEnterHandler() {
        return event -> {
            if (getShapeView().getShapeState() == SELECTED) {
                return;
            }

            getShapeView().applyState(HIGHLIGHT);
        };
    }

    public String getTranslation(String constant) {
        return translationService.getTranslation(constant);
    }
}
