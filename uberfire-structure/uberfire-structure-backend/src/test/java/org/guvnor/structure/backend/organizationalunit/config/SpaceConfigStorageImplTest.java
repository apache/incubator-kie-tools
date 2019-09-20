/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.structure.backend.organizationalunit.config;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.guvnor.structure.organizationalunit.config.BranchPermissions;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequest;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestComment;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.io.object.ObjectStorage;
import org.uberfire.io.IOService;
import org.uberfire.mocks.FileSystemTestingUtils;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.same;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SpaceConfigStorageImplTest {

    private static final String PATH_PREFIX = "git://amend-repo-test/";

    @Mock
    private ObjectStorage objectStorage;

    private IOService ioService;

    private SpaceConfigStorageImpl spaceConfigStorage;

    private static FileSystemTestingUtils fileSystemTestingUtils = new FileSystemTestingUtils();

    @Before
    public void setup() throws Exception {
        fileSystemTestingUtils.setup();

        ioService = spy(fileSystemTestingUtils.getIoService());

        spaceConfigStorage = spy(new SpaceConfigStorageImpl(objectStorage,
                                                            ioService));
    }

    @After
    public void cleanupFileSystem() {
        fileSystemTestingUtils.cleanup();
    }

    @Test
    public void loadCustomBranchPermissionsTest() {
        final BranchPermissions customBranchPermissions = mock(BranchPermissions.class);
        doReturn(customBranchPermissions).when(objectStorage).read("/config/myProject/myBranch/BranchPermissions.json");

        final BranchPermissions branchPermissions = spaceConfigStorage.loadBranchPermissions("myBranch",
                                                                                             "myProject");

        assertSame(customBranchPermissions,
                   branchPermissions);
    }

    @Test
    public void loadDefaultBranchPermissionsTest() {
        final BranchPermissions defaultBranchPermissions = mock(BranchPermissions.class);
        doReturn(defaultBranchPermissions).when(spaceConfigStorage).getDefaultBranchPermissions("myBranch");

        final BranchPermissions branchPermissions = spaceConfigStorage.loadBranchPermissions("myBranch",
                                                                                             "myProject");

        assertSame(defaultBranchPermissions,
                   branchPermissions);
    }

    @Test
    public void saveBranchPermissionsTest() {
        final BranchPermissions customBranchPermissions = mock(BranchPermissions.class);

        spaceConfigStorage.saveBranchPermissions("myBranch",
                                                 "myProject",
                                                 customBranchPermissions);

        verify(objectStorage).write(eq("/config/myProject/myBranch/BranchPermissions.json"),
                                    same(customBranchPermissions));
    }

    @Test
    public void deleteBranchPermissionsTest() {
        spaceConfigStorage.deleteBranchPermissions("myBranch",
                                                   "myProject");

        verify(objectStorage).delete(eq("/config/myProject/myBranch/BranchPermissions.json"));
    }

    @Test
    public void loadChangeRequestsTest() {
        final List<Long> ids = new ArrayList<Long>() {{
            add(1L);
            add(2L);
        }};

        final ChangeRequest expectedChangeRequest0 = mock(ChangeRequest.class);
        final ChangeRequest expectedChangeRequest1 = mock(ChangeRequest.class);

        doReturn(ids).when(spaceConfigStorage).getChangeRequestIds("MyProject");

        doReturn(expectedChangeRequest0).when(objectStorage).read("/MyProject/change_requests/1/information.cr");
        doReturn(expectedChangeRequest1).when(objectStorage).read("/MyProject/change_requests/2/information.cr");

        final List<ChangeRequest> changeRequests = spaceConfigStorage.loadChangeRequests("MyProject");

        assertSame(expectedChangeRequest0,
                   changeRequests.get(0));

        assertSame(expectedChangeRequest1,
                   changeRequests.get(1));
    }

    @Test
    public void loadChangeRequestTest() {
        final ChangeRequest expectedChangeRequest = mock(ChangeRequest.class);

        doReturn(expectedChangeRequest).when(objectStorage).read("/MyProject/change_requests/1/information.cr");

        final ChangeRequest changeRequest = spaceConfigStorage.loadChangeRequest("MyProject", 1L);

        assertSame(expectedChangeRequest,
                   changeRequest);
    }

    @Test
    public void saveChangeRequestTest() {
        final ChangeRequest changeRequest = mock(ChangeRequest.class);

        doReturn(1L).when(changeRequest).getId();

        spaceConfigStorage.saveChangeRequest("MyProject", changeRequest);

        verify(objectStorage).write(eq("/MyProject/change_requests/1/information.cr"),
                                    same(changeRequest));
    }

    @Test
    public void deleteAllChangeRequestsTest() {
        final org.uberfire.java.nio.file.Path crsPath = fileSystemTestingUtils.getIoService().get(URI.create(PATH_PREFIX + "MyProject/change_requests"));
        final org.uberfire.java.nio.file.Path commentsPath = fileSystemTestingUtils.getIoService().get(URI.create(PATH_PREFIX + "MyProject/change_requests/2/comments"));

        fileSystemTestingUtils.getIoService().createFile(fileSystemTestingUtils.getIoService().get(URI.create(PATH_PREFIX + "MyProject/change_requests/1/information.cr")));
        fileSystemTestingUtils.getIoService().createFile(fileSystemTestingUtils.getIoService().get(URI.create(PATH_PREFIX + "MyProject/change_requests/2/information.cr")));
        fileSystemTestingUtils.getIoService().createFile(fileSystemTestingUtils.getIoService().get(URI.create(PATH_PREFIX + "MyProject/change_requests/2/comments/1.comment")));
        fileSystemTestingUtils.getIoService().createFile(fileSystemTestingUtils.getIoService().get(URI.create(PATH_PREFIX + "MyProject/change_requests/2/comments/2.comment")));

        doReturn(true).when(objectStorage).exists("/MyProject/change_requests");
        doReturn(true).when(objectStorage).exists("/MyProject/change_requests/2/comments");
        doReturn(crsPath).when(objectStorage).getPath("/MyProject/change_requests");
        doReturn(commentsPath).when(objectStorage).getPath("/MyProject/change_requests/2/comments");

        spaceConfigStorage.deleteAllChangeRequests("MyProject");

        verify(objectStorage).delete(eq("/MyProject/change_requests/2/comments/1.comment"));
        verify(objectStorage).delete(eq("/MyProject/change_requests/2/comments/2.comment"));
        verify(objectStorage).delete(eq("/MyProject/change_requests/1/information.cr"));
        verify(objectStorage).delete(eq("/MyProject/change_requests/2/information.cr"));
    }

    @Test
    public void deleteChangeRequestTest() {
        spaceConfigStorage.deleteChangeRequest("MyProject",
                                               1L);

        verify(objectStorage).delete(eq("/MyProject/change_requests/1/information.cr"));
    }

    @Test
    public void deleteRepositoryTest() {
        final org.uberfire.java.nio.file.Path crsPath = fileSystemTestingUtils.getIoService().get(URI.create(PATH_PREFIX + "MyProject/change_requests"));
        final org.uberfire.java.nio.file.Path commentsPath = fileSystemTestingUtils.getIoService().get(URI.create(PATH_PREFIX + "MyProject/change_requests/2/comments"));

        fileSystemTestingUtils.getIoService().createFile(fileSystemTestingUtils.getIoService().get(URI.create(PATH_PREFIX + "MyProject/change_requests/1/information.cr")));
        fileSystemTestingUtils.getIoService().createFile(fileSystemTestingUtils.getIoService().get(URI.create(PATH_PREFIX + "MyProject/change_requests/2/information.cr")));
        fileSystemTestingUtils.getIoService().createFile(fileSystemTestingUtils.getIoService().get(URI.create(PATH_PREFIX + "MyProject/change_requests/2/comments/1.comment")));
        fileSystemTestingUtils.getIoService().createFile(fileSystemTestingUtils.getIoService().get(URI.create(PATH_PREFIX + "MyProject/change_requests/2/comments/2.comment")));

        doReturn(true).when(objectStorage).exists("/MyProject/change_requests");
        doReturn(true).when(objectStorage).exists("/MyProject/change_requests/2/comments");
        doReturn(crsPath).when(objectStorage).getPath("/MyProject/change_requests");
        doReturn(commentsPath).when(objectStorage).getPath("/MyProject/change_requests/2/comments");

        spaceConfigStorage.deleteRepository("MyProject");

        verify(objectStorage).delete(eq("/MyProject/change_requests/2/comments/1.comment"));
        verify(objectStorage).delete(eq("/MyProject/change_requests/2/comments/2.comment"));
        verify(objectStorage).delete(eq("/MyProject/change_requests/1/information.cr"));
        verify(objectStorage).delete(eq("/MyProject/change_requests/2/information.cr"));
        verify(objectStorage).delete(eq("/MyProject"));
    }

    @Test
    public void getChangeRequestIdsTest() {
        final org.uberfire.java.nio.file.Path crsPath = fileSystemTestingUtils.getIoService().get(URI.create(PATH_PREFIX + "MyProject/change_requests"));
        fileSystemTestingUtils.getIoService().createFile(fileSystemTestingUtils.getIoService().get(URI.create(PATH_PREFIX + "MyProject/change_requests/1/information.cr")));
        fileSystemTestingUtils.getIoService().createFile(fileSystemTestingUtils.getIoService().get(URI.create(PATH_PREFIX + "MyProject/change_requests/2/information.cr")));

        doReturn(true).when(objectStorage).exists("/MyProject/change_requests");
        doReturn(crsPath).when(objectStorage).getPath("/MyProject/change_requests");

        final List<Long> ids = spaceConfigStorage.getChangeRequestIds("MyProject");

        assertSame(1L, ids.get(0));
        assertSame(2L, ids.get(1));
    }

    @Test
    public void getChangeRequestIdsSkipInvalidIdsTest() {
        final org.uberfire.java.nio.file.Path crsPath = fileSystemTestingUtils.getIoService().get(URI.create(PATH_PREFIX + "MyProject/change_requests"));
        fileSystemTestingUtils.getIoService().createFile(fileSystemTestingUtils.getIoService().get(URI.create(PATH_PREFIX + "MyProject/change_requests/invalid-id/information.cr")));
        fileSystemTestingUtils.getIoService().createFile(fileSystemTestingUtils.getIoService().get(URI.create(PATH_PREFIX + "MyProject/change_requests/1/information.cr")));

        doReturn(true).when(objectStorage).exists("/MyProject/change_requests");
        doReturn(crsPath).when(objectStorage).getPath("/MyProject/change_requests");

        final List<Long> ids = spaceConfigStorage.getChangeRequestIds("MyProject");

        assertSame(1, ids.size());
        assertSame(1L, ids.get(0));
    }

    @Test
    public void getChangeRequestIdsNoResultsTest() {
        final org.uberfire.java.nio.file.Path crsPath = fileSystemTestingUtils.getIoService().get(URI.create(PATH_PREFIX + "MyProject/change_requests"));
        fileSystemTestingUtils.getIoService().createFile(fileSystemTestingUtils.getIoService().get(URI.create(PATH_PREFIX + "MyProject/change_requests/1/information.cr")));
        fileSystemTestingUtils.getIoService().createFile(fileSystemTestingUtils.getIoService().get(URI.create(PATH_PREFIX + "MyProject/change_requests/2/information.cr")));

        doReturn(true).when(objectStorage).exists("/MyProject/change_requests");
        doReturn(crsPath).when(objectStorage).getPath("/MyProject/change_requests");

        final List<Long> ids = spaceConfigStorage.getChangeRequestIds("MyOtherProject");

        assertSame(0, ids.size());
    }

    @Test
    public void loadChangeRequestCommentsTest() {
        final List<Long> ids = new ArrayList<Long>() {{
            add(1L);
            add(2L);
        }};

        final ChangeRequestComment expectedChangeRequestComment0 = mock(ChangeRequestComment.class);
        final ChangeRequestComment expectedChangeRequestComment1 = mock(ChangeRequestComment.class);

        doReturn(ids).when(spaceConfigStorage).getChangeRequestCommentIds("MyProject", 1L);

        doReturn(expectedChangeRequestComment0).when(objectStorage).read("/MyProject/change_requests/1/comments/1.comment");
        doReturn(expectedChangeRequestComment1).when(objectStorage).read("/MyProject/change_requests/1/comments/2.comment");

        final List<ChangeRequestComment> comments = spaceConfigStorage.loadChangeRequestComments("MyProject", 1L);

        assertSame(expectedChangeRequestComment0,
                   comments.get(0));

        assertSame(expectedChangeRequestComment1,
                   comments.get(1));
    }

    @Test
    public void loadChangeRequestCommentTest() {
        final ChangeRequestComment expectedChangeRequestComment = mock(ChangeRequestComment.class);

        doReturn(expectedChangeRequestComment).when(objectStorage).read("/MyProject/change_requests/1/comments/1.comment");

        final ChangeRequestComment changeRequestComment = spaceConfigStorage.loadChangeRequestComment("MyProject", 1L, 1L);

        assertSame(expectedChangeRequestComment,
                   changeRequestComment);
    }

    @Test
    public void saveChangeRequestCommentTest() {
        final ChangeRequestComment changeRequestComment = mock(ChangeRequestComment.class);

        doReturn(1L).when(changeRequestComment).getId();

        spaceConfigStorage.saveChangeRequestComment("MyProject", 1L, changeRequestComment);

        verify(objectStorage).write(eq("/MyProject/change_requests/1/comments/1.comment"),
                                    same(changeRequestComment));
    }

    @Test
    public void deleteAllChangeRequestCommentsTest() {
        final org.uberfire.java.nio.file.Path commentsPath = fileSystemTestingUtils.getIoService().get(URI.create(PATH_PREFIX + "MyProject/change_requests/1/comments"));

        fileSystemTestingUtils.getIoService().createFile(fileSystemTestingUtils.getIoService().get(URI.create(PATH_PREFIX + "MyProject/change_requests/1/comments/1.comment")));
        fileSystemTestingUtils.getIoService().createFile(fileSystemTestingUtils.getIoService().get(URI.create(PATH_PREFIX + "MyProject/change_requests/1/comments/2.comment")));

        doReturn(true).when(objectStorage).exists("/MyProject/change_requests/1/comments");
        doReturn(commentsPath).when(objectStorage).getPath("/MyProject/change_requests/1/comments");

        spaceConfigStorage.deleteAllChangeRequestComments("MyProject", 1L);

        verify(objectStorage).delete(eq("/MyProject/change_requests/1/comments/1.comment"));
        verify(objectStorage).delete(eq("/MyProject/change_requests/1/comments/2.comment"));
    }

    @Test
    public void deleteChangeRequestCommentTest() {
        spaceConfigStorage.deleteChangeRequestComment("MyProject", 1L, 1L);

        verify(objectStorage).delete(eq("/MyProject/change_requests/1/comments/1.comment"));
    }

    @Test
    public void getChangeRequestCommentIdsTest() {
        final org.uberfire.java.nio.file.Path commentsPath = fileSystemTestingUtils.getIoService().get(URI.create(PATH_PREFIX + "MyProject/change_requests/1/comments"));

        fileSystemTestingUtils.getIoService().createFile(fileSystemTestingUtils.getIoService().get(URI.create(PATH_PREFIX + "MyProject/change_requests/1/comments/1.comment")));
        fileSystemTestingUtils.getIoService().createFile(fileSystemTestingUtils.getIoService().get(URI.create(PATH_PREFIX + "MyProject/change_requests/1/comments/20.comment")));
        fileSystemTestingUtils.getIoService().createFile(fileSystemTestingUtils.getIoService().get(URI.create(PATH_PREFIX + "MyProject/change_requests/1/comments/30.comment")));

        doReturn(true).when(objectStorage).exists("/MyProject/change_requests/1/comments");
        doReturn(commentsPath).when(objectStorage).getPath("/MyProject/change_requests/1/comments");

        final List<Long> ids = spaceConfigStorage.getChangeRequestCommentIds("MyProject", 1L);

        assertSame(1L, ids.get(0));
        assertSame(20L, ids.get(1));
        assertSame(30L, ids.get(2));
    }

    @Test
    public void getChangeRequestCommentIdsSkipInvalidIdsTest() {
        final org.uberfire.java.nio.file.Path commentsPath = fileSystemTestingUtils.getIoService().get(URI.create(PATH_PREFIX + "MyProject/change_requests/1/comments"));

        fileSystemTestingUtils.getIoService().createFile(fileSystemTestingUtils.getIoService().get(URI.create(PATH_PREFIX + "MyProject/change_requests/1/comments/1.comment")));
        fileSystemTestingUtils.getIoService().createFile(fileSystemTestingUtils.getIoService().get(URI.create(PATH_PREFIX + "MyProject/change_requests/1/comments/20.comment")));
        fileSystemTestingUtils.getIoService().createFile(fileSystemTestingUtils.getIoService().get(URI.create(PATH_PREFIX + "MyProject/change_requests/1/comments/invalid-id.comment")));

        doReturn(true).when(objectStorage).exists("/MyProject/change_requests/1/comments");
        doReturn(commentsPath).when(objectStorage).getPath("/MyProject/change_requests/1/comments");

        final List<Long> ids = spaceConfigStorage.getChangeRequestCommentIds("MyProject", 1L);

        assertSame(2, ids.size());
        assertSame(1L, ids.get(0));
        assertSame(20L, ids.get(1));
    }

    @Test
    public void getChangeRequestCommentIdsNoResultsOtherChangeRequestTest() {
        final org.uberfire.java.nio.file.Path commentsPath = fileSystemTestingUtils.getIoService().get(URI.create(PATH_PREFIX + "MyProject/change_requests/1/comments"));

        fileSystemTestingUtils.getIoService().createFile(fileSystemTestingUtils.getIoService().get(URI.create(PATH_PREFIX + "MyProject/change_requests/1/comments/1.comment")));
        fileSystemTestingUtils.getIoService().createFile(fileSystemTestingUtils.getIoService().get(URI.create(PATH_PREFIX + "MyProject/change_requests/1/comments/20.comment")));
        fileSystemTestingUtils.getIoService().createFile(fileSystemTestingUtils.getIoService().get(URI.create(PATH_PREFIX + "MyProject/change_requests/1/comments/30.comment")));

        doReturn(true).when(objectStorage).exists("/MyProject/change_requests/1/comments");
        doReturn(commentsPath).when(objectStorage).getPath("/MyProject/change_requests/1/comments");

        final List<Long> ids = spaceConfigStorage.getChangeRequestCommentIds("MyProject", 2L);

        assertSame(0, ids.size());
    }

    @Test
    public void getChangeRequestCommentIdsNoResultsOtherProjectTest() {
        final org.uberfire.java.nio.file.Path commentsPath = fileSystemTestingUtils.getIoService().get(URI.create(PATH_PREFIX + "MyProject/change_requests/1/comments"));

        fileSystemTestingUtils.getIoService().createFile(fileSystemTestingUtils.getIoService().get(URI.create(PATH_PREFIX + "MyProject/change_requests/1/comments/1.comment")));
        fileSystemTestingUtils.getIoService().createFile(fileSystemTestingUtils.getIoService().get(URI.create(PATH_PREFIX + "MyProject/change_requests/1/comments/20.comment")));
        fileSystemTestingUtils.getIoService().createFile(fileSystemTestingUtils.getIoService().get(URI.create(PATH_PREFIX + "MyProject/change_requests/1/comments/30.comment")));

        doReturn(true).when(objectStorage).exists("/MyProject/change_requests/1/comments");
        doReturn(commentsPath).when(objectStorage).getPath("/MyProject/change_requests/1/comments");

        final List<Long> ids = spaceConfigStorage.getChangeRequestCommentIds("MyOtherProject", 1L);

        assertSame(0, ids.size());
    }
}