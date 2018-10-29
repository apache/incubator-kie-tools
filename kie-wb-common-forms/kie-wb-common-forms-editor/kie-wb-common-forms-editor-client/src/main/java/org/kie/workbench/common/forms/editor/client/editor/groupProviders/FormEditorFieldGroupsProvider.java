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

package org.kie.workbench.common.forms.editor.client.editor.groupProviders;

import java.util.Collection;

import org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponentGroup;
import org.uberfire.ext.layout.editor.client.widgets.LayoutComponentPaletteGroupProvider;

public class FormEditorFieldGroupsProvider implements LayoutComponentPaletteGroupProvider {

    private String name;
    private Collection<LayoutDragComponent> layoutComponents;

    public FormEditorFieldGroupsProvider(String name, Collection<LayoutDragComponent> layoutComponents) {
        this.name = name;
        this.layoutComponents = layoutComponents;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public LayoutDragComponentGroup getComponentGroup() {
        LayoutDragComponentGroup group = new LayoutDragComponentGroup(name);

        layoutComponents.stream()
                .forEach(component -> {
                    String id;
                    if(component instanceof EditorFieldLayoutComponent) {
                        id = ((EditorFieldLayoutComponent)component).getFieldId();
                    } else {
                        id = component.getClass().getName();
                    }
                    group.addLayoutDragComponent(id, component);
                });

        return group;
    }
}
