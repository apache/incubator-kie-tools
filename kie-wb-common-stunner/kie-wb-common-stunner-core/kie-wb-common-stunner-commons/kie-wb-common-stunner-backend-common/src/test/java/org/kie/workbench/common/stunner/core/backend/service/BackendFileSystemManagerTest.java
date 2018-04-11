/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.backend.service;

import java.util.Arrays;

import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.FileSystem;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BackendFileSystemManagerTest {

    private static final String ASSET1_NAME = "asset1-name";
    private static final String ASSET1_CONTENT = "asset1-content";
    private static final String ASSET2_NAME = "asset2-name";
    private static final String ASSET2_CONTENT = "asset2-content";

    @Mock
    private IOService ioService;

    @Mock
    private CommentedOptionFactory optionFactory;

    @Mock
    private FileSystem fileSystem;

    @Mock
    private org.uberfire.java.nio.file.Path path;

    private BackendFileSystemManager tested;
    private BackendFileSystemManager.Asset asset1;
    private BackendFileSystemManager.Asset asset2;

    @Before
    public void setup() throws Exception {
        asset1 = new BackendFileSystemManager.AssetBuilder()
                .setFileName(ASSET1_NAME)
                .fromString(ASSET1_CONTENT)
                .build();
        asset2 = new BackendFileSystemManager.AssetBuilder()
                .setFileName(ASSET2_NAME)
                .fromString(ASSET2_CONTENT)
                .build();
        when(path.getFileSystem()).thenReturn(fileSystem);
        tested = new BackendFileSystemManager(ioService,
                                              optionFactory);
    }

    @Test
    public void testDeployAssets() throws Exception {
        final org.uberfire.java.nio.file.Path asset1Path = mock(org.uberfire.java.nio.file.Path.class);
        final org.uberfire.java.nio.file.Path asset2Path = mock(org.uberfire.java.nio.file.Path.class);
        when(path.resolve(eq(ASSET1_NAME))).thenReturn(asset1Path);
        when(path.resolve(eq(ASSET2_NAME))).thenReturn(asset2Path);
        when(ioService.exists(eq(path))).thenReturn(false);
        final String message = "deploy-message";
        final ArgumentCaptor<byte[]> bytesCaptor1 = ArgumentCaptor.forClass(byte[].class);
        final ArgumentCaptor<byte[]> bytesCaptor2 = ArgumentCaptor.forClass(byte[].class);
        tested.deploy(path,
                      new BackendFileSystemManager.Assets(Arrays.asList(asset1,
                                                                        asset2)),
                      message);
        verify(ioService, times(1)).createDirectories(eq(path));
        verify(ioService, times(1)).startBatch(eq(fileSystem));
        verify(ioService, times(1)).write(eq(asset1Path),
                                          bytesCaptor1.capture(),
                                          any(CommentedOption.class));
        verify(ioService, times(1)).write(eq(asset2Path),
                                          bytesCaptor2.capture(),
                                          any(CommentedOption.class));
        verify(optionFactory, times(2)).makeCommentedOption(eq(message));
        verify(ioService, times(1)).endBatch();
        final byte[] bytes1 = bytesCaptor1.getValue();
        final String expectedContent1 = new String(bytes1, BackendFileSystemManager.UT8);
        Assert.assertEquals(ASSET1_CONTENT, expectedContent1);
        final byte[] bytes2 = bytesCaptor2.getValue();
        final String expectedContent2 = new String(bytes2, BackendFileSystemManager.UT8);
        Assert.assertEquals(ASSET2_CONTENT, expectedContent2);
    }
}
