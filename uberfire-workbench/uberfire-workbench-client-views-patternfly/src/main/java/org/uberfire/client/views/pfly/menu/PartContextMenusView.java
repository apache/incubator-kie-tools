/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.views.pfly.menu;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.client.workbench.widgets.menu.PartContextMenusPresenter;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
public class PartContextMenusView
        extends Composite
        implements PartContextMenusPresenter.View {

    @Inject
    private AuthorizationManager authzManager;

    @Inject
    private User identity;

    @Override
    public void buildMenu( final Menus menus ) {
    }

    @Override
    public void clear() {
    }

}
