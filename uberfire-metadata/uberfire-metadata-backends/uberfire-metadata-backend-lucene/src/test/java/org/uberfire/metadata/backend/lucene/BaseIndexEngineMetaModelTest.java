/*
 * Copyright 2012 JBoss Inc
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

package org.uberfire.metadata.backend.lucene;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;

import org.junit.Test;
import org.uberfire.metadata.backend.lucene.FieldFactory;
import org.uberfire.metadata.backend.lucene.LuceneIndexEngine;
import org.uberfire.metadata.backend.lucene.LuceneSetup;
import org.uberfire.metadata.backend.lucene.fields.SimpleFieldFactory;
import org.uberfire.metadata.engine.MetaIndexEngine;
import org.uberfire.metadata.engine.MetaModelStore;
import org.uberfire.metadata.model.KObject;
import org.uberfire.metadata.model.KProperty;
import org.uberfire.metadata.model.schema.MetaType;

import static org.junit.Assert.*;

/**
 *
 */
public abstract class BaseIndexEngineMetaModelTest {

    @Test
    public void testSimpleIndex() throws IOException {
        final FieldFactory factory = new SimpleFieldFactory();

        final MetaIndexEngine engine = new LuceneIndexEngine( getMetaModelStore(), getLuceneSetup(), factory );

        engine.index( new KObject() {
            @Override
            public String getId() {
                return "unique.id.here";
            }

            @Override
            public MetaType getType() {
                return new MetaType() {
                    @Override
                    public String getName() {
                        return "Path";
                    }
                };
            }

            @Override
            public String getClusterId() {
                return "cluster.id.here";
            }

            @Override
            public String getSegmentId() {
                return "/";
            }

            @Override
            public String getKey() {
                return "some.key.here";
            }

            @Override
            public Iterable<KProperty<?>> getProperties() {
                return new HashSet<KProperty<?>>() {{
                    add( new KProperty<String>() {
                        @Override
                        public String getName() {
                            return "dcore.author";
                        }

                        @Override
                        public String getValue() {
                            return "Some Author name here.";
                        }

                        @Override
                        public boolean isSearchable() {
                            return true;
                        }
                    } );
                    add( new KProperty<String>() {
                        @Override
                        public String getName() {
                            return "dcore.comment";
                        }

                        @Override
                        public String getValue() {
                            return "My comment here that has some content that is important to my users.";
                        }

                        @Override
                        public boolean isSearchable() {
                            return true;
                        }
                    } );
                }};
            }
        } );

        assertNotNull( getMetaModelStore().getMetaObject( "Path" ) );

        assertNotNull( getMetaModelStore().getMetaObject( "Path" ).getProperty( "dcore.author" ) );
        assertNotNull( getMetaModelStore().getMetaObject( "Path" ).getProperty( "dcore.comment" ) );
        assertNull( getMetaModelStore().getMetaObject( "Path" ).getProperty( "dcore.review" ) );
        assertNull( getMetaModelStore().getMetaObject( "Path" ).getProperty( "dcore.lastModifiedTime" ) );

        engine.index( new KObject() {
            @Override
            public String getId() {
                return "unique.id.here";
            }

            @Override
            public MetaType getType() {
                return new MetaType() {
                    @Override
                    public String getName() {
                        return "Path";
                    }
                };
            }

            @Override
            public String getClusterId() {
                return "cluster.id.here";
            }

            @Override
            public String getSegmentId() {
                return "/";
            }

            @Override
            public String getKey() {
                return "some.key.here";
            }

            @Override
            public Iterable<KProperty<?>> getProperties() {
                return new HashSet<KProperty<?>>() {{
                    add( new KProperty<String>() {
                        @Override
                        public String getName() {
                            return "dcore.author";
                        }

                        @Override
                        public String getValue() {
                            return "Some Author name here.";
                        }

                        @Override
                        public boolean isSearchable() {
                            return true;
                        }
                    } );
                    add( new KProperty<String>() {
                        @Override
                        public String getName() {
                            return "dcore.comment";
                        }

                        @Override
                        public String getValue() {
                            return "My comment here that has some content that is important to my users.";
                        }

                        @Override
                        public boolean isSearchable() {
                            return true;
                        }
                    } );
                    add( new KProperty<Integer>() {
                        @Override
                        public String getName() {
                            return "dcore.review";
                        }

                        @Override
                        public Integer getValue() {
                            return 10;
                        }

                        @Override
                        public boolean isSearchable() {
                            return true;
                        }
                    } );
                    add( new KProperty<Date>() {
                        @Override
                        public String getName() {
                            return "dcore.lastModifiedTime";
                        }

                        @Override
                        public Date getValue() {
                            return new Date();
                        }

                        @Override
                        public boolean isSearchable() {
                            return true;
                        }
                    } );
                }};
            }
        } );

        assertNotNull( getMetaModelStore().getMetaObject( "Path" ).getProperty( "dcore.author" ) );
        assertNotNull( getMetaModelStore().getMetaObject( "Path" ).getProperty( "dcore.comment" ) );
        assertNotNull( getMetaModelStore().getMetaObject( "Path" ).getProperty( "dcore.review" ) );
        assertNotNull( getMetaModelStore().getMetaObject( "Path" ).getProperty( "dcore.lastModifiedTime" ) );

        engine.index( new KObject() {
            @Override
            public String getId() {
                return "some.id.here";
            }

            @Override
            public MetaType getType() {
                return new MetaType() {
                    @Override
                    public String getName() {
                        return "PathX";
                    }
                };
            }

            @Override
            public String getClusterId() {
                return "some.cluster.id.here";
            }

            @Override
            public String getSegmentId() {
                return "/";
            }

            @Override
            public String getKey() {
                return "some.key.here";
            }

            @Override
            public Iterable<KProperty<?>> getProperties() {
                return new HashSet<KProperty<?>>() {{
                    add( new KProperty<String>() {
                        @Override
                        public String getName() {
                            return "dcore.author";
                        }

                        @Override
                        public String getValue() {
                            return "Some Author name here.";
                        }

                        @Override
                        public boolean isSearchable() {
                            return true;
                        }
                    } );
                    add( new KProperty<Date>() {
                        @Override
                        public String getName() {
                            return "dcore.lastModifiedTime";
                        }

                        @Override
                        public Date getValue() {
                            return new Date();
                        }

                        @Override
                        public boolean isSearchable() {
                            return true;
                        }
                    } );
                }};
            }
        } );

        assertNotNull( getMetaModelStore().getMetaObject( "Path" ) );

        assertNotNull( getMetaModelStore().getMetaObject( "Path" ).getProperty( "dcore.author" ) );
        assertNotNull( getMetaModelStore().getMetaObject( "Path" ).getProperty( "dcore.comment" ) );
        assertNotNull( getMetaModelStore().getMetaObject( "Path" ).getProperty( "dcore.review" ) );
        assertNotNull( getMetaModelStore().getMetaObject( "Path" ).getProperty( "dcore.lastModifiedTime" ) );

        assertNotNull( getMetaModelStore().getMetaObject( "PathX" ) );

        assertNotNull( getMetaModelStore().getMetaObject( "PathX" ).getProperty( "dcore.author" ) );
        assertNotNull( getMetaModelStore().getMetaObject( "PathX" ).getProperty( "dcore.lastModifiedTime" ) );
    }

    protected abstract LuceneSetup getLuceneSetup();

    protected abstract MetaModelStore getMetaModelStore();

}
