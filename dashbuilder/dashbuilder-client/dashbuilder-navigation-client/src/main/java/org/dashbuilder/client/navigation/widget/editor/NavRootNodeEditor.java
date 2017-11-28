/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.client.navigation.widget.editor;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.dashbuilder.client.navigation.event.NavItemEditCancelledEvent;
import org.dashbuilder.client.navigation.event.NavItemEditStartedEvent;
import org.dashbuilder.client.navigation.plugin.PerspectivePluginManager;
import org.dashbuilder.navigation.NavTree;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.authz.PerspectiveTreeProvider;
import org.uberfire.client.mvp.PlaceManager;

/**
 * An item editor used to handle {@link NavTree} root nodes
 */
@Dependent
public class NavRootNodeEditor extends NavItemEditor {

    public interface View extends NavItemEditor.View<NavRootNodeEditor> {

    }

    NavRootNodeEditorView view;

    @Inject
    public NavRootNodeEditor(NavRootNodeEditorView view,
                             SyncBeanManager beanManager,
                             PlaceManager placeManager,
                             PerspectiveTreeProvider perspectiveTreeProvider,
                             TargetPerspectiveEditor targetPerspectiveEditor,
                             PerspectivePluginManager perspectivePluginManager,
                             Event<NavItemEditStartedEvent> navItemEditStartedEvent,
                             Event<NavItemEditCancelledEvent> navItemEditCancelledEvent) {

        super(view, beanManager,
                placeManager,
                perspectiveTreeProvider,
                targetPerspectiveEditor,
                perspectivePluginManager,
                navItemEditStartedEvent,
                navItemEditCancelledEvent);

        this.view = view;
        this.view.init(this);

        super.setChildEditorClass(NavItemDefaultEditor.class);
    }
}
