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

package org.kie.workbench.common.dmn.client.editors.types.common;

import java.util.List;
import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.Definitions;
import org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition;
import org.kie.workbench.common.dmn.api.definition.v1_1.UnaryTests;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase.Namespace.FEEL;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ItemDefinitionUtilsTest {

    @Mock
    private DMNGraphUtils dmnGraphUtils;

    private ItemDefinitionUtils utils;

    @Before
    public void setup() {
        utils = spy(new ItemDefinitionUtils(dmnGraphUtils));
    }

    @Test
    public void testFindByName() {

        final String name = "item1";
        final ItemDefinition item1 = makeItem("item1");
        final ItemDefinition item2 = makeItem("item2");
        final Definitions definitions = mock(Definitions.class);
        final List<ItemDefinition> itemDefinitions = asList(item1, item2);

        when(dmnGraphUtils.getDefinitions()).thenReturn(definitions);
        when(definitions.getItemDefinition()).thenReturn(itemDefinitions);

        final Optional<ItemDefinition> actual = utils.findByName(name);
        final Optional<ItemDefinition> expected = Optional.of(item1);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetPrefixForNamespaceURIWhenDefinitionsIsNull() {

        final String namespaceURI = FEEL.getUri();

        when(dmnGraphUtils.getDefinitions()).thenReturn(null);

        final Optional<String> prefix = utils.getPrefixForNamespaceURI(namespaceURI);

        assertFalse(prefix.isPresent());
    }

    @Test
    public void testGetPrefixForNamespaceURIWhenPrefixForNamespaceURIIsNotPresent() {

        final String namespaceURI = FEEL.getUri();
        final Definitions definitions = mock(Definitions.class);

        when(dmnGraphUtils.getDefinitions()).thenReturn(definitions);
        when(definitions.getPrefixForNamespaceURI(namespaceURI)).thenReturn(Optional.empty());

        final Optional<String> prefix = utils.getPrefixForNamespaceURI(namespaceURI);

        assertFalse(prefix.isPresent());
    }

    @Test
    public void testGetPrefixForNamespaceURIWhenPrefixForNamespaceURIIsPresent() {

        final String namespaceURI = FEEL.getUri();
        final Definitions definitions = mock(Definitions.class);
        final Optional<String> expectedPrefix = Optional.of("prefix");

        when(dmnGraphUtils.getDefinitions()).thenReturn(definitions);
        when(definitions.getPrefixForNamespaceURI(namespaceURI)).thenReturn(expectedPrefix);

        final Optional<String> actualPrefix = utils.getPrefixForNamespaceURI(namespaceURI);

        assertEquals(expectedPrefix, actualPrefix);
    }

    @Test
    public void testNormaliseTypeRefWhenPrefixForNamespaceURIIsNotPresent() {

        final String expectedNamespace = FEEL.getUri();
        final String expectedLocalPart = "string";
        final String expectedPrefix = "";
        final QName qName = new QName(expectedNamespace, expectedLocalPart);

        doReturn(Optional.empty()).when(utils).getPrefixForNamespaceURI(expectedNamespace);

        final QName actualQName = utils.normaliseTypeRef(qName);

        assertEquals(expectedLocalPart, actualQName.getLocalPart());
        assertEquals(expectedNamespace, actualQName.getNamespaceURI());
        assertEquals(expectedPrefix, actualQName.getPrefix());
    }

    @Test
    public void testNormaliseTypeRefWhenPrefixForNamespaceURIIsPresent() {

        final String expectedLocalPart = "string";
        final String expectedPrefix = "feel";
        final String expectedNamespace = "";
        final String namespaceURI = FEEL.getUri();
        final QName qName = new QName(namespaceURI, expectedLocalPart);

        doReturn(Optional.of(expectedPrefix)).when(utils).getPrefixForNamespaceURI(namespaceURI);

        final QName actualQName = utils.normaliseTypeRef(qName);

        assertEquals(expectedLocalPart, actualQName.getLocalPart());
        assertEquals(expectedNamespace, actualQName.getNamespaceURI());
        assertEquals(expectedPrefix, actualQName.getPrefix());
    }

    @Test
    public void testGetConstraintTextWhenItemDefinitionDoesNotHaveAllowedValues() {

        final ItemDefinition itemDefinition = mock(ItemDefinition.class);
        final String expectedText = "";

        final String actualText = utils.getConstraintText(itemDefinition);

        assertEquals(expectedText, actualText);
    }

    @Test
    public void testGetConstraintTextWhenItemDefinitionHasAllowedValues() {

        final ItemDefinition itemDefinition = mock(ItemDefinition.class);
        final UnaryTests allowedValues = mock(UnaryTests.class);
        final String expectedText = "(1..10)";

        when(allowedValues.getText()).thenReturn(expectedText);
        when(itemDefinition.getAllowedValues()).thenReturn(allowedValues);

        final String actualText = utils.getConstraintText(itemDefinition);

        assertEquals(expectedText, actualText);
    }

    private ItemDefinition makeItem(final String itemName) {
        final ItemDefinition itemDefinition = mock(ItemDefinition.class);
        final Name name = mock(Name.class);

        when(name.getValue()).thenReturn(itemName);
        when(itemDefinition.getName()).thenReturn(name);

        return itemDefinition;
    }
}
