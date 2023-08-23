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

package org.kie.workbench.common.dmn.client.editors.types;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.Definitions;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class QNameConverterTest {

    private static final String ENCODED_FEEL_DATE = "[][date][]";

    private static final String ENCODED_DMN_UNKNOWN = "[" + org.kie.dmn.model.v1_1.KieDMNModelInstrumentedBase.URI_DMN + "]" +
            "[unknown]" +
            "[" + DMNModelInstrumentedBase.Namespace.DMN.getPrefix() + "]";

    private QNameConverter converter;

    @Before
    public void setup() {
        this.converter = new QNameConverter() {
            @Override
            protected List<String> getRegexGroups(final String componentValue) {
                final List<String> regExGroups = new ArrayList<>();
                final Pattern p = Pattern.compile(QNAME_DECODING_PATTERN);
                final Matcher m = p.matcher(componentValue);
                if (m.find()) {
                    for (int i = 0; i <= m.groupCount(); i++) {
                        regExGroups.add(m.group(i));
                    }
                }
                return regExGroups;
            }
        };
    }

    @Test
    public void testToWidgetValueWhenDMNElementDefinesNameSpaces() {
        final Decision decision = new Decision();
        decision.getNsContext().put(DMNModelInstrumentedBase.Namespace.FEEL.getPrefix(),
                                    DMNModelInstrumentedBase.Namespace.FEEL.getUri());
        converter.setDMNModel(decision);

        final String encoding = converter.toWidgetValue(BuiltInType.DATE.asQName());
        assertEquals(ENCODED_FEEL_DATE, encoding);
    }

    @Test
    public void testToWidgetValueWhenDMNDiagramDefinesNameSpaces() {
        final Definitions definitions = new Definitions();
        definitions.getNsContext().put(DMNModelInstrumentedBase.Namespace.FEEL.getPrefix(),
                                       DMNModelInstrumentedBase.Namespace.FEEL.getUri());

        final Decision decision = new Decision();
        decision.setParent(definitions);

        converter.setDMNModel(decision);

        final String encoding = converter.toWidgetValue(BuiltInType.DATE.asQName());
        assertEquals(ENCODED_FEEL_DATE, encoding);
    }

    @Test
    public void testToWidgetValueWhenDMNDiagramDoesNotDefinesNameSpaces() {
        converter.setDMNModel(new Decision());

        final String encoding = converter.toWidgetValue(new QName(org.kie.dmn.model.v1_1.KieDMNModelInstrumentedBase.URI_DMN,
                                                                  "unknown",
                                                                  DMNModelInstrumentedBase.Namespace.DMN.getPrefix()));
        assertEquals(ENCODED_DMN_UNKNOWN, encoding);
    }

    @Test
    public void testToModelValueWithCorrectlyEncodedValue() {
        final QName typeRef = converter.toModelValue(ENCODED_FEEL_DATE);
        assertEquals(QName.NULL_NS_URI,
                     typeRef.getPrefix());
        assertEquals("",
                     typeRef.getNamespaceURI());
        assertEquals(BuiltInType.DATE.getName(),
                     typeRef.getLocalPart());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToModelValueWithIncorrectlyEncodedValue() {
        converter.toModelValue("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToModelValueWithIncorrectlyEncodedValue_NotEnoughBrackets() {
        converter.toModelValue("[][]");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToModelValueWithIncorrectlyEncodedValue_UnclosedBrackets() {
        converter.toModelValue("[][[]");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToModelValueWithIncorrectlyEncodedValue_UnopenBrackets() {
        converter.toModelValue("[]][]");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToModelValueWithIncorrectlyEncodedValue_TextOutside() {
        converter.toModelValue("a[]b[]c[]d");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToModelValueWithIncorrectlyEncodedValue_TextAtStart() {
        converter.toModelValue("a[][][]");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToModelValueWithIncorrectlyEncodedValue_TextAtEnd() {
        converter.toModelValue("[][][]d");
    }
}
