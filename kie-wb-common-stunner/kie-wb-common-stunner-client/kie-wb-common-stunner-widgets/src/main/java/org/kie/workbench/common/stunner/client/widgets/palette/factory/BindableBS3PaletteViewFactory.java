/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.widgets.palette.factory;

import java.util.Map;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.stunner.client.widgets.palette.factory.icons.IconRenderer;
import org.kie.workbench.common.stunner.client.widgets.palette.factory.icons.IconResource;
import org.kie.workbench.common.stunner.client.widgets.palette.factory.icons.PaletteIconSettings;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;

public abstract class BindableBS3PaletteViewFactory<V extends IsWidget> implements BS3PaletteViewFactory {

    protected abstract Class<?> getDefinitionSetType();

    protected abstract Class<? extends IconRenderer> getPaletteIconRendererType();

    protected abstract Map<String, IconResource> getCategoryIconResources();

    protected abstract Map<String, IconResource> getDefinitionIconResources();

    @Override
    public PaletteIconSettings getCategoryIconSettings(String categoryId) {
        PaletteIconSettings settings = getIconSettings(categoryId,
                                                       getCategoryIconResources());
        if (settings != null) {
            return settings;
        }
        return null;
    }

    @Override
    public PaletteIconSettings getDefinitionIconSettings(String defSetId,
                                                         String itemId) {
        return getIconSettings(itemId,
                               getDefinitionIconResources());
    }

    protected PaletteIconSettings getIconSettings(String id,
                                                  Map<String, IconResource> iconResources) {
        if (iconResources != null) {
            IconResource resource = iconResources.get(id);
            if (resource != null) {
                return new PaletteIconSettings(getPaletteIconRendererType(),
                                               resource);
            }
        }
        return null;
    }

    @Override
    public boolean accepts(final String id) {
        final String dId = getDefinitionSetId(getDefinitionSetType());
        return null != id && id.equals(dId);
    }

    private String getDefinitionSetId(final Class<?> type) {
        return BindableAdapterUtils.getDefinitionSetId(type);
    }
}
