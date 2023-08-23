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

package org.kie.workbench.common.dmn.client.editors.types.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.ConstraintType;
import org.kie.workbench.common.dmn.api.definition.model.Definitions;
import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.api.definition.model.UnaryTests;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase.Namespace.FEEL;
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

        when(dmnGraphUtils.getModelDefinitions()).thenReturn(definitions);
        when(definitions.getItemDefinition()).thenReturn(itemDefinitions);

        final Optional<ItemDefinition> actual = utils.findByName(name);
        final Optional<ItemDefinition> expected = Optional.of(item1);

        assertEquals(expected, actual);
    }

    @Test
    public void testAddItemDefinitions() {

        final Definitions definitions = mock(Definitions.class);
        final ItemDefinition itemDefinition1 = mock(ItemDefinition.class);
        final ItemDefinition itemDefinition2 = mock(ItemDefinition.class);
        final ItemDefinition itemDefinition3 = mock(ItemDefinition.class);
        final ItemDefinition itemDefinition4 = mock(ItemDefinition.class);
        final List<ItemDefinition> newItemDefinitions = asList(itemDefinition1, itemDefinition3);
        final List<ItemDefinition> expectedItemDefinitions = asList(itemDefinition2, itemDefinition4, itemDefinition1, itemDefinition3);
        final List<ItemDefinition> actualItemDefinitions = new ArrayList<>(asList(itemDefinition2, itemDefinition4));

        when(dmnGraphUtils.getModelDefinitions()).thenReturn(definitions);
        when(definitions.getItemDefinition()).thenReturn(actualItemDefinitions);

        utils.addItemDefinitions(newItemDefinitions);

        assertEquals(expectedItemDefinitions, actualItemDefinitions);
    }

    @Test
    public void testGetPrefixForNamespaceURIWhenDefinitionsIsNull() {

        final String namespaceURI = FEEL.getUri();

        when(dmnGraphUtils.getModelDefinitions()).thenReturn(null);

        final Optional<String> prefix = utils.getPrefixForNamespaceURI(namespaceURI);

        assertFalse(prefix.isPresent());
    }

    @Test
    public void testGetPrefixForNamespaceURIWhenPrefixForNamespaceURIIsNotPresent() {

        final String namespaceURI = FEEL.getUri();
        final Definitions definitions = mock(Definitions.class);

        when(dmnGraphUtils.getModelDefinitions()).thenReturn(definitions);
        when(definitions.getPrefixForNamespaceURI(namespaceURI)).thenReturn(Optional.empty());

        final Optional<String> prefix = utils.getPrefixForNamespaceURI(namespaceURI);

        assertFalse(prefix.isPresent());
    }

    @Test
    public void testGetPrefixForNamespaceURIWhenPrefixForNamespaceURIIsPresent() {

        final String namespaceURI = FEEL.getUri();
        final Definitions definitions = mock(Definitions.class);
        final Optional<String> expectedPrefix = Optional.of("prefix");

        when(dmnGraphUtils.getModelDefinitions()).thenReturn(definitions);
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

        when(allowedValues.getText()).thenReturn(new Text(expectedText));
        when(itemDefinition.getAllowedValues()).thenReturn(allowedValues);

        final String actualText = utils.getConstraintText(itemDefinition);

        assertEquals(expectedText, actualText);
    }

    @Test
    public void testGetConstraintType() {
        final ItemDefinition itemDefinition = mock(ItemDefinition.class);
        final UnaryTests allowedValues = mock(UnaryTests.class);
        final ConstraintType expectedConstraintType = ConstraintType.RANGE;

        when(itemDefinition.getAllowedValues()).thenReturn(allowedValues);
        when(allowedValues.getConstraintType()).thenReturn(expectedConstraintType);

        final ConstraintType actualConstraintType = utils.getConstraintType(itemDefinition);

        assertEquals(expectedConstraintType, actualConstraintType);
    }

    private ItemDefinition makeItem(final String itemName) {
        final ItemDefinition itemDefinition = mock(ItemDefinition.class);
        final Name name = mock(Name.class);

        when(name.getValue()).thenReturn(itemName);
        when(itemDefinition.getName()).thenReturn(name);

        return itemDefinition;
    }
}
