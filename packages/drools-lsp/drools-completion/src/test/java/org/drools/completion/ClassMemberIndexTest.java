/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.drools.completion;

import java.util.List;

import org.drools.completion.fixtures.InitProbe;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClassMemberIndexTest {

    private final ClassMemberIndex index =
            new ClassMemberIndex(getClass().getClassLoader());

    @Test
    void exposesBeanPropertiesAndPublicFields() {
        List<Field> members = index.membersOf("org.drools.completion.fixtures.Pet");

        assertThat(members).extracting(f -> f.name)
                .contains("name", "friendly", "legs");
        assertThat(members).extracting(f -> f.name)
                // namedAfter: isNamedAfter() returns String, not boolean — not a property.
                .doesNotContain("class", "getClass", "ignoredBecauseItTakesArgs", "namedAfter");
        assertThat(members)
                .anySatisfy(f -> {
                    assertThat(f.name).isEqualTo("name");
                    assertThat(f.type).isEqualTo("String");
                });
    }

    @Test
    void reflectionDoesNotRunStaticInitializers() {
        index.membersOf("org.drools.completion.fixtures.Pet");
        assertThat(InitProbe.petInitialized)
                .as("membersOf must not execute static initializers of user classes")
                .isFalse();
    }

    @Test
    void exposesEnumConstants() {
        List<Field> members = index.membersOf("org.drools.completion.fixtures.PetKind");

        assertThat(members).extracting(f -> f.name).contains("CAT", "DOG");
        assertThat(members)
                .anySatisfy(f -> {
                    assertThat(f.name).isEqualTo("CAT");
                    assertThat(f.type).isEqualTo("PetKind");
                });
    }

    @Test
    void supertypesOfReportsSuperclassAndInterfaces() {
        // ArrayList extends AbstractList implements List, RandomAccess,
        // Cloneable, Serializable — a stable JDK hierarchy.
        List<String> supers = index.supertypesOf("java.util.ArrayList");

        assertThat(supers)
                .contains("java.util.AbstractList", "java.util.List")
                .as("java.lang.Object is omitted as a supertype")
                .doesNotContain("java.lang.Object");
    }

    @Test
    void supertypesOfOmitsObjectForPlainClass() {
        // Pet extends Object directly and implements nothing.
        assertThat(index.supertypesOf("org.drools.completion.fixtures.Pet")).isEmpty();
    }

    @Test
    void supertypesOfUnknownClassIsEmpty() {
        assertThat(index.supertypesOf("does.not.Exist")).isEmpty();
    }

    @Test
    void supertypesOfEmptyIndexIsEmpty() {
        assertThat(ClassMemberIndex.empty().supertypesOf("java.util.ArrayList")).isEmpty();
    }

    @Test
    void unknownClassYieldsNoMembers() {
        assertThat(index.membersOf("does.not.Exist")).isEmpty();
        // Negative result is cached and stays consistent.
        assertThat(index.membersOf("does.not.Exist")).isEmpty();
    }

    @Test
    void emptyIndexYieldsNoMembers() {
        assertThat(ClassMemberIndex.empty().membersOf("java.lang.String")).isEmpty();
    }

    @Test
    void closingDoesNotCloseExternallyOwnedLoader() throws Exception {
        ClassLoader borrowed = getClass().getClassLoader();
        ClassMemberIndex borrowedIndex = new ClassMemberIndex(borrowed);

        borrowedIndex.close();
        borrowedIndex.close();

        assertThat(borrowed.loadClass("java.lang.String")).isNotNull();
        ClassMemberIndex.empty().close(); // must not throw
    }
}
