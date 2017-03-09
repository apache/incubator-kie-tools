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

package org.kie.workbench.common.stunner.cm.client.palette;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.constants.IconType;
import org.kie.workbench.common.stunner.bpmn.definition.Categories;
import org.kie.workbench.common.stunner.client.widgets.palette.factory.BindableBS3PaletteGlyphViewFactory;
import org.kie.workbench.common.stunner.client.widgets.palette.factory.icons.IconRenderer;
import org.kie.workbench.common.stunner.client.widgets.palette.factory.icons.IconResource;
import org.kie.workbench.common.stunner.client.widgets.palette.factory.icons.bs3.BS3IconRenderer;
import org.kie.workbench.common.stunner.cm.CaseManagementDefinitionSet;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;

@ApplicationScoped
public class CaseManagementBS3PaletteViewFactory extends BindableBS3PaletteGlyphViewFactory {

    private final static Map<String, IconResource> CATEGORY_RENDERERS_SETTINGS = new HashMap<String, IconResource>() {{
        put(Categories.SUBPROCESSES,
            new IconResource(IconType.STAR));
        put(Categories.ACTIVITIES,
            new IconResource(IconType.TASKS));
    }};

    protected CaseManagementBS3PaletteViewFactory() {
        this(null);
    }

    @Inject
    public CaseManagementBS3PaletteViewFactory(final ShapeManager shapeManager) {
        super(shapeManager);
    }

    @Override
    protected Class<?> getDefinitionSetType() {
        return CaseManagementDefinitionSet.class;
    }

    @Override
    protected Class<? extends IconRenderer> getPaletteIconRendererType() {
        return BS3IconRenderer.class;
    }

    @Override
    protected Map<String, IconResource> getCategoryIconResources() {
        return CATEGORY_RENDERERS_SETTINGS;
    }

    @Override
    protected Map<String, IconResource> getDefinitionIconResources() {
        return null;
    }
}
