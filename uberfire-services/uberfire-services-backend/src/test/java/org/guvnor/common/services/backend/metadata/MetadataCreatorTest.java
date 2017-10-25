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
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.guvnor.common.services.backend.metadata.attribute.DiscussionView;
import org.guvnor.common.services.backend.metadata.attribute.GeneratedAttributesView;
import org.guvnor.common.services.backend.metadata.attribute.GeneratedFileAttributes;
import org.guvnor.common.services.backend.metadata.attribute.OtherMetaView;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.io.IOService;
import org.uberfire.io.attribute.DublinCoreView;
import org.uberfire.java.nio.base.version.VersionAttributeView;
import org.uberfire.java.nio.base.version.VersionAttributes;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.api.FileSystemProviders;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;
import org.uberfire.rpc.SessionInfo;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MetadataCreatorTest {

    private SimpleFileSystemProvider fileSystemProvider;

    @Mock
    private IOService configIOService;
    @Mock
    private SessionInfo sessionInfo;
    @Mock
    private DublinCoreView dcoreView;
    @Mock
    private DiscussionView discussView;
    @Mock
    private OtherMetaView otherMetaView;
    @Mock
    private VersionAttributeView versionAttributeView;
    @Mock
    private GeneratedFileAttributes generatedFileAttributes;
    @Mock
    private GeneratedAttributesView generatedAttributesView;

    private MetadataCreator service;
    private Path mainFilePath;
    private ArrayList<VersionRecord> versionRecords;

    @Before
    public void setUp() throws Exception {

        versionRecords = new ArrayList<VersionRecord>();
        versionRecords.add(createVersionRecord());

        VersionAttributes versionAttributes = new VersionAttributesMock(versionRecords);
        when(versionAttributeView.readAttributes()).thenReturn(versionAttributes);

        when(dcoreView.readAttributes()).thenReturn(new DublinCoreAttributesMock());
        when(otherMetaView.readAttributes()).thenReturn(new OtherMetaAttributesMock());
        when(discussView.readAttributes()).thenReturn(new DiscussionAttributesMock());
        when(generatedAttributesView.readAttributes()).thenReturn(generatedFileAttributes);

        fileSystemProvider = new SimpleFileSystemProvider();

        //Ensure URLs use the default:// scheme
        fileSystemProvider.forceAsDefault();

        mainFilePath = fileSystemProvider.getPath(this.getClass().getResource("myfile.file").toURI());

        service = new MetadataCreator(mainFilePath,
                                      configIOService,
                                      sessionInfo,
                                      dcoreView,
                                      discussView,
                                      otherMetaView,
                                      versionAttributeView,
                                      generatedAttributesView);
    }

    @Test
    public void testSimple() throws Exception {
        Metadata metadata = service.create();

        assertNotNull(metadata);
        assertNotNull(metadata.getTags());
        assertNotNull(metadata.getDiscussion());
        assertNotNull(metadata.getVersion());
    }

    @Test
    public void testGeneratedAttributes() {
        when(generatedFileAttributes.isGenerated()).thenReturn(true);
        when(generatedAttributesView.readAttributes()).thenReturn(generatedFileAttributes);

        Metadata metadata = service.create();

        assertTrue(metadata.isGenerated());
    }

    @Test
    //See https://issues.jboss.org/browse/GUVNOR-2399
    public void testConcurrency() throws Throwable {
        //Ensure FileSystemProviders has been setup
        FileSystemProviders.getDefaultProvider();

        //Mock FileSystem operations
        final AtomicBoolean exists = new AtomicBoolean(false);
        when(configIOService.exists(any(Path.class))).<Boolean>thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                return exists.get();
            }
        });
        when(configIOService.write(any(Path.class),
                                   any(String.class))).<Path>thenAnswer(new Answer<Path>() {
            @Override
            public Path answer(final InvocationOnMock invocation) throws Throwable {
                exists.set(true);
                return mainFilePath;
            }
        });
        when(configIOService.readAllString(any(Path.class))).<String>thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                if (!exists.get()) {
                    throw new NoSuchFileException();
                }
                return "content";
            }
        });
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                exists.set(false);
                return null;
            }
        }).when(configIOService).delete(any(Path.class));

        final int THREADS = 100;
        final Result result = new Result();
        final ExecutorService es = Executors.newCachedThreadPool();
        for (int i = 0; i < THREADS; i++) {
            final int threadCount = i;
            final Operation op = i % 2 == 1 ? Operation.WRITE : Operation.CHECK;
            es.execute(new Runnable() {
                @Override
                public void run() {
                    try {

                        System.out.println("[Thread : " + threadCount + "] Running...");
                        switch (op) {
                            case WRITE:
                                System.out.println("[Thread : " + threadCount + "] Writing..." + output());
                                configIOService.write(mainFilePath,
                                                      "content");
                                configIOService.delete(mainFilePath);
                                break;
                            case CHECK:
                                System.out.println("[Thread : " + threadCount + "] Checking..." + output());
                                service.create();
                        }
                    } catch (Throwable e) {
                        result.setFailed(true);
                        result.setException(e);
                    } finally {
                        System.out.println("[Thread : " + threadCount + "] Completed.");
                    }
                }

                private String output() {
                    if (exists.get()) {
                        return "Exists";
                    } else {
                        return "Not exists";
                    }
                }
            });
        }

        try {
            es.shutdown();
            es.awaitTermination(1000 * 5,
                                TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
        }
        if (result.isFailed()) {
            throw result.getException();
        }
    }

    private enum Operation {
        WRITE,
        CHECK
    }

    private static class Result {

        private Throwable exception;
        private boolean failed = false;

        public Throwable getException() {
            return exception;
        }

        public void setException(Throwable exception) {
            this.exception = exception;
        }

        public boolean isFailed() {
            return failed;
        }

        public void setFailed(boolean failed) {
            this.failed = failed;
        }
    }

    private VersionRecord createVersionRecord() {
        return new VersionRecord() {
            @Override
            public String id() {
                return "1";
            }

            @Override
            public String author() {
                return "admin";
            }

            @Override
            public String email() {
                return "admin@mail.zap";
            }

            @Override
            public String comment() {
                return "Some commit";
            }

            @Override
            public Date date() {
                return new Date();
            }

            @Override
            public String uri() {
                return "myfile.file";
            }
        };
    }
}