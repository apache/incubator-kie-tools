/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.structure.backend.repositories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.event.Event;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorage;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorageRegistry;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequest;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestAlreadyOpenException;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestComment;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestCountSummary;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestDiff;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestListUpdatedEvent;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestStatus;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestStatusUpdatedEvent;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestUpdatedEvent;
import org.guvnor.structure.repositories.changerequest.portable.NothingToMergeException;
import org.guvnor.structure.repositories.changerequest.portable.PaginatedChangeRequestCommentList;
import org.guvnor.structure.repositories.changerequest.portable.PaginatedChangeRequestList;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.java.nio.base.TextualDiff;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProvider;
import org.uberfire.java.nio.fs.jgit.JGitPathImpl;
import org.uberfire.java.nio.fs.jgit.util.Git;
import org.uberfire.java.nio.fs.jgit.util.model.CommitInfo;
import org.uberfire.java.nio.fs.jgit.util.model.PathInfo;
import org.uberfire.java.nio.fs.jgit.util.model.RevertCommitContent;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.spaces.Space;
import org.uberfire.spaces.SpacesAPI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ChangeRequestServiceTest {

    private ChangeRequestServiceImpl service;

    @Mock
    private SpaceConfigStorageRegistry spaceConfigStorageRegistry;

    @Mock
    private RepositoryService repositoryService;

    @Mock
    private SpacesAPI spaces;

    @Mock
    private Event<ChangeRequestListUpdatedEvent> changeRequestListUpdatedEvent;

    @Mock
    private Event<ChangeRequestUpdatedEvent> changeRequestUpdatedEvent;

    @Mock
    private Event<ChangeRequestStatusUpdatedEvent> changeRequestStatusUpdatedEventEvent;

    @Mock
    private BranchAccessAuthorizer branchAccessAuthorizer;

    @Mock
    private SessionInfo sessionInfo;

    @Mock
    private SpaceConfigStorage spaceConfigStorage;

    @Mock
    private Repository repository;

    @Mock
    private Branch sourceBranch;

    @Mock
    private Branch targetBranch;

    @Mock
    private Branch hiddenBranch;

    @Mock
    private Git git;

    @Mock
    private JGitFileSystem fs;

    @Mock
    private JGitFileSystemProvider provider;

    @Mock
    private RevCommit commonCommit;

    @Mock
    private RevCommit lastCommit;

    @Before
    public void setUp() {
        Space mySpace = mock(Space.class);

        User user = mock(User.class);

        doReturn(user).when(sessionInfo).getIdentity();
        doReturn("authorId").when(user).getIdentifier();

        doReturn(spaceConfigStorage).when(spaceConfigStorageRegistry).get("mySpace");
        doReturn(mySpace).when(spaces).getSpace("mySpace");
        doReturn(repository).when(repositoryService).getRepositoryFromSpace(mySpace,
                                                                            "myRepository");

        doReturn("sourceBranch").when(sourceBranch).getName();
        doReturn("targetBranch").when(targetBranch).getName();
        doReturn("hiddenBranch").when(hiddenBranch).getName();

        Branch branch = mock(Branch.class);
        doReturn("branch").when(branch).getName();

        List<Branch> branches = Stream.of(sourceBranch, targetBranch, hiddenBranch, branch)
                .collect(Collectors.toList());
        doReturn(branches).when(repository).getBranches();

        doReturn("myRepository").when(repository).getAlias();
        doReturn(mySpace).when(repository).getSpace();
        doReturn("mySpace").when(mySpace).getName();

        doReturn(Optional.of(sourceBranch)).when(repository).getBranch("sourceBranch");
        doReturn(Optional.of(targetBranch)).when(repository).getBranch("targetBranch");
        doReturn(Optional.of(hiddenBranch)).when(repository).getBranch("hiddenBranch");

        doReturn(commonCommit).when(git).getCommonAncestorCommit("sourceBranch",
                                                                 "targetBranch");

        doReturn(lastCommit).when(git).getLastCommit(anyString());

        doReturn(true).when(branchAccessAuthorizer).authorize(anyString(),
                                                              anyString(),
                                                              anyString(),
                                                              anyString(),
                                                              anyString(),
                                                              any());

        this.service = spy(new ChangeRequestServiceImpl(spaceConfigStorageRegistry,
                                                        repositoryService,
                                                        spaces,
                                                        changeRequestListUpdatedEvent,
                                                        changeRequestUpdatedEvent,
                                                        changeRequestStatusUpdatedEventEvent,
                                                        branchAccessAuthorizer,
                                                        sessionInfo));

        doReturn(fs).when(service).getFileSystemFromBranch(repository, "sourceBranch");
        doReturn(fs).when(service).getFileSystemFromBranch(repository, "targetBranch");
        doReturn(git).when(fs).getGit();

        doNothing().when(provider).executePostCommitHook(any());
        doReturn(provider).when(fs).provider();

        doReturn(mock(PathInfo.class)).when(git).getPathInfo(anyString(),
                                                             anyString());

        doReturn(mock(JGitPathImpl.class)).when(service).createJGitPathImpl(eq(fs),
                                                                            anyString(),
                                                                            anyString(),
                                                                            any(ObjectId.class),
                                                                            anyBoolean());

        doReturn("commit message").when(service).getFullCommitMessage(any(RevCommit.class));
    }

    @Test
    public void createFirstChangeRequestTest() {
        doReturn(Collections.emptyList()).when(spaceConfigStorage).getChangeRequestIds("myRepository");

        ChangeRequest newChangeRequest = service.createChangeRequest("mySpace",
                                                                     "myRepository",
                                                                     "sourceBranch",
                                                                     "targetBranch",
                                                                     "summary",
                                                                     "description");

        assertThat(newChangeRequest.getId()).isEqualTo(1L);
        verify(spaceConfigStorageRegistry.get("mySpace")).saveChangeRequest("myRepository",
                                                                            newChangeRequest);
        verify(changeRequestListUpdatedEvent).fire(any(ChangeRequestListUpdatedEvent.class));
    }

    @Test
    public void createChangeRequestTest() {
        List<Long> ids = Arrays.asList(1L, 10L, 2L, 3L, 4L);
        doReturn(ids).when(spaceConfigStorage).getChangeRequestIds("myRepository");

        ChangeRequest newChangeRequest = service.createChangeRequest("mySpace",
                                                                     "myRepository",
                                                                     "sourceBranch",
                                                                     "targetBranch",
                                                                     "summary",
                                                                     "description");

        assertThat(newChangeRequest.getId()).isEqualTo(11L);
        assertThat(newChangeRequest.getStatus()).isEqualTo(ChangeRequestStatus.OPEN);
        verify(spaceConfigStorageRegistry.get("mySpace")).saveChangeRequest("myRepository",
                                                                            newChangeRequest);
        verify(changeRequestListUpdatedEvent).fire(any(ChangeRequestListUpdatedEvent.class));
    }

    @Test(expected = ChangeRequestAlreadyOpenException.class)
    public void createChangeRequestFailWhenAlreadyOpenTest() {
        List<ChangeRequest> crList = Collections.singletonList(createCommonChangeRequest());
        doReturn(crList).when(spaceConfigStorage).loadChangeRequests("myRepository");

        service.createChangeRequest("mySpace",
                                    "myRepository",
                                    "sourceBranch",
                                    "targetBranch",
                                    "summary",
                                    "description");
    }

    @Test(expected = NoSuchElementException.class)
    public void createChangeRequestInvalidRepositoryTest() {
        service.createChangeRequest("mySpace",
                                    "myOtherRepository",
                                    "sourceBranch",
                                    "targetBranch",
                                    "summary",
                                    "description");
    }

    @Test
    public void getChangeRequestsTest() {
        List<ChangeRequest> crList = Collections.nCopies(5, createCommonChangeRequest());

        doReturn(crList).when(spaceConfigStorage).loadChangeRequests("myRepository");
        List<ChangeRequest> actualList = service.getChangeRequests("mySpace",
                                                                   "myRepository");

        assertThat(actualList).isNotEmpty();
        assertThat(actualList).hasSize(5);
    }

    @Test
    public void getChangeRequestUserCannotAccessBranchesTest() {
        doReturn(false).when(branchAccessAuthorizer).authorize(anyString(),
                                                               anyString(),
                                                               anyString(),
                                                               anyString(),
                                                               anyString(),
                                                               any());

        List<ChangeRequest> crList = Collections.nCopies(5, mock(ChangeRequest.class));

        doReturn(crList).when(spaceConfigStorage).loadChangeRequests("myRepository");
        List<ChangeRequest> actualList = service.getChangeRequests("mySpace",
                                                                   "myRepository");

        assertThat(actualList).isEmpty();
    }

    @Test
    public void getChangeRequestUserCanAccessSomeBranchesTest() {
        doReturn(false).when(branchAccessAuthorizer).authorize(anyString(),
                                                               anyString(),
                                                               anyString(),
                                                               anyString(),
                                                               eq("hiddenBranch"),
                                                               any());

        doReturn(true).when(branchAccessAuthorizer).authorize(anyString(),
                                                              anyString(),
                                                              anyString(),
                                                              anyString(),
                                                              eq("branch"),
                                                              any());

        ChangeRequest cr1 = createCommonChangeRequestWithTargetBranch("hiddenBranch");
        ChangeRequest cr2 = createCommonChangeRequestWithTargetBranch("hiddenBranch");
        ChangeRequest cr3 = createCommonChangeRequestWithTargetBranch("targetBranch");
        ChangeRequest cr4 = createCommonChangeRequestWithTargetBranch("targetBranch");

        List<ChangeRequest> crList = Arrays.asList(cr1, cr2, cr3, cr4);
        doReturn(crList).when(spaceConfigStorage).loadChangeRequests("myRepository");

        List<ChangeRequest> actualList = service.getChangeRequests("mySpace",
                                                                   "myRepository");

        assertThat(actualList).isNotEmpty();
        assertThat(actualList).hasSize(2);
    }

    @Test
    public void getChangeRequestsWithFilterTest() {
        ChangeRequest cr1 = createCommonChangeRequestWithSummary("findme");
        ChangeRequest cr2 = createCommonChangeRequestWithSummary("findme");
        ChangeRequest cr3 = createCommonChangeRequestWithSummary("hidden");
        ChangeRequest cr4 = createCommonChangeRequestWithSummary("hidden");

        List<ChangeRequest> crList = Arrays.asList(cr1, cr2, cr3, cr4);
        doReturn(crList).when(spaceConfigStorage).loadChangeRequests("myRepository");

        List<ChangeRequest> actualList = service.getChangeRequests("mySpace",
                                                                   "myRepository",
                                                                   "find");

        assertThat(actualList).isNotEmpty();
        assertThat(actualList).hasSize(2);
    }

    @Test
    public void getChangeRequestsWithStatusTest() {
        ChangeRequest cr1 = createCommonChangeRequestWithStatus(ChangeRequestStatus.OPEN);
        ChangeRequest cr2 = createCommonChangeRequestWithStatus(ChangeRequestStatus.REJECTED);
        ChangeRequest cr3 = createCommonChangeRequestWithStatus(ChangeRequestStatus.ACCEPTED);
        ChangeRequest cr4 = createCommonChangeRequestWithStatus(ChangeRequestStatus.OPEN);

        List<ChangeRequest> crList = Arrays.asList(cr1, cr2, cr3, cr4);
        doReturn(crList).when(spaceConfigStorage).loadChangeRequests("myRepository");
        List<ChangeRequestStatus> statusList = new ArrayList<ChangeRequestStatus>() {{
            add(ChangeRequestStatus.OPEN);
        }};

        List<ChangeRequest> actualList = service.getChangeRequests("mySpace",
                                                                   "myRepository",
                                                                   statusList);

        assertThat(actualList).isNotEmpty();
        assertThat(actualList).hasSize(2);
    }

    @Test
    public void getChangeRequestsWithStatusAndFilterTest() {
        ChangeRequest cr1 = createCommonChangeRequestWithStatusSummary(ChangeRequestStatus.OPEN,
                                                                       "findme");
        ChangeRequest cr2 = createCommonChangeRequestWithStatusSummary(ChangeRequestStatus.REJECTED,
                                                                       "findme");
        ChangeRequest cr3 = createCommonChangeRequestWithStatusSummary(ChangeRequestStatus.ACCEPTED,
                                                                       "findme");
        ChangeRequest cr4 = createCommonChangeRequestWithStatusSummary(ChangeRequestStatus.OPEN,
                                                                       "findme");

        List<ChangeRequest> crList = Arrays.asList(cr1, cr2, cr3, cr4);
        doReturn(crList).when(spaceConfigStorage).loadChangeRequests("myRepository");
        List<ChangeRequestStatus> statusList = new ArrayList<ChangeRequestStatus>() {{
            add(ChangeRequestStatus.OPEN);
        }};

        List<ChangeRequest> actualList = service.getChangeRequests("mySpace",
                                                                   "myRepository",
                                                                   statusList,
                                                                   "find");

        assertThat(actualList).isNotEmpty();
        assertThat(actualList).hasSize(2);
    }

    @Test
    public void getChangeRequestsPaginatedWithFilterTest() {
        ChangeRequest crsWithFilter = createCommonChangeRequestWithStatusSummary(ChangeRequestStatus.OPEN,
                                                                                 "findme");

        ChangeRequest crsHidden = createCommonChangeRequestWithStatusSummary(ChangeRequestStatus.OPEN,
                                                                             "hidden");

        List<ChangeRequest> crList = new ArrayList<ChangeRequest>() {{
            addAll(Collections.nCopies(26, crsWithFilter));
            addAll(Collections.nCopies(30, crsHidden));
        }};

        doReturn(crList).when(spaceConfigStorage).loadChangeRequests("myRepository");

        PaginatedChangeRequestList actualList = service.getChangeRequests("mySpace",
                                                                          "myRepository",
                                                                          0,
                                                                          10,
                                                                          "find");

        assertThat(actualList.getChangeRequests()).isNotEmpty();
        assertEquals(10, (int) actualList.getChangeRequests().size());

        actualList = service.getChangeRequests("mySpace",
                                               "myRepository",
                                               1,
                                               10,
                                               "find");

        assertThat(actualList.getChangeRequests()).isNotEmpty();
        assertEquals(10, (int) actualList.getChangeRequests().size());

        actualList = service.getChangeRequests("mySpace",
                                               "myRepository",
                                               2,
                                               10,
                                               "find");

        assertThat(actualList.getChangeRequests()).isNotEmpty();
        assertEquals(6, (int) actualList.getChangeRequests().size());
    }

    @Test
    public void getChangeRequestsPaginatedWithStatusAndFilterTest() {
        ChangeRequest crsWithStatusAndFilter = createCommonChangeRequestWithStatusSummary(ChangeRequestStatus.ACCEPTED,
                                                                                          "findme");

        ChangeRequest crsOnlyFilter = createCommonChangeRequestWithSummary("findme");

        ChangeRequest crsOnlyStatus = createCommonChangeRequestWithStatus(ChangeRequestStatus.ACCEPTED);

        ChangeRequest crsHidden = createCommonChangeRequestWithSummary("hidden");

        List<ChangeRequest> crList = new ArrayList<ChangeRequest>() {{
            addAll(Collections.nCopies(20, crsOnlyStatus));
            addAll(Collections.nCopies(26, crsWithStatusAndFilter));
            addAll(Collections.nCopies(20, crsOnlyFilter));
            addAll(Collections.nCopies(30, crsHidden));
        }};
        List<ChangeRequestStatus> statusList = new ArrayList<ChangeRequestStatus>() {{
            add(ChangeRequestStatus.ACCEPTED);
        }};

        doReturn(crList).when(spaceConfigStorage).loadChangeRequests("myRepository");

        PaginatedChangeRequestList actualList = service.getChangeRequests("mySpace",
                                                                          "myRepository",
                                                                          0,
                                                                          10,
                                                                          statusList,
                                                                          "find");

        assertThat(actualList.getChangeRequests()).isNotEmpty();
        assertEquals(10, (int) actualList.getChangeRequests().size());

        actualList = service.getChangeRequests("mySpace",
                                               "myRepository",
                                               1,
                                               10,
                                               statusList,
                                               "find");

        assertThat(actualList.getChangeRequests()).isNotEmpty();
        assertEquals(10, (int) actualList.getChangeRequests().size());

        actualList = service.getChangeRequests("mySpace",
                                               "myRepository",
                                               2,
                                               10,
                                               statusList,
                                               "find");

        assertThat(actualList.getChangeRequests()).isNotEmpty();
        assertEquals(6, (int) actualList.getChangeRequests().size());
    }

    @Test
    public void getChangeRequestTest() {
        ChangeRequest cr1 = createCommonChangeRequestWithId(1L);
        ChangeRequest cr2 = createCommonChangeRequestWithId(2L);
        ChangeRequest cr3 = createCommonChangeRequestWithId(3L);
        ChangeRequest cr4 = createCommonChangeRequestWithId(4L);

        List<ChangeRequest> crList = Arrays.asList(cr1, cr2, cr3, cr4);
        doReturn(crList).when(spaceConfigStorage).loadChangeRequests("myRepository");

        ChangeRequest actual = service.getChangeRequest("mySpace",
                                                        "myRepository",
                                                        3L);

        assertThat(actual.getId()).isEqualTo(3L);
    }

    @Test(expected = NoSuchElementException.class)
    public void getChangeRequestNotFoundTest() {
        service.getChangeRequest("mySpace",
                                 "myRepository",
                                 10L);
    }

    @Test
    public void countChangeRequestsTest() {
        List<ChangeRequest> crList = new ArrayList<>();
        crList.addAll(Collections.nCopies(15, createCommonChangeRequestWithStatus(ChangeRequestStatus.OPEN)));
        crList.addAll(Collections.nCopies(5, createCommonChangeRequestWithStatus(ChangeRequestStatus.ACCEPTED)));

        doReturn(crList).when(spaceConfigStorage).loadChangeRequests("myRepository");

        ChangeRequestCountSummary countSummary = service.countChangeRequests("mySpace",
                                                                             "myRepository");

        assertEquals(15, (int) countSummary.getOpen());
        assertEquals(20, (int) countSummary.getTotal());
    }

    @Test
    public void getDiffTestNoResultsTest() {
        doReturn(Collections.emptyList()).when(git).conflictBranchesChecker(anyString(),
                                                                            anyString());
        doReturn(Collections.emptyList()).when(git).textualDiffRefs(anyString(),
                                                                    anyString());

        List<ChangeRequestDiff> diffs = service.getDiff("mySpace",
                                                        "myRepository",
                                                        "sourceBranch",
                                                        "targetBranch");

        assertThat(diffs).isEmpty();
    }

    @Test
    public void getDiffTestNoResultsForChangeRequestTest() {
        List<ChangeRequest> crList = Collections.nCopies(3, createCommonChangeRequest());
        doReturn(crList).when(spaceConfigStorage).loadChangeRequests("myRepository");

        doReturn(Collections.emptyList()).when(git).conflictBranchesChecker(anyString(),
                                                                            anyString());
        doReturn(Collections.emptyList()).when(git).textualDiffRefs(anyString(),
                                                                    anyString());

        List<ChangeRequestDiff> diffs = service.getDiff("mySpace",
                                                        "myRepository",
                                                        1L);

        assertThat(diffs).isEmpty();
    }

    @Test
    public void getDiffTestWithResultsTest() {
        doReturn(mock(Path.class)).when(sourceBranch).getPath();

        doReturn(mock(Path.class)).when(targetBranch).getPath();

        TextualDiff textualDiff = new TextualDiff("old/file/path",
                                                  "new/file/path",
                                                  "ADD",
                                                  10,
                                                  10,
                                                  "diff text");

        List<TextualDiff> diffList = Collections.nCopies(10, textualDiff);

        doReturn(Collections.emptyList()).when(git).conflictBranchesChecker(anyString(),
                                                                            anyString());
        doReturn(diffList).when(git).textualDiffRefs(anyString(),
                                                     anyString(),
                                                     anyString(),
                                                     anyString());
        List<ChangeRequestDiff> diffs = service.getDiff("mySpace",
                                                        "myRepository",
                                                        "sourceBranch",
                                                        "targetBranch");

        assertThat(diffs).isNotEmpty();
        assertThat(diffs).hasSize(10);
    }

    @Test
    public void getDiffTestWithResultsForChangeRequestTest() {
        List<ChangeRequest> crList = Collections.nCopies(3, createCommonChangeRequest());
        doReturn(crList).when(spaceConfigStorage).loadChangeRequests("myRepository");

        doReturn(mock(Path.class)).when(sourceBranch).getPath();

        doReturn(mock(Path.class)).when(targetBranch).getPath();

        TextualDiff textualDiff = new TextualDiff("old/file/path",
                                                  "new/file/path",
                                                  "ADD",
                                                  10,
                                                  10,
                                                  "diff text");

        List<TextualDiff> diffList = Collections.nCopies(10, textualDiff);

        doReturn(Collections.emptyList()).when(git).conflictBranchesChecker(anyString(),
                                                                            anyString());
        doReturn(diffList).when(git).textualDiffRefs(anyString(),
                                                     anyString(),
                                                     anyString(),
                                                     anyString());
        List<ChangeRequestDiff> diffs = service.getDiff("mySpace",
                                                        "myRepository",
                                                        1L);

        assertThat(diffs).isNotEmpty();
        assertThat(diffs).hasSize(10);
    }

    @Test(expected = IllegalStateException.class)
    public void getDiffTestInvalidBranchTest() {
        doReturn(Optional.ofNullable(null)).when(repository).getBranch("branchA");

        service.getDiff("mySpace",
                        "myRepository",
                        "branchA",
                        "branchB");
    }

    @Test(expected = NoSuchElementException.class)
    public void getDiffTestInvalidChangeRequestTest() {
        service.getDiff("mySpace",
                        "myRepository",
                        10L);
    }

    @Test
    public void deleteChangeRequestsTest() {
        ChangeRequest crs = createCommonChangeRequestWithTargetBranch("hiddenBranch");
        List<ChangeRequest> crList = Collections.nCopies(10, crs);

        doReturn(crList).when(spaceConfigStorage).loadChangeRequests("myRepository");

        service.deleteChangeRequests("mySpace",
                                     "myRepository",
                                     "sourceBranch");

        verify(spaceConfigStorage, times(10)).deleteChangeRequest(anyString(),
                                                                  anyLong());

        verify(changeRequestListUpdatedEvent).fire(any(ChangeRequestListUpdatedEvent.class));
    }

    @Test
    public void deleteChangeRequestsSomeTest() {
        ChangeRequest crsSourceBranch = createCommonChangeRequestWithSourceTargetBranch("branch",
                                                                                        "hiddenBranch");

        ChangeRequest crsTargetBranch = createCommonChangeRequestWithSourceTargetBranch("hiddenBranch",
                                                                                        "branch");

        ChangeRequest crsHidden = createCommonChangeRequestWithSourceTargetBranch("hiddenBranch",
                                                                                  "hiddenBranch");

        doReturn(Optional.of(mock(Branch.class))).when(repository).getBranch("branch");

        List<ChangeRequest> crList = new ArrayList<ChangeRequest>() {{
            addAll(Collections.nCopies(10, crsSourceBranch));
            addAll(Collections.nCopies(20, crsTargetBranch));
            addAll(Collections.nCopies(15, crsHidden));
        }};

        doReturn(crList).when(spaceConfigStorage).loadChangeRequests("myRepository");

        service.deleteChangeRequests("mySpace",
                                     "myRepository",
                                     "branch");

        verify(spaceConfigStorage, times(30)).deleteChangeRequest(anyString(),
                                                                  anyLong());

        verify(changeRequestListUpdatedEvent).fire(any(ChangeRequestListUpdatedEvent.class));
    }

    @Test
    public void deleteChangeRequestsNoneTest() {
        ChangeRequest crs = createCommonChangeRequestWithSourceTargetBranch("hiddenBranch",
                                                                            "hiddenBranch");

        List<ChangeRequest> crList = Collections.nCopies(10, crs);

        doReturn(crList).when(spaceConfigStorage).loadChangeRequests("myRepository");

        service.deleteChangeRequests("mySpace",
                                     "myRepository",
                                     "branch");

        verify(spaceConfigStorage, never()).deleteChangeRequest(anyString(),
                                                                anyLong());

        verify(changeRequestListUpdatedEvent, never()).fire(any(ChangeRequestListUpdatedEvent.class));
    }

    @Test
    public void rejectChangeRequestSuccessTest() {
        List<ChangeRequest> crList = Collections.nCopies(3, createCommonChangeRequestWithStatus(ChangeRequestStatus.OPEN));
        doReturn(crList).when(spaceConfigStorage).loadChangeRequests("myRepository");

        service.rejectChangeRequest("mySpace",
                                    "myRepository",
                                    1L);
        verify(spaceConfigStorage).saveChangeRequest(eq("myRepository"),
                                                     any(ChangeRequest.class));
        verify(changeRequestStatusUpdatedEventEvent).fire(any(ChangeRequestStatusUpdatedEvent.class));
    }

    @Test(expected = IllegalStateException.class)
    public void rejectChangeRequestFailWhenChangeRequestNotOpenTest() {
        List<ChangeRequest> crList = Collections.nCopies(3, createCommonChangeRequestWithStatus(ChangeRequestStatus.ACCEPTED));
        doReturn(crList).when(spaceConfigStorage).loadChangeRequests("myRepository");

        service.rejectChangeRequest("mySpace",
                                    "myRepository",
                                    1L);
    }

    @Test
    public void closeChangeRequestSuccessTest() {
        List<ChangeRequest> crList = Collections.nCopies(3, createCommonChangeRequestWithStatus(ChangeRequestStatus.OPEN));
        doReturn(crList).when(spaceConfigStorage).loadChangeRequests("myRepository");

        service.closeChangeRequest("mySpace",
                                   "myRepository",
                                   1L);
        verify(spaceConfigStorage).saveChangeRequest(eq("myRepository"),
                                                     any(ChangeRequest.class));
        verify(changeRequestStatusUpdatedEventEvent).fire(any(ChangeRequestStatusUpdatedEvent.class));
    }

    @Test(expected = IllegalStateException.class)
    public void closeChangeRequestFailWhenChangeRequestNotOpenTest() {
        List<ChangeRequest> crList = Collections.nCopies(3, createCommonChangeRequestWithStatus(ChangeRequestStatus.ACCEPTED));
        doReturn(crList).when(spaceConfigStorage).loadChangeRequests("myRepository");

        service.closeChangeRequest("mySpace",
                                   "myRepository",
                                   1L);
    }

    @Test
    public void reopenChangeRequestSuccessTest() {
        List<ChangeRequest> crList = Collections.nCopies(3, createCommonChangeRequestWithStatus(ChangeRequestStatus.CLOSED));
        doReturn(crList).when(spaceConfigStorage).loadChangeRequests("myRepository");

        service.reopenChangeRequest("mySpace",
                                    "myRepository",
                                    1L);
        verify(spaceConfigStorage).saveChangeRequest(eq("myRepository"),
                                                     any(ChangeRequest.class));
        verify(changeRequestStatusUpdatedEventEvent).fire(any(ChangeRequestStatusUpdatedEvent.class));
    }

    @Test(expected = IllegalStateException.class)
    public void reopenChangeRequestFailWhenChangeRequestNotClosedTest() {
        List<ChangeRequest> crList = Collections.nCopies(3, createCommonChangeRequestWithStatus(ChangeRequestStatus.ACCEPTED));
        doReturn(crList).when(spaceConfigStorage).loadChangeRequests("myRepository");

        service.reopenChangeRequest("mySpace",
                                    "myRepository",
                                    1L);
    }

    @Test(expected = ChangeRequestAlreadyOpenException.class)
    public void reopenChangeRequestFailWhenOtherIsOpenSameBranchesTest() {
        ChangeRequest crToReopen = createCommonChangeRequestWithIdStatus(1L, ChangeRequestStatus.REJECTED);
        ChangeRequest otherOpen = createCommonChangeRequestWithIdStatus(2L, ChangeRequestStatus.OPEN);

        List<ChangeRequest> crList = Stream.of(crToReopen, otherOpen).collect(Collectors.toList());
        doReturn(crList).when(spaceConfigStorage).loadChangeRequests("myRepository");

        service.reopenChangeRequest("mySpace",
                                    "myRepository",
                                    1L);
    }

    @Test
    public void mergeChangeRequestSuccessTest() {
        List<ChangeRequest> crList = Collections.nCopies(3, createCommonChangeRequestWithStatus(ChangeRequestStatus.OPEN));
        doReturn(crList).when(spaceConfigStorage).loadChangeRequests("myRepository");

        DiffEntry diffEntry = mock(DiffEntry.class);
        doReturn("old/file/path").when(diffEntry).getOldPath();
        doReturn("new/file/path").when(diffEntry).getNewPath();
        doReturn(DiffEntry.ChangeType.MODIFY).when(diffEntry).getChangeType();
        List<DiffEntry> diffList = Collections.nCopies(10, diffEntry);

        doReturn(Collections.emptyList()).when(git).conflictBranchesChecker(anyString(),
                                                                            anyString());
        doReturn(diffList).when(git).listDiffs(anyString(),
                                               anyString());

        List<String> commitList = Stream.of("commit-id").collect(Collectors.toList());
        doReturn(commitList).when(git).merge(anyString(),
                                             anyString(),
                                             eq(true),
                                             eq(false),
                                             any());

        boolean result = service.mergeChangeRequest("mySpace",
                                                    "myRepository",
                                                    1L);

        verify(git).merge(anyString(),
                          anyString(),
                          eq(true),
                          eq(false),
                          any());

        verify(fs).publishEvents(any(org.uberfire.java.nio.file.Path.class),
                                 anyList());

        verify(spaceConfigStorage).saveChangeRequest(eq("myRepository"),
                                                     any(ChangeRequest.class));

        verify(changeRequestStatusUpdatedEventEvent).fire(any(ChangeRequestStatusUpdatedEvent.class));

        verify(provider).executePostCommitHook(fs);

        assertTrue(result);
    }

    @Test(expected = NothingToMergeException.class)
    public void mergeChangeRequestFailWhenThereIsNoChangesTest() {
        List<ChangeRequest> crList = Collections.nCopies(3, createCommonChangeRequestWithStatus(ChangeRequestStatus.OPEN));
        doReturn(crList).when(spaceConfigStorage).loadChangeRequests("myRepository");

        DiffEntry diffEntry = mock(DiffEntry.class);
        doReturn("old/file/path").when(diffEntry).getOldPath();
        doReturn("new/file/path").when(diffEntry).getNewPath();
        doReturn(DiffEntry.ChangeType.MODIFY).when(diffEntry).getChangeType();
        List<DiffEntry> diffList = Collections.nCopies(10, diffEntry);

        doReturn(Collections.emptyList()).when(git).conflictBranchesChecker(anyString(),
                                                                            anyString());
        doReturn(diffList).when(git).listDiffs(anyString(),
                                               anyString());

        doReturn(Collections.emptyList()).when(git).merge(anyString(),
                                                          anyString(),
                                                          eq(true),
                                                          eq(false),
                                                          any());

        service.mergeChangeRequest("mySpace",
                                   "myRepository",
                                   1L);
    }

    @Test(expected = IllegalStateException.class)
    public void mergeChangeRequestFailWhenChangeRequestNotOpenTest() {
        List<ChangeRequest> crList = Collections.nCopies(3, createCommonChangeRequestWithStatus(ChangeRequestStatus.ACCEPTED));
        doReturn(crList).when(spaceConfigStorage).loadChangeRequests("myRepository");

        service.mergeChangeRequest("mySpace",
                                   "myRepository",
                                   1L);
    }

    @Test(expected = IllegalStateException.class)
    public void revertChangeRequestFailWhenChangeRequestNotAcceptedTest() {
        List<ChangeRequest> crList = Collections.nCopies(3, createCommonChangeRequestWithStatus(ChangeRequestStatus.REJECTED));
        doReturn(crList).when(spaceConfigStorage).loadChangeRequests("myRepository");

        service.revertChangeRequest("mySpace",
                                    "myRepository",
                                    1L);
    }

    @Test
    public void revertChangeRequestFailTest() {
        final String lastCommitId = "abcde12";
        List<ChangeRequest> crList = Collections.nCopies(3, createCommonChangeRequestWithStatusLastCommitId(ChangeRequestStatus.ACCEPTED,
                                                                                                            lastCommitId));
        doReturn(crList).when(spaceConfigStorage).loadChangeRequests("myRepository");

        doReturn(false).when(git).revertMerge(anyString(),
                                              anyString(),
                                              anyString(),
                                              anyString());

        boolean result = service.revertChangeRequest("mySpace",
                                                     "myRepository",
                                                     1L);

        verify(fs, never()).publishEvents(any(org.uberfire.java.nio.file.Path.class),
                                          anyList());

        verify(spaceConfigStorage).saveChangeRequest(eq("myRepository"),
                                                     any(ChangeRequest.class));

        verify(changeRequestStatusUpdatedEventEvent).fire(any(ChangeRequestStatusUpdatedEvent.class));

        assertFalse(result);
    }

    @Test
    public void revertChangeRequestSuccessTest() {
        final String lastCommitId = "0000000000000000000000000000000000000000";
        List<ChangeRequest> crList = Collections.nCopies(3, createCommonChangeRequestWithStatusLastCommitId(ChangeRequestStatus.ACCEPTED,
                                                                                                            lastCommitId));
        doReturn(crList).when(spaceConfigStorage).loadChangeRequests("myRepository");

        RevCommit commit = mock(RevCommit.class);
        doReturn(commit).when(git).getLastCommit("targetBranch");

        doReturn(true).when(git).commit(eq("targetBranch"),
                                        any(CommitInfo.class),
                                        eq(false),
                                        any(RevCommit.class),
                                        any(RevertCommitContent.class));

        DiffEntry diffEntry = mock(DiffEntry.class);
        doReturn("old/file/path").when(diffEntry).getOldPath();
        doReturn("new/file/path").when(diffEntry).getNewPath();
        doReturn(DiffEntry.ChangeType.MODIFY).when(diffEntry).getChangeType();
        List<DiffEntry> diffList = Collections.nCopies(10, diffEntry);

        doReturn(Collections.emptyList()).when(git).conflictBranchesChecker(anyString(),
                                                                            anyString());
        doReturn(diffList).when(git).listDiffs(anyString(),
                                               anyString());

        doReturn(true).when(git).revertMerge(anyString(),
                                             anyString(),
                                             anyString(),
                                             anyString());

        boolean result = service.revertChangeRequest("mySpace",
                                                     "myRepository",
                                                     1L);

        verify(fs).publishEvents(any(org.uberfire.java.nio.file.Path.class),
                                 anyList());

        verify(spaceConfigStorage).saveChangeRequest(eq("myRepository"),
                                                     any(ChangeRequest.class));

        verify(changeRequestStatusUpdatedEventEvent).fire(any(ChangeRequestStatusUpdatedEvent.class));

        verify(provider).executePostCommitHook(fs);

        assertTrue(result);
    }

    @Test
    public void updateChangeRequestSummaryTest() {
        List<ChangeRequest> crList = Collections.nCopies(3, createCommonChangeRequestWithStatus(ChangeRequestStatus.OPEN));
        doReturn(crList).when(spaceConfigStorage).loadChangeRequests("myRepository");

        service.updateChangeRequestSummary("mySpace",
                                           "myRepository",
                                           1L,
                                           "newSummary");

        verify(spaceConfigStorage).saveChangeRequest(eq("myRepository"),
                                                     any(ChangeRequest.class));
        verify(changeRequestUpdatedEvent).fire(any(ChangeRequestUpdatedEvent.class));
    }

    @Test
    public void updateChangeRequestDescriptionTest() {
        List<ChangeRequest> crList = Collections.nCopies(3, createCommonChangeRequestWithStatus(ChangeRequestStatus.OPEN));
        doReturn(crList).when(spaceConfigStorage).loadChangeRequests("myRepository");

        service.updateChangeRequestDescription("mySpace",
                                               "myRepository",
                                               1L,
                                               "newDescription");

        verify(spaceConfigStorage).saveChangeRequest(eq("myRepository"),
                                                     any(ChangeRequest.class));
        verify(changeRequestUpdatedEvent).fire(any(ChangeRequestUpdatedEvent.class));
    }

    @Test
    public void getCommentsAllTest() {
        ChangeRequestComment comment = new ChangeRequestComment(1L, "author", new Date(), "text");
        List<ChangeRequestComment> commentList = Collections.nCopies(3, comment);
        doReturn(commentList).when(spaceConfigStorage).loadChangeRequestComments("myRepository", 1L);

        PaginatedChangeRequestCommentList paginatedList = service.getComments("mySpace",
                                                                              "myRepository",
                                                                              1L,
                                                                              0,
                                                                              0);

        assertEquals(3, (int) paginatedList.getChangeRequestComments().size());
    }

    @Test
    public void getCommentsPaginatedTest() {
        ChangeRequestComment comment = new ChangeRequestComment(1L, "author", new Date(), "text");
        List<ChangeRequestComment> commentList = Collections.nCopies(25, comment);
        doReturn(commentList).when(spaceConfigStorage).loadChangeRequestComments("myRepository", 1L);

        int page0Size = service.getComments("mySpace",
                                            "myRepository",
                                            1L,
                                            0,
                                            10).getChangeRequestComments().size();

        int page1Size = service.getComments("mySpace",
                                            "myRepository",
                                            1L,
                                            1,
                                            10).getChangeRequestComments().size();

        int page2Size = service.getComments("mySpace",
                                            "myRepository",
                                            1L,
                                            2,
                                            10).getChangeRequestComments().size();

        int page3Size = service.getComments("mySpace",
                                            "myRepository",
                                            1L,
                                            3,
                                            10).getChangeRequestComments().size();

        assertEquals(10, page0Size);
        assertEquals(10, page1Size);
        assertEquals(5, page2Size);
        assertEquals(0, page3Size);
    }

    @Test
    public void addCommentTest() {
        doReturn(Collections.emptyList()).when(spaceConfigStorage).getChangeRequestCommentIds("myRepository", 1L);

        service.addComment("mySpace",
                           "myRepository",
                           1L,
                           "myComment");

        verify(spaceConfigStorageRegistry.get("mySpace")).saveChangeRequestComment(eq("myRepository"),
                                                                                   eq(1L),
                                                                                   any(ChangeRequestComment.class));
        verify(changeRequestUpdatedEvent).fire(any(ChangeRequestUpdatedEvent.class));
    }

    @Test
    public void deleteCommentTest() {
        service.deleteComment("mySpace",
                              "myRepository",
                              1L,
                              1L);

        verify(spaceConfigStorageRegistry.get("mySpace")).deleteChangeRequestComment(eq("myRepository"),
                                                                                     eq(1L),
                                                                                     eq(1L));
        verify(changeRequestUpdatedEvent).fire(any(ChangeRequestUpdatedEvent.class));
    }

    @Test
    public void squashChangeRequestSuccessTest() {
        List<ChangeRequest> crList = Collections.nCopies(3, createCommonChangeRequestWithStatus(ChangeRequestStatus.OPEN));
        doReturn(crList).when(spaceConfigStorage).loadChangeRequests("myRepository");

        DiffEntry diffEntry = mock(DiffEntry.class);
        doReturn("old/file/path").when(diffEntry).getOldPath();
        doReturn("new/file/path").when(diffEntry).getNewPath();
        doReturn(DiffEntry.ChangeType.MODIFY).when(diffEntry).getChangeType();
        List<DiffEntry> diffList = Collections.nCopies(10, diffEntry);

        doReturn(Collections.emptyList()).when(git).conflictBranchesChecker(anyString(),
                                                                            anyString());
        doReturn(diffList).when(git).listDiffs(anyString(),
                                               anyString());

        List<String> commitList = Stream.of("commit-id").collect(Collectors.toList());
        doReturn(commitList).when(git).merge(anyString(),
                                             anyString(),
                                             eq(true),
                                             eq(true),
                                             any(CommitInfo.class));

        boolean result = service.squashChangeRequest("mySpace",
                                                     "myRepository",
                                                     1L,
                                                     "myComment");

        verify(git).merge(anyString(),
                          anyString(),
                          eq(true),
                          eq(true),
                          any(CommitInfo.class));

        verify(fs).publishEvents(any(org.uberfire.java.nio.file.Path.class),
                                 anyList());

        verify(spaceConfigStorage).saveChangeRequest(eq("myRepository"),
                                                     any(ChangeRequest.class));

        verify(changeRequestStatusUpdatedEventEvent).fire(any(ChangeRequestStatusUpdatedEvent.class));

        verify(provider).executePostCommitHook(fs);

        assertTrue(result);
    }

    @Test(expected = NothingToMergeException.class)
    public void squashChangeRequestFailWhenThereIsNoChangesTest() {
        List<ChangeRequest> crList = Collections.nCopies(3, createCommonChangeRequestWithStatus(ChangeRequestStatus.OPEN));
        doReturn(crList).when(spaceConfigStorage).loadChangeRequests("myRepository");

        DiffEntry diffEntry = mock(DiffEntry.class);
        doReturn("old/file/path").when(diffEntry).getOldPath();
        doReturn("new/file/path").when(diffEntry).getNewPath();
        doReturn(DiffEntry.ChangeType.MODIFY).when(diffEntry).getChangeType();
        List<DiffEntry> diffList = Collections.nCopies(10, diffEntry);

        doReturn(Collections.emptyList()).when(git).conflictBranchesChecker(anyString(),
                                                                            anyString());
        doReturn(diffList).when(git).listDiffs(anyString(),
                                               anyString());

        doReturn(Collections.emptyList()).when(git).merge(anyString(),
                                                          anyString(),
                                                          eq(true),
                                                          eq(true),
                                                          any(CommitInfo.class));

        service.squashChangeRequest("mySpace",
                                   "myRepository",
                                   1L,
                                   "myComment");
    }

    @Test(expected = IllegalStateException.class)
    public void squashChangeRequestFailWhenChangeRequestNotOpenTest() {
        List<ChangeRequest> crList = Collections.nCopies(3, createCommonChangeRequestWithStatus(ChangeRequestStatus.ACCEPTED));
        doReturn(crList).when(spaceConfigStorage).loadChangeRequests("myRepository");

        service.squashChangeRequest("mySpace",
                                   "myRepository",
                                   1L,
                                   "myComment");
    }

    @Test
    public void getCommitsTest() {
        List<ChangeRequest> crList = Collections.nCopies(3, createCommonChangeRequestWithStatus(ChangeRequestStatus.OPEN));
        doReturn(crList).when(spaceConfigStorage).loadChangeRequests("myRepository");

        service.getCommits("mySpace",
                           "myRepository",
                           1L);

        verify(git).listCommits("commonCommitId",
                                lastCommit.getName());
    }

    private ChangeRequest createCommonChangeRequestWithFields(final Long id,
                                                              final String sourceBranch,
                                                              final String targetBranch,
                                                              final ChangeRequestStatus status,
                                                              final String summary,
                                                              final String lastCommitId) {
        return new ChangeRequest(id,
                                 "mySpace",
                                 "myRepository",
                                 sourceBranch,
                                 targetBranch,
                                 status,
                                 "author",
                                 summary,
                                 "description",
                                 new Date(),
                                 "commonCommitId",
                                 lastCommitId,
                                 null);
    }

    private ChangeRequest createCommonChangeRequest() {
        return createCommonChangeRequestWithFields(1L,
                                                   "sourceBranch",
                                                   "targetBranch",
                                                   ChangeRequestStatus.OPEN,
                                                   "summary",
                                                   null);
    }

    private ChangeRequest createCommonChangeRequestWithId(final Long id) {
        return createCommonChangeRequestWithFields(id,
                                                   "sourceBranch",
                                                   "targetBranch",
                                                   ChangeRequestStatus.OPEN,
                                                   "summary",
                                                   null);
    }

    private ChangeRequest createCommonChangeRequestWithStatus(final ChangeRequestStatus status) {
        return createCommonChangeRequestWithFields(1L,
                                                   "sourceBranch",
                                                   "targetBranch",
                                                   status,
                                                   "summary",
                                                   null);
    }

    private ChangeRequest createCommonChangeRequestWithSummary(final String summary) {
        return createCommonChangeRequestWithFields(1L,
                                                   "sourceBranch",
                                                   "targetBranch",
                                                   ChangeRequestStatus.OPEN,
                                                   summary,
                                                   null);
    }

    private ChangeRequest createCommonChangeRequestWithSourceBranch(final String sourceBranch) {
        return createCommonChangeRequestWithFields(1L,
                                                   sourceBranch,
                                                   "targetBranch",
                                                   ChangeRequestStatus.OPEN,
                                                   "summary",
                                                   null);
    }

    private ChangeRequest createCommonChangeRequestWithTargetBranch(final String targetBranch) {
        return createCommonChangeRequestWithFields(1L,
                                                   "sourceBranch",
                                                   targetBranch,
                                                   ChangeRequestStatus.OPEN,
                                                   "summary",
                                                   null);
    }

    private ChangeRequest createCommonChangeRequestWithStatusLastCommitId(final ChangeRequestStatus status,
                                                                          final String lastCommitId) {
        return createCommonChangeRequestWithFields(1L,
                                                   "sourceBranch",
                                                   "targetBranch",
                                                   status,
                                                   "summary",
                                                   lastCommitId);
    }

    private ChangeRequest createCommonChangeRequestWithSourceTargetBranch(final String sourceBranch,
                                                                          final String targetBranch) {
        return createCommonChangeRequestWithFields(1L,
                                                   sourceBranch,
                                                   targetBranch,
                                                   ChangeRequestStatus.OPEN,
                                                   "summary",
                                                   null);
    }

    private ChangeRequest createCommonChangeRequestWithStatusSummary(final ChangeRequestStatus status,
                                                                     final String summary) {
        return createCommonChangeRequestWithFields(1L,
                                                   "sourceBranch",
                                                   "targetBranch",
                                                   status,
                                                   summary,
                                                   null);
    }

    private ChangeRequest createCommonChangeRequestWithIdStatus(final Long id,
                                                                final ChangeRequestStatus status) {
        return createCommonChangeRequestWithFields(id,
                                                   "sourceBranch",
                                                   "targetBranch",
                                                   status,
                                                   "summary",
                                                   null);
    }
}
