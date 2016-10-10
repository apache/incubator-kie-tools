/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.command.batch;

import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandManagerListener;
import org.kie.workbench.common.stunner.core.command.CommandResult;

import java.util.Collection;

public interface BatchCommandManagerListener<T, V> extends CommandManagerListener<T, V> {

    void onExecuteBatch( T context, Collection<Command<T, V>> commands, BatchCommandResult<V> result );

    void onUndoBatch( T context, Collection<Command<T, V>> commands, CommandResult<V> result );

}
