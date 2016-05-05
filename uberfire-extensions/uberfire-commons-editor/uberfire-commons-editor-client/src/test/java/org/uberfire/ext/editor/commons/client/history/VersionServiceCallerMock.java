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

import java.lang.Override;import java.util.ArrayList;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.ext.editor.commons.version.VersionService;
import org.uberfire.java.nio.base.version.VersionRecord;

class VersionServiceCallerMock
        implements Caller<VersionService> {

    private VersionServiceMock service;

    VersionServiceCallerMock(ArrayList<VersionRecord> versions) {
        service = new VersionServiceMock(versions);
    }

    @Override public VersionService call() {
        return service;
    }

    @Override public VersionService call(RemoteCallback<?> remoteCallback) {
        service.setCallback(remoteCallback);
        return service;
    }

    @Override public VersionService call(RemoteCallback<?> remoteCallback, ErrorCallback<?> errorCallback) {
        service.setCallback(remoteCallback);
        return service;
    }

}
