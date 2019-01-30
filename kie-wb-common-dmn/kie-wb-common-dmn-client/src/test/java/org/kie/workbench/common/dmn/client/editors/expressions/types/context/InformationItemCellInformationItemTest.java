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

package org.kie.workbench.common.dmn.client.editors.expressions.types.context;

import java.util.List;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.InformationItemCell.HasNameAndDataTypeCell;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class InformationItemCellInformationItemTest extends InformationItemCellNameTest {

    private static final QName TYPE_REF1 = new QName();

    private static final QName TYPE_REF2 = new QName();

    private InformationItem informationItem;

    @Override
    protected InformationItemCell makeInformationItemCell() {
        this.informationItem = new InformationItem();
        return new InformationItemCell(() -> HasNameAndDataTypeCell.wrap(informationItem),
                                       listSelector);
    }

    @Test
    public void testGetName() {
        informationItem.getName().setValue(VALUE1);

        assertThat(cell.getValue()).isNotNull();
        assertThat(cell.getValue().getValue().getName().getValue()).isEqualTo(VALUE1);
    }

    @Test
    public void testSetName() {
        informationItem.getName().setValue(VALUE1);

        cell.getValue().getValue().setName(new Name(VALUE2));

        assertThat(informationItem.getName().getValue()).isEqualTo(VALUE2);
    }

    @Test
    public void testGetTypeRef() {
        informationItem.setTypeRef(TYPE_REF1);

        assertThat(cell.getValue()).isNotNull();
        assertThat(cell.getValue().getValue()).isInstanceOf(HasNameAndDataTypeCell.class);
        assertThat(((HasNameAndDataTypeCell) cell.getValue().getValue()).getTypeRef()).isEqualTo(TYPE_REF1);
    }

    @Test
    public void testSetTypeRef() {
        informationItem.setTypeRef(TYPE_REF1);

        assertThat(cell.getValue().getValue()).isInstanceOf(HasNameAndDataTypeCell.class);
        ((HasNameAndDataTypeCell) cell.getValue().getValue()).setTypeRef(TYPE_REF2);

        assertThat(informationItem.getTypeRef()).isEqualTo(TYPE_REF2);
    }

    @Test
    public void testAsDMNModelInstrumentedBase() {
        assertThat(((HasNameAndDataTypeCell) cell.getValue().getValue()).asDMNModelInstrumentedBase()).isEqualTo(informationItem);
    }

    @Test
    public void testRenderCell() {
        cell.getValue().getValue().render(cellRenderContext);

        verify(group).add(text1);
        verify(group).add(text2);
    }

    @Test
    public void testHasNameAndDataTypeCellGetHasTypeRefs() {

        final InformationItem informationItem = mock(InformationItem.class);
        final HasNameAndDataTypeCell hasNameAndDataTypeCell = HasNameAndDataTypeCell.wrap(informationItem);
        final HasTypeRef hasTypeRef1 = mock(HasTypeRef.class);
        final HasTypeRef hasTypeRef2 = mock(HasTypeRef.class);
        final List<HasTypeRef> expectedHasTypeRefs = asList(hasTypeRef1, hasTypeRef2);

        when(informationItem.getHasTypeRefs()).thenReturn(expectedHasTypeRefs);

        final List<HasTypeRef> actualHasTypeRefs = hasNameAndDataTypeCell.getHasTypeRefs();

        assertEquals(expectedHasTypeRefs, actualHasTypeRefs);
    }
}
