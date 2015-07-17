/*
 *
 *  * Copyright 2012 JBoss Inc
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  * use this file except in compliance with the License. You may obtain a copy of
 *  * the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  * License for the specific language governing permissions and limitations under
 *  * the License.
 *
 */

package org.uberfire.client.views.pfly.menu;

import org.jboss.errai.ioc.client.container.IOC;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuVisitor;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

import static org.uberfire.commons.validation.PortablePreconditions.*;

/**
 * Created by Cristiano Nicolai.
 */
public class UserMenuBuilder implements MenuFactory.CustomMenuBuilder,
        MenuFactory.CommandMenu {

    public static UserMenuBuilder withUserMenu() {
        return new UserMenuBuilder();
    }

    private Command command;

    @Override
    public Object respondsWith( final Command command ) {
        this.command = checkNotNull( "command", command );
        return this;
    }

    @Override
    public void push( MenuFactory.CustomMenuBuilder element ) {
        throw new UnsupportedOperationException( "Not implemented." );
    }

    @Override
    public MenuItem build() {
        return new BaseMenuCustom<UserMenu>() {

            @Override
            public UserMenu build() {
                return IOC.getBeanManager().lookupBean( UserMenu.class ).getInstance();
            }

            @Override
            public void accept( MenuVisitor visitor ) {
                visitor.visit( this );
            }
        };
    }

}
