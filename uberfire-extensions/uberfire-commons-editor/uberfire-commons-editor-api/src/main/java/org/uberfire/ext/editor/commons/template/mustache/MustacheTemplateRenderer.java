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

package org.uberfire.ext.editor.commons.template.mustache;

import org.uberfire.ext.editor.commons.template.TemplateRenderer;

/**
 * Represents a {@link TemplateRenderer} based on Mustache engine.
 * May have implementations for Client and Backend.
 * <p>
 * See {@linktourl https://mustache.github.io/}
 * See {@linktourl https://github.com/janl/mustache.js}
 * See {@linktourl https://github.com/spullara/mustache.java}
 * @param <D> data model to be rendered on the template.
 */
public interface MustacheTemplateRenderer<D> extends TemplateRenderer<D> {

    String render(String template, D data);
}
