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

package org.kie.workbench.common.command.client;

public interface Command<T, V> {

    /**
     * Check whether the command operation is allowed.
     * Does not perform any update or mutation.
     */
    CommandResult<V> allow(final T context);

    /**
     * Executes the command operation.
     * Does perform some update or mutation.
     */
    CommandResult<V> execute(final T context);

    /**
     * Undo the changes done by this command execution.
     */
    CommandResult<V> undo(final T context);
}
