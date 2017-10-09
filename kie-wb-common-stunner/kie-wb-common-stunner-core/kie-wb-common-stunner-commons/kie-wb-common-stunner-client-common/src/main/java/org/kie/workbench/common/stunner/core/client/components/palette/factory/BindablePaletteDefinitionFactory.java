/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.components.palette.factory;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.components.palette.Palette;
import org.kie.workbench.common.stunner.core.client.components.palette.model.HasPaletteItems;
import org.kie.workbench.common.stunner.core.client.components.palette.model.PaletteDefinitionBuilder;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;

public abstract class BindablePaletteDefinitionFactory<B extends PaletteDefinitionBuilder, I extends HasPaletteItems, P extends Palette<I>>
        extends AbstractPaletteDefinitionFactory<B, I, P> {

    public BindablePaletteDefinitionFactory(final ShapeManager shapeManager,
                                            final B paletteBuilder,
                                            final ManagedInstance<P> palette) {
        super(shapeManager,
              paletteBuilder,
              palette);
    }

    protected abstract Class<?> getDefinitionSetType();

    protected abstract B newBuilder();

    @Override
    public boolean accepts(final String defSetId) {
        final String s = getId(getDefinitionSetType());
        return null != defSetId && defSetId.equals(s);
    }

    @Override
    public B newBuilder(final String defSetId) {
        return newBuilder();
    }

    protected String getId(final Class<?> defSetType) {
        return BindableAdapterUtils.getDefinitionSetId(defSetType);
    }
}
