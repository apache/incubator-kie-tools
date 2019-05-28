/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.included.imports.persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class NamespaceHandlerTest {

    @Test
    public void testAddIncludedNamespace() {

        final Map<String, String> nsContext = new HashMap<>();
        final String namespace = "some_namespace";
        final String expectedAlias = NamespaceHandler.INCLUDED_NAMESPACE + "1";
        final String actual = NamespaceHandler.addIncludedNamespace(nsContext, namespace);

        assertEquals(1, nsContext.size());
        assertEquals(expectedAlias, actual);
        assertEquals(namespace, nsContext.get(expectedAlias));
    }

    @Test
    public void testAddExistingIncludedNamespace() {

        final Map<String, String> nsContext = new HashMap<>();
        final String namespace = "some_namespace";
        final String expectedAlias = NamespaceHandler.INCLUDED_NAMESPACE + "1";

        nsContext.put(expectedAlias, namespace);

        final String actual = NamespaceHandler.addIncludedNamespace(nsContext, namespace);
        assertEquals(1, nsContext.size());
        assertEquals(expectedAlias, actual);
        assertEquals(namespace, nsContext.get(expectedAlias));
    }

    @Test
    public void testGetFreeIncludedNamespaceIdWithExistingNamespaces() {

        final Map<String, String> nsContext = new HashMap<>();
        nsContext.put("dmn", "some_uri");
        nsContext.put("other", "some_another");
        nsContext.put(NamespaceHandler.INCLUDED_NAMESPACE + "1", "uri_1");
        nsContext.put(NamespaceHandler.INCLUDED_NAMESPACE + "2", "uri_2");
        nsContext.put(NamespaceHandler.INCLUDED_NAMESPACE + "3", "uri_2");

        final String expected = NamespaceHandler.INCLUDED_NAMESPACE + "4";
        final String actual = NamespaceHandler.getFreeIncludedNamespaceId(nsContext);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetFreeIncludedNamespaceIdReusingId() {

        final Map<String, String> nsContext = new HashMap<>();
        nsContext.put("dmn", "some_uri");
        nsContext.put("includ3d", "some_another");
        nsContext.put(NamespaceHandler.INCLUDED_NAMESPACE + "1", "uri_1");
        nsContext.put(NamespaceHandler.INCLUDED_NAMESPACE + "3", "uri_2");

        // Reusing "included2" since it's not present in the nsContext.
        final String expected = NamespaceHandler.INCLUDED_NAMESPACE + "2";
        final String actual = NamespaceHandler.getFreeIncludedNamespaceId(nsContext);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetFreeIncludedNamespaceId() {

        final Map<String, String> nsContext = new HashMap<>();

        final String expected = NamespaceHandler.INCLUDED_NAMESPACE + "1";
        final String actual = NamespaceHandler.getFreeIncludedNamespaceId(nsContext);

        assertEquals(expected, actual);
    }

    @Test
    public void testRemoveIncludedNamespace() {

        final Map<String, String> nsContext = new HashMap<>();
        final String included1 = NamespaceHandler.INCLUDED_NAMESPACE + "1";
        final String included2 = NamespaceHandler.INCLUDED_NAMESPACE + "2";
        final String included3 = NamespaceHandler.INCLUDED_NAMESPACE + "3";

        final String ns1 = "http://something1";
        final String ns2 = "http://something2";
        final String ns3 = "http://something3";

        nsContext.put(included1, ns1);
        nsContext.put(included2, ns2);
        nsContext.put(included3, ns3);

        NamespaceHandler.removeIncludedNamespace(nsContext, ns2);

        assertEquals(2, nsContext.size());
        assertTrue(nsContext.containsKey(included1));
        assertTrue(nsContext.containsKey(included3));
    }

    @Test
    public void testRemoveIncludedNamespaceNotPresent() {

        final Map<String, String> nsContext = new HashMap<>();
        final String included1 = NamespaceHandler.INCLUDED_NAMESPACE + "1";
        final String included2 = NamespaceHandler.INCLUDED_NAMESPACE + "2";

        final String ns1 = "http://something1";
        final String ns2 = "http://something2";
        final String ns3 = "http://something3";

        nsContext.put(included1, ns1);
        nsContext.put(included2, ns2);

        NamespaceHandler.removeIncludedNamespace(nsContext, ns3);

        assertEquals(2, nsContext.size());
        assertTrue(nsContext.containsKey(included1));
        assertTrue(nsContext.containsKey(included2));
    }

    @Test
    public void testGetAlias() {

        final Map<String, String> nsContext = new HashMap<>();
        final String included1 = NamespaceHandler.INCLUDED_NAMESPACE + "1";
        final String included2 = NamespaceHandler.INCLUDED_NAMESPACE + "2";
        final String included3 = NamespaceHandler.INCLUDED_NAMESPACE + "3";

        final String ns1 = "http://something1";
        final String ns2 = "http://something2";
        final String ns3 = "http://something3";

        nsContext.put(included1, ns1);
        nsContext.put(included2, ns2);
        nsContext.put(included3, ns3);

        final Optional<Map.Entry<String, String>> actual = NamespaceHandler.getAlias(nsContext, ns2);

        assertTrue(actual.isPresent());
        assertEquals(ns2, actual.get().getValue());
        assertEquals(included2, actual.get().getKey());
    }

    @Test
    public void testGetAliasNonExistent() {

        final Map<String, String> nsContext = new HashMap<>();
        final String included1 = NamespaceHandler.INCLUDED_NAMESPACE + "1";

        final String ns1 = "http://something1";

        nsContext.put(included1, ns1);

        final Optional<Map.Entry<String, String>> actual = NamespaceHandler.getAlias(nsContext, "some");

        assertFalse(actual.isPresent());
    }
}