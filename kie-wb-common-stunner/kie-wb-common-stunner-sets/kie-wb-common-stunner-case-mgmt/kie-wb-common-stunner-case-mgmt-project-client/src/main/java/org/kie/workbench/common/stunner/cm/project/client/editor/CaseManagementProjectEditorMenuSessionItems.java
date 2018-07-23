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

package org.kie.workbench.common.stunner.cm.project.client.editor;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.project.client.editor.AbstractProjectEditorMenuSessionItems;
import org.kie.workbench.common.stunner.project.client.session.EditorSessionCommands;

@Dependent
@Typed(CaseManagementProjectEditorMenuSessionItems.class)
public class CaseManagementProjectEditorMenuSessionItems extends AbstractProjectEditorMenuSessionItems<CaseManagementProjectDiagramEditorMenuItemsBuilder> {

    @Inject
    public CaseManagementProjectEditorMenuSessionItems(final CaseManagementProjectDiagramEditorMenuItemsBuilder itemsBuilder,
                                                       final EditorSessionCommands sessionCommands) {
        super(itemsBuilder,
              sessionCommands);
    }
}
