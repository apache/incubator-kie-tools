/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n;

import org.jboss.errai.ui.shared.api.annotations.TranslationKey;

public interface StunnerBPMNConstants {

    @TranslationKey(defaultValue = "")
    String ASSIGNEE_NEW = "assignee.newLabel";

    @TranslationKey(defaultValue = "")
    String ASSIGNEE_LABEL = "assignee.label";

    @TranslationKey(defaultValue = "")
    String ASSIGNEE_WITH_DUPLICATES = "assignee.duplicates";

    @TranslationKey(defaultValue = "")
    String ASSIGNEE_CANNOT_BE_EMPTY = "assignee.cannotBeEmpty";

    @TranslationKey(defaultValue = "")
    String ASSIGNEE_SEARCH_ERROR = "assignee.searchError";
}
