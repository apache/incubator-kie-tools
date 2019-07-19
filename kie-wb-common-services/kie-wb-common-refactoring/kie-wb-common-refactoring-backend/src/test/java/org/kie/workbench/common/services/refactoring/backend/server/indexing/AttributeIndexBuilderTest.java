/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.services.refactoring.backend.server.indexing;

import java.util.List;

import org.drools.compiler.lang.descr.AttributeDescr;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.refactoring.SharedPart;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.metadata.model.KProperty;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AttributeIndexBuilderTest {

    @Mock
    DefaultIndexBuilder builder;

    @Captor
    ArgumentCaptor<SharedPart> captor;

    @Test(expected = IllegalStateException.class)
    public void unSupportedAttribute() {
        new AttributeIndexBuilder(builder).visit(new AttributeDescr("name", "value"));
    }

    @Test()
    public void testIgnored() {
        final AttributeIndexBuilder attributeIndexBuilder = new AttributeIndexBuilder(builder);
        attributeIndexBuilder.visit(new AttributeDescr("no-loop", "value"));
        attributeIndexBuilder.visit(new AttributeDescr("lock-on-active", "value"));
        attributeIndexBuilder.visit(new AttributeDescr("salience", "value"));
        attributeIndexBuilder.visit(new AttributeDescr("auto-focus", "value"));
        attributeIndexBuilder.visit(new AttributeDescr("dialect", "value"));
        attributeIndexBuilder.visit(new AttributeDescr("date-effective", "value"));
        attributeIndexBuilder.visit(new AttributeDescr("date-expires", "value"));
        attributeIndexBuilder.visit(new AttributeDescr("enabled", "value"));
        attributeIndexBuilder.visit(new AttributeDescr("duration", "value"));
        attributeIndexBuilder.visit(new AttributeDescr("timer", "value"));
        attributeIndexBuilder.visit(new AttributeDescr("calendars", "value"));

        verify(builder, never()).addGenerator(any());
    }

    @Test()
    public void testValidValue() {
        new AttributeIndexBuilder(builder).visit(new AttributeDescr("activation-group", "value"));

        verify(builder).addGenerator(captor.capture());
        final SharedPart value = captor.getValue();
        List<KProperty<?>> kProperties = value.toIndexElements();
        assertEquals(1, kProperties.size());
        assertEquals("shared:activationgroup", kProperties.get(0).getName());
        assertEquals("value", kProperties.get(0).getValue());
    }
}