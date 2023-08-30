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


package org.appformer.client.context;

import java.util.stream.Stream;

public enum Channel {

    DEFAULT("DEFAULT"),
    ONLINE("ONLINE"),
    VSCODE_DESKTOP("VSCODE_DESKTOP"),
    VSCODE_WEB("VSCODE_WEB"),
    GITHUB("GITHUB"),
    EMBEDDED("EMBEDDED"),
    ONLINE_MULTI_FILE("ONLINE_MULTI_FILE");

    private final String name;

    Channel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Channel withName(String name) {
        return Stream.of(Channel.values())
                .filter(channel -> channel.getName().equalsIgnoreCase(name))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Name not recognized: " + name));
    }

}
