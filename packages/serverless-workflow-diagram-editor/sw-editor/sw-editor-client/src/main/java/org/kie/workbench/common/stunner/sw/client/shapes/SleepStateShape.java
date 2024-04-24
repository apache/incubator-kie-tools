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
import org.kie.workbench.common.stunner.sw.definition.SleepState;
import org.kie.workbench.common.stunner.sw.definition.State;

import static org.kie.workbench.common.stunner.sw.client.shapes.icons.IconPath.CLOCK;
import static org.kie.workbench.common.stunner.sw.client.shapes.icons.IconPosition.RIGHT_TOP_CORNER;
import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.SLEEP_DURATION;

public class SleepStateShape extends StateShape implements IsTruncatable {

    public static final String ICON_SVG = "M54.24,53.11c3.71-4.77,5.92-10.77,5.92-17.28,0-15.56-12.61-28.17-28.17-28.17S3.83,20.27,3.83,35.83c0,6.49,2.19,12.46,5.87,17.22l-2.53,2.53c-1.63,1.63-1.8,4.46-.28,6.2,1.74,2,4.76,2.07,6.59,.24l2.8-2.8c4.49,3.02,9.9,4.79,15.71,4.79s11.17-1.75,15.65-4.74l2.75,2.75c1.63,1.63,4.46,1.8,6.2,.28,2-1.74,2.07-4.76,.24-6.59l-2.59-2.59Zm-13.09-8.11l-10.59-7.06c-.74-.4-1.18-1.22-1.18-2.11l-.1-15c0-1.47,1.28-2.65,2.65-2.65,1.57,0,2.75,1.18,2.75,2.65v13.58l9.41,6.26c1.21,.82,1.54,2.46,.64,3.67-.72,1.21-2.36,1.54-3.57,.64M23.86,3.69C19.89-.5,13.34-1.27,8.47,2.14c-4.87,3.41-6.39,9.82-3.81,14.99L23.86,3.69M40.14,3.69c3.97-4.19,10.52-4.95,15.39-1.54,4.87,3.41,6.39,9.82,3.81,14.99L40.14,3.69Z";

    public SleepStateShape(State state, ResourceContentService resourceContentService, TranslationService translationService) {
        super(state, resourceContentService, translationService);
    }

    @Override
    public void applyProperties(Node<View<State>, Edge> element, MutationContext mutationContext) {
        super.applyProperties(element, mutationContext);
        SleepState state = (SleepState) element.getContent().getDefinition();
        getView().addChild(new CornerIcon(CLOCK,
                                          RIGHT_TOP_CORNER,
                                          getTranslation(SLEEP_DURATION) + ": " + truncate(state.getDuration())));
    }

    @Override
    public String getIconColor() {
        return ((ColorTheme) StunnerTheme.getTheme()).getSleepStateIconFillColor();
    }

    @Override
    public String getIconSvg() {
        return ICON_SVG;
    }
}
