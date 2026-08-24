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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class JavaSourceTypeParserTest {

    private JavaSourceType only(String src) {
        List<JavaSourceType> ts = JavaSourceTypeParser.parse(src);
        assertEquals(1, ts.size(), () -> "expected exactly one top-level type, got " + ts.size());
        return ts.get(0);
    }

    private Optional<Field> member(JavaSourceType t, String name) {
        return t.members.stream().filter(f -> f.name.equals(name)).findFirst();
    }

    /**
     * Reflection lists instance members: it filters static fields out and its
     * property scan rejects static methods. A static offered as a fact property
     * before a build and withdrawn after it is worse than never offering it,
     * because a rule written against it does not compile.
     */
    @Test
    void staticMembersAreNotFactProperties() {
        JavaSourceType t = only(
            "package com.example;\n"
            + "public class Order {\n"
            + "  public static final String VERSION = \"1\";\n"
            + "  public int id;\n"
            + "  public static String getBuild() { return \"b\"; }\n"
            + "  public String getCode() { return \"c\"; }\n"
            + "}\n");

        assertTrue(member(t, "id").isPresent(), () -> "members=" + t.members);
        assertTrue(member(t, "code").isPresent(), () -> "members=" + t.members);
        assertTrue(member(t, "VERSION").isEmpty(),
            () -> "a static field is not a fact property: " + t.members);
        assertTrue(member(t, "build").isEmpty(),
            () -> "nor is a static getter: " + t.members);
    }

    /**
     * Reflection reports public methods and public constructors only, so the
     * source view must not offer more than the compiled view will: a member that
     * appears before a build and vanishes after it is worse than one that never
     * appeared, and hover's Constructors section would otherwise name a
     * constructor the author cannot call.
     */
    @Test
    void nonPublicGettersAndConstructorsAreNotMembers() {
        JavaSourceType t = only(
            "package com.example;\n"
            + "public class Order {\n"
            + "  public int id;\n"
            + "  private String secret;\n"
            + "  private String getSecret() { return secret; }\n"
            + "  public String getCode() { return \"c\"; }\n"
            + "  private Order() { }\n"
            + "  public Order(int id) { }\n"
            + "}\n");

        assertTrue(member(t, "id").isPresent(), () -> "members=" + t.members);
        assertTrue(member(t, "code").isPresent(), () -> "members=" + t.members);
        assertTrue(member(t, "secret").isEmpty(),
            () -> "a non-public getter must not be a member: " + t.members);
        assertEquals(List.of("Order(int)"), t.constructors);
    }

    @Test
    void parsesClassFieldsGettersAndFqcn() {
        JavaSourceType t = only(
            "package com.example.model;\n"
            + "public class Patient {\n"
            + "  private String name;\n"
            + "  public int ageYears;\n"
            + "  public String getName() { return name; }\n"
            + "  public boolean isActive() { return true; }\n"
            + "  public Patient(String name, int ageYears) { }\n"
            + "}\n");
        assertEquals("com.example.model.Patient", t.fqcn);
        assertEquals("Patient", t.simpleName);
        assertEquals(Field.Origin.GETTER, member(t, "name").orElseThrow().origin);
        assertEquals("int", member(t, "ageYears").orElseThrow().type);
        assertEquals(Field.Origin.GETTER, member(t, "active").orElseThrow().origin);
        assertTrue(t.constructors.contains("Patient(String, int)"),
            () -> "constructors=" + t.constructors);
    }

    @Test
    void parsesEnumConstantsWithArgs() {
        JavaSourceType t = only(
            "package com.example;\n"
            + "public enum Severity {\n"
            + "  LOW(1), HIGH(3);\n"
            + "  private final int weight;\n"
            + "  Severity(int weight) { this.weight = weight; }\n"
            + "  public int getWeight() { return weight; }\n"
            + "}\n");
        assertTrue(t.isEnum);
        Field low = member(t, "LOW").orElseThrow();
        assertEquals(Field.Origin.ENUM_CONSTANT, low.origin);
        assertEquals("1", low.args);
        assertEquals("Severity", low.type);
        assertEquals(Field.Origin.GETTER, member(t, "weight").orElseThrow().origin);
    }

    @Test
    void parsesInterfaceAndRecordNames() {
        assertEquals("com.example.Repo", only(
            "package com.example;\npublic interface Repo { String id(); }\n").fqcn);
        JavaSourceType rec = only(
            "package com.example;\npublic record Point(int x, int y) { }\n");
        assertEquals("com.example.Point", rec.fqcn);
        assertEquals("int", member(rec, "x").orElseThrow().type);
        assertTrue(rec.constructors.contains("Point(int, int)"),
            () -> "constructors=" + rec.constructors);
    }

    @Test
    void capturesExtendsAndPosition() {
        JavaSourceType t = only(
            "package com.example;\n"
            + "public class Child extends com.example.Parent {\n"
            + "}\n");
        assertEquals("Parent", t.extendsSimpleName);
        assertEquals(1, t.declLine); // 0-based; "public class Child" is line index 1
    }

    @Test
    void toleratesGarbageAndDefaultPackage() {
        assertTrue(JavaSourceTypeParser.parse("").isEmpty());
        assertTrue(JavaSourceTypeParser.parse("this is not java {{{").isEmpty()
            || !JavaSourceTypeParser.parse("this is not java {{{").isEmpty()); // never throws
        JavaSourceType t = only("public class NoPkg { int x; }");
        assertEquals("NoPkg", t.fqcn); // default package => fqcn == simpleName
    }
}
