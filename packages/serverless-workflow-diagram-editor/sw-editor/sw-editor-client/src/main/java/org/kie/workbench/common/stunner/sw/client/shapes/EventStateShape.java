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

import org.appformer.kogito.bridge.client.resource.ResourceContentService;
import org.kie.j2cl.tools.di.ui.translation.client.TranslationService;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.theme.StunnerTheme;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.sw.client.shapes.icons.CornerIcon;
import org.kie.workbench.common.stunner.sw.client.theme.ColorTheme;
import org.kie.workbench.common.stunner.sw.definition.EventState;
import org.kie.workbench.common.stunner.sw.definition.State;
import org.kie.workbench.common.stunner.sw.definition.StateExecTimeout;
import org.kie.workbench.common.stunner.sw.definition.WorkflowTimeouts;

import static org.kie.workbench.common.stunner.sw.client.shapes.icons.IconPath.CLOCK;
import static org.kie.workbench.common.stunner.sw.client.shapes.icons.IconPath.FILTER;
import static org.kie.workbench.common.stunner.sw.client.shapes.icons.IconPosition.BOTTOM_FROM_RIGHT_TOP_CORNER;
import static org.kie.workbench.common.stunner.sw.client.shapes.icons.IconPosition.RIGHT_TOP_CORNER;
import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.TIMEOUT_ACTION;
import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.TIMEOUT_EVENT;
import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.TIMEOUT_STATE;

public class EventStateShape extends StateShape implements HasDataFilter {

    public static final String ICON_SVG = "M 45.254 45.197 L 36.216 45.197 L 42.206 31.792 C 42.613 30.98 42.307 29.965 41.597 29.355 C 40.887 28.746 39.768 28.849 39.06 29.457 L 19.561 45.705 C 18.953 46.215 18.646 47.128 18.953 47.939 C 19.258 48.754 19.968 49.262 20.883 49.262 L 29.921 49.262 L 23.928 62.667 C 23.521 63.478 23.825 64.493 24.538 65.103 C 24.943 65.51 25.247 65.609 25.756 65.609 C 26.263 65.609 26.67 65.406 27.076 65.103 L 46.573 48.854 C 47.183 48.347 47.488 47.431 47.183 46.62 C 46.879 45.808 46.066 45.197 45.254 45.197 Z M 53.378 16.865 C 53.275 16.865 53.275 16.865 53.175 16.865 C 53.275 16.153 53.378 15.543 53.378 14.834 C 53.378 9.247 48.808 4.679 43.222 4.679 C 40.073 4.679 37.333 6.101 35.505 8.334 C 33.068 3.765 28.394 0.615 22.911 0.615 C 15.094 0.615 8.694 7.014 8.694 14.834 C 8.694 15.747 8.794 16.661 8.997 17.575 C 4.023 19.2 0.469 23.872 0.571 29.355 C 0.773 36.057 6.258 41.237 12.858 41.237 L 18.646 41.237 L 36.419 26.41 C 37.535 25.496 38.856 24.989 40.276 24.989 C 41.597 24.989 42.919 25.396 43.932 26.207 C 46.169 27.933 46.981 30.98 45.761 33.521 L 42.511 41.237 L 53.275 41.237 C 59.878 41.237 65.36 36.057 65.563 29.457 C 65.766 22.552 60.285 16.865 53.378 16.865 Z";

    public EventStateShape(State state, ResourceContentService resourceContentService, TranslationService translationService) {
        super(state, resourceContentService, translationService);
    }

    @Override
    public void applyProperties(Node<View<State>, Edge> element, MutationContext mutationContext) {
        super.applyProperties(element, mutationContext);
        EventState state = (EventState) element.getContent().getDefinition();
        if (state.getTimeouts() != null && state.getTimeouts() instanceof WorkflowTimeouts) {
            getView().addChild(new CornerIcon(CLOCK,
                                              RIGHT_TOP_CORNER,
                                              getTranslation(TIMEOUT_EVENT) + ": " + truncate(((WorkflowTimeouts) state.getTimeouts()).getEventTimeout()) + "\r\n"
                                                      + getTranslation(TIMEOUT_STATE) + ": " + truncate(((WorkflowTimeouts) state.getTimeouts()).getStateExecTimeout() instanceof String ?
                                                      (String) ((WorkflowTimeouts) state.getTimeouts()).getStateExecTimeout()
                                                      : ((StateExecTimeout) ((WorkflowTimeouts) state.getTimeouts()).getStateExecTimeout()).getTotal()) + "\r\n"
                                                      + getTranslation(TIMEOUT_ACTION) + ": " + truncate(((WorkflowTimeouts) state.getTimeouts()).getActionExecTimeout())));
        }

        if (state.getStateDataFilter() != null) {
            getView().addChild(new CornerIcon(FILTER,
                                              BOTTOM_FROM_RIGHT_TOP_CORNER,
                                              getStateDataFilter(state.getStateDataFilter())));
        }
    }

    @Override
    public String getIconColor() {
        return ((ColorTheme) StunnerTheme.getTheme()).getEventStateIconFillColor();
    }

    @Override
    public String getIconSvg() {
        return ICON_SVG;
    }
}
