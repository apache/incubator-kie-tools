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

package org.uberfire.ext.editor.commons.client.history.event;

import org.uberfire.backend.vfs.Path;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.java.nio.base.version.VersionRecord;

public class VersionSelectedEvent {

    private VersionRecord versionRecord;
    private Path pathToFile;

    public VersionSelectedEvent(Path pathToFile,VersionRecord versionRecord) {
        this.pathToFile = PortablePreconditions.checkNotNull("pathToFile", pathToFile);
        this.versionRecord = PortablePreconditions.checkNotNull("versionRecord", versionRecord);
    }

    public VersionRecord getVersionRecord() {
        return versionRecord;
    }

    public Path getPathToFile() {
        return pathToFile;
    }
}
