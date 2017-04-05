/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.wizard.column.commons;

import java.util.function.BiConsumer;

import org.drools.workbench.models.datamodel.workitems.PortableWorkDefinition;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;

public interface HasWorkItemPage {

    String getWorkItem();

    void setWorkItem(final String workItem);

    ActionCol52 editingCol();

    Boolean isWorkItemSet();

    PortableWorkDefinition getWorkItemDefinition();

    void forEachWorkItem(final BiConsumer<String, String> biConsumer);

    void setWorkItemPageAsCompleted();
}
