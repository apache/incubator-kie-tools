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

package org.kie.workbench.common.stunner.client.widgets.palette.bs3.factory;

import java.util.Map;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;

public abstract class BindableBS3PaletteViewFactory<V extends IsWidget> implements BS3PaletteViewFactory {

    protected abstract Class<?> getDefinitionSetType();

    protected abstract Map<Class<?>, V> getDefinitionViews();

    protected abstract Map<String, V> getCategoryViews();

    protected abstract V resize(V widget,
                                int width,
                                int height);

    @Override
    public boolean accepts(final String id) {
        final String dId = getDefinitionSetId(getDefinitionSetType());
        return null != id && id.equals(dId);
    }

    @Override
    public IsWidget getCategoryView(final String defSetId,
                                    final String categoryId,
                                    final int width,
                                    final int height) {
        final Map.Entry<String, V> entry = getDCategoryViewEntry(categoryId);
        if (null != entry) {
            final V w = entry.getValue();
            return resize(w,
                          width,
                          height);
        }
        return null;
    }

    @Override
    public IsWidget getDefinitionView(final String defSetId,
                                      final String defId,
                                      final int width,
                                      final int height) {
        final Map.Entry<Class<?>, V> entry = getDefinitionViewEntry(defId);
        if (null != entry) {
            final V w = entry.getValue();
            return resize(w,
                          width,
                          height);
        }
        return null;
    }

    private Map.Entry<Class<?>, V> getDefinitionViewEntry(final String id) {
        final Map<Class<?>, V> map = getDefinitionViews();
        if (null != map && !map.isEmpty()) {
            for (final Map.Entry<Class<?>, V> entry : map.entrySet()) {
                final String _id = getDefinitionId(entry.getKey());
                if (_id.equals(id)) {
                    return entry;
                }
            }
        }
        return null;
    }

    private Map.Entry<String, V> getDCategoryViewEntry(final String id) {
        final Map<String, V> map = getCategoryViews();
        if (null != map && !map.isEmpty()) {
            for (final Map.Entry<String, V> entry : map.entrySet()) {
                if (entry.getKey().equals(id)) {
                    return entry;
                }
            }
        }
        return null;
    }

    private String getDefinitionSetId(final Class<?> type) {
        return BindableAdapterUtils.getDefinitionSetId(type);
    }

    private String getDefinitionId(final Class<?> type) {
        return BindableAdapterUtils.getDefinitionId(type);
    }
}
