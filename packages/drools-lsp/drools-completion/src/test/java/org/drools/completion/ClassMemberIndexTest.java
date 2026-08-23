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

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import org.drools.completion.fixtures.InitProbe;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        // The failed load is memoized (not the empty result itself); repeated
        // lookups skip Class.forName but keep returning the same empty list.
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

    private JavaMemberSource fakeSource() {
        return new JavaMemberSource() {
            public List<Field> membersOf(String fqcn) {
                return "com.example.Only".equals(fqcn)
                    ? List.of(new Field("code", "String", null, Field.Origin.FIELD),
                              new Field("active", "boolean", null, Field.Origin.GETTER))
                    : List.of();
            }
            public Set<String> memberNames(String fqcn) {
                return "com.example.Only".equals(fqcn) ? Set.of("code", "active") : null;
            }
            public List<String> supertypesOf(String fqcn) {
                return "com.example.Only".equals(fqcn) ? List.of("Base") : List.of();
            }
            public List<String> constructorsOf(String fqcn) {
                return "com.example.Only".equals(fqcn) ? List.of("Only(String)") : List.of();
            }
        };
    }

    @Test
    void reflectsConstructorsForClasspathType() {
        ClassMemberIndex idx = new ClassMemberIndex(getClass().getClassLoader());
        List<String> ctors = idx.constructorsOf("java.lang.String");
        assertTrue(ctors.stream().anyMatch(c -> c.startsWith("String(")),
            () -> "ctors=" + ctors);
    }

    @Test
    void tagsMemberOrigins() {
        ClassMemberIndex idx = new ClassMemberIndex(getClass().getClassLoader());
        // java.awt.Point's getX()/getY()/getLocation() are bean-property getters that
        // win over the same-named fields x,y (DRL property access binds to bean
        // properties, so the getter-derived view is the semantically correct one).
        List<Field> pointMembers = idx.membersOf("java.awt.Point");
        assertTrue(pointMembers.stream().anyMatch(f -> f.name.equals("x") && f.origin == Field.Origin.GETTER));
        assertTrue(pointMembers.stream().anyMatch(f -> f.name.equals("location") && f.origin == Field.Origin.GETTER));
        // Pet.legs has no getter, so it surfaces as a plain FIELD.
        List<Field> petMembers = idx.membersOf("org.drools.completion.fixtures.Pet");
        assertTrue(petMembers.stream().anyMatch(f -> f.name.equals("legs") && f.origin == Field.Origin.FIELD));
    }

    @Test
    void fallsBackToSourceWhenClassNotLoadable() {
        ClassMemberIndex idx = new ClassMemberIndex(getClass().getClassLoader());
        idx.setSourceFallback(fakeSource());
        // com.example.Only is not on the test classpath -> reflection miss -> fallback
        assertTrue(idx.membersOf("com.example.Only").stream().anyMatch(f -> f.name.equals("code")));
        assertEquals(Set.of("code", "active"), idx.memberNames("com.example.Only"));
        assertEquals(List.of("Base"), idx.supertypesOf("com.example.Only"));
        assertEquals(List.of("Only(String)"), idx.constructorsOf("com.example.Only"));
    }

    @Test
    void compiledWinsOverSource() {
        ClassMemberIndex idx = new ClassMemberIndex(getClass().getClassLoader());
        idx.setSourceFallback(fakeSource());
        // java.lang.String IS loadable -> fallback never consulted (fake returns empty for it anyway;
        // assert we get real reflected members, not the fake's)
        assertTrue(idx.memberNames("java.lang.String").contains("CASE_INSENSITIVE_ORDER"));
    }

    @Test
    void memberNamesStillNullWhenNeitherKnows() {
        ClassMemberIndex idx = new ClassMemberIndex(getClass().getClassLoader());
        idx.setSourceFallback(fakeSource());
        assertNull(idx.memberNames("com.nope.Nothing"));
    }

    @Test
    void sourceOnlyWorkspaceUsesFallback() {
        // Zero classpath entries -> the works-before-build scenario. of() must not
        // hand back the shared empty() singleton, or a later setSourceFallback would
        // silently no-op.
        ClassMemberIndex idx = ClassMemberIndex.of(Set.<Path>of());
        idx.setSourceFallback(fakeSource());
        assertTrue(idx.membersOf("com.example.Only").stream().anyMatch(f -> f.name.equals("code")));
    }
}
