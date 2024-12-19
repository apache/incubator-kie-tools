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
import org.kie.workbench.common.stunner.sw.client.shapes.icons.BottomDepiction;
import org.kie.workbench.common.stunner.sw.client.shapes.icons.CornerIcon;
import org.kie.workbench.common.stunner.sw.client.shapes.icons.DataDepiction;
import org.kie.workbench.common.stunner.sw.client.theme.ColorTheme;
import org.kie.workbench.common.stunner.sw.definition.OperationState;
import org.kie.workbench.common.stunner.sw.definition.State;
import org.kie.workbench.common.stunner.sw.definition.StateExecTimeout;
import org.kie.workbench.common.stunner.sw.definition.WorkflowTimeouts;

import static org.kie.workbench.common.stunner.sw.client.shapes.icons.IconPath.CLOCK;
import static org.kie.workbench.common.stunner.sw.client.shapes.icons.IconPath.FILTER;
import static org.kie.workbench.common.stunner.sw.client.shapes.icons.IconPath.PARALLEL;
import static org.kie.workbench.common.stunner.sw.client.shapes.icons.IconPath.SEQUENTIAL;
import static org.kie.workbench.common.stunner.sw.client.shapes.icons.IconPath.SERVICE;
import static org.kie.workbench.common.stunner.sw.client.shapes.icons.IconPath.SUBFLOW;
import static org.kie.workbench.common.stunner.sw.client.shapes.icons.IconPosition.BOTTOM_FROM_RIGHT_TOP_CORNER;
import static org.kie.workbench.common.stunner.sw.client.shapes.icons.IconPosition.LEFT_FROM_RIGHT_TOP_CORNER;
import static org.kie.workbench.common.stunner.sw.client.shapes.icons.IconPosition.LEFT_TOP_CORNER;
import static org.kie.workbench.common.stunner.sw.client.shapes.icons.IconPosition.RIGHT_TOP_CORNER;
import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.TIMEOUT_ACTION;
import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.TIMEOUT_STATE;

public class OperationStateShape extends StateShape implements HasActions,
                                                               HasDataFilter {

    public static final String ICON_SVG = "M61.99,20.83c.41,1.07,.06,2.29-.79,3.07l-5.41,4.93c.14,1.04,.21,2.1,.21,3.17s-.08,2.14-.21,3.17l5.41,4.92c.85,.79,1.2,2,.79,3.08-.55,1.49-1.21,2.92-1.96,4.29l-.59,1.01c-.83,1.38-1.75,2.68-2.76,3.91-.75,.89-1.96,1.2-3.06,.85l-6.96-2.22c-1.68,1.29-3.64,2.36-5.5,3.19l-1.56,7.14c-.25,1.12-1.12,1.92-2.28,2.22-1.72,.29-3.5,.44-5.42,.44-1.7,0-3.48-.15-5.2-.44-1.15-.3-2.02-1.1-2.27-2.22l-1.56-7.14c-1.98-.83-3.83-1.9-5.5-3.19l-6.96,2.22c-1.1,.35-2.32,.04-3.06-.85-1.01-1.24-1.94-2.54-2.76-3.91l-.59-1.01c-.76-1.36-1.42-2.8-1.97-4.29-.4-1.08-.06-2.29,.79-3.08l5.41-4.92c-.14-1.04-.21-2.1-.21-3.17s.07-2.14,.21-3.17l-5.41-4.93c-.86-.79-1.2-1.99-.79-3.07,.55-1.49,1.22-2.93,1.97-4.29l.58-1.01c.83-1.38,1.75-2.67,2.77-3.91,.74-.89,1.96-1.2,3.06-.85l6.96,2.22c1.67-1.29,3.52-2.37,5.5-3.18l1.56-7.14c.25-1.13,1.12-2.04,2.27-2.23,1.73-.29,3.5-.44,5.31-.44s3.59,.15,5.31,.44c1.15,.19,2.03,1.09,2.28,2.23l1.56,7.14c1.86,.82,3.82,1.89,5.5,3.18l6.96-2.22c1.1-.35,2.31-.04,3.06,.85,1.01,1.23,1.94,2.53,2.76,3.91l.59,1.01c.75,1.36,1.41,2.8,1.96,4.29h0Zm-29.99,21.17c5.53,0,10-4.47,10-10.11s-4.47-10-10-10-10,4.59-10,10,4.48,10.11,10,10.11Z";

    public OperationStateShape(State state, ResourceContentService resourceContentService, TranslationService translationService) {
        super(state, resourceContentService, translationService);
    }

    @Override
    public void applyProperties(Node<View<State>, Edge> element, MutationContext mutationContext) {
        super.applyProperties(element, mutationContext);
        OperationState state = (OperationState) element.getContent().getDefinition();
        if (state.getTimeouts() != null && state.getTimeouts() instanceof WorkflowTimeouts) {
            getView().addChild(new CornerIcon(CLOCK,
                                              LEFT_FROM_RIGHT_TOP_CORNER,
                                              getTranslation(TIMEOUT_STATE) + ": " + truncate(((WorkflowTimeouts) state.getTimeouts()).getStateExecTimeout() instanceof String ?
                                                      (String) ((WorkflowTimeouts) state.getTimeouts()).getStateExecTimeout()
                                                      : ((StateExecTimeout) ((WorkflowTimeouts) state.getTimeouts()).getStateExecTimeout()).getTotal()) + "\r\n"
                                                      + getTranslation(TIMEOUT_ACTION) + ": " + truncate(((WorkflowTimeouts) state.getTimeouts()).getActionExecTimeout())));
        }

        getView().addChild(new CornerIcon(SERVICE,
                                          RIGHT_TOP_CORNER,
                                          getActionStringFromArray(state.getActions())));
        if (state.getStateDataFilter() != null) {
            getView().addChild(new CornerIcon(FILTER,
                                              BOTTOM_FROM_RIGHT_TOP_CORNER,
                                              getStateDataFilter(state.getStateDataFilter())));
        }

        boolean isDefault = state.getActionMode() == null || !(state.getActionMode().equals("parallel"));
        getView().addChild(new BottomDepiction(isDefault ? SEQUENTIAL : PARALLEL));

        if (hasSubflows(state.getActions())) {
            getView().addChild(new DataDepiction(SUBFLOW, LEFT_TOP_CORNER));
        }
    }

    @Override
    public String getIconColor() {
        return ((ColorTheme) StunnerTheme.getTheme()).getOperationStateIconFillColor();
    }

    @Override
    public String getIconSvg() {
        return ICON_SVG;
    }
}
