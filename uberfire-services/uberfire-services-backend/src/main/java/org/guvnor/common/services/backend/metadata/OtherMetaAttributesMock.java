/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.common.services.backend.metadata;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.guvnor.common.services.backend.metadata.attribute.OtherMetaAttributes;
import org.uberfire.java.nio.file.attribute.FileTime;

public class OtherMetaAttributesMock
        implements OtherMetaAttributes {

    @Override
    public List<String> tags() {
        return new ArrayList<String>();
    }

    @Override
    public FileTime lastModifiedTime() {
        return new FileTime() {
            @Override
            public long to(TimeUnit unit) {
                return 0;
            }

            @Override
            public long toMillis() {
                return 0;
            }

            @Override
            public int compareTo(FileTime fileTime) {
                return 0;
            }
        };
    }

    @Override
    public FileTime lastAccessTime() {
        return new FileTime() {
            @Override
            public long to(TimeUnit unit) {
                return 0;
            }

            @Override
            public long toMillis() {
                return 0;
            }

            @Override
            public int compareTo(FileTime fileTime) {
                return 0;
            }
        };
    }

    @Override
    public FileTime creationTime() {
        return new FileTime() {
            @Override
            public long to(TimeUnit unit) {
                return 0;
            }

            @Override
            public long toMillis() {
                return 0;
            }

            @Override
            public int compareTo(FileTime fileTime) {
                return 0;
            }
        };
    }

    @Override
    public boolean isRegularFile() {
        return false;
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public boolean isSymbolicLink() {
        return false;
    }

    @Override
    public boolean isOther() {
        return false;
    }

    @Override
    public long size() {
        return 0;
    }

    @Override
    public Object fileKey() {
        return null;
    }
}
