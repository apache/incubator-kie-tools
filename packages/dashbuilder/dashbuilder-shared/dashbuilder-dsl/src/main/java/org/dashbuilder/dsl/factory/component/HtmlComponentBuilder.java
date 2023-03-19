/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dsl.factory.component;

public class HtmlComponentBuilder extends AbstractComponentBuilder<HtmlComponentBuilder> {

    private static final String DRAG_TYPE = "org.uberfire.ext.plugin.client.perspective.editor.layout.editor.HTMLLayoutDragComponent";

    public static final String HTML_CODE_PROP = "HTML_CODE";

    public static HtmlComponentBuilder create() {
        return new HtmlComponentBuilder();
    }

    public HtmlComponentBuilder html(String html) {
        property(HTML_CODE_PROP, html);
        return this;
    }

    @Override
    String getDragType() {
        return DRAG_TYPE;
    }

}