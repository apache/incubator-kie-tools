/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uberfire.client.workbench.widgets.menu.impl;

import org.uberfire.client.mvp.Command;
import org.uberfire.client.workbench.widgets.menu.MenuItemCommand;
import org.uberfire.commons.util.Preconditions;

/**
 * Default implementation of MenuItemCommand
 */
public class DefaultMenuItemCommand extends DefaultMenuItem
    implements
    MenuItemCommand {

    private final Command command;

    public DefaultMenuItemCommand(final String caption,
                                  final Command command) {

        super( caption );
        Preconditions.checkNotNull( "command",
                                    command );
        this.command = command;
    }

    /**
     * @return the command
     */
    @Override
    public Command getCommand() {
        return command;
    }

    @Override
    public String getSignatureId() {
        return DefaultMenuItemCommand.class.getName() + "#" + caption;
    }

}
