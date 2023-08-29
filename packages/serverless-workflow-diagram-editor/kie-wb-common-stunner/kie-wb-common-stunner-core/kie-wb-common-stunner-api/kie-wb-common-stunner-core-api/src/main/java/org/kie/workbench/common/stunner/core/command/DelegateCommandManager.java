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


package org.kie.workbench.common.stunner.core.command;

public abstract class DelegateCommandManager<C, V> implements CommandManager<C, V> {

    protected abstract CommandManager<C, V> getDelegate();

    @Override
    public CommandResult<V> allow(final C context,
                                  final Command<C, V> command) {
        if (null != getDelegate()) {
            final CommandResult<V> r = getDelegate().allow(context,
                                                           command);
            postAllow(context,
                      command,
                      r);
            return r;
        }
        return null;
    }

    protected void postAllow(final C context,
                             final Command<C, V> command,
                             final CommandResult<V> result) {
    }

    @Override
    public CommandResult<V> execute(final C context,
                                    final Command<C, V> command) {
        if (null != getDelegate()) {
            final CommandResult<V> r = getDelegate().execute(context,
                                                             command);
            postExecute(context,
                        command,
                        r);
            return r;
        }
        return null;
    }

    protected void postExecute(final C context,
                               final Command<C, V> command,
                               final CommandResult<V> result) {
    }

    @Override
    public CommandResult<V> undo(final C context,
                                 final Command<C, V> command) {
        if (null != getDelegate()) {
            final CommandResult<V> r = getDelegate().undo(context,
                                                          command);
            postUndo(context,
                     command,
                     r);
            return r;
        }
        return null;
    }

    protected void postUndo(final C context,
                            final Command<C, V> command,
                            final CommandResult<V> result) {
    }
}
