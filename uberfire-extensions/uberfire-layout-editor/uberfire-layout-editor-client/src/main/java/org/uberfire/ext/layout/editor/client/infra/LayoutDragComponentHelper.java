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

package org.uberfire.ext.layout.editor.client.infra;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.experimental.client.service.ClientExperimentalFeaturesRegistryService;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.client.api.HasDragAndDropSettings;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.infra.experimental.DisabledExperimentalLayoutComponent;

@Dependent
public class LayoutDragComponentHelper {

    private SyncBeanManager beanManager;
    private ClientExperimentalFeaturesRegistryService experimentalFeaturesRegistryService;

    private DndDataJSONConverter converter = new DndDataJSONConverter();
    private List<Object> instances = new ArrayList<>();

    @Inject
    public LayoutDragComponentHelper(SyncBeanManager beanManager, ClientExperimentalFeaturesRegistryService experimentalFeaturesRegistryService) {
        this.beanManager = beanManager;
        this.experimentalFeaturesRegistryService = experimentalFeaturesRegistryService;
    }

    @PreDestroy
    public void destroy() {
        for (Object instance : instances) {
            destroy(instance);
        }
    }

    public LayoutDragComponent lookupDragTypeBean(String dragTypeClassName) {
        return lookupBean(dragTypeClassName);
    }

    private LayoutDragComponent lookupBean(String dragTypeClassName) {
        Collection<SyncBeanDef<LayoutDragComponent>> iocBeanDefs = beanManager.lookupBeans(LayoutDragComponent.class);

        Optional<SyncBeanDef<LayoutDragComponent>> optional = iocBeanDefs.stream()
                .filter(syncBeanDefBeanClassNamePredicate(dragTypeClassName))
                .findAny();

        if (optional.isPresent()) {
            SyncBeanDef<LayoutDragComponent> beanDef = optional.get();

            LayoutDragComponent instance;

            if (isAnEnabledExperimentalFeature(beanDef)) {
                instance = beanDef.getInstance();
            } else {
                DisabledExperimentalLayoutComponent disabled = beanManager.lookupBean(DisabledExperimentalLayoutComponent.class).newInstance();
                disabled.setFeatureId(beanDef.getBeanClass().getName());

                instance = disabled;
            }
            instances.add(instance);
            return instance;
        }

        return null;
    }

    boolean isAnEnabledExperimentalFeature(SyncBeanDef<LayoutDragComponent> beanDef) {
        return experimentalFeaturesRegistryService.isFeatureEnabled(beanDef.getBeanClass().getName());
    }

    Predicate<SyncBeanDef<LayoutDragComponent>> syncBeanDefBeanClassNamePredicate(String dragTypeClassName) {
        return beanDef -> beanDef.getBeanClass().getName().equals(dragTypeClassName);
    }

    public String getRealBeanClass(LayoutDragComponent instance) {
        return Factory.maybeUnwrapProxy(instance).getClass().getName();
    }

    public LayoutComponent getLayoutComponentFromDrop(String dropData) {
        LayoutDragComponent component = extractComponent(dropData);
        LayoutComponent layoutComponent = getLayoutComponent(component);
        return layoutComponent;
    }

    public LayoutComponent getLayoutComponent(LayoutDragComponent dragComponent) {

        LayoutComponent layoutComponent = new LayoutComponent(getRealBeanClass(dragComponent));

        if (dragComponent instanceof HasDragAndDropSettings) {
            Map<String, String> properties = ((HasDragAndDropSettings) dragComponent).getMapSettings();

            if (properties != null) {
                layoutComponent.addProperties(properties);
            }
        }

        return layoutComponent;
    }

    private LayoutDragComponent extractComponent(String dropData) {
        return converter
                .readJSONDragComponent(dropData);
    }

    private boolean hasComponent(LayoutComponent component) {
        return component != null;
    }

    protected void destroy(Object o) {
        BeanHelper.destroy(o);
    }
}
