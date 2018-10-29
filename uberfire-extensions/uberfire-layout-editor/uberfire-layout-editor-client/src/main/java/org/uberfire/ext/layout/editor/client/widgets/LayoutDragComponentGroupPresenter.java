/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.layout.editor.client.widgets;

import java.util.Map;
import java.util.Random;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponentGroup;
import org.uberfire.ext.layout.editor.client.infra.UniqueIDGenerator;

@Dependent
public class LayoutDragComponentGroupPresenter {

    private final View view;
    private LayoutDragComponentGroup group;
    private UniqueIDGenerator idGenerator = new UniqueIDGenerator();

    @Inject
    public LayoutDragComponentGroupPresenter(final View view) {
        this.view = view;
        view.init(this);
    }

    public void init(LayoutDragComponentGroup group) {
        this.group = group;
        view.setTitle(idGenerator.createAccordionID(group.getName()), group.getName());
        view.setExpanded(group.isExpandeByDefault());
        view.addComponents(group.getComponents());
    }

    public void setExpanded(boolean expanded) {
        view.setExpanded(expanded);
    }

    public void addComponent(String componentId,
                             LayoutDragComponent component) {
        if (view.hasComponent(componentId)) {
            view.setComponentVisible(componentId, true);
        } else {
            view.addComponent(componentId, component);
        }
    }

    public void removeComponent(String componentId) {
        if (view.hasComponent(componentId)) {
            view.setComponentVisible(componentId, false);
        }
    }

    public boolean hasComponent(String componentId) {
        return view.hasComponent(componentId);
    }

    public LayoutDragComponentGroupPresenter.View getView() {
        return view;
    }

    public interface View extends UberElement<LayoutDragComponentGroupPresenter> {

        void setTitle(String id,
                      String name);

        void addComponents(Map<String, LayoutDragComponent> components);

        void addComponent(String componentId,
                          LayoutDragComponent component);

        void removeComponent(String componentId);

        void setComponentVisible(String componentId, boolean visible);

        boolean hasComponent(String componentId);

        void setExpanded(boolean expanded);
    }
}