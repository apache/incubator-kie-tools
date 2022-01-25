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
package org.kie.workbench.common.dmn.client.editors.expressions.types.relation;

import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.editors.expressions.types.BaseColumnHeaderMetaDataContextMenuTest;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.Silent.class)
public class RelationColumnHeaderMetaDataTest extends BaseColumnHeaderMetaDataContextMenuTest<RelationColumnHeaderMetaData, Name, HasName> {

    @Mock
    private InformationItem informationItem;

    @Override
    protected RelationColumnHeaderMetaData getHeaderMetaData() {
        return new RelationColumnHeaderMetaData(informationItem,
                                                clearValueConsumer,
                                                setValueConsumer,
                                                setTypeRefConsumer,
                                                translationService,
                                                cellEditorControls,
                                                editor,
                                                listSelector,
                                                listSelectorItemsSupplier,
                                                listSelectorItemConsumer);
    }
}
