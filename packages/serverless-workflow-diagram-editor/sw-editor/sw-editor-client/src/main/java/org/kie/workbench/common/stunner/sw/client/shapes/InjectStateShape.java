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
import org.kie.workbench.common.stunner.sw.definition.InjectState;
import org.kie.workbench.common.stunner.sw.definition.State;

import static org.kie.workbench.common.stunner.sw.client.shapes.icons.IconPath.FILTER;
import static org.kie.workbench.common.stunner.sw.client.shapes.icons.IconPosition.BOTTOM_FROM_RIGHT_TOP_CORNER;

public class InjectStateShape extends StateShape implements HasDataFilter {

    public static final String ICON_SVG = "M35.02,28.49s-.03-.03-.05-.04l-10.29-10.29c-1.78-1.78-4.68-1.79-6.46,0-1.79,1.79-1.78,4.68,0,6.46l2.81,2.81H4.57c-2.52,0-4.57,2.04-4.57,4.57s2.05,4.57,4.57,4.57H20.94l-2.75,2.75c-1.78,1.78-1.79,4.68,0,6.46s4.68,1.78,6.46,0l10.09-10.09c.28-.2,.54-.44,.77-.71,.5-.58,.82-1.27,.97-2,.37-1.59-.11-3.33-1.46-4.5M.01,4.23C.19,1.83,2.25,0,4.66,0H59.43c2.53,0,4.57,2.05,4.57,4.57s-2.04,4.57-4.57,4.57H4.57C1.93,9.14-.18,6.91,.01,4.23M46.09,18.29h13.24c2.34,0,4.49,1.91,4.65,4.24,.19,2.68-1.92,4.9-4.56,4.9h-13.43c-2.64,0-4.75-2.22-4.56-4.9,.16-2.34,2.31-4.24,4.65-4.24M59.43,36.57c2.64,0,4.75,2.22,4.56,4.9-.16,2.34-2.31,4.24-4.65,4.24h-13.24c-2.34,0-4.49-1.91-4.65-4.24-.19-2.68,1.92-4.9,4.56-4.9h13.43M0,59.43c0-2.53,2.05-4.57,4.57-4.57H59.34c2.34,0,4.49,1.91,4.65,4.24,.19,2.68-1.92,4.9-4.56,4.9H4.57c-2.52,0-4.57-2.04-4.57-4.57Z";

    public InjectStateShape(State state, ResourceContentService resourceContentService, TranslationService translationService) {
        super(state, resourceContentService, translationService);
    }

    @Override
    public void applyProperties(Node<View<State>, Edge> element, MutationContext mutationContext) {
        super.applyProperties(element, mutationContext);
        InjectState state = (InjectState) element.getContent().getDefinition();

        if (state.getStateDataFilter() != null) {
            getView().addChild(new CornerIcon(FILTER,
                                              BOTTOM_FROM_RIGHT_TOP_CORNER,
                                              getStateDataFilter(state.getStateDataFilter())));
        }
    }

    @Override
    public String getIconColor() {
        return ((ColorTheme) StunnerTheme.getTheme()).getInjectStateIconFillColor();
    }

    @Override
    public String getIconSvg() {
        return ICON_SVG;
    }
}
