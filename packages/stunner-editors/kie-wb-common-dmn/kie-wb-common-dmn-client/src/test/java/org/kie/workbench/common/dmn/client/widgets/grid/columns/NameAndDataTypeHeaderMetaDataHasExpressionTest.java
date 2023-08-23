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
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class NameAndDataTypeHeaderMetaDataHasExpressionTest extends BaseNameAndDataTypeHeaderMetaDataTest {

    @Override
    public void setup(final Optional<HasName> hasValue) {
        this.metaData = new NameAndDataTypeHeaderMetaData(hasExpression,
                                                          hasValue,
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

    @Override
    public void testGetTypeRef() {
        setup(Optional.empty());

        assertThat(metaData.getTypeRef()).isEqualTo(hasExpression.getVariable().getTypeRef());
    }

    @Override
    public void testSetTypeRef() {
        setup(Optional.empty());

        final QName typeRef = new QName();

        metaData.setTypeRef(typeRef);

        assertThat(hasExpression.getVariable().getTypeRef()).isEqualTo(typeRef);
    }

    @Override
    public void testAsDMNModelInstrumentedBase() {
        setup(Optional.empty());

        assertThat(metaData.asDMNModelInstrumentedBase()).isEqualTo(hasExpression.getVariable());
    }
}