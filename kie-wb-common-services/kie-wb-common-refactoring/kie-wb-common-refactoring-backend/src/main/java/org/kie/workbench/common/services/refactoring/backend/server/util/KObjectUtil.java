/*
 * Copyright 2014 JBoss, by Red Hat, Inc
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

package org.kie.workbench.common.services.refactoring.backend.server.util;

import java.io.File;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.model.KObjectKey;
import org.uberfire.ext.metadata.model.KProperty;
import org.uberfire.ext.metadata.model.schema.MetaType;
import org.uberfire.java.nio.base.FileSystemId;
import org.uberfire.java.nio.base.SegmentedPath;
import org.uberfire.java.nio.file.Path;

import static org.apache.commons.codec.binary.Base64.encodeBase64String;

public class KObjectUtil {

    private static final MetaType META_TYPE = new MetaType() {
        @Override
        public String getName() {
            return Path.class.getName();
        }
    };

    private KObjectUtil() {

    }

    public static KObjectKey toKObjectKey(final Path path,
                                          final String classifier) {
        return new KObjectKey() {

            @Override
            public String getId() {
                return sha1(getType().getName() + "|" + classifier + "|" + getKey());
            }

            @Override
            public MetaType getType() {
                return META_TYPE;
            }

            @Override
            public String getClusterId() {
                final String fsId = ((FileSystemId) path.getFileSystem()).id();
                final String segmentId = ((SegmentedPath) path).getSegmentId();
                return fsId + File.separator + segmentId;
            }

            @Override
            public String getSegmentId() {
                return ((SegmentedPath) path).getSegmentId();
            }

            @Override
            public String getKey() {
                return path.toUri().toString();
            }

            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder("KObject{" +
                                                             ", key='" + getKey() + '\'' +
                                                             ", id='" + getId() + '\'' +
                                                             ", type=" + getType() +
                                                             ", clusterId='" + getClusterId() + '\'' +
                                                             ", segmentId='" + getSegmentId() + '\'');
                sb.append('}');

                return sb.toString();
            }
        };
    }

    public static KObject toKObject(final Path path,
                                    final String classifier,
                                    final Set<KProperty<?>> indexElements) {
        return new KObject() {

            @Override
            public String getId() {
                return sha1(getType().getName() + "|" + classifier + "|" + getKey());
            }

            @Override
            public MetaType getType() {
                return META_TYPE;
            }

            @Override
            public String getClusterId() {
                final String fsId = ((FileSystemId) path.getFileSystem()).id();
                final String segmentId = ((SegmentedPath) path).getSegmentId();
                return fsId + File.separator + segmentId;
            }

            @Override
            public String getSegmentId() {
                return ((SegmentedPath) path).getSegmentId();
            }

            @Override
            public String getKey() {
                return path.toUri().toString();
            }

            @Override
            public Iterable<KProperty<?>> getProperties() {
                return indexElements;
            }

            @Override
            public boolean fullText() {
                return false;
            }

            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder("KObject{" +
                                                             ", key='" + getKey() + '\'' +
                                                             ", id='" + getId() + '\'' +
                                                             ", type=" + getType() +
                                                             ", clusterId='" + getClusterId() + '\'' +
                                                             ", segmentId='" + getSegmentId() + '\'');

                for (KProperty<?> xproperty : getProperties()) {
                    sb.append(", " + xproperty.getName() + "='" + xproperty.getValue() + '\'');
                }

                sb.append('}');

                return sb.toString();
            }
        };
    }

    private static String sha1(final String input) {
        if (input == null || input.trim().length() == 0) {
            return "--";
        }

        return encodeBase64String(DigestUtils.sha1(input));
    }
}
