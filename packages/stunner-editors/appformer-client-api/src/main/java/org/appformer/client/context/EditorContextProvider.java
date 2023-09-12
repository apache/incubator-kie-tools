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

import java.util.Optional;

/**
 * Provide access to EditorContext properties
 */
public interface EditorContextProvider {

    /**
     * Access the channel where the editor is running (e.g. ONLINE, GITHUB or VSCODE).
     * @return The channel where the editor is running or DEFAULT if no channel is available.
     */
    Channel getChannel();

    /**
     * Access the operating system where the editor is running (e.g. MACOS, WINDOWS or LINUX).
     * @return The operating system where the editor is running or Optional.empty() if no information is available.
     */
    Optional<OperatingSystem> getOperatingSystem();

    /**
     * Checks if the editor is in read only mode.
     * @return If the editor is in read only mode or if is not.
     */
    boolean isReadOnly();
}
