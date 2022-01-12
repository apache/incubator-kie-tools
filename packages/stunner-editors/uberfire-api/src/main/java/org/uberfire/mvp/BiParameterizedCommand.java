/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.mvp;

import java.util.function.BiConsumer;

/**
 * A command representing a future activity, with two parameters. Similar to {@link BiConsumer}
 * This was deliberately created in addition to the existing GWT Command to allow better
 * re-use of menu structures when a WorkbenchPart is embedded within Eclipse.
 * This is the two-arity specialization of {@link ParameterizedCommand}
 */
@FunctionalInterface
public interface BiParameterizedCommand<T, U> {

    void execute(T parameter1,
                 U parameter2);

}
