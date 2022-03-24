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
import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;

@Dependent
public class LayoutDragComponentHelper {

    private SyncBeanManager beanManager;

    private List<Object> instances = new ArrayList<>();

    @Inject
    public LayoutDragComponentHelper(SyncBeanManager beanManager) {
        this.beanManager = beanManager;
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
            instance = beanDef.getInstance();
            instances.add(instance);
            return instance;
        }

        return null;
    }

    Predicate<SyncBeanDef<LayoutDragComponent>> syncBeanDefBeanClassNamePredicate(String dragTypeClassName) {
        return beanDef -> beanDef.getBeanClass().getName().equals(dragTypeClassName);
    }

    protected void destroy(Object o) {
        IOC.getBeanManager().destroyBean(o);
    }
}
