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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import org.uberfire.ext.metadata.backend.lucene.model.KClusterImpl;
import org.uberfire.ext.metadata.model.KCluster;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.model.KObjectKey;
import org.uberfire.ext.metadata.model.KProperty;
import org.uberfire.ext.metadata.model.schema.MetaType;
import org.uberfire.java.nio.base.FileSystemId;
import org.uberfire.java.nio.base.SegmentedPath;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.attribute.FileAttribute;

import static org.apache.commons.codec.binary.Base64.*;
import static org.apache.commons.io.FilenameUtils.*;

/**
 *
 */
public final class KObjectUtil {

    private static final MessageDigest DIGEST;

    static {
        try {
            DIGEST = MessageDigest.getInstance( "SHA1" );
        } catch ( final NoSuchAlgorithmException e ) {
            throw new RuntimeException( e );
        }
    }

    private static final MetaType META_TYPE = new MetaType() {
        @Override
        public String getName() {
            return Path.class.getName();
        }
    };

    private KObjectUtil() {

    }

    public static KObjectKey toKObjectKey( final Path path ) {
        return new KObjectKey() {
            @Override
            public String getId() {
                return sha1( getType().getName() + "|" + getKey() );
            }

            @Override
            public MetaType getType() {
                return META_TYPE;
            }

            @Override
            public String getClusterId() {
                return ( (FileSystemId) path.getFileSystem() ).id();
            }

            @Override
            public String getSegmentId() {
                return ( (SegmentedPath) path ).getSegmentId();
            }

            @Override
            public String getKey() {
                return path.toUri().toString();
            }
        };
    }

    public static KObject toKObject( final Path path,
                                     final FileAttribute<?>... attrs ) {
        return new KObject() {

            @Override
            public String getId() {
                return sha1( getType().getName() + "|" + getKey() );
            }

            @Override
            public MetaType getType() {
                return META_TYPE;
            }

            @Override
            public String getClusterId() {
                return ( (FileSystemId) path.getFileSystem() ).id();
            }

            @Override
            public String getSegmentId() {
                return ( (SegmentedPath) path ).getSegmentId();
            }

            @Override
            public String getKey() {
                return path.toUri().toString();
            }

            @Override
            public Iterable<KProperty<?>> getProperties() {
                return new ArrayList<KProperty<?>>( attrs.length ) {{
                    for ( final FileAttribute<?> attr : attrs ) {
                        add( new KProperty<Object>() {
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
                        } );
                    }
                    add( new KProperty<String>() {
                        @Override
                        public String getName() {
                            return "filename";
                        }

                        @Override
                        public String getValue() {
                            if ( path.getFileName() == null ) {
                                return "/";
                            }
                            return path.getFileName().toString();
                        }

                        @Override
                        public boolean isSearchable() {
                            return true;
                        }
                    } );
                    add( new KProperty<String>() {
                        @Override
                        public String getName() {
                            return "extension";
                        }

                        @Override
                        public String getValue() {
                            if ( path.getFileName() == null ) {
                                return "";
                            }
                            return getExtension( path.getFileName().toString() );
                        }

                        @Override
                        public boolean isSearchable() {
                            return true;
                        }
                    } );
                    add( new KProperty<String>() {
                        @Override
                        public String getName() {
                            return "basename";
                        }

                        @Override
                        public String getValue() {
                            if ( path.getFileName() == null ) {
                                return "";
                            }
                            return getBaseName( path.getFileName().toString() );
                        }

                        @Override
                        public boolean isSearchable() {
                            return true;
                        }
                    } );

                }};
            }

            @Override
            public boolean fullText() {
                return true;
            }

            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder( "KObject{" +
                                                              ", key='" + getKey() + '\'' +
                                                              ", id='" + getId() + '\'' +
                                                              ", type=" + getType() +
                                                              ", clusterId='" + getClusterId() + '\'' +
                                                              ", segmentId='" + getSegmentId() + '\'' );

                for ( KProperty<?> xproperty : getProperties() ) {
                    sb.append( ", " + xproperty.getName() + "='" + xproperty.getValue() + '\'' );
                }

                sb.append( '}' );

                return sb.toString();
            }
        };
    }

    public static KCluster toKCluster( final FileSystemId fs ) {
        return new KClusterImpl( fs.id() );
    }

    public static KCluster toKCluster( final FileSystem fs ) {
        return toKCluster( (FileSystemId) fs );
    }

    private static String sha1( final String input ) {
        if ( input == null || input.trim().length() == 0 ) {
            return "--";
        }
        return encodeBase64String( DIGEST.digest( input.getBytes() ) );
    }
}
