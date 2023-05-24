/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.sw.resources.i18n;

import org.jboss.errai.ui.shared.api.annotations.TranslationKey;

public interface SWConstants {

    @TranslationKey(defaultValue = "")
    String ACTION_IS_NULL = "Action.null";
    @TranslationKey(defaultValue = "")
    String ACTIONS_ARE_NULL = "Actions.null";
    @TranslationKey(defaultValue = "")
    String ACTION_NAME = "Action.name";
    @TranslationKey(defaultValue = "")
    String ACTION_IS_FUNC = "Action.function";
    @TranslationKey(defaultValue = "")
    String ACTION_IS_EVENT = "Action.event";
    @TranslationKey(defaultValue = "")
    String ACTION_IS_SUBFLOW = "Action.subflow";

    @TranslationKey(defaultValue = "")
    String TIMEOUT_EVENT = "Timeout.event";
    @TranslationKey(defaultValue = "")
    String TIMEOUT_STATE = "Timeout.state";
    @TranslationKey(defaultValue = "")
    String TIMEOUT_ACTION = "Timeout.action";
    @TranslationKey(defaultValue = "")
    String TIMEOUT_BRANCH = "Timeout.branch";

    @TranslationKey(defaultValue = "")
    String SHAPE_END = "Shape.end";
    @TranslationKey(defaultValue = "")
    String SHAPE_START = "Shape.start";

    @TranslationKey(defaultValue = "")
    String SLEEP_DURATION = "Sleep.duration";

    @TranslationKey(defaultValue = "")
    String DATA_FILTER_IS_NULL = "Datafilter.null";
    @TranslationKey(defaultValue = "")
    String DATA_FILTER_PARAMETER = "Datafilter.parameter";
    @TranslationKey(defaultValue = "")
    String DATA_FILTER_INPUT = "Datafilter.input";
    @TranslationKey(defaultValue = "")
    String DATA_FILTER_OUTPUT = "Datafilter.output";

    @TranslationKey(defaultValue = "")
    String BRANCHES_NOT_DEFINED = "Branches.null";
    @TranslationKey(defaultValue = "")
    String BRANCH = "Branch.title";

    @TranslationKey(defaultValue = "")
    String ENUM_ATLEAST = "Enum.atleast";
    @TranslationKey(defaultValue = "")
    String ENUM_ALLOF = "Enum.allof";

    @TranslationKey(defaultValue = "")
    String EVENT_REFERENCE = "Event.reference";

    @TranslationKey(defaultValue = "")
    String EVENT_FILTER_IS_NULL = "Eventfilter.null";
    @TranslationKey(defaultValue = "")
    String EVENT_FILTER_PARAMETER = "Eventfilter.parameter";
    @TranslationKey(defaultValue = "")
    String EVENT_FILTER_USE_DATA = "Eventfilter.usedata";
    @TranslationKey(defaultValue = "")
    String EVENT_FILTER_DATA = "Eventfilter.data";
    @TranslationKey(defaultValue = "")
    String EVENT_FILTER_TO_STATE_DATA = "Eventfilter.tostatedata";

    @TranslationKey(defaultValue = "")
    String CONDITION_IS_NULL = "Condition.null";
    @TranslationKey(defaultValue = "")
    String DATA_CONDITION_TRANSITION = "Condition.data";
    @TranslationKey(defaultValue = "")
    String DATA_CONDITION_TRANSITION_NAME = "Condition.dataname";
    @TranslationKey(defaultValue = "")
    String DATA_CONDITION_TRANSITION_VALUE = "Condition.datavalue";
    @TranslationKey(defaultValue = "")
    String EVENT_CONDITION_TRANSITION = "Condition.event";
    @TranslationKey(defaultValue = "")
    String EVENT_CONDITION_TRANSITION_NAME = "Condition.eventname";
    @TranslationKey(defaultValue = "")
    String EVENT_CONDITION_TRANSITION_VALUE = "Condition.eventvalue";

    @TranslationKey(defaultValue = "")
    String INPUT_COLLECTION = "Input.collection";
    @TranslationKey(defaultValue = "")
    String OUTPUT_COLLECTION = "Output.collection";
    @TranslationKey(defaultValue = "")
    String ITERATION_PARAMETER = "Iteration.parameter";
    @TranslationKey(defaultValue = "")
    String FOR_EACH_BATCH_SIZE = "Foreach.batchsize";
}
