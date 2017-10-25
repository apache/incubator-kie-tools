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

import org.guvnor.common.services.backend.metadata.attribute.OtherMetaView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;
import org.uberfire.rpc.SessionInfo;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MetadataServiceImplTest {

    private SimpleFileSystemProvider fileSystemProvider;

    @Mock
    private IOService ioService;

    @Mock
    private IOService configIOService;

    @Mock
    private SessionInfo sessionInfo;

    @Mock
    private OtherMetaView otherMetaView;

    private Path path;
    private MetadataServerSideService service;

    @Before
    public void setUp() throws Exception {
        fileSystemProvider = new SimpleFileSystemProvider();

        //Ensure URLs use the default:// scheme
        fileSystemProvider.forceAsDefault();

        path = fileSystemProvider.getPath(this.getClass().getResource("myfile.file").toURI());

        service = new MetadataServiceImpl(ioService,
                                          configIOService,
                                          sessionInfo);
    }

    @Test
    public void testGetEmptyTagsNoOtherMetaView() {
        final List<String> tags = service.getTags(path);

        assertNotNull(tags);
        assertEquals(0,
                     tags.size());
    }

    @Test
    public void testGetEmptyTags() {
        when(otherMetaView.readAttributes()).thenReturn(new OtherMetaAttributesMock());
        when(ioService.getFileAttributeView(path,
                                            OtherMetaView.class)).thenReturn(otherMetaView);
        final List<String> tags = service.getTags(path);

        assertNotNull(tags);
        assertEquals(0,
                     tags.size());
    }

    @Test
    public void testGetTags() {
        when(otherMetaView.readAttributes()).thenReturn(new OtherMetaAttributesMock() {

            List<String> tags = new ArrayList<String>() {{
                add("tag1");
            }};

            @Override
            public List<String> tags() {
                return tags;
            }
        });
        when(ioService.getFileAttributeView(path,
                                            OtherMetaView.class)).thenReturn(otherMetaView);
        final List<String> tags = service.getTags(path);

        assertNotNull(tags);
        assertEquals(1,
                     tags.size());
    }
}