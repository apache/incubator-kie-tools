/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.layout.editor.client.infra;

import com.google.gwt.core.client.GWT;

import java.util.concurrent.atomic.AtomicLong;

public class UniqueIDGenerator {

    private AtomicLong counter = new AtomicLong();

    public String createContainerID() {
        return "container: " + String.valueOf( counter.getAndIncrement() );
    }

    public String createRowID( String containerID ) {
        return containerID + "|row: " + String.valueOf( counter.getAndIncrement() );
    }

    public String createColumnID( String rowID ) {
        return rowID + "|column: " + String.valueOf( counter.getAndIncrement() );
    }

}

