/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.drools.lsp.server;

import java.util.List;

import com.google.gson.JsonObject;

/**
 * Wire types for the {@code drools/*} file-grouping methods.
 *
 * <p>Plain mutable beans rather than records, because lsp4j deserializes
 * incoming payloads by field. Paths cross this boundary as URI strings: the
 * protocol speaks URIs, the resolver speaks {@link java.nio.file.Path}, and
 * {@link DroolsLspServer} converts between them.
 */
public final class FileGroupingProtocol {

    private FileGroupingProtocol() {
    }

    /**
     * One entry of the {@code drools/fileGroups} response.
     *
     * <p>{@code kind} is a display noun, set only when the active resolver can be
     * more specific than "group" — {@code "KIE base"} for a group read from a
     * {@code kmodule.xml}. Null means the client should use its own neutral
     * wording. {@code declaredIn} is the file that declared the group, for
     * answering "why is this file in this group?".
     */
    public static class FileGroup {

        private List<String> files;
        private String kind;
        private String declaredIn;

        public FileGroup() {
        }

        FileGroup(List<String> files, String kind, String declaredIn) {
            this.files = files;
            this.kind = kind;
            this.declaredIn = declaredIn;
        }

        public List<String> getFiles() {
            return files;
        }

        public void setFiles(List<String> files) {
            this.files = files;
        }

        public String getKind() {
            return kind;
        }

        public void setKind(String kind) {
            this.kind = kind;
        }

        public String getDeclaredIn() {
            return declaredIn;
        }

        public void setDeclaredIn(String declaredIn) {
            this.declaredIn = declaredIn;
        }
    }

    /** Payload of {@code drools/setFileGroup}. A blank group clears the pin. */
    public static class FileGroupParams {

        private String uri;
        private String group;

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
        }
    }

    /**
     * Payload of {@code drools/setWorkspaceFiles}: every file the client
     * considers part of the workspace, as document URIs. Null withdraws the list
     * and lets the server discover files itself.
     */
    public static class WorkspaceFilesParams {

        private List<String> uris;

        public List<String> getUris() {
            return uris;
        }

        public void setUris(List<String> uris) {
            this.uris = uris;
        }
    }

    /** Payload of {@code drools/setGroupingConfig}: the setting's value, or null to clear it. */
    public static class GroupingConfigParams {

        private JsonObject config;

        public JsonObject getConfig() {
            return config;
        }

        public void setConfig(JsonObject config) {
            this.config = config;
        }
    }
}
