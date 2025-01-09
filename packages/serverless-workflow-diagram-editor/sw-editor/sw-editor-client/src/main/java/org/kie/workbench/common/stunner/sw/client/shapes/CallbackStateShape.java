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
import org.kie.workbench.common.stunner.sw.client.shapes.icons.DataDepiction;
import org.kie.workbench.common.stunner.sw.client.theme.ColorTheme;
import org.kie.workbench.common.stunner.sw.definition.ActionNode;
import org.kie.workbench.common.stunner.sw.definition.CallbackState;
import org.kie.workbench.common.stunner.sw.definition.State;
import org.kie.workbench.common.stunner.sw.definition.StateExecTimeout;
import org.kie.workbench.common.stunner.sw.definition.WorkflowTimeouts;

import static org.kie.workbench.common.stunner.sw.client.shapes.icons.IconPath.CLOCK;
import static org.kie.workbench.common.stunner.sw.client.shapes.icons.IconPath.EVENT;
import static org.kie.workbench.common.stunner.sw.client.shapes.icons.IconPath.FILTER;
import static org.kie.workbench.common.stunner.sw.client.shapes.icons.IconPath.SERVICE;
import static org.kie.workbench.common.stunner.sw.client.shapes.icons.IconPath.SUBFLOW;
import static org.kie.workbench.common.stunner.sw.client.shapes.icons.IconPosition.BOTTOM_FROM_RIGHT_TOP_CORNER;
import static org.kie.workbench.common.stunner.sw.client.shapes.icons.IconPosition.CENTER_TOP;
import static org.kie.workbench.common.stunner.sw.client.shapes.icons.IconPosition.LEFT_FROM_RIGHT_TOP_CORNER;
import static org.kie.workbench.common.stunner.sw.client.shapes.icons.IconPosition.LEFT_TOP_CORNER;
import static org.kie.workbench.common.stunner.sw.client.shapes.icons.IconPosition.RIGHT_TOP_CORNER;
import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.EVENT_REFERENCE;
import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.TIMEOUT_ACTION;
import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.TIMEOUT_EVENT;
import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.TIMEOUT_STATE;

public class CallbackStateShape extends StateShape implements HasActions,
                                                              HasEventAndDataFilter {

    public static final String ICON_SVG = "M63.97,36.13c0-2.13-1.64-3.95-3.76-4.07-2.31-.13-4.23,1.7-4.23,3.99,0,6.61-5.37,11.98-11.98,11.98H24.03v-4.99c0-1.18-.7-2.25-1.78-2.74-1.08-.47-2.34-.28-3.23,.52l-9.98,8.98c-.63,.56-.99,1.37-.99,2.22s.36,1.66,.99,2.23l9.98,8.98c.56,.51,1.28,.77,2,.77,.41,0,.83-.09,1.22-.26,1.08-.48,1.78-1.55,1.78-2.74v-4.99h19.97c10.98,0,19.92-8.91,19.97-19.89ZM20,16.08h19.85l.11,4.99c0,1.18,.7,2.25,1.78,2.74,.4,.17,.81,.26,1.11,.26,.73,0,1.44-.27,2.01-.77l9.98-8.98c.74-.57,1.1-1.38,1.1-2.34s-.36-1.66-.99-2.23L44.97,.76c-.88-.79-2.15-.98-3.22-.51-1.08,.6-1.89,1.67-1.89,2.85l.11,4.99H20C8.99,8.09,.03,17.05,.03,28.06c0,2.21,1.79,3.99,3.99,3.99s3.99-1.79,3.99-3.99c0-6.6,5.38-11.98,11.98-11.98Z";

    public CallbackStateShape(State state, ResourceContentService resourceContentService, TranslationService translationService) {
        super(state, resourceContentService, translationService);
    }

    @Override
    public void applyProperties(Node<View<State>, Edge> element, MutationContext mutationContext) {
        super.applyProperties(element, mutationContext);
        CallbackState state = (CallbackState) element.getContent().getDefinition();
        if (state.getTimeouts() != null && state.getTimeouts() instanceof WorkflowTimeouts) {
            getView().addChild(new CornerIcon(CLOCK,
                                              CENTER_TOP,
                                              getTranslation(TIMEOUT_EVENT) + ": " + truncate(((WorkflowTimeouts) state.getTimeouts()).getEventTimeout()) + "\r\n"
                                                      + getTranslation(TIMEOUT_STATE) + ": " + truncate(((WorkflowTimeouts) state.getTimeouts()).getStateExecTimeout() instanceof String ?
                                                      (String) ((WorkflowTimeouts) state.getTimeouts()).getStateExecTimeout()
                                                      : ((StateExecTimeout) ((WorkflowTimeouts) state.getTimeouts()).getStateExecTimeout()).getTotal()) + "\r\n"
                                                      + getTranslation(TIMEOUT_ACTION) + ": " + truncate(((WorkflowTimeouts) state.getTimeouts()).getActionExecTimeout())));
        }

        getView().addChild(new CornerIcon(EVENT,
                                          LEFT_FROM_RIGHT_TOP_CORNER,
                                          getTranslation(EVENT_REFERENCE) + ": " + truncate(state.getEventRef())));

        getView().addChild(new CornerIcon(SERVICE,
                                          RIGHT_TOP_CORNER,
                                          getActionString(state.getAction())));

        if (state.getStateDataFilter() != null || state.getEventDataFilter() != null) {
            getView().addChild(new CornerIcon(FILTER,
                                              BOTTOM_FROM_RIGHT_TOP_CORNER,
                                              getStateDataFilter(state.getStateDataFilter())
                                                      + "\r\n\r\n"
                                                      + truncate(getEventFilter(state.getEventDataFilter()))));
        }

        if (null != state.getAction() && hasSubflows(new ActionNode[]{state.getAction()})) {
            getView().addChild(new DataDepiction(SUBFLOW, LEFT_TOP_CORNER));
        }
    }

    @Override
    public String getIconColor() {
        return ((ColorTheme) StunnerTheme.getTheme()).getCallbackStateIconFillColor();
    }

    @Override
    public String getIconSvg() {
        return ICON_SVG;
    }
}
