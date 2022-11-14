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

import javax.enterprise.context.ApplicationScoped;

import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.sw.client.resources.GlyphFactory;
import org.kie.workbench.common.stunner.sw.client.shapes.EndShape;
import org.kie.workbench.common.stunner.sw.client.shapes.StartShape;
import org.kie.workbench.common.stunner.sw.client.shapes.StateShape;
import org.kie.workbench.common.stunner.sw.client.shapes.TransitionShape;
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
import org.kie.workbench.common.stunner.sw.definition.State;
import org.kie.workbench.common.stunner.sw.definition.SwitchState;
import org.kie.workbench.common.stunner.sw.definition.Transition;
import org.kie.workbench.common.stunner.sw.definition.Workflow;

import static org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils.getDefinitionId;

@ApplicationScoped
public class ShapeFactory
        implements org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory<Object, Shape> {

    @Override
    @SuppressWarnings("all")
    public Shape newShape(Object instance) {
        if (instance instanceof Start) {
            return new StartShape();
        } else if (instance instanceof End) {
            return new EndShape();
        } else if (instance instanceof State) {
            State state = (State) instance;
            return StateShape.create(state.getName()).setType(state.getType());
        } else if (TransitionShape.isTransition(instance)) {
            return TransitionShape.create(instance).setAppearance(instance);
        } else {
            return null;
        }
    }

    @Override
    @SuppressWarnings("all")
    public Glyph getGlyph(String definitionId) {
        if (definitionId.equals(getDefinitionId(InjectState.class))) {
            return GlyphFactory.STATE_INJECT;
        } else if (definitionId.equals(getDefinitionId(SwitchState.class))) {
            return GlyphFactory.STATE_SWITCH;
        } else if (definitionId.equals(getDefinitionId(EventState.class))) {
            return GlyphFactory.STATE_EVENT;
        } else if (definitionId.equals(getDefinitionId(OperationState.class))) {
            return GlyphFactory.STATE_OPERATION;
        } else if (definitionId.equals(getDefinitionId(SleepState.class))) {
            return GlyphFactory.STATE_INJECT;
        } else if (definitionId.equals(getDefinitionId(ParallelState.class))) {
            return GlyphFactory.STATE_INJECT;
        } else if (definitionId.equals(getDefinitionId(ForEachState.class))) {
            return GlyphFactory.STATE_INJECT;
        } else if (definitionId.equals(getDefinitionId(CallbackState.class))) {
            return GlyphFactory.STATE_INJECT;
        } else if (definitionId.equals(getDefinitionId(Workflow.class))) {
            return GlyphFactory.TRANSITION;
        } else if (definitionId.equals(getDefinitionId(Start.class))) {
            return GlyphFactory.START;
        } else if (definitionId.equals(getDefinitionId(End.class))) {
            return GlyphFactory.END;
        } else if (definitionId.equals(getDefinitionId(ActionsContainer.class))) {
            return GlyphFactory.CALL_FUNCTION;
        } else if (definitionId.equals(getDefinitionId(OnEvent.class))) {
            return GlyphFactory.EVENTS;
        } else if (definitionId.equals(getDefinitionId(EventRef.class))) {
            return GlyphFactory.EVENT;
        } else if (definitionId.equals(getDefinitionId(EventTimeout.class))) {
            return GlyphFactory.EVENT_TIMEOUT;
        } else if (definitionId.equals(getDefinitionId(CallFunctionAction.class))) {
            return GlyphFactory.CALL_FUNCTION;
        } else if (definitionId.equals(getDefinitionId(CallSubflowAction.class))) {
            return GlyphFactory.CALL_SUBFLOW;
        } else if (definitionId.equals(getDefinitionId(Transition.class))) {
            return GlyphFactory.TRANSITION;
        } else if (definitionId.equals(getDefinitionId(StartTransition.class))) {
            return GlyphFactory.TRANSITION_START;
        } else if (definitionId.equals(getDefinitionId(ErrorTransition.class))) {
            return GlyphFactory.TRANSITION_ERROR;
        } else if (definitionId.equals(getDefinitionId(EventConditionTransition.class))) {
            return GlyphFactory.TRANSITION_CONDITION;
        } else if (definitionId.equals(getDefinitionId(DataConditionTransition.class))) {
            return GlyphFactory.TRANSITION_CONDITION;
        } else if (definitionId.equals(getDefinitionId(DefaultConditionTransition.class))) {
            return GlyphFactory.TRANSITION_CONDITION;
        } else if (definitionId.equals(getDefinitionId(ActionTransition.class))) {
            return GlyphFactory.TRANSITION_ACTION;
        } else if (definitionId.equals(getDefinitionId(CompensationTransition.class))) {
            return GlyphFactory.TRANSITION_COMPENSATION;
        }

        throw new IllegalArgumentException("Definition " + definitionId + " do not have a Glyph.");
    }
}
