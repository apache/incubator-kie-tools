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

package org.kie.workbench.common.dmn.api.definition.model;

import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase.Namespace;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DMNModelInstrumentedBaseTest {

    private static final String DUMMY_PREFIX = "dummy";
    private static final String DUMMY_URI = "http://http://www.kiegroup.org";

    @Test
    public void testParent() {
        final MockDMNModelClass parent = new MockDMNModelClass();
        final MockDMNModelClass child = new MockDMNModelClass();

        child.setParent(parent);

        assertEquals(parent,
                     child.getParent());
    }

    @Test
    public void testGetPrefixForNamespaceURIInheritance() {
        final MockDMNModelClass parent = new MockDMNModelClass();
        final MockDMNModelClass child = new MockDMNModelClass();

        parent.getNsContext().put(Namespace.FEEL.getPrefix(),
                                  Namespace.FEEL.getUri());

        child.setParent(parent);

        final Optional<String> parentFeelPrefix = parent.getPrefixForNamespaceURI(Namespace.FEEL.getUri());
        assertTrue(parentFeelPrefix.isPresent());
        assertEquals(Namespace.FEEL.getPrefix(),
                     parentFeelPrefix.get());

        final Optional<String> childFeelPrefix = child.getPrefixForNamespaceURI(Namespace.FEEL.getUri());
        assertTrue(childFeelPrefix.isPresent());
        assertEquals(Namespace.FEEL.getPrefix(),
                     childFeelPrefix.get());
    }

    @Test
    public void testGetPrefixForNamespaceURIOverride() {
        final MockDMNModelClass parent = new MockDMNModelClass();
        final MockDMNModelClass child = new MockDMNModelClass();

        parent.getNsContext().put(Namespace.FEEL.getPrefix(),
                                  Namespace.FEEL.getUri());
        child.getNsContext().put(Namespace.FEEL.getPrefix(),
                                 DUMMY_URI);

        child.setParent(parent);

        final Optional<String> parentFeelPrefix = parent.getPrefixForNamespaceURI(Namespace.FEEL.getUri());
        assertTrue(parentFeelPrefix.isPresent());
        assertEquals(Namespace.FEEL.getPrefix(),
                     parentFeelPrefix.get());

        final Optional<String> childFeelPrefix = child.getPrefixForNamespaceURI(DUMMY_URI);
        assertTrue(childFeelPrefix.isPresent());
        assertEquals(Namespace.FEEL.getPrefix(),
                     childFeelPrefix.get());
    }

    @Test
    public void testGetPrefixForNamespaceURIDifferentPrefixInChildAndParent() {
        final MockDMNModelClass parent = new MockDMNModelClass();
        final MockDMNModelClass child = new MockDMNModelClass();

        parent.getNsContext().put(Namespace.FEEL.getPrefix(),
                                  Namespace.FEEL.getUri());
        child.getNsContext().put(DUMMY_PREFIX,
                                 Namespace.FEEL.getUri());

        child.setParent(parent);

        final Optional<String> parentFeelPrefix = parent.getPrefixForNamespaceURI(Namespace.FEEL.getUri());
        assertTrue(parentFeelPrefix.isPresent());
        assertEquals(Namespace.FEEL.getPrefix(),
                     parentFeelPrefix.get());

        final Optional<String> childFeelPrefix = child.getPrefixForNamespaceURI(Namespace.FEEL.getUri());
        assertTrue(childFeelPrefix.isPresent());
        assertEquals(DUMMY_PREFIX,
                     childFeelPrefix.get());
    }

    @Test
    public void testGetPrefixForNamespaceURIDifferentNothingInChild() {
        final MockDMNModelClass parent = new MockDMNModelClass();
        final MockDMNModelClass child = new MockDMNModelClass();

        parent.getNsContext().put(Namespace.FEEL.getPrefix(),
                                  Namespace.FEEL.getUri());

        child.setParent(parent);

        final Optional<String> parentFeelPrefix = parent.getPrefixForNamespaceURI(Namespace.FEEL.getUri());
        assertTrue(parentFeelPrefix.isPresent());
        assertEquals(Namespace.FEEL.getPrefix(),
                     parentFeelPrefix.get());

        final Optional<String> childFeelPrefix = child.getPrefixForNamespaceURI(Namespace.FEEL.getUri());
        assertTrue(childFeelPrefix.isPresent());
        assertEquals(Namespace.FEEL.getPrefix(),
                     childFeelPrefix.get());
    }

    @Test
    public void testGetPrefixForNamespaceURIDifferentNothingInParent() {
        final MockDMNModelClass parent = new MockDMNModelClass();
        final MockDMNModelClass child = new MockDMNModelClass();

        child.getNsContext().put(Namespace.FEEL.getPrefix(),
                                 Namespace.FEEL.getUri());

        child.setParent(parent);

        final Optional<String> parentFeelPrefix = parent.getPrefixForNamespaceURI(Namespace.FEEL.getUri());
        assertFalse(parentFeelPrefix.isPresent());

        final Optional<String> childFeelPrefix = child.getPrefixForNamespaceURI(Namespace.FEEL.getUri());
        assertTrue(childFeelPrefix.isPresent());
        assertEquals(Namespace.FEEL.getPrefix(),
                     childFeelPrefix.get());
    }

    @Test
    public void testGetPrefixForNamespaceURIMultipleNameSpaces() {
        final MockDMNModelClass model = new MockDMNModelClass();

        model.getNsContext().put(Namespace.FEEL.getPrefix(),
                                 Namespace.FEEL.getUri());
        model.getNsContext().put(DUMMY_PREFIX,
                                 DUMMY_URI);

        final Optional<String> modelFeelPrefix = model.getPrefixForNamespaceURI(Namespace.FEEL.getUri());
        assertTrue(modelFeelPrefix.isPresent());
        assertEquals(Namespace.FEEL.getPrefix(),
                     modelFeelPrefix.get());

        final Optional<String> modelDummyPrefix = model.getPrefixForNamespaceURI(DUMMY_URI);
        assertTrue(modelDummyPrefix.isPresent());
        assertEquals(DUMMY_PREFIX,
                     modelDummyPrefix.get());
    }

    @Test
    public void testNamespaces() {

        final List<Namespace> namespaces = asList(Namespace.values());

        assertEquals(7, namespaces.size());
        assertEquals("http://www.omg.org/spec/DMN/20180521/FEEL/", namespaces.get(0).getUri());
        assertEquals("http://www.omg.org/spec/DMN/20180521/MODEL/", namespaces.get(1).getUri());
        assertEquals("http://www.drools.org/kie/dmn/1.2", namespaces.get(2).getUri());
        assertEquals("https://kie.apache.org/dmn/", namespaces.get(3).getUri());
        assertEquals("http://www.omg.org/spec/DMN/20180521/DMNDI/", namespaces.get(4).getUri());
        assertEquals("http://www.omg.org/spec/DMN/20180521/DI/", namespaces.get(5).getUri());
        assertEquals("http://www.omg.org/spec/DMN/20180521/DC/", namespaces.get(6).getUri());
        assertEquals("feel", namespaces.get(0).getPrefix());
        assertEquals("dmn", namespaces.get(1).getPrefix());
        assertEquals("kie", namespaces.get(2).getPrefix());
        assertEquals("", namespaces.get(3).getPrefix());
        assertEquals("dmndi", namespaces.get(4).getPrefix());
        assertEquals("di", namespaces.get(5).getPrefix());
        assertEquals("dc", namespaces.get(6).getPrefix());
    }

    public class MockDMNModelClass extends DMNModelInstrumentedBase {
        //Nothing to add!
    }
}
