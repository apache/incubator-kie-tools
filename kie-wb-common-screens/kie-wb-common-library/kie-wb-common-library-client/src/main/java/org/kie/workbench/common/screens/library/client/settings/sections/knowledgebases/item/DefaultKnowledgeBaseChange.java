/*
 * Copyright (C) 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.library.client.settings.sections.knowledgebases.item;

import org.kie.workbench.common.services.shared.kmodule.KBaseModel;

public class DefaultKnowledgeBaseChange {

    private final KBaseModel newDefault;

    public DefaultKnowledgeBaseChange(final KBaseModel newDefault) {
        this.newDefault = newDefault;
    }

    public KBaseModel getNewDefault() {
        return newDefault;
    }
}
