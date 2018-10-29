/*
 * Copyright 2017 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.plugin.client.perspective.editor.api;

import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponentGroup;
import org.uberfire.ext.plugin.client.resources.i18n.PerspectiveEditorPaletteI18n;

/**
 * {@link PerspectiveEditorComponentGroupProvider} holding all the available {@link PerspectiveEditorNavComponent} instances
 */
@ApplicationScoped
public class PerspectiveEditorNavGroupProvider implements PerspectiveEditorComponentGroupProvider {

    private SyncBeanManager beanManager;
    private PerspectiveEditorPaletteI18n i18n;

    @Inject
    public PerspectiveEditorNavGroupProvider(SyncBeanManager beanManager, PerspectiveEditorPaletteI18n i18n) {
        this.beanManager = beanManager;
        this.i18n = i18n;
    }

    @Override
    public String getName() {
        return i18n.navComponentGroupName();
    }

    @Override
    public LayoutDragComponentGroup getComponentGroup() {
        LayoutDragComponentGroup group = new LayoutDragComponentGroup(getName());
        Collection<SyncBeanDef<PerspectiveEditorNavComponent>> beanDefs = beanManager.lookupBeans(PerspectiveEditorNavComponent.class);
        for (SyncBeanDef<PerspectiveEditorNavComponent> beanDef : beanDefs) {
            PerspectiveEditorNavComponent dragComponent = beanDef.getInstance();
            group.addLayoutDragComponent(dragComponent.getDragComponentTitle(), dragComponent);
        }
        return group;
    }
}
