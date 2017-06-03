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

package org.drools.workbench.screens.guided.dtable.client.widget;

import java.util.ArrayList;
import java.util.List;

import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.CompositeColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.junit.Test;

import static org.junit.Assert.*;

public class ValidatorTest {

    @Test
    public void testIsConditionValidWhenHeaderIsBlank() throws Exception {
        final Validator validator = new Validator();

        assertFalse(validator.isConditionValid(fakeConditionCol52("")));
    }

    @Test
    public void testIsConditionValidWhenHeaderIsNotUnique() throws Exception {
        final ConditionCol52 conditionCol1 = fakeConditionCol52("header");
        final ConditionCol52 conditionCol2 = fakeConditionCol52("header");
        final Validator validator = new Validator(fakeCompositeColumn(conditionCol1,
                                                                      conditionCol2));

        assertFalse(validator.isConditionValid(conditionCol2));
    }

    @Test
    public void testIsConditionValidWhenHeaderIsValid() throws Exception {
        final ConditionCol52 conditionCol1 = fakeConditionCol52("header1");
        final ConditionCol52 conditionCol2 = fakeConditionCol52("header2");
        final Validator validator = new Validator(fakeCompositeColumn(conditionCol1,
                                                                      conditionCol2));

        assertTrue(validator.isConditionValid(conditionCol2));
    }

    @Test
    public void testIsConditionHeaderNotBlankWhenHeaderIsNull() throws Exception {
        final Validator validator = new Validator();
        final ConditionCol52 col52 = fakeConditionCol52(null);

        assertFalse(validator.isConditionHeaderNotBlank(col52));
    }

    @Test
    public void testIsConditionHeaderNotBlankWhenHeaderIsEmpty() throws Exception {
        final Validator validator = new Validator();
        final ConditionCol52 col52 = fakeConditionCol52("");

        assertFalse(validator.isConditionHeaderNotBlank(col52));
    }

    @Test
    public void testIsConditionHeaderNotBlankWhenHeaderIsValid() throws Exception {
        final Validator validator = new Validator();
        final ConditionCol52 col52 = fakeConditionCol52("my header");

        assertTrue(validator.isConditionHeaderNotBlank(col52));
    }

    @Test
    public void testIsConditionHeaderUniqueWhenHeaderIsInvalid() throws Exception {
        final ConditionCol52 conditionCol1 = fakeConditionCol52("header");
        final ConditionCol52 conditionCol2 = fakeConditionCol52("header");
        final Validator validator = new Validator(fakeCompositeColumn(conditionCol1,
                                                                      conditionCol2));

        assertFalse(validator.isConditionHeaderUnique(conditionCol2));
    }

    @Test
    public void testIsConditionHeaderUniqueWhenHeaderIsValid() throws Exception {
        final ConditionCol52 conditionCol1 = fakeConditionCol52("header1");
        final ConditionCol52 conditionCol2 = fakeConditionCol52("header2");
        final Validator validator = new Validator(fakeCompositeColumn(conditionCol1,
                                                                      conditionCol2));

        assertTrue(validator.isConditionHeaderUnique(conditionCol2));
    }

    private ConditionCol52 fakeConditionCol52(final String header) {
        final ConditionCol52 conditionCol52 = new ConditionCol52();

        conditionCol52.setHeader(header);
        conditionCol52.setConstraintValueType(BaseSingleFieldConstraint.TYPE_PREDICATE);

        return conditionCol52;
    }

    private List<CompositeColumn<? extends BaseColumn>> fakeCompositeColumn(final ConditionCol52... columns) {
        final List<CompositeColumn<? extends BaseColumn>> compositeColumns = new ArrayList<>();

        compositeColumns.add(fakeCompositeColumn(new ArrayList<ConditionCol52>() {{
            for (ConditionCol52 column : columns) {
                add(column);
            }
        }}));

        return compositeColumns;
    }

    private CompositeColumn<?> fakeCompositeColumn(final ArrayList<?> objects) {
        return new CompositeColumn() {
            @Override
            public String getHeader() {
                return null;
            }

            @Override
            public void setHeader(final String header) {

            }

            @Override
            public boolean isHideColumn() {
                return false;
            }

            @Override
            public void setHideColumn(final boolean hideColumn) {

            }

            @Override
            public int getWidth() {
                return 0;
            }

            @Override
            public void setWidth(final int width) {

            }

            @Override
            public DTCellValue52 getDefaultValue() {
                return null;
            }

            @Override
            public void setDefaultValue(final DTCellValue52 defaultValue) {

            }

            @Override
            public List<?> getChildColumns() {
                return objects;
            }

            @Override
            public void setChildColumns(final List columns) {

            }
        };
    }
}