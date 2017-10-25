/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.guvnor.common.services.project.backend.server.utils;

import javax.lang.model.SourceVersion;

/**
 * Utilities to manipulate Java/Maven identifiers
 */
public class IdentifierUtils {

    /**
     * Convert Maven Identifiers to equivalents compatible with Java
     * @param identifiers
     * @return
     */
    public static String[] convertMavenIdentifierToJavaIdentifier(final String[] identifiers) {
        if (identifiers == null || identifiers.length < 1) {
            return new String[0];
        }
        final String[] legalIdentifiers = new String[identifiers.length];
        for (int idx = 0; idx < identifiers.length; idx++) {
            final String identifier = identifiers[idx];
            final StringBuilder legalIdentifier = new StringBuilder("");
            Character c = identifier.charAt(0);
            if (!Character.isJavaIdentifierStart(c)) {
                legalIdentifier.append("_");
            } else {
                legalIdentifier.append(c);
            }
            for (int i = 1; i < identifier.length(); i++) {
                c = identifier.charAt(i);
                if (Character.isJavaIdentifierPart(c)) {
                    legalIdentifier.append(c);
                } else {
                    legalIdentifier.append("_");
                }
            }
            if (SourceVersion.isKeyword(legalIdentifier.toString())) {
                legalIdentifier.insert(0,
                                       "_");
            }
            legalIdentifiers[idx] = legalIdentifier.toString();
        }
        return legalIdentifiers;
    }
}
