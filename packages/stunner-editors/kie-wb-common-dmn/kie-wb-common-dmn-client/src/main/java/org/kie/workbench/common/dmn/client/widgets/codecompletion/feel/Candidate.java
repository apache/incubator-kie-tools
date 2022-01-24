/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.widgets.codecompletion.feel;

import java.util.Objects;

public class Candidate {

    private String label;
    private String insertText;
    private CompletionItemKind kind;

    public Candidate(final String label,
                     final String insertText,
                     final CompletionItemKind kind) {
        this.label = label;
        this.insertText = insertText;
        this.kind = kind;
    }

    public Candidate(final String text,
                     final CompletionItemKind kind) {
        this(text, text, kind);
    }

    public String getLabel() {
        return label;
    }

    public String getInsertText() {
        return insertText;
    }

    public CompletionItemKind getKind() {
        return kind;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Candidate candidate = (Candidate) o;
        return Objects.equals(insertText, candidate.insertText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(insertText);
    }

    @Override
    public String toString() {
        return "Candidate{" +
                "label='" + label + '\'' +
                ", insertText='" + insertText + '\'' +
                ", kind=" + kind +
                '}';
    }
}
