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

package org.uberfire.ext.editor.commons.client.template.mustache;

import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.ScriptInjector;
import org.uberfire.ext.editor.commons.template.TemplateRenderer;
import org.uberfire.ext.editor.commons.template.mustache.MustacheTemplateRenderer;

/**
 * Client implementation for {@link TemplateRenderer} based on MustacheJS engine.
 * See {@linktourl https://github.com/janl/mustache.js}
 */
@ApplicationScoped
public class ClientMustacheTemplateRenderer implements MustacheTemplateRenderer<Object> {

    private final Function<String, ScriptInjector.FromString> scriptInjector;
    private final Supplier<MustacheSource> sourceSupplier;

    @Inject
    public ClientMustacheTemplateRenderer() {
        this(() -> GWT.create(MustacheSource.class), ScriptInjector::fromString);
    }

    protected ClientMustacheTemplateRenderer(final Supplier<MustacheSource> sourceSupplier, final Function<String,
            ScriptInjector.FromString> scriptInjector) {
        this.sourceSupplier = sourceSupplier;
        this.scriptInjector = scriptInjector;
    }

    @PostConstruct
    protected void init() {
        //Injecting the JS native script
        final MustacheSource source = sourceSupplier.get();
        inject(source.mustache().getText());
    }

    private void inject(final String raw) {
        final ScriptInjector.FromString js = scriptInjector.apply(raw);
        js.setWindow(ScriptInjector.TOP_WINDOW).setRemoveTag(false).inject();
    }

    public String render(final String template, final Object data) {
        return Mustache.to_html(template, data);
    }
}
