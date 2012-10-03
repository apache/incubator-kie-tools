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
package org.uberfire.client.workbench.widgets.toolbar.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.uberfire.client.mvp.Command;
import org.uberfire.client.workbench.widgets.toolbar.ToolBarItem;
import org.uberfire.commons.util.Preconditions;

/**
 * Default implementation of ToolBarItem
 */
public class DefaultToolBarItem
    implements
    ToolBarItem {

    private boolean  isEnabled = true;

    private String[] roles     = new String[]{};

    private String   tooltip;

    private Command  command;

    public DefaultToolBarItem(final String tooltip,
                              final Command command) {
        Preconditions.checkNotNull( "tooltip",
                                    tooltip );
        Preconditions.checkNotNull( "command",
                                    command );
        this.tooltip = tooltip;
        this.command = command;
    }

    @Override
    public String getTooltip() {
        return this.tooltip;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    @Override
    public Command getCommand() {
        return this.command;
    }

    @Override
    public String getSignatureId() {
        return DefaultToolBarItem.class.getName();
    }

    @Override
    public void setRoles(final String[] roles) {
        this.roles = roles;
    }

    @Override
    public Collection<String> getRoles() {
        String[] clone = new String[roles.length];
        System.arraycopy( roles,
                          0,
                          clone,
                          0,
                          roles.length );

        return Arrays.asList( clone );
    }

    @Override
    public Collection<String> getTraits() {
        return Collections.emptyList();
    }

}
