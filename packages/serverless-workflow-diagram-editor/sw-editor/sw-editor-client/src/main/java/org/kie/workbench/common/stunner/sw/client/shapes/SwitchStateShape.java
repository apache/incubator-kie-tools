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
import org.kie.workbench.common.stunner.sw.definition.State;
import org.kie.workbench.common.stunner.sw.definition.SwitchState;

import static org.kie.workbench.common.stunner.sw.client.shapes.icons.IconPath.CLOCK;
import static org.kie.workbench.common.stunner.sw.client.shapes.icons.IconPath.FILTER;
import static org.kie.workbench.common.stunner.sw.client.shapes.icons.IconPosition.BOTTOM_FROM_RIGHT_TOP_CORNER;
import static org.kie.workbench.common.stunner.sw.client.shapes.icons.IconPosition.RIGHT_TOP_CORNER;
import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.TIMEOUT_EVENT;
import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.TIMEOUT_STATE;

public class SwitchStateShape extends StateShape implements HasDataFilter {

    public static final String ICON_COLOR = "#009596";
    public static final String ICON_SVG = "M60.6,46.54h-6.53v-8.71c-.06-1.38-.72-2.69-1.83-3.52l-7.42-5.56h0l-8.41-6.36V4.4C36.4,1.87,34.26-.17,31.69,.01c-2.25,.16-4.09,2.22-4.09,4.48V22.41l-8.41,6.31h0l-7.42,5.55c-1.11,.83-1.76,2.14-1.83,3.52v8.71H3.4c-2.94-.03-4.42,3.53-2.34,5.61l11.01,10.89c1.29,1.29,3.38,1.29,4.68,0l11.01-10.89c2.08-1.96,.61-5.52-2.34-5.47h-6.67v-6.63s8.22-6.08,13.28-9.87c4.96,3.73,13.2,9.92,13.24,9.9v6.63h-6.67c-2.94-.05-4.42,3.51-2.34,5.47l11.01,10.89c1.29,1.29,3.38,1.29,4.68,0l11.01-10.89c2.08-2.08,.6-5.64-2.34-5.61Z";

    public SwitchStateShape(State state, ResourceContentService resourceContentService, TranslationService translationService) {
        super(state, resourceContentService, translationService);
    }

    @Override
    public void applyProperties(Node<View<State>, Edge> element, MutationContext mutationContext) {
        super.applyProperties(element, mutationContext);
        SwitchState state = (SwitchState) element.getContent().getDefinition();
        if (state.getTimeouts() != null) {
            getView().addChild(new CornerIcon(CLOCK,
                                              RIGHT_TOP_CORNER,
                                              getTranslation(TIMEOUT_EVENT) + ": " + state.getTimeouts().getEventTimeout() + "\r\n"
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
