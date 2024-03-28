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

import org.kie.workbench.common.stunner.client.lienzo.shape.impl.ShapeStateDefaultHandler;
import org.kie.workbench.common.stunner.core.client.shape.common.DashArray;
import org.kie.workbench.common.stunner.core.client.shape.impl.ConnectorShape;
import org.kie.workbench.common.stunner.core.client.theme.StunnerTheme;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.kie.workbench.common.stunner.sw.client.theme.ColorTheme;
import org.kie.workbench.common.stunner.sw.definition.CompensationTransition;
import org.kie.workbench.common.stunner.sw.definition.DataConditionTransition;
import org.kie.workbench.common.stunner.sw.definition.DefaultConditionTransition;
import org.kie.workbench.common.stunner.sw.definition.ErrorTransition;
import org.kie.workbench.common.stunner.sw.definition.EventConditionTransition;
import org.kie.workbench.common.stunner.sw.definition.StartTransition;
import org.kie.workbench.common.stunner.sw.definition.Transition;

public class TransitionShape<W>
        extends ConnectorShape<W, TransitionView> {

    private static final DashArray DASH_ARRAY = DashArray.create(8, 8);
    private static final DashArray DOT_ARRAY = DashArray.create(4, 6);

    public TransitionShape(TransitionView view) {
        super(view,
              new ShapeStateDefaultHandler()
                      .setBorderShape(() -> view)
                      .setBackgroundShape(view));
    }

    public TransitionShape<W> setAppearance(Object transitionType) {
        if (transitionType instanceof ErrorTransition) {
            getShapeView().setDashArray(DASH_ARRAY);
            final ErrorTransition definition = (ErrorTransition) transitionType;
            getShapeView().setTitle(definition.getErrorRef());
            getShapeView().setTitleBackgroundColor(((ColorTheme) StunnerTheme.getTheme()).getErrorTransitionBoxColor());
        } else if (transitionType instanceof CompensationTransition) {
            getShapeView().setDashArray(DOT_ARRAY);
        } else if (transitionType instanceof EventConditionTransition) {
            final EventConditionTransition definition = (EventConditionTransition) transitionType;
            getShapeView().setTitle(definition.getEventRef());
            getShapeView().setTitleBackgroundColor(((ColorTheme) StunnerTheme.getTheme()).getEventConditionTransitionBoxColor());
        } else if (transitionType instanceof DataConditionTransition) {
            final DataConditionTransition definition = (DataConditionTransition) transitionType;
            if (StringUtils.nonEmpty(definition.getName())) {
                getShapeView().setTitle(definition.getName());
            } else {
                getShapeView().setTitle(definition.getCondition());
            }
            getShapeView().setTitleBackgroundColor(((ColorTheme) StunnerTheme.getTheme()).getTransitionBoxColor());
        }

        return this;
    }

    public static TransitionShape<TransitionView> create(Object transitionType) {
        return new TransitionShape<>(new TransitionView(getColor(transitionType)));
    }

    public static String getColor(Object transition) {
        Class<?> clazz = transition.getClass();

        if (clazz.equals(StartTransition.class)) {
            return ((ColorTheme) StunnerTheme.getTheme()).getStartTransitionColor();
        } else if (clazz.equals(ErrorTransition.class)) {
            return ((ColorTheme) StunnerTheme.getTheme()).getErrorTransitionColor();
        } else if (clazz.equals(EventConditionTransition.class)) {
            return ((ColorTheme) StunnerTheme.getTheme()).getEventConditionTransitionColor();
        } else if (clazz.equals(DataConditionTransition.class)) {
            return ((ColorTheme) StunnerTheme.getTheme()).getDataConditionTransitionColor();
        } else if (clazz.equals(DefaultConditionTransition.class)) {
            return ((ColorTheme) StunnerTheme.getTheme()).getDefaultConditionTransitionColor();
        } else if (clazz.equals(CompensationTransition.class)) {
            return ((ColorTheme) StunnerTheme.getTheme()).getCompensationTransitionColor();
        }
        return ((ColorTheme) StunnerTheme.getTheme()).getStartTransitionColor();
    }

    public static boolean isTransition(Object object) {
        return object instanceof Transition
                || object instanceof StartTransition
                || object instanceof ErrorTransition
                || object instanceof EventConditionTransition
                || object instanceof DataConditionTransition
                || object instanceof DefaultConditionTransition
                || object instanceof CompensationTransition;
    }
}
