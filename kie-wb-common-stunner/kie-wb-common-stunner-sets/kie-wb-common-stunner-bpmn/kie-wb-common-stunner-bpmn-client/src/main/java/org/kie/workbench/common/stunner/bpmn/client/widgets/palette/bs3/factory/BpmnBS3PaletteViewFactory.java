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

package org.kie.workbench.common.stunner.bpmn.client.widgets.palette.bs3.factory;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.constants.IconSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.definition.Categories;
import org.kie.workbench.common.stunner.client.widgets.palette.bs3.factory.BindableBS3PaletteGlyphViewFactory;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;

@ApplicationScoped
public class BpmnBS3PaletteViewFactory extends BindableBS3PaletteGlyphViewFactory<Icon> {

    private final static Map<String, Icon> CATEGORY_VIEWS = new HashMap<String, Icon>() {{
        put(Categories.ACTIVITIES,
            getIcon(IconType.SQUARE));
        put(Categories.LANES,
            getIcon(IconType.COPY));
        put(Categories.GATEWAYS,
            getIcon(IconType.ASTERISK));
        put(Categories.EVENTS,
            getIcon(IconType.CIRCLE));
        put(Categories.CONNECTING_OBJECTS,
            getIcon(IconType.LONG_ARROW_RIGHT));
    }};

    protected BpmnBS3PaletteViewFactory() {
        this(null);
    }

    @Inject
    public BpmnBS3PaletteViewFactory(final ShapeManager shapeManager) {
        super(shapeManager);
    }

    @Override
    protected Class<?> getDefinitionSetType() {
        return BPMNDefinitionSet.class;
    }

    @Override
    protected Map<Class<?>, Icon> getDefinitionViews() {
        // Currently not using any bootstrap icons for the palette items.
        return null;
    }

    @Override
    protected Map<String, Icon> getCategoryViews() {
        return CATEGORY_VIEWS;
    }

    @Override
    protected Icon resize(final Icon widget,
                          final int width,
                          final int height) {
        widget.setSize(IconSize.LARGE);
        return widget;
    }

    private static Icon getIcon(final IconType iconType) {
        return new Icon(iconType);
    }
}
