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

import java.util.Map;
import java.util.Optional;

public class NamespaceHandler {

    final static String INCLUDED_NAMESPACE = "included";

    public static String addIncludedNamespace(final Map<String, String> nsContext,
                                              final String namespace) {

        final Optional<Map.Entry<String, String>> existingNamespace = getAlias(nsContext, namespace);
        if (existingNamespace.isPresent()) {
            return existingNamespace.get().getKey();
        }

        final String alias = getFreeIncludedNamespaceId(nsContext);
        nsContext.put(alias, namespace);
        return alias;
    }

    static String getFreeIncludedNamespaceId(final Map<String, String> nsContext) {

        final int includedNamespaces = (int) nsContext.keySet().stream().filter(k -> k.startsWith(INCLUDED_NAMESPACE)).count();
        String freeAliasCandidate = INCLUDED_NAMESPACE + 1;
        for (int i = 1; i <= includedNamespaces; i++) {
            if (!nsContext.containsKey(freeAliasCandidate)) {
                return freeAliasCandidate;
            }
            freeAliasCandidate = INCLUDED_NAMESPACE + (i + 1);
        }

        return freeAliasCandidate;
    }

    static void removeIncludedNamespace(final Map<String, String> nsContext, final String namespace) {

        final Optional<Map.Entry<String, String>> namespaceToRemove = getAlias(nsContext, namespace);
        namespaceToRemove.ifPresent(stringStringEntry -> nsContext.remove(stringStringEntry.getKey()));
    }

    public static Optional<Map.Entry<String, String>> getAlias(final Map<String, String> nsContext,
                                                               final String namespace) {
        return nsContext.entrySet()
                   .stream()
                   .filter(k -> k.getValue().equals(namespace))
                   .findFirst();
    }
}
