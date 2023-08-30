/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.core.command.impl;

import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandListener;
import org.kie.workbench.common.stunner.core.command.CommandResult;

public class DelegateCommandManagerListener<C, V> implements CommandListener<C, V> {

    private final CommandListener<C, V> delegate;

    public DelegateCommandManagerListener(final CommandListener<C, V> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void onAllow(final C context,
                        final Command<C, V> command,
                        final CommandResult<V> result) {
        if (null != delegate) {
            delegate.onAllow(context,
                             command,
                             result);
        }
    }

    @Override
    public void onExecute(final C context,
                          final Command<C, V> command,
                          final CommandResult<V> result) {
        if (null != delegate) {
            delegate.onExecute(context,
                               command,
                               result);
        }
    }

    @Override
    public void onUndo(final C context,
                       final Command<C, V> command,
                       final CommandResult<V> result) {
        if (null != delegate) {
            delegate.onUndo(context,
                            command,
                            result);
        }
    }
}
