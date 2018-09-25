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

package org.kie.workbench.common.dmn.api.definition.v1_1;

import java.util.Optional;

import org.junit.Test;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase.Namespace;

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

    public class MockDMNModelClass extends DMNModelInstrumentedBase {
        //Nothing to add!
    }
}
