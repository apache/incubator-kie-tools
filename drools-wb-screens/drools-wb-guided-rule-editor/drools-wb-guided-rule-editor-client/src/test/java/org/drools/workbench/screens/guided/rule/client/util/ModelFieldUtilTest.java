/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.rule.client.util;

import org.drools.workbench.models.datamodel.rule.ActionFieldList;
import org.drools.workbench.models.datamodel.rule.ActionFieldValue;
import org.drools.workbench.models.datamodel.rule.ActionInsertFact;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.oracle.ModelField;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ModelFieldUtilTest {

    @Mock
    private ModelField field1;

    @Mock
    private ModelField field2;

    @Test
    public void nullModelFields() {
        assertNull(ModelFieldUtil.getAvailableFieldCompletions(null,
                                                               null));
    }

    @Test
    public void emptyModelFields() {
        assertEquals(0,
                     ModelFieldUtil.getAvailableFieldCompletions(new ModelField[]{},
                                                                 null).length);
    }

    @Test
    public void nullActionFieldList() {
        final ModelField[] result = ModelFieldUtil.getAvailableFieldCompletions(new ModelField[]{field1},
                                                                                null);
        assertEquals(1,
                     result.length);
        assertEquals(field1,
                     result[0]);
    }

    @Test
    public void emptyActionFieldList() {
        final ModelField[] result = ModelFieldUtil.getAvailableFieldCompletions(new ModelField[]{field1},
                                                                                new ActionInsertFact());
        assertEquals(1,
                     result.length);
        assertEquals(field1,
                     result[0]);
    }

    @Test
    public void filtering() {
        when(field1.getName()).thenReturn("field1");
        when(field2.getName()).thenReturn("field2");
        final ActionFieldList afl = new ActionInsertFact();
        final ActionFieldValue afv = new ActionFieldValue();
        afv.setField("field1");
        afl.addFieldValue(afv);

        final ModelField[] result = ModelFieldUtil.getAvailableFieldCompletions(new ModelField[]{field1, field2},
                                                                                afl);
        assertEquals(1,
                     result.length);
        assertEquals(field2,
                     result[0]);
    }
}
