/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.services.verifier.plugin.client;

import java.util.ArrayList;
import java.util.List;

public class Logger {

    private static List<String> list = new ArrayList();


    public static void add( final String message ) {
        list.add( message );
    }

    public static String log() {
        final StringBuilder builder = new StringBuilder();

        for ( final String line : list ) {
            builder.append( line );
            builder.append( "\n" );
        }

        final String log = builder.toString();
        list.clear();
        return log;
    }

    public static boolean isEmpty() {
        return list.isEmpty();
    }
}
