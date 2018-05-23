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

package org.kie.workbench.common.dmn.client.property.dmn;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.dmn.api.definition.v1_1.Definitions;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QNameFieldConverterTest {

    private static final String ENCODED_FEEL_DATE = "[][date][" + DMNModelInstrumentedBase.Namespace.FEEL.getPrefix() + "]";

    private static final String ENCODED_DMN_UNKNOWN = "[" + org.kie.dmn.model.v1_1.DMNModelInstrumentedBase.URI_DMN + "]" +
            "[unknown]" +
            "[" + DMNModelInstrumentedBase.Namespace.DMN.getPrefix() + "]";

    @Mock
    private DMNGraphUtils dmnGraphUtils;

    private QNameFieldConverter converter;

    @Before
    public void setup() {
        this.converter = new QNameFieldConverter(dmnGraphUtils) {
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

        final Definitions definitions = new Definitions();
        definitions.getNsContext().put(DMNModelInstrumentedBase.Namespace.FEEL.getPrefix(),
                                       DMNModelInstrumentedBase.Namespace.FEEL.getUri());

        when(dmnGraphUtils.getDefinitions()).thenReturn(definitions);
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
        final Decision decision = new Decision();
        converter.setDMNModel(decision);

        final String encoding = converter.toWidgetValue(BuiltInType.DATE.asQName());
        assertEquals(ENCODED_FEEL_DATE, encoding);
    }

    @Test
    public void testToWidgetValueWhenDMNDiagramDoesNotDefinesNameSpaces() {
        converter.setDMNModel(new Decision());

        final String encoding = converter.toWidgetValue(new QName(org.kie.dmn.model.v1_1.DMNModelInstrumentedBase.URI_DMN,
                                                                  "unknown",
                                                                  DMNModelInstrumentedBase.Namespace.DMN.getPrefix()));
        assertEquals(ENCODED_DMN_UNKNOWN, encoding);
    }

    @Test
    public void testToModelValueWithCorrectlyEncodedValue() {
        final QName typeRef = converter.toModelValue(ENCODED_FEEL_DATE);
        assertEquals(DMNModelInstrumentedBase.Namespace.FEEL.getPrefix(),
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
