/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.client.navbar;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;
import elemental2.dom.DomGlobal;
import org.dashbuilder.client.resources.i18n.AppConstants;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.security.shared.service.AuthenticationService;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuFactory.CustomMenuBuilder;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

@ApplicationScoped
public class LogoutMenuBuilder implements MenuFactory.CustomMenuBuilder {

    private AnchorListItem link = new AnchorListItem();
    
    @Inject
    private Caller<AuthenticationService> authService;

    @PostConstruct
    public void buildLink() {
        link.setIcon(IconType.SIGN_OUT);

        link.getWidget(0).setStyleName("nav-item-iconic"); // Fix for IE11
        link.setTitle(AppConstants.INSTANCE.logoutMenuTooltip());
        
        link.addClickHandler(e -> this.logout());

    }

    @Override
    public void push(CustomMenuBuilder element) {
        // do nothing
    }

    @Override
    public MenuItem build() {
        return new BaseMenuCustom<IsWidget>() {

            @Override
            public IsWidget build() {
                return link;
            }

            @Override
            public MenuPosition getPosition() {
                return MenuPosition.RIGHT;
            }
        };

    }
    
    private void logout() {
        authService.call(r -> {
            final String location = GWT.getModuleBaseURL().replaceFirst( "/" + GWT.getModuleName() + "/", "/logout.jsp" );
            DomGlobal.window.location.assign(location);
        }).logout();
    }

}