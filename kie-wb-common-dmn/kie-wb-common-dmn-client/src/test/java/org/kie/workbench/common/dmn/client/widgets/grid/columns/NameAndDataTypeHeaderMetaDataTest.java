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

package org.kie.workbench.common.dmn.client.widgets.grid.columns;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.client.editors.expressions.util.RendererUtils;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RendererUtils.class)
public class NameAndDataTypeHeaderMetaDataTest extends BaseNameAndDataTypeHeaderMetaDataTest {

    public void setup(final Optional<HasName> hasName) {
        this.metaData = new NameAndDataTypeHeaderMetaData(hasName,
                                                          () -> hasTypeRef,
                                                          clearDisplayNameConsumer,
                                                          setDisplayNameConsumer,
                                                          setTypeRefConsumer,
                                                          cellEditorControls,
                                                          headerEditor,
                                                          EDITOR_TITLE) {
            @Override
            public String getColumnGroup() {
                return NAME_DATA_TYPE_COLUMN_GROUP;
            }
        };
    }

    @Test
    public void testGetHasTypeRefs() {

        metaData = new NameAndDataTypeHeaderMetaData(null, null, null, null, null, null, null, null) {

            public String getColumnGroup() {
                return null;
            }
        };

        assertEquals(singletonList(metaData), metaData.getHasTypeRefs());
    }
}
