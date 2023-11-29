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
package org.yard.validator.util;

import java.util.ArrayList;
import java.util.Collection;

public class Logger {

    private static final Collection<Callback> callbacks = new ArrayList<>();

    public static void startLogging(final Callback callback) {
        Logger.callbacks.add(callback);
    }

    public static void log(final String s) {
        for (Callback callback : callbacks) {
            callback.callback(getTime() + " " + s);
        }
    }

    private static String getTime() {
        return System.currentTimeMillis() + "";
    }
}
