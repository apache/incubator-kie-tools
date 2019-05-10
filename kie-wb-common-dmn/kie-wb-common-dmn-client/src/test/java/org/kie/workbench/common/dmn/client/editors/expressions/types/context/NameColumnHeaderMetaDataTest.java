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
package org.kie.workbench.common.dmn.client.editors.expressions.types.context;

import java.util.Optional;

import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.client.editors.expressions.types.BaseColumnHeaderMetaDataContextMenuTest;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NameColumnHeaderMetaDataTest extends BaseColumnHeaderMetaDataContextMenuTest<NameColumnHeaderMetaData> {

    @Mock
    private HasExpression hasExpression;

    @Override
    protected NameColumnHeaderMetaData getHeaderMetaData() {
        return new NameColumnHeaderMetaData(hasExpression,
                                            Optional.of(hasName),
                                            clearDisplayNameConsumer,
                                            setDisplayNameConsumer,
                                            setTypeRefConsumer,
                                            cellEditorControls,
                                            editor,
                                            Optional.of(EDITOR_TITLE),
                                            listSelector,
                                            listSelectorItemsSupplier,
                                            listSelectorItemConsumer);
    }
}
