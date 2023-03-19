/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.sw.client.shapes;

import org.appformer.kogito.bridge.client.resource.ResourceContentService;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.sw.client.shapes.icons.CornerIcon;
import org.kie.workbench.common.stunner.sw.definition.ParallelState;
import org.kie.workbench.common.stunner.sw.definition.State;

import static org.kie.workbench.common.stunner.sw.client.shapes.icons.IconPath.CLOCK;
import static org.kie.workbench.common.stunner.sw.client.shapes.icons.IconPath.FILTER;
import static org.kie.workbench.common.stunner.sw.client.shapes.icons.IconPosition.BOTTOM_FROM_RIGHT_TOP_CORNER;
import static org.kie.workbench.common.stunner.sw.client.shapes.icons.IconPosition.RIGHT_TOP_CORNER;
import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.TIMEOUT_BRANCH;
import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.TIMEOUT_STATE;

public class ParallelStateShape extends StateShape implements HasDataFilter {

    public static final String ICON_COLOR = "#4CB140";
    public static final String ICON_SVG = "M50.54,.01c2.34,.16,4.24,2.31,4.24,4.65V59.43c0,2.64-2.23,4.75-4.91,4.56-2.33-.17-4.23-2.31-4.23-4.65V4.57c0-2.63,2.22-4.75,4.9-4.56M13.46,63.99c-2.34-.16-4.24-2.31-4.24-4.65V4.57c0-2.52,2.04-4.57,4.57-4.57s4.57,2.05,4.57,4.57V59.43c0,2.64-2.22,4.75-4.9,4.56Z";

    public ParallelStateShape(State state, ResourceContentService resourceContentService, TranslationService translationService) {
        super(state, resourceContentService, translationService);
    }

    @Override
    public void applyProperties(Node<View<State>, Edge> element, MutationContext mutationContext) {
        super.applyProperties(element, mutationContext);
        ParallelState state = (ParallelState) element.getContent().getDefinition();
        if (state.getTimeouts() != null) {
            getView().addChild(new CornerIcon(CLOCK,
                                              RIGHT_TOP_CORNER,
                                              getTranslation(TIMEOUT_BRANCH) + ": " + state.getTimeouts().getBranchExecTimeout() + "\r\n"
                                                      + getTranslation(TIMEOUT_STATE) + ": " + state.getTimeouts().getStateExecTimeout()));
        }

        if (state.getStateDataFilter() != null) {
            getView().addChild(new CornerIcon(FILTER,
                                              BOTTOM_FROM_RIGHT_TOP_CORNER,
                                              getStateDataFilter(state.getStateDataFilter())));
        }
    }

    @Override
    public String getIconColor() {
        return ICON_COLOR;
    }

    @Override
    public String getIconSvg() {
        return ICON_SVG;
    }
}
