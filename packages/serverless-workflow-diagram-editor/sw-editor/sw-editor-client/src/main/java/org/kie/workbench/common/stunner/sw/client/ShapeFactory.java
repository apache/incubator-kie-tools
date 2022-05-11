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

package org.kie.workbench.common.stunner.sw.client;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeViewDef;
import org.kie.workbench.common.stunner.svg.client.shape.def.SVGShapeDef;
import org.kie.workbench.common.stunner.svg.client.shape.factory.SVGShapeFactory;
import org.kie.workbench.common.stunner.sw.client.shapes.AnyStateShapeDef;
import org.kie.workbench.common.stunner.sw.client.shapes.TransitionShape;
import org.kie.workbench.common.stunner.sw.client.shapes.TransitionShapeDef;
import org.kie.workbench.common.stunner.sw.client.shapes.TransitionView;
import org.kie.workbench.common.stunner.sw.definition.ActionTransition;
import org.kie.workbench.common.stunner.sw.definition.ActionsContainer;
import org.kie.workbench.common.stunner.sw.definition.CallFunctionAction;
import org.kie.workbench.common.stunner.sw.definition.CallSubflowAction;
import org.kie.workbench.common.stunner.sw.definition.CallbackState;
import org.kie.workbench.common.stunner.sw.definition.CompensationTransition;
import org.kie.workbench.common.stunner.sw.definition.DataConditionTransition;
import org.kie.workbench.common.stunner.sw.definition.DefaultConditionTransition;
import org.kie.workbench.common.stunner.sw.definition.End;
import org.kie.workbench.common.stunner.sw.definition.ErrorTransition;
import org.kie.workbench.common.stunner.sw.definition.EventConditionTransition;
import org.kie.workbench.common.stunner.sw.definition.EventRef;
import org.kie.workbench.common.stunner.sw.definition.EventState;
import org.kie.workbench.common.stunner.sw.definition.EventTimeout;
import org.kie.workbench.common.stunner.sw.definition.ForEachState;
import org.kie.workbench.common.stunner.sw.definition.InjectState;
import org.kie.workbench.common.stunner.sw.definition.OnEvent;
import org.kie.workbench.common.stunner.sw.definition.OperationState;
import org.kie.workbench.common.stunner.sw.definition.ParallelState;
import org.kie.workbench.common.stunner.sw.definition.SleepState;
import org.kie.workbench.common.stunner.sw.definition.Start;
import org.kie.workbench.common.stunner.sw.definition.StartTransition;
import org.kie.workbench.common.stunner.sw.definition.SwitchState;
import org.kie.workbench.common.stunner.sw.definition.Transition;
import org.kie.workbench.common.stunner.sw.definition.Workflow;

import static org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils.getDefinitionId;

@ApplicationScoped
public class ShapeFactory
        implements org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory<Object, Shape> {

    // TODO: Refactor this, no need for storing state...
    private final Map<Class<?>, ShapeViewDef> typeViewDefinitions = new HashMap<Class<?>, ShapeViewDef>() {{
        put(Workflow.class, new AnyStateShapeDef());
        put(Start.class, new AnyStateShapeDef(AnyStateShapeDef.FontStyle.INSIDE_CENTER_WITH_AlPHA, true));
        put(End.class, new AnyStateShapeDef(AnyStateShapeDef.FontStyle.INSIDE_CENTER_WITH_AlPHA, true));
        put(ActionsContainer.class, new AnyStateShapeDef(AnyStateShapeDef.FontStyle.INSIDE_CENTER));
        put(OnEvent.class, new AnyStateShapeDef(AnyStateShapeDef.FontStyle.INSIDE_CENTER, true));
        put(EventRef.class, new AnyStateShapeDef(AnyStateShapeDef.FontStyle.OUTSIDE_CENTER_BOTTOM, true));
        put(CallFunctionAction.class, new AnyStateShapeDef(AnyStateShapeDef.FontStyle.INSIDE_CENTER));
        put(CallSubflowAction.class, new AnyStateShapeDef(AnyStateShapeDef.FontStyle.INSIDE_CENTER));
        put(InjectState.class, new AnyStateShapeDef());
        put(SwitchState.class, new AnyStateShapeDef());
        put(OperationState.class, new AnyStateShapeDef());
        put(EventState.class, new AnyStateShapeDef());
        put(SleepState.class, new AnyStateShapeDef());
        put(ParallelState.class, new AnyStateShapeDef());
        put(ForEachState.class, new AnyStateShapeDef());
        put(CallbackState.class, new AnyStateShapeDef());
        put(Transition.class, new TransitionShapeDef());
        put(StartTransition.class, new TransitionShapeDef());
        put(ErrorTransition.class, new TransitionShapeDef());
        put(EventConditionTransition.class, new TransitionShapeDef());
        put(DataConditionTransition.class, new TransitionShapeDef());
        put(DefaultConditionTransition.class, new TransitionShapeDef());
        put(ActionTransition.class, new TransitionShapeDef());
        put(CompensationTransition.class, new TransitionShapeDef());
        put(EventTimeout.class, new AnyStateShapeDef(AnyStateShapeDef.FontStyle.OUTSIDE_CENTER_BOTTOM, true));
    }};

    private final SVGShapeFactory svgShapeFactory;

    @Inject
    public ShapeFactory(final SVGShapeFactory svgShapeFactory) {
        this.svgShapeFactory = svgShapeFactory;
    }

    @Override
    @SuppressWarnings("all")
    public Shape newShape(Object instance) {
        ShapeViewDef def = typeViewDefinitions.get(instance.getClass());
        if (def instanceof TransitionShapeDef) {
            return new TransitionShape((TransitionShapeDef) def, new TransitionView());
        } else {
            return svgShapeFactory.newShape(instance, (SVGShapeDef) def);
        }
    }

    @Override
    @SuppressWarnings("all")
    public Glyph getGlyph(String definitionId) {
        Map.Entry<Class<?>, ShapeViewDef> typeDefs = typeViewDefinitions.entrySet().stream()
                .filter(e -> getDefinitionId(e.getKey()).equals(definitionId))
                .findAny()
                .get();
        Class<?> type = typeDefs.getKey();
        ShapeViewDef def = typeDefs.getValue();
        return def.getGlyph(type, definitionId);
    }
}
