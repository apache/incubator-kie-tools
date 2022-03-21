/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.assigneeEditor;

import java.util.function.Consumer;

import org.kie.workbench.common.stunner.bpmn.client.forms.fields.assigneeEditor.widget.AssigneeLiveSearchEntryCreationEditor;
import org.kie.workbench.common.stunner.bpmn.forms.model.AssigneeType;
import org.uberfire.ext.widgets.common.client.dropdown.EntryCreationLiveSearchService;

public interface AssigneeLiveSearchService
        extends EntryCreationLiveSearchService<String, AssigneeLiveSearchEntryCreationEditor> {

    void init(AssigneeType type);

    void addCustomEntry(String customEntry);

    void setSearchErrorHandler(Consumer<Throwable> searchErrorHandler);
}
