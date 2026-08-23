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
import java.util.Set;

/**
 * A source of type members that {@link ClassMemberIndex} can consult on a
 * reflection miss (e.g. a type declared in {@code .java} source that has no
 * compiled classpath entry yet), so completion/hover/lint keep working before
 * a build exists.
 *
 * <p>Contracts mirror {@code ClassMemberIndex}'s own: {@link #membersOf} is
 * empty (never {@code null}) when {@code fqcn} is unknown; {@link #memberNames}
 * returns {@code null} when unknown, letting callers distinguish "no such
 * member" (a real typo) from "couldn't verify" (classpath/source gap) — see
 * {@link ClassMemberIndex#memberNames}; {@link #supertypesOf} returns direct
 * supertypes as fully-qualified names where resolvable within the source
 * index — unresolvable supertypes are omitted, never guessed at — empty when
 * unknown; {@link #constructorsOf} returns signatures, empty when unknown.
 */
public interface JavaMemberSource {

    List<Field> membersOf(String fqcn);

    Set<String> memberNames(String fqcn);

    List<String> supertypesOf(String fqcn);

    List<String> constructorsOf(String fqcn);
}
