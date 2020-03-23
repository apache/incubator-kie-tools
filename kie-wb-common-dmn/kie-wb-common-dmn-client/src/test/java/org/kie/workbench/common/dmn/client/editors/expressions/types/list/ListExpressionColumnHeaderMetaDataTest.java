/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.client.editors.expressions.types.list;

import java.util.Optional;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.NameAndDataTypeHeaderMetaDataHasExpressionTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ListExpressionColumnHeaderMetaDataTest extends NameAndDataTypeHeaderMetaDataHasExpressionTest {

    @Override
    public void setup(final Optional<HasName> hasValue) {
        metaData = new ListExpressionColumnHeaderMetaData(hasExpression,
                                                          hasValue,
                                                          clearValueConsumer,
                                                          setValueConsumer,
                                                          setTypeRefConsumer,
                                                          translationService,
                                                          cellEditorControls,
                                                          headerEditor);
        when(translationService.getTranslation(anyString())).thenAnswer(i -> i.getArguments()[0]);
    }

    @Test
    @Override
    @Ignore("Placeholder is not supported by ListExpressionColumnHeaderMetaData")
    public void testRenderPlaceHolder() {
        super.testRenderPlaceHolder();
    }

    @Test
    public void testGetColumnGroup() {
        setup(Optional.empty());

        assertThat(metaData.getColumnGroup()).isEqualTo(ListExpressionColumnHeaderMetaData.NAME_DATA_TYPE_COLUMN_GROUP);
    }

    @Test
    public void testGetPopoverTitle() {
        setup(Optional.empty());

        assertThat(metaData.getPopoverTitle()).isEqualTo(DMNEditorConstants.ListEditor_EditExpression);
    }
}
