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

package org.kie.workbench.common.stunner.bpmn.backend.forms.conditions.parser;

import java.text.ParseException;

import javax.lang.model.SourceVersion;

import org.apache.commons.lang3.ArrayUtils;

public class ParsingUtils {

    public static String parseJavaName(final String token, final int startIndex, final char[] stopCharacters) throws ParseException {
        if (startIndex < 0 || startIndex >= token.length()) {
            throw new IndexOutOfBoundsException("startIndex: " + startIndex + " exceeds token bounds: " + token);
        }
        final StringBuilder javaName = new StringBuilder();
        char currentChar;
        int currentIndex = startIndex;
        while (currentIndex < token.length()) {
            currentChar = token.charAt(currentIndex);
            if (ArrayUtils.contains(stopCharacters, currentChar)) {
                break;
            } else {
                javaName.append(currentChar);
            }
            currentIndex++;
        }

        if (javaName.length() == 0) {
            throw new ParseException("Expected java name was not found at position: " + startIndex, startIndex);
        } else if (!SourceVersion.isName(javaName)) {
            throw new ParseException("Invalid java name was found at position: " + startIndex, startIndex);
        }
        return javaName.toString();
    }
}
