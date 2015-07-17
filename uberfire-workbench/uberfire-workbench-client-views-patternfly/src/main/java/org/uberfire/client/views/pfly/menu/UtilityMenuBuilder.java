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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

import static org.uberfire.commons.validation.PortablePreconditions.*;

public class UtilityMenuBuilder implements MenuFactory.CustomMenuBuilder, MenuFactory.CommandMenu {

    public static UtilityMenuBuilder newUtilityMenu( final String caption ) {
        return new UtilityMenuBuilder( caption );
    }

    private String caption;

    private Command command;

    public UtilityMenuBuilder( final String caption ) {
        this.caption = caption;
    }

    @Override
    public UtilityMenuBuilder respondsWith( final Command command ) {
        this.command = checkNotNull( "command", command );
        return this;
    }

    @Override
    public void push( MenuFactory.CustomMenuBuilder element ) {

    }

    @Override
    public MenuItem build() {
        return new BaseMenuCustom<UtilityMenu>() {
            @Override
            public UtilityMenu build() {
                final UtilityMenu menu = new UtilityMenu( caption );
                if ( command != null ) {
                    menu.addClickHandler( new ClickHandler() {
                        @Override
                        public void onClick( ClickEvent event ) {
                            command.execute();
                        }
                    } );
                }
                return menu;
            }
        };
    }
}
