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

package org.kie.workbench.common.dmn.api.property.dmn.dataproviders;

import java.util.Map;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.ConstraintType;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorData;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class ConstraintTypeDataProviderTest {

    @Mock
    private TranslationService translationService;

    private Map<Object, String> values;

    @Before
    public void setup() {
        final ConstraintTypeDataProvider provider = new ConstraintTypeDataProvider(translationService);
        provider.init();
        final SelectorData selectorData = provider.getSelectorData(null);
        values = selectorData.getValues();
    }

    @Test
    public void testGetSelectorData() {

        assertConstraintTypeIsPresent(ConstraintType.EXPRESSION, values);
        assertConstraintTypeIsPresent(ConstraintType.ENUMERATION, values);
        assertConstraintTypeIsPresent(ConstraintType.RANGE, values);
    }

    private void assertConstraintTypeIsPresent(final ConstraintType constraintType,
                                               final Map<Object, String> values) {
        assertTrue(values.containsKey(constraintType.value()));
    }
}