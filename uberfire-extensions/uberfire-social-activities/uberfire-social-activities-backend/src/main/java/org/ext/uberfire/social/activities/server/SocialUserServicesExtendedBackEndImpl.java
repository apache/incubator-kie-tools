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

package org.ext.uberfire.social.activities.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.uberfire.backend.server.io.ConfigIOServiceProducer;
import org.uberfire.java.nio.base.AbstractPath;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;

//this type can't be managed bean, otherwise WAS will fail
//https://bugzilla.redhat.com/show_bug.cgi?id=1266138
public class SocialUserServicesExtendedBackEndImpl {

    private FileSystem fileSystem;

    public SocialUserServicesExtendedBackEndImpl(final FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    public List<String> getAllBranches() {
        FileSystem _fileSystem = getFileSystem();
        final List<String> branches = new ArrayList<String>();
        for (Iterator it = _fileSystem.getRootDirectories().iterator(); it.hasNext(); ) {
            AbstractPath path = (AbstractPath) it.next();
            branches.add(path.getHost());
        }
        return branches;
    }

    public Path buildPath(final String serviceType,
                          final String relativePath) {
        FileSystem _fileSystem = getFileSystem();
        if (relativePath != null && !"".equals(relativePath)) {
            return _fileSystem.getPath("social",
                                       serviceType,
                                       relativePath);
        } else {
            return _fileSystem.getPath("social",
                                       serviceType);
        }
    }

    FileSystem getFileSystem() {
        FileSystem _fileSystem = getConfigIOServiceProducer().configFileSystem();
        if (_fileSystem == null) {
            _fileSystem = fileSystem;
        }
        return _fileSystem;
    }

    ConfigIOServiceProducer getConfigIOServiceProducer() {
        return ConfigIOServiceProducer.getInstance();
    }
}
