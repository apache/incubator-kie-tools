/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.widgets.grid.columns;

import java.util.Optional;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.mockito.Mockito;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class NameAndDataTypeHeaderMetaDataTest extends BaseNameAndDataTypeHeaderMetaDataTest {

    @Override
    public void setup(final Optional<HasName> hasValue) {
        this.metaData = new NameAndDataTypeHeaderMetaData(hasValue,
                                                          () -> hasTypeRef,
                                                          clearValueConsumer,
                                                          setValueConsumer,
                                                          setTypeRefConsumer,
                                                          translationService,
                                                          cellEditorControls,
                                                          headerEditor) {
            @Override
            public String getColumnGroup() {
                return NAME_DATA_TYPE_COLUMN_GROUP;
            }

            @Override
            public Optional<String> getPlaceHolder() {
                return Optional.of(PLACEHOLDER);
            }

            @Override
            public String getPopoverTitle() {
                return POPOVER_TITLE;
            }
        };
        when(translationService.getTranslation(Mockito.<String>any())).thenAnswer(i -> i.getArguments()[0]);
    }

    @Test
    public void testGetHasTypeRefs() {
        metaData = new NameAndDataTypeHeaderMetaData(HasExpression.NOP,
                                                     Optional.empty(),
                                                     null,
                                                     null,
                                                     null,
                                                     translationService,
                                                     null,
                                                     null) {

            public String getColumnGroup() {
                return null;
            }
        };

        assertEquals(singletonList(metaData), metaData.getHasTypeRefs());
    }
}
