/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.widgets.components.glyph;

import java.util.function.Supplier;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.Icon;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.core.client.components.glyph.DOMGlyphRenderer;
import org.kie.workbench.common.stunner.core.client.components.views.WidgetElementRendererView;
import org.uberfire.mvp.Command;

/**
 * Renders a BS3 icon using the given icon type from the glyph definition.
 */
@Dependent
public class BS3IconTypeGlyphRenderer implements DOMGlyphRenderer<BS3IconTypeGlyph> {

    private final Supplier<WidgetElementRendererView> viewInstanceSupplier;
    private final Command viewInstancesDestroyer;

    protected BS3IconTypeGlyphRenderer() {
        this.viewInstanceSupplier = null;
        this.viewInstancesDestroyer = null;
    }

    @Inject
    public BS3IconTypeGlyphRenderer(final @Any ManagedInstance<WidgetElementRendererView> viewInstances) {
        this.viewInstanceSupplier = viewInstances::get;
        this.viewInstancesDestroyer = viewInstances::destroyAll;
    }

    BS3IconTypeGlyphRenderer(final Supplier<WidgetElementRendererView> viewInstanceSupplier,
                             final Command viewInstancesDestroyer) {
        this.viewInstanceSupplier = viewInstanceSupplier;
        this.viewInstancesDestroyer = viewInstancesDestroyer;
    }

    @Override
    public Class<BS3IconTypeGlyph> getGlyphType() {
        return BS3IconTypeGlyph.class;
    }

    @Override
    public IsElement render(final BS3IconTypeGlyph glyph,
                            final double width,
                            final double height) {
        final WidgetElementRendererView view = viewInstanceSupplier.get();
        final Icon icon = new Icon(glyph.getIconType());
        return view.setWidget(icon);
    }

    @PreDestroy
    public void destroy() {
        viewInstancesDestroyer.execute();
    }
}
