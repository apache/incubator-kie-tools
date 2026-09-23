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

/**
 * A Java type parsed from {@code .java} source (top-level class, enum, interface,
 * or record). Carries the instance members, the statics, constructor signatures,
 * supertype simple names, and the declaration's source position, so the
 * source-typing layer can
 * feed completion/hover/lint/definition before a compile exists. Immutable.
 */
public final class JavaSourceType {

    public final String fqcn;
    public final String simpleName;
    public final boolean isEnum;
    /** Simple name of the {@code extends} supertype, or {@code null}. */
    public final String extendsSimpleName;
    /** Simple names of implemented (or, for interfaces, extended) types. */
    public final List<String> interfaceSimpleNames;
    /** Fields, bean-property getters, and enum constants, deduped by name. */
    public final List<Field> members;
    /** Constructor signatures, e.g. {@code "Foo(int, String)"}. */
    public final List<String> constructors;
    /**
     * The public static fields, with their types. Not members — a static is not
     * a fact property — but reachable through {@code Type.NAME}, which is what
     * {@link JavaSourceTypeIndex#staticFieldsOf} and
     * {@link JavaSourceTypeIndex#memberNames} answer for.
     */
    public final List<Field> staticFields;
    public final List<String> staticMethods;
    /** 0-based line/column of the type's name token. */
    public final int declLine;
    public final int declColumn;

    JavaSourceType(String fqcn, String simpleName, boolean isEnum, String extendsSimpleName,
                   List<String> interfaceSimpleNames, List<Field> members,
                   List<String> constructors, List<Field> staticFields,
                   List<String> staticMethods, int declLine, int declColumn) {
        this.fqcn = fqcn;
        this.simpleName = simpleName;
        this.isEnum = isEnum;
        this.extendsSimpleName = extendsSimpleName;
        this.interfaceSimpleNames = List.copyOf(interfaceSimpleNames);
        this.members = List.copyOf(members);
        this.constructors = List.copyOf(constructors);
        this.staticFields = List.copyOf(staticFields);
        this.staticMethods = List.copyOf(staticMethods);
        this.declLine = declLine;
        this.declColumn = declColumn;
    }
}
