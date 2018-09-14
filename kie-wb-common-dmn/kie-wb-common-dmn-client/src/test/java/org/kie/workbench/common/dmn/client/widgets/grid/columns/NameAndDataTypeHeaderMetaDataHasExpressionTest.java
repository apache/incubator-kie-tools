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

import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class NameAndDataTypeHeaderMetaDataHasExpressionTest extends BaseNameAndDataTypeHeaderMetaDataTest {

    private Decision hasExpression = new Decision();

    private Optional<LiteralExpression> expression = Optional.of(new LiteralExpression());

    public void setup(final Optional<HasName> hasName) {
        this.metaData = new NameAndDataTypeHeaderMetaData<LiteralExpression>(hasExpression,
                                                                             expression,
                                                                             hasName,
                                                                             clearDisplayNameConsumer,
                                                                             setDisplayNameConsumer,
                                                                             setTypeRefConsumer,
                                                                             cellEditorControls,
                                                                             headerEditor) {
            @Override
            public String getColumnGroup() {
                return NAME_DATA_TYPE_COLUMN_GROUP;
            }
        };
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