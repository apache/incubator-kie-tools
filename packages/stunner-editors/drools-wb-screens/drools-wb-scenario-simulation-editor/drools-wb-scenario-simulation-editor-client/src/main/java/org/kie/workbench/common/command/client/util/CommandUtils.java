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

package org.kie.workbench.common.command.client.util;

import java.util.LinkedList;
import java.util.List;

import org.kie.workbench.common.command.client.CommandResult;

public class CommandUtils {

    public static boolean isError(final CommandResult<?> result) {
        return isCommandResultError(result);
    }

    public static <V> List<V> toList(final Iterable<V> iterable) {
        final List<V> result = new LinkedList<>();
        iterable.iterator().forEachRemaining(result::add);
        return result;
    }

    private static boolean isCommandResultError(final CommandResult<?> result) {
        return result != null && CommandResult.Type.ERROR.equals(result.getType());
    }
}
