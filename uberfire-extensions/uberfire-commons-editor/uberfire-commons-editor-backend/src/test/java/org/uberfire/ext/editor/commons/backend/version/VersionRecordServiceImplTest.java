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
package org.uberfire.ext.editor.commons.backend.version;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.base.version.VersionAttributeView;
import org.uberfire.java.nio.base.version.VersionAttributes;
import org.uberfire.java.nio.base.version.VersionHistory;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.attribute.FileAttributeView;
import org.uberfire.java.nio.file.attribute.FileTime;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class VersionRecordServiceImplTest {

    private MockIOService ioService;
    private Path          pathToFile;
    private Path          pathToDotFile;
    private ArrayList<VersionRecord> dotFileVersionRecords  = new ArrayList<VersionRecord>();
    private ArrayList<VersionRecord> mainFileVersionRecords = new ArrayList<VersionRecord>();

    @Before
    public void setUp() throws Exception {

        pathToFile = mock(Path.class);
        pathToDotFile = mock(Path.class);

        when(pathToFile.resolveSibling(anyString())).thenReturn(pathToDotFile);

        ioService = new MockIOService() {
            @Override
            public <V extends FileAttributeView> V getFileAttributeView(org.uberfire.java.nio.file.Path path, Class<V> vClass) throws IllegalArgumentException {
                return (V) new MockVersionAttributeView(path);
            }
        };

        mainFileVersionRecords.add(makeVersionRecord("id1", "file.txt", new Date(1)));
        mainFileVersionRecords.add(makeVersionRecord("id3", "file.txt", new Date(3)));
        mainFileVersionRecords.add(makeVersionRecord("id4", "file.txt", new Date(4)));

        dotFileVersionRecords.add(makeVersionRecord("id1", ".file.txt", new Date(1)));
        dotFileVersionRecords.add(makeVersionRecord("id2", ".file.txt", new Date(2)));
        dotFileVersionRecords.add(makeVersionRecord("id5", ".file.txt", new Date(5)));

    }

    @Test
    public void testSimple() throws Exception {

        ioService.setExistingPaths(pathToFile, pathToDotFile);

        VersionRecordServiceImpl versionRecordServiceImpl = new VersionRecordServiceImpl(
                ioService,
                new VersionUtil()
        );

        List<VersionRecord> versions = versionRecordServiceImpl.load(pathToFile);

        assertEquals(5, versions.size());
        assertEquals("id1", versions.get(0).id());
        assertEquals("id2", versions.get(1).id());
        assertEquals("id3", versions.get(2).id());
        assertEquals("id4", versions.get(3).id());
        assertEquals("id5", versions.get(4).id());

    }

    @Test
    public void testMainFileHasHigherPriorityWhenCommitTimeIsEqual() throws Exception {

        ioService.setExistingPaths(pathToFile, pathToDotFile);

        mainFileVersionRecords.clear();
        dotFileVersionRecords.clear();
        mainFileVersionRecords.add(makeVersionRecord("main file",
                                                     "default://f6d48ce4c00a915185668c8df33bb500b3889c95@uf-playground/mortgages/src/main/resources/org/mortgages/Bankruptcy%20history.rdrl",
                                                     new Date(1)));
        dotFileVersionRecords.add(makeVersionRecord("dot1 file",
                                                     "default://f6d48ce4c00a915185668c8df33bb500b3889c95@uf-playground/mortgages/src/main/resources/org/mortgages/.Bankruptcy%20history.rdrl",
                                                     new Date(1)));

        VersionRecordServiceImpl versionRecordServiceImpl = new VersionRecordServiceImpl(
                ioService,
                new VersionUtil()
        );

        List<VersionRecord> versions = versionRecordServiceImpl.load(pathToFile);

        assertEquals(2, versions.size());
        assertEquals("main file", versions.get(0).id());
        assertEquals("dot1 file", versions.get(1).id());

    }

    @Test
    public void testNoDotFile() throws Exception {

        ioService.setExistingPaths(pathToFile);

        VersionRecordServiceImpl versionRecordServiceImpl = new VersionRecordServiceImpl(
                ioService,
                new VersionUtil()
        );

        List<VersionRecord> versions = versionRecordServiceImpl.load(pathToFile);

        assertEquals(3, versions.size());
        assertEquals("id1", versions.get(0).id());
        assertEquals("id3", versions.get(1).id());
        assertEquals("id4", versions.get(2).id());

    }

    @Test
    public void testLoadRecord() throws Exception {

        ioService.setExistingPaths(pathToFile);

        VersionRecordServiceImpl versionRecordServiceImpl = new VersionRecordServiceImpl(
                ioService,
                new VersionUtil() {
                    @Override public Path getPath(Path path, String version) throws URISyntaxException {
                        return path;
                    }

                    @Override public String getVersion(Path path) {
                        return "id3";
                    }
                }
        );

        VersionRecord record = versionRecordServiceImpl.loadRecord(pathToFile);

        assertEquals(record.id(), "id3");

    }

    @Test
    public void testLoadRecordMaster() throws Exception {

        ioService.setExistingPaths(pathToFile);

        VersionRecordServiceImpl versionRecordServiceImpl = new VersionRecordServiceImpl(
                ioService,
                new VersionUtil() {
                    @Override public Path getPath(Path path, String version) throws URISyntaxException {
                        return path;
                    }

                    @Override public String getVersion(Path path) {
                        return "master";
                    }
                }
        );

        VersionRecord record = versionRecordServiceImpl.loadRecord(pathToFile);

        assertEquals(record.id(), "id1");

    }

    private VersionRecord makeVersionRecord(final String id, final String uri, final Date date) {
        return new VersionRecord() {
            @Override public String id() {
                return id;
            }

            @Override public String author() {
                return null;
            }

            @Override public String email() {
                return null;
            }

            @Override public String comment() {
                return null;
            }

            @Override public Date date() {
                return date;
            }

            @Override public String uri() {
                return uri;
            }
        };
    }

    private class MockVersionAttributeView
            extends VersionAttributeView<org.uberfire.java.nio.file.Path> {

        List<VersionRecord> records;

        public MockVersionAttributeView(org.uberfire.java.nio.file.Path path) {
            super(path);
            if (pathToFile.equals(path)) {
                records = mainFileVersionRecords;
            } else {
                records = dotFileVersionRecords;
            }
        }

        @Override public VersionAttributes readAttributes() throws IOException {
            return new VersionAttributes() {
                @Override public VersionHistory history() {
                    return new VersionHistory() {
                        @Override public List<VersionRecord> records() {
                            return records;
                        }
                    };
                }

                @Override public FileTime lastModifiedTime() {
                    return null;
                }

                @Override public FileTime lastAccessTime() {
                    return null;
                }

                @Override public FileTime creationTime() {
                    return null;
                }

                @Override public boolean isRegularFile() {
                    return false;
                }

                @Override public boolean isDirectory() {
                    return false;
                }

                @Override public boolean isSymbolicLink() {
                    return false;
                }

                @Override public boolean isOther() {
                    return false;
                }

                @Override public long size() {
                    return 0;
                }

                @Override public Object fileKey() {
                    return null;
                }
            };
        }

        @Override public Class[] viewTypes() {
            return new Class[0];
        }
    }

}