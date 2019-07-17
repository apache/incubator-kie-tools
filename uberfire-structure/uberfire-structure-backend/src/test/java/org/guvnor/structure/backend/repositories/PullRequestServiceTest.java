/*
 * 2016 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.structure.backend.repositories;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guvnor.structure.backend.repositories.git.GitMetadataStoreImpl;
import org.guvnor.structure.repositories.GitMetadata;
import org.guvnor.structure.repositories.GitMetadataStore;
import org.guvnor.structure.repositories.PullRequest;
import org.guvnor.structure.repositories.PullRequestAlreadyExistsException;
import org.guvnor.structure.repositories.PullRequestService;
import org.guvnor.structure.repositories.PullRequestStatus;
import org.guvnor.structure.repositories.RepositoryNotFoundException;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.repositories.impl.GitMetadataImpl;
import org.guvnor.structure.repositories.impl.PullRequestImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.io.object.ObjectStorage;
import org.uberfire.backend.server.spaces.SpacesAPIImpl;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.MergeCopyOption;
import org.uberfire.java.nio.file.Path;
import org.uberfire.spaces.SpacesAPI;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PullRequestServiceTest {

    private PullRequestService service;

    private GitMetadataStore metadataStore;

    @Mock
    private IOService ioService;

    @Mock
    private SpacesAPI spaces;

    @Mock
    private ObjectStorage storage;
    private Map<String, GitMetadataImpl> metadatas;

    @Before
    public void setUp() throws Exception {
        metadatas = new HashMap<>();
        metadataStore = new GitMetadataStoreImpl(storage,
                                                 new SpacesAPIImpl());

        this.service = new PullRequestServiceImpl(metadataStore,
                                                  ioService,
                                                  mock(RepositoryService.class),
                                                  spaces);

        doAnswer(invocationOnMock -> {
            String key = invocationOnMock.getArgumentAt(0,
                                                        String.class);
            GitMetadataImpl metadata = invocationOnMock.getArgumentAt(1,
                                                                      GitMetadataImpl.class);
            storage.write(key, metadata, true);

            return null;
        }).when(storage).write(anyString(), any());

        doAnswer(invocationOnMock -> {
            String key = invocationOnMock.getArgumentAt(0,
                                                        String.class);
            GitMetadataImpl metadata = invocationOnMock.getArgumentAt(1,
                                                                      GitMetadataImpl.class);
            metadatas.put(key,
                          metadata);
            return null;
        }).when(storage).write(anyString(),
                               any(),
                               anyBoolean());

        doAnswer(invocationOnMock -> {
            String key = invocationOnMock.getArgumentAt(0,
                                                        String.class);
            return metadatas.get(key);
        }).when(storage).read(anyString());

        doAnswer(invocationOnMock -> {
            String key = invocationOnMock.getArgumentAt(0,
                                                        String.class);
            return metadatas.remove(key);
        }).when(storage).delete(anyString());

        metadataStore.write("parent/a");
        metadataStore.write("child/a",
                            "parent/a");
    }

    @Test(expected = RepositoryNotFoundException.class)
    public void testCreatePullRequestToUnexistentRepository() {
        PullRequest pullRequest = service.createPullRequest("test-realm",
                                                            "child/a",
                                                            "develop",
                                                            "test-realm",
                                                            "doesNotExist/a",
                                                            "master");
    }

    @Test
    public void testCreatePullRequest() {
        PullRequest pullRequest = service.createPullRequest("test-realm",
                                                            "child/a",
                                                            "develop",
                                                            "test-realm",
                                                            "parent/a",
                                                            "master");
        List<PullRequest> pullRequests = service.getPullRequestsByBranch(0,
                                                                         0,
                                                                         pullRequest.getTargetRepository(),
                                                                         pullRequest.getTargetBranch());
        assertEquals(1,
                     pullRequests.size());
        assertEquals(PullRequestStatus.OPEN,
                     pullRequests.get(0).getStatus());
    }

    @Test
    public void testCreateSeveralPullRequest() {
        final String repository = "parent/a";
        final String branch = "master";
        service.createPullRequest("test-realm",
                                  "child/a",
                                  "develop",
                                  "test-realm",
                                  repository,
                                  branch);
        service.createPullRequest("test-realm",
                                  "child/b",
                                  "develop",
                                  "test-realm",
                                  repository,
                                  "otherBranch");
        service.createPullRequest("test-realm",
                                  "child/c",
                                  "develop",
                                  "test-realm",
                                  repository,
                                  branch);
        service.createPullRequest("test-realm",
                                  "child/d",
                                  "develop",
                                  "test-realm",
                                  repository,
                                  branch);
        List<PullRequest> pullRequests = service.getPullRequestsByRepository(0,
                                                                             0,
                                                                             repository);
        assertEquals(4,
                     pullRequests.size());
        assertTrue(pullRequests.stream().allMatch(elem -> elem.getStatus().equals(PullRequestStatus.OPEN)));
    }

    @Test
    public void testAcceptPullRequest() {
        PullRequest pullRequest = service.createPullRequest("test-realm",
                                                            "child/a",
                                                            "develop",
                                                            "test-realm",
                                                            "parent/a",
                                                            "master");

        service.acceptPullRequest(pullRequest);

        List<PullRequest> pullRequests = service.getPullRequestsByBranch(0,
                                                                         0,
                                                                         pullRequest.getTargetRepository(),
                                                                         pullRequest.getTargetBranch());
        assertEquals(PullRequestStatus.MERGED,
                     pullRequests.get(0).getStatus());

        verify(ioService).copy(any(Path.class),
                               any(Path.class),
                               any(MergeCopyOption.class));
    }

    @Test
    public void testExceptionWhenTryingtoAcceptPullRequest() {

        when(ioService.copy(any(Path.class),
                            any(Path.class),
                            any())).thenThrow(new RuntimeException("Mock exception"));
        PullRequest pullRequest = service.createPullRequest("test-realm",
                                                            "child/a",
                                                            "develop",
                                                            "test-realm",
                                                            "parent/a",
                                                            "master");
        try {
            service.acceptPullRequest(pullRequest);
            fail("Should throw exception before this point");
        } catch (Exception e) {
            List<PullRequest> pullRequests = service.getPullRequestsByBranch(0,
                                                                             0,
                                                                             pullRequest.getTargetRepository(),
                                                                             pullRequest.getTargetBranch());
            assertEquals(PullRequestStatus.OPEN,
                         pullRequests.get(0).getStatus());
        }
    }

    @Test
    public void testFailToCreatePullRequest() {
        PullRequest pullRequest = new PullRequestImpl("test-realm",
                                                      "child/a",
                                                      "develop",
                                                      "test-realm",
                                                      "parent/a",
                                                      "master");
        doThrow(new RuntimeException("Mocked exception")).when(this.storage).write(any(String.class),
                                                                                   any(GitMetadata.class),
                                                                                   anyBoolean());
        try {
            pullRequest = service.createPullRequest("test-realm",
                                                    "child/a",
                                                    "develop",
                                                    "test-realm",
                                                    "parent/a",
                                                    "master");
            fail("Should throw exception before this point");
        } catch (Exception e) {
            List<PullRequest> pullRequests = service.getPullRequestsByBranch(0,
                                                                             0,
                                                                             pullRequest.getTargetRepository(),
                                                                             pullRequest.getTargetBranch());
            assertEquals(0,
                         pullRequests.size());
        }
    }

    @Test
    public void testRejectPullRequest() {
        PullRequest pullRequest = service.createPullRequest("test-realm",
                                                            "child/a",
                                                            "develop",
                                                            "test-realm",
                                                            "parent/a",
                                                            "master");
        service.rejectPullRequest(pullRequest);

        List<PullRequest> pullRequests = service.getPullRequestsByBranch(0,
                                                                         0,
                                                                         pullRequest.getTargetRepository(),
                                                                         pullRequest.getTargetBranch());
        assertEquals(PullRequestStatus.REJECTED,
                     pullRequests.get(0).getStatus());
    }

    @Test
    public void testClosePullRequest() {
        PullRequest pullRequest = service.createPullRequest("test-realm",
                                                            "child/a",
                                                            "develop",
                                                            "test-realm",
                                                            "parent/a",
                                                            "master");
        service.closePullRequest(pullRequest);

        List<PullRequest> pullRequests = service.getPullRequestsByBranch(0,
                                                                         0,
                                                                         pullRequest.getTargetRepository(),
                                                                         pullRequest.getTargetBranch());
        assertEquals(PullRequestStatus.CLOSED,
                     pullRequests.get(0).getStatus());
    }

    @Test
    public void testChangeStatusToMergedPullRequest() {
        PullRequest pullRequest = service.createPullRequest("test-realm",
                                                            "child/a",
                                                            "develop",
                                                            "test-realm",
                                                            "parent/a",
                                                            "master");
        ((PullRequestServiceImpl) service).changePullRequestStatus(pullRequest.getTargetRepository(),
                                                                   pullRequest.getId(),
                                                                   PullRequestStatus.MERGED);
        List<PullRequest> pullRequests = service.getPullRequestsByBranch(0,
                                                                         0,
                                                                         pullRequest.getTargetRepository(),
                                                                         pullRequest.getTargetBranch());
        assertEquals(PullRequestStatus.MERGED,
                     pullRequests.get(0).getStatus());
    }

    @Test
    public void testChangeStatusToClosedPullRequest() {
        PullRequest pullRequest = service.createPullRequest("test-realm",
                                                            "child/a",
                                                            "develop",
                                                            "test-realm",
                                                            "parent/a",
                                                            "master");
        ((PullRequestServiceImpl) service).changePullRequestStatus(pullRequest.getTargetRepository(),
                                                                   pullRequest.getId(),
                                                                   PullRequestStatus.CLOSED);
        List<PullRequest> pullRequests = service.getPullRequestsByBranch(0,
                                                                         0,
                                                                         pullRequest.getTargetRepository(),
                                                                         pullRequest.getTargetBranch());
        assertEquals(PullRequestStatus.CLOSED,
                     pullRequests.get(0).getStatus());
    }

    @Test
    public void testDeletePullRequest() {
        PullRequest pullRequest = service.createPullRequest("test-realm",
                                                            "child/a",
                                                            "develop",
                                                            "test-realm",
                                                            "parent/a",
                                                            "master");
        service.deletePullRequest(pullRequest);
        List<PullRequest> pullRequests = service.getPullRequestsByBranch(0,
                                                                         0,
                                                                         pullRequest.getTargetRepository(),
                                                                         pullRequest.getTargetBranch());
        assertEquals(0,
                     pullRequests.size());
    }

    @Test
    public void testGetAllPullRequests() {
        service.createPullRequest("test-realm",
                                  "child/a",
                                  "develop",
                                  "test-realm",
                                  "parent/a",
                                  "master");
        service.createPullRequest("test-realm",
                                  "child/b",
                                  "develop",
                                  "test-realm",
                                  "parent/a",
                                  "develop");
        service.createPullRequest("test-realm",
                                  "child/c",
                                  "develop",
                                  "test-realm",
                                  "parent/a",
                                  "master");

        final List<PullRequest> pullRequestsForBranchMaster = service.getPullRequestsByBranch(0,
                                                                                              0,
                                                                                              "parent/a",
                                                                                              "master");
        final List<PullRequest> pullRequestsForBranchDevelop = service.getPullRequestsByBranch(0,
                                                                                               0,
                                                                                               "parent/a",
                                                                                               "develop");
        final List<PullRequest> pullRequestsForRepository = service.getPullRequestsByRepository(0,
                                                                                                0,
                                                                                                "parent/a");

        assertEquals(2,
                     pullRequestsForBranchMaster.size());
        assertEquals(1,
                     pullRequestsForBranchDevelop.size());
        assertEquals(3,
                     pullRequestsForRepository.size());
    }

    @Test
    public void testGetAllPullRequestsWithDifferentStatus() {
        PullRequest pullRequestA = service.createPullRequest("test-realm",
                                                             "child/a",
                                                             "develop",
                                                             "test-realm",
                                                             "parent/a",
                                                             "master");
        PullRequest pullRequestB = service.createPullRequest("test-realm",
                                                             "child/b",
                                                             "develop",
                                                             "test-realm",
                                                             "parent/a",
                                                             "develop");
        PullRequest pullRequestC = service.createPullRequest("test-realm",
                                                             "child/c",
                                                             "develop",
                                                             "test-realm",
                                                             "parent/a",
                                                             "master");
        PullRequest pullRequestD = service.createPullRequest("test-realm",
                                                             "child/d",
                                                             "develop",
                                                             "test-realm",
                                                             "parent/a",
                                                             "master");

        service.acceptPullRequest(pullRequestA);
        service.rejectPullRequest(pullRequestB);
        service.closePullRequest(pullRequestC);

        final List<PullRequest> pullRequestsMerged = service.getPullRequestsByStatus(0,
                                                                                     0,
                                                                                     "parent/a",
                                                                                     PullRequestStatus.MERGED);
        final List<PullRequest> pullRequestsClosed = service.getPullRequestsByStatus(0,
                                                                                     0,
                                                                                     "parent/a",
                                                                                     PullRequestStatus.CLOSED);
        final List<PullRequest> pullRequestsOpened = service.getPullRequestsByStatus(0,
                                                                                     0,
                                                                                     "parent/a",
                                                                                     PullRequestStatus.OPEN);
        final List<PullRequest> pullRequestsRejected = service.getPullRequestsByStatus(0,
                                                                                       0,
                                                                                       "parent/a",
                                                                                       PullRequestStatus.REJECTED);

        assertEquals(1,
                     pullRequestsMerged.size());
        assertEquals(1,
                     pullRequestsClosed.size());
        assertEquals(1,
                     pullRequestsOpened.size());
        assertEquals(1,
                     pullRequestsRejected.size());
    }

    @Test
    public void testGeneratePullRequestId() {
        PullRequest pullRequestOne = service.createPullRequest("test-realm",
                                                               "child/a",
                                                               "develop",
                                                               "test-realm",
                                                               "parent/a",
                                                               "master");
        PullRequest pullRequestTwo = service.createPullRequest("test-realm",
                                                               "child/a",
                                                               "fix",
                                                               "test-realm",
                                                               "parent/a",
                                                               "master");
        PullRequest pullRequestThree = service.createPullRequest("test-realm",
                                                                 "child/a",
                                                                 "fix",
                                                                 "test-realm",
                                                                 "parent/a",
                                                                 "develop");
        PullRequest pullRequestFour = service.createPullRequest("test-realm",
                                                                "child/b",
                                                                "fix",
                                                                "test-realm",
                                                                "parent/a",
                                                                "master");
        assertEquals(1,
                     pullRequestOne.getId());
        assertEquals(2,
                     pullRequestTwo.getId());
        assertEquals(3,
                     pullRequestThree.getId());
        assertEquals(4,
                     pullRequestFour.getId());
    }

    @Test(expected = PullRequestAlreadyExistsException.class)
    public void testCannotCreateSamePullRequest() {
        service.createPullRequest("test-realm",
                                  "child/a",
                                  "fix",
                                  "test-realm",
                                  "parent/a",
                                  "master");
        service.createPullRequest("test-realm",
                                  "child/a",
                                  "fix",
                                  "test-realm",
                                  "parent/a",
                                  "master");
    }

    @Test
    public void testCreateANewPullRequestWhenItISClosed() {
        final PullRequest pr1 = service.createPullRequest("test-realm",
                                                          "child/a",
                                                          "fix",
                                                          "test-realm",
                                                          "parent/a",
                                                          "master");
        service.acceptPullRequest(pr1);
        final PullRequest pr2 = service.createPullRequest("test-realm",
                                                          "child/a",
                                                          "fix",
                                                          "test-realm",
                                                          "parent/a",
                                                          "master");
        service.acceptPullRequest(pr2);
        assertEquals(2,
                     pr2.getId());
    }

    @Test
    public void testGeneratedNumbersWhenPRAlreadyExists() {
        final PullRequest pr1 = service.createPullRequest("test-realm",
                                                          "child/a",
                                                          "fix",
                                                          "test-realm",
                                                          "parent/a",
                                                          "master");
        try {
            final PullRequest pr2 = service.createPullRequest("test-realm",
                                                              "child/a",
                                                              "fix",
                                                              "test-realm",
                                                              "parent/a",
                                                              "master");
        } catch (PullRequestAlreadyExistsException e) {

        }
        final PullRequest pr2 = service.createPullRequest("test-realm",
                                                          "child/b",
                                                          "fix",
                                                          "test-realm",
                                                          "parent/a",
                                                          "master");
        assertEquals(2,
                     pr2.getId());
    }

    @Test
    public void testBuildHiddenPath() {

        ((PullRequestServiceImpl) service).buildHiddenPath(new PullRequestImpl(1,
                                                                               "test-realm",
                                                                               "source/a",
                                                                               "develop",
                                                                               "test-realm",
                                                                               "target/a",
                                                                               "master",
                                                                               PullRequestStatus.OPEN));
        final URI uri = URI.create("git://PR-1-source/a/develop-master@target/a");

        verify(ioService).get(eq(uri));
    }

    @Test
    public void testBuildPath() {

        final Path path = ((PullRequestServiceImpl) service).buildPath("source/a",
                                                                       "develop");
        final URI uri = URI.create("git://develop@source/a");

        verify(ioService).get(uri);
    }

    @Test
    public void testPagination() {
        final PullRequestImpl pr = new PullRequestImpl(1,
                                                       "test-realm",
                                                       "source/a",
                                                       "develop",
                                                       "test-realm",
                                                       "target/a",
                                                       "master",
                                                       PullRequestStatus.OPEN);
        List<PullRequest> pullRequests = Arrays.asList(pr,
                                                       pr,
                                                       pr,
                                                       pr,
                                                       pr,
                                                       pr,
                                                       pr,
                                                       pr,
                                                       pr,
                                                       pr);

        assertEquals(10,
                     ((PullRequestServiceImpl) service).paginate(0,
                                                                 0,
                                                                 pullRequests).size());
        assertEquals(10,
                     ((PullRequestServiceImpl) service).paginate(0,
                                                                 15,
                                                                 pullRequests).size());
        assertEquals(5,
                     ((PullRequestServiceImpl) service).paginate(1,
                                                                 5,
                                                                 pullRequests).size());
        assertEquals(1,
                     ((PullRequestServiceImpl) service).paginate(9,
                                                                 1,
                                                                 pullRequests).size());
    }
}
