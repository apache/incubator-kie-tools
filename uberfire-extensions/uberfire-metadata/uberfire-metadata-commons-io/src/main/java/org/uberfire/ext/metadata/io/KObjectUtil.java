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

package org.uberfire.ext.metadata.io;

import org.apache.commons.codec.digest.DigestUtils;
import org.uberfire.ext.metadata.backend.lucene.fields.FieldFactory;
import org.uberfire.ext.metadata.backend.lucene.model.KClusterImpl;
import org.uberfire.ext.metadata.model.KCluster;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.model.KObjectKey;
import org.uberfire.ext.metadata.model.KProperty;
import org.uberfire.ext.metadata.model.schema.MetaType;
import org.uberfire.java.nio.base.FileSystemId;
import org.uberfire.java.nio.base.SegmentedPath;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.attribute.FileAttribute;

import java.util.ArrayList;

import static org.apache.commons.codec.binary.Base64.encodeBase64String;
import static org.apache.commons.io.FilenameUtils.getBaseName;
import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.uberfire.ext.metadata.backend.lucene.index.directory.DirectoryFactory.CLUSTER_ID_SEGMENT_SEPARATOR;

/**
 *
 */
public final class KObjectUtil {

    private static final MetaType META_TYPE = () -> Path.class.getName();

    private KObjectUtil() {

    }

    public static KObjectKey toKObjectKey(final Path path) {
        return new KObjectKey() {
            @Override
            public String getId() {
                return sha1(getType().getName() + "|" + getKey());
            }

            @Override
            public MetaType getType() {
                return META_TYPE;
            }

            @Override
            public String getClusterId() {
                final String fsId = ((FileSystemId) path.getFileSystem()).id();
                final String segmentId = ((SegmentedPath) path).getSegmentId();
                return fsId + CLUSTER_ID_SEGMENT_SEPARATOR + segmentId;
            }

            @Override
            public String getSegmentId() {
                return ((SegmentedPath) path).getSegmentId();
            }

            @Override
            public String getKey() {
                return path.toUri().toString();
            }
        };
    }

    public static KObject toKObject(final Path path,
                                    final FileAttribute<?>... attrs) {
        return new KObject() {

            @Override
            public String getId() {
                return sha1(getType().getName() + "|" + getKey());
            }

            @Override
            public MetaType getType() {
                return META_TYPE;
            }

            @Override
            public String getClusterId() {
                final String fsId = ((FileSystemId) path.getFileSystem()).id();
                final String segmentId = ((SegmentedPath) path).getSegmentId();
                return fsId + CLUSTER_ID_SEGMENT_SEPARATOR + segmentId;
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
                return new ArrayList<KProperty<?>>(attrs.length) {{
                    for (final FileAttribute<?> attr : attrs) {
                        add(new KProperty<Object>() {
                            @Override
                            public String getName() {
                                return attr.name();
                            }

                            @Override
                            public Object getValue() {
                                return attr.value();
                            }

                            @Override
                            public boolean isSearchable() {
                                return true;
                            }
                        });
                    }
                    add(new KProperty<String>() {
                        @Override
                        public String getName() {
                            return "filename";
                        }

                        @Override
                        public String getValue() {
                            if (path.getFileName() == null) {
                                return "/";
                            }
                            return path.getFileName().toString();
                        }

                        @Override
                        public boolean isSearchable() {
                            return true;
                        }
                    });
                    add(new KProperty<String>() {
                        @Override
                        public String getName() {
                            return FieldFactory.FILE_NAME_FIELD_SORTED;
                        }

                        @Override
                        public String getValue() {
                            if (path.getFileName() == null) {
                                return "";
                            }
                            return getBaseName(path.getFileName().toString()).toLowerCase();
                        }

                        @Override
                        public boolean isSearchable() {
                            return false;
                        }

                        @Override
                        public boolean isSortable() {
                            return true;
                        }
                    });
                    add(new KProperty<String>() {
                        @Override
                        public String getName() {
                            return "extension";
                        }

                        @Override
                        public String getValue() {
                            if (path.getFileName() == null) {
                                return "";
                            }
                            return getExtension(path.getFileName().toString());
                        }

                        @Override
                        public boolean isSearchable() {
                            return true;
                        }
                    });
                    add(new KProperty<String>() {
                        @Override
                        public String getName() {
                            return "basename";
                        }

                        @Override
                        public String getValue() {
                            if (path.getFileName() == null) {
                                return "";
                            }
                            return getBaseName(path.getFileName().toString());
                        }

                        @Override
                        public boolean isSearchable() {
                            return true;
                        }
                    });
                }};
            }

            @Override
            public boolean fullText() {
                return true;
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

    public static KCluster toKCluster(final Path fsPath) {
        final String fsId = ((FileSystemId) fsPath.getFileSystem()).id();
        final String segmentId = ((SegmentedPath) fsPath).getSegmentId();
        return new KClusterImpl(fsId + CLUSTER_ID_SEGMENT_SEPARATOR + segmentId);
    }

    private static String sha1(final String input) {
        if (input == null || input.trim().length() == 0) {
            return "--";
        }
        return encodeBase64String(DigestUtils.sha1(input));
    }
}
