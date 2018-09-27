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

package org.kie.workbench.common.services.verifier.reporting.client.reporting;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class NoteBuilder<T> {

    private final SafeHtmlBuilder htmlBuilder;
    private final T parent;

    public NoteBuilder(final SafeHtmlBuilder htmlBuilder,
                       final T parent) {
        this.htmlBuilder = htmlBuilder;
        this.parent = parent;
        htmlBuilder.appendHtmlConstant("<blockquote>");
    }

    public HTMLTableBuilder<NoteBuilder<T>> startExampleTable() {
        return new HTMLTableBuilder<>(htmlBuilder,
                                      this);
    }

    public NoteBuilder<T> addParagraph(final String text) {
        Util.addParagraph(htmlBuilder,
                          text);
        return this;
    }

    public T end() {
        htmlBuilder.appendHtmlConstant("</blockquote>");
        return parent;
    }
}