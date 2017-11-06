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

package org.kie.workbench.common.stunner.core.i18n;

public class CoreTranslationMessages {

    private static final String SEP = ".";
    private static final String CORE_PREF = "org.kie.workbench.common.stunner.core" + SEP;
    private static final String COMMAND_PREF = CORE_PREF + "command" + SEP;
    private static final String RULE_PREF = CORE_PREF + "rule" + SEP;

    public static final String ERROR = CORE_PREF + "error";

    public static final String WARNING = CORE_PREF + "warn";

    public static final String INFO = CORE_PREF + "info";

    public static final String REASON = CORE_PREF + "reason";

    public static final String DELETE = CORE_PREF + "delete";

    public static final String EDIT = CORE_PREF + "edit";

    public static final String ARE_YOU_SURE = CORE_PREF + "areYouSure";

    public static final String ELEMENT_UUID = CORE_PREF + "element_uuid";

    public static final String COMMAND_SUCCESS = COMMAND_PREF + "success";

    public static final String COMMAND_FAILED = COMMAND_PREF + "fail";

    public static final String VALIDATION_SUCCESS = RULE_PREF + "success";

    public static final String VALIDATION_FAILED = RULE_PREF + "fail";

    public static final String DIAGRAM_LOAD_FAIL_UNSUPPORTED_ELEMENTS = "org.kie.workbench.common.stunner.core.client.diagram.load.fail.unsupported";
}