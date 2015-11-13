/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.editor.commons.client.history;

import java.lang.Override;import java.lang.String;import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.version.VersionService;
import org.uberfire.java.nio.base.version.VersionRecord;

class VersionServiceMock
        implements VersionService {

    private ArrayList<VersionRecord> versions = new ArrayList<VersionRecord>();

    private RemoteCallback callback;

    public VersionServiceMock(ArrayList<VersionRecord> versions) {
        this.versions = versions;
    }

    @Override public List<VersionRecord> getVersions(Path path) {
        callback.callback(versions);
        return null;
    }

    @Override public Path getPathToPreviousVersion(String uri) {
        return null;
    }

    @Override public Path restore(Path path, String comment) {
        return null;
    }

    public void setCallback(RemoteCallback<?> remoteCallback) {
        callback = remoteCallback;
    }

}
