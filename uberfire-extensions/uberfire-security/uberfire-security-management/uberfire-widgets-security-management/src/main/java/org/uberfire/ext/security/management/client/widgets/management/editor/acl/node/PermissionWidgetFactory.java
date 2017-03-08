/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.security.management.client.widgets.management.editor.acl.node;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.security.client.authz.tree.PermissionNode;
import org.uberfire.security.client.authz.tree.impl.PermissionLeafNode;

@ApplicationScoped
public class PermissionWidgetFactory {

    SyncBeanManager beanManager;

    @Inject
    public PermissionWidgetFactory(SyncBeanManager beanManager) {
        this.beanManager = beanManager;
    }

    public PermissionNodeEditor createEditor(PermissionNode node) {
        if (node instanceof PermissionLeafNode) {
            return beanManager.lookupBean(LeafPermissionNodeEditor.class).newInstance();
        }
        return beanManager.lookupBean(MultiplePermissionNodeEditor.class).newInstance();
    }

    public PermissionNodeViewer createViewer(PermissionNode node) {
        if (node instanceof PermissionLeafNode) {
            return beanManager.lookupBean(LeafPermissionNodeViewer.class).newInstance();
        }
        return beanManager.lookupBean(MultiplePermissionNodeViewer.class).newInstance();
    }

    public PermissionSwitch createSwitch() {
        return beanManager.lookupBean(PermissionSwitch.class).newInstance();
    }

    public PermissionExceptionSwitch createExceptionSwitch() {
        return beanManager.lookupBean(PermissionExceptionSwitch.class).newInstance();
    }
}
