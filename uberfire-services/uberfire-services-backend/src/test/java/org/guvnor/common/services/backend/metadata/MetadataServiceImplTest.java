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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.guvnor.common.services.backend.metadata.attribute.OtherMetaView;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.shared.exceptions.GenericPortableException;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;
import org.uberfire.rpc.SessionInfo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Mock
    private CommentedOptionFactory commentedOptionFactory;

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
                                          commentedOptionFactory,
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
    public void testSaveMetaData() throws IOException {
        final InputStream is = spy(new ByteArrayInputStream("hello".getBytes(StandardCharsets.UTF_8)));
        doReturn(is).when(ioService).newInputStream(any(),
                                                    any());
        final CommentedOption comment = new CommentedOption("comment");
        doReturn(comment).when(commentedOptionFactory).makeCommentedOption("comment");
        service.saveMetadata(Paths.convert(path),
                             new Metadata(),
                             "comment");

        verify(ioService).write(any(org.uberfire.java.nio.file.Path.class),
                                any(byte[].class),
                                anyMap(),
                                eq(comment));
        verify(is).close();
    }

    @Test(expected = GenericPortableException.class)
    public void testSaveMetaDataException() throws IOException {
        final InputStream is = new ByteArrayInputStream("hello".getBytes(StandardCharsets.UTF_8));

        try {

            doReturn(is).when(ioService).newInputStream(any(),
                                                        any());

            doThrow(IOException.class).when(ioService).write(any(org.uberfire.java.nio.file.Path.class),
                                                             any(byte[].class),
                                                             anyMap(),
                                                             any());
            service.saveMetadata(Paths.convert(path),
                                 new Metadata(),
                                 "comment");
        } finally {
            is.close();
        }
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