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

package org.uberfire.client.menu;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.ioc.client.container.IOC;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CustomSplashHelp implements MenuFactory.CustomMenuBuilder {

    @Override
    public void push( MenuFactory.CustomMenuBuilder element ) {

    }

    @Override
    public MenuItem build() {
        return new BaseMenuCustom<IsWidget>() {

            @Override
            public IsWidget build() {
                return IOC.getBeanManager().lookupBean( SplashScreenMenuPresenter.class ).getInstance();
            }

            @Override
            public MenuPosition getPosition() {
                return MenuPosition.LEFT;
            }
        };
    }
}
