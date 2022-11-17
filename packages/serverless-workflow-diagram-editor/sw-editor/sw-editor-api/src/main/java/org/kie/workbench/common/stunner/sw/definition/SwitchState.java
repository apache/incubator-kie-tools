/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.sw.definition;

import jsinterop.annotations.JsType;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.stunner.client.json.mapper.annotation.JSONMapper;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.Morph;

/**
 * Switch states can be viewed as workflow gateways.
 * They can direct transitions of a workflow based on certain conditions.
 * There are two exclusive types of conditions for switch states: DataBased & EventBased.
 *
 * @see <a href="https://github.com/serverlessworkflow/specification/blob/main/specification.md#Switch-State"> Switch state </a>
 */
@Bindable
@Definition
@Morph(base = State.class)
@JSONMapper
@JsType
public class SwitchState extends State {

    public static final String TYPE_SWITCH = "switch";

    /**
     * Default transition of the workflow if there is no matching data conditions or event timeout is reached.
     * Can be a transition or end definition
     */
    private DefaultConditionTransition defaultCondition;

    /**
     * Events, which the switch state must wait for before transitioning to another workflow state.
     */
    private EventConditionTransition[] eventConditions;

    /**
     * Data-based condition statement, which causes a transition to another workflow state if evaluated to true.
     */
    private DataConditionTransition[] dataConditions;

    /**
     * If true, this state is used to compensate another state. Default is "false".
     */
    private Boolean usedForCompensation;

    public SwitchState() {
        this.type = TYPE_SWITCH;
    }

    public DefaultConditionTransition getDefaultCondition() {
        return defaultCondition;
    }

    public void setDefaultCondition(DefaultConditionTransition defaultCondition) {
        this.defaultCondition = defaultCondition;
    }

    public EventConditionTransition[] getEventConditions() {
        return eventConditions;
    }

    public void setEventConditions(EventConditionTransition[] eventConditions) {
        this.eventConditions = eventConditions;
    }

    public DataConditionTransition[] getDataConditions() {
        return dataConditions;
    }

    public void setDataConditions(DataConditionTransition[] dataConditions) {
        this.dataConditions = dataConditions;
    }

    public Boolean getUsedForCompensation() {
        return usedForCompensation;
    }

    public void setUsedForCompensation(Boolean usedForCompensation) {
        this.usedForCompensation = usedForCompensation;
    }
}
