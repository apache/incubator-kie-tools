/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.guvnor.structure.navigator;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;

@Portable
public class DataContent {

    private boolean isDirectory;
    private String lastMessage;
    private String lastCommiter;
    private String lastCommiterEmail;
    private String age;
    private Path path;

    public DataContent() {
    }

    public DataContent(boolean isDirectory,
                       String lastMessage,
                       String lastCommiter,
                       String lastCommiterEmail,
                       String age,
                       Path path) {
        this.isDirectory = isDirectory;
        this.lastMessage = lastMessage;
        this.lastCommiter = lastCommiter;
        this.lastCommiterEmail = lastCommiterEmail;
        this.age = age;
        this.path = path;
    }

    public String getLastCommiterEmail() {
        return lastCommiterEmail;
    }

    public String getAge() {
        return age;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getLastCommiter() {
        return lastCommiter;
    }

    public Path getPath() {
        return path;
    }
}
