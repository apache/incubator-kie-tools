/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import org.kie.workbench.common.stunner.client.lienzo.shape.impl.ShapeStateDefaultHandler;
import org.kie.workbench.common.stunner.core.client.shape.common.DashArray;
import org.kie.workbench.common.stunner.core.client.shape.impl.ConnectorShape;
import org.kie.workbench.common.stunner.core.util.StringUtils;
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
            getShapeView().setTitleBackgroundColor("red");
        } else if (transitionType instanceof CompensationTransition) {
            getShapeView().setDashArray(DOT_ARRAY);
        } else if (transitionType instanceof EventConditionTransition) {
            final EventConditionTransition definition = (EventConditionTransition) transitionType;
            getShapeView().setTitle(definition.getEventRef());
            getShapeView().setTitleBackgroundColor("orange");
        } else if (transitionType instanceof DataConditionTransition) {
            final DataConditionTransition definition = (DataConditionTransition) transitionType;
            if (StringUtils.nonEmpty(definition.getName())) {
                getShapeView().setTitle(definition.getName());
            } else {
                getShapeView().setTitle(definition.getCondition());
            }
            getShapeView().setTitleBackgroundColor("gray");
        }

        return this;
    }

    public static TransitionShape<TransitionView> create(Object transitionType) {
        return new TransitionShape<>(new TransitionView(getColor(transitionType)));
    }

    public static String getColor(Object transition) {
        Class<?> clazz = transition.getClass();

        if (clazz.equals(StartTransition.class)) {
            return "#757575";
        } else if (clazz.equals(ErrorTransition.class)) {
            return "#c9190b";
        } else if (clazz.equals(EventConditionTransition.class)) {
            return "#828282";
        } else if (clazz.equals(DataConditionTransition.class)) {
            return "#757575";
        } else if (clazz.equals(DefaultConditionTransition.class)) {
            return "#12DE70";
        } else if (clazz.equals(CompensationTransition.class)) {
            return "#f0ab00";
        }
        return "#757575";
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
