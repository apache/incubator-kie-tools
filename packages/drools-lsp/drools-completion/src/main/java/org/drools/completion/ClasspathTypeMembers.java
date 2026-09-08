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

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Members of types the DRL does not declare, supplied by the host.
 *
 * <p>A DRL {@code declare} may extend a Java class
 * ({@code declare Assessment extends InputFact}), and a pattern may name a Java
 * class directly. In both cases the type's members live on the classpath — or,
 * before a build, in workspace sources — not in the declared-type index, so the
 * inheritance walks in {@link DRLDeclaredTypeParser#fieldsIncludingInherited}
 * and {@link LhsBindingResolver} terminate at the boundary and the inherited
 * members simply vanish from hover, completion, and binding types.
 *
 * <p>The host installs a lookup that resolves a type name to its members with
 * inherited ones already folded in; this class is the single seam both walks
 * consult. Absent a lookup every method is inert, so the module works
 * standalone, and in tests, with no host at all.
 *
 * <p>The lookup is keyed by the type name as the DRL writes it, which carries no
 * import context. A host resolving a bare simple name therefore cannot tell
 * which of two same-named classes the document meant, and answers for neither.
 * Closing that needs the document's imports to reach the lookup — the callers
 * are text-level and hold none today — so the seam would take a resolved name
 * instead of a simple one.
 */
public final class ClasspathTypeMembers {

    private static final Logger logger = Logger.getLogger(ClasspathTypeMembers.class.getName());

    private static volatile Function<String, List<Field>> lookup;

    private ClasspathTypeMembers() {
    }

    /**
     * Installs the members lookup, keyed by the type name as written in the DRL
     * (simple or qualified) and returning members with inherited ones folded in.
     * {@code null} clears it. The host is expected to read its live type indexes
     * inside the function so a rebuilt classpath needs no re-install.
     */
    public static void install(Function<String, List<Field>> memberLookup) {
        lookup = memberLookup;
    }

    /** Members of {@code typeName}, or empty when unknown or no lookup is installed. */
    static List<Field> membersOf(String typeName) {
        Function<String, List<Field>> current = lookup;
        if (current == null || typeName == null || typeName.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            List<Field> members = current.apply(typeName);
            return members == null ? Collections.emptyList() : members;
        } catch (RuntimeException e) {
            logger.fine(() -> "Member lookup failed for " + typeName + ": " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * {@code typeName} as a {@link DeclaredType} stand-in so the declared-type
     * walks can continue through it, or {@code null} when it has no known
     * members. The stand-in carries no supertype of its own: the member list is
     * already flattened, so a walk must stop there rather than looking for a
     * parent the host has already accounted for.
     */
    static DeclaredType asDeclaredType(String typeName) {
        List<Field> members = membersOf(typeName);
        if (members.isEmpty()) {
            return null;
        }
        return new DeclaredType(typeName, members, false, 0, 0);
    }
}
