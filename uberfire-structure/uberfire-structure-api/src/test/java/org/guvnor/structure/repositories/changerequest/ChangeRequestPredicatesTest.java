package org.guvnor.structure.repositories.changerequest;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.guvnor.structure.repositories.changerequest.portable.ChangeRequest;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class ChangeRequestPredicatesTest {

    @Test
    public void matchAllTest() {
        List<ChangeRequest> changeRequests = Collections.nCopies(10, mock(ChangeRequest.class));

        List<ChangeRequest> filteredList = changeRequests.stream()
                .filter(ChangeRequestPredicates.matchAll())
                .collect(Collectors.toList());

        assertThat(filteredList).hasSize(10);
    }

    @Test
    public void matchIdTest() {
        ChangeRequest crOne = mock(ChangeRequest.class);
        doReturn(1L).when(crOne).getId();

        ChangeRequest crTwo = mock(ChangeRequest.class);
        doReturn(2L).when(crTwo).getId();

        List<ChangeRequest> changeRequests = Stream.of(crOne,
                                                       crTwo).collect(Collectors.toList());

        List<ChangeRequest> filteredList = changeRequests.stream()
                .filter(ChangeRequestPredicates.matchId(2L))
                .collect(Collectors.toList());

        assertThat(filteredList).hasSize(1);
    }

    @Test
    public void matchSearchFilterTest() {
        ChangeRequest crOne = mock(ChangeRequest.class);
        doReturn("CR 1").when(crOne).toString();

        ChangeRequest crTwo = mock(ChangeRequest.class);
        doReturn("CR 2").when(crTwo).toString();

        ChangeRequest crThree = mock(ChangeRequest.class);
        doReturn("3").when(crThree).toString();

        List<ChangeRequest> changeRequests = Stream.of(crOne,
                                                       crTwo,
                                                       crThree).collect(Collectors.toList());

        List<ChangeRequest> filteredList = changeRequests.stream()
                .filter(ChangeRequestPredicates.matchSearchFilter("CR",
                                                                  elem -> elem.toString().toLowerCase()))
                .collect(Collectors.toList());

        assertThat(filteredList).hasSize(2);
    }

    @Test
    public void matchStatusTest() {
        ChangeRequest crOne = mock(ChangeRequest.class);
        doReturn(ChangeRequestStatus.OPEN).when(crOne).getStatus();

        ChangeRequest crTwo = mock(ChangeRequest.class);
        doReturn(ChangeRequestStatus.ACCEPTED).when(crTwo).getStatus();

        List<ChangeRequest> changeRequests = Stream.of(crOne,
                                                       crTwo).collect(Collectors.toList());

        List<ChangeRequest> filteredList = changeRequests.stream()
                .filter(ChangeRequestPredicates.matchStatus(ChangeRequestStatus.OPEN))
                .collect(Collectors.toList());

        assertThat(filteredList).hasSize(1);
    }

    @Test
    public void matchInStatusListTest() {
        ChangeRequest crOne = mock(ChangeRequest.class);
        doReturn(ChangeRequestStatus.OPEN).when(crOne).getStatus();

        ChangeRequest crTwo = mock(ChangeRequest.class);
        doReturn(ChangeRequestStatus.ACCEPTED).when(crTwo).getStatus();

        ChangeRequest crThree = mock(ChangeRequest.class);
        doReturn(ChangeRequestStatus.REJECTED).when(crThree).getStatus();

        List<ChangeRequest> changeRequests = Stream.of(crOne,
                                                       crTwo,
                                                       crThree).collect(Collectors.toList());

        List<ChangeRequestStatus> statusList = Stream.of(ChangeRequestStatus.OPEN,
                                                         ChangeRequestStatus.ACCEPTED).collect(Collectors.toList());

        List<ChangeRequest> filteredList = changeRequests.stream()
                .filter(ChangeRequestPredicates.matchInStatusList(statusList))
                .collect(Collectors.toList());

        assertThat(filteredList).hasSize(2);
    }

    @Test
    public void matchInTargetBranchListTest() {
        ChangeRequest crOne = mock(ChangeRequest.class);
        doReturn("branch1").when(crOne).getTargetBranch();

        ChangeRequest crTwo = mock(ChangeRequest.class);
        doReturn("branch2").when(crTwo).getTargetBranch();

        ChangeRequest crThree = mock(ChangeRequest.class);
        doReturn("branch3").when(crThree).getTargetBranch();

        List<ChangeRequest> changeRequests = Stream.of(crOne,
                                                       crTwo,
                                                       crThree).collect(Collectors.toList());

        List<String> branchList = Stream.of("branch1",
                                            "branch2").collect(Collectors.toList());

        List<ChangeRequest> filteredList = changeRequests.stream()
                .filter(ChangeRequestPredicates.matchInTargetBranchList(branchList))
                .collect(Collectors.toList());

        assertThat(filteredList).hasSize(2);
    }

    @Test
    public void matchSourceBranchTest() {
        ChangeRequest crOne = mock(ChangeRequest.class);
        doReturn("branch1").when(crOne).getSourceBranch();

        ChangeRequest crTwo = mock(ChangeRequest.class);
        doReturn("branch2").when(crTwo).getSourceBranch();

        List<ChangeRequest> changeRequests = Stream.of(crOne,
                                                       crTwo).collect(Collectors.toList());

        List<ChangeRequest> filteredList = changeRequests.stream()
                .filter(ChangeRequestPredicates.matchSourceBranch("branch1"))
                .collect(Collectors.toList());

        assertThat(filteredList).hasSize(1);
    }

    @Test
    public void matchTargetBranchTest() {
        ChangeRequest crOne = mock(ChangeRequest.class);
        doReturn("branch1").when(crOne).getTargetBranch();

        ChangeRequest crTwo = mock(ChangeRequest.class);
        doReturn("branch2").when(crTwo).getTargetBranch();

        List<ChangeRequest> changeRequests = Stream.of(crOne,
                                                       crTwo).collect(Collectors.toList());

        List<ChangeRequest> filteredList = changeRequests.stream()
                .filter(ChangeRequestPredicates.matchTargetBranch("branch1"))
                .collect(Collectors.toList());

        assertThat(filteredList).hasSize(1);
    }

    @Test
    public void matchSearchFilterAndStatusListTest() {
        ChangeRequest crOne = mock(ChangeRequest.class);
        doReturn(ChangeRequestStatus.OPEN).when(crOne).getStatus();
        doReturn("CR 1").when(crOne).toString();

        ChangeRequest crTwo = mock(ChangeRequest.class);
        doReturn(ChangeRequestStatus.ACCEPTED).when(crTwo).getStatus();
        doReturn("CR 2").when(crTwo).toString();

        ChangeRequest crThree = mock(ChangeRequest.class);
        doReturn(ChangeRequestStatus.REJECTED).when(crThree).getStatus();
        doReturn("CR 3").when(crThree).toString();

        List<ChangeRequest> changeRequests = Stream.of(crOne,
                                                       crTwo,
                                                       crThree).collect(Collectors.toList());

        List<ChangeRequestStatus> statusList = Stream.of(ChangeRequestStatus.OPEN,
                                                         ChangeRequestStatus.ACCEPTED).collect(Collectors.toList());

        List<ChangeRequest> filteredList = changeRequests.stream()
                .filter(ChangeRequestPredicates.matchSearchFilterAndStatusList("CR",
                                                                               elem -> elem.toString().toLowerCase(),
                                                                               statusList))
                .collect(Collectors.toList());

        assertThat(filteredList).hasSize(2);
    }

    @Test
    public void matchSourceOrTargetBranchTest() {
        ChangeRequest crOne = mock(ChangeRequest.class);
        doReturn("branch").when(crOne).getSourceBranch();
        doReturn("targetBranch1").when(crOne).getTargetBranch();

        ChangeRequest crTwo = mock(ChangeRequest.class);
        doReturn("sourceBranch2").when(crTwo).getSourceBranch();
        doReturn("targetBranch2").when(crTwo).getTargetBranch();

        ChangeRequest crThree = mock(ChangeRequest.class);
        doReturn("sourceBranch3").when(crThree).getSourceBranch();
        doReturn("branch").when(crThree).getTargetBranch();

        List<ChangeRequest> changeRequests = Stream.of(crOne,
                                                       crTwo,
                                                       crThree).collect(Collectors.toList());

        List<ChangeRequest> filteredList = changeRequests.stream()
                .filter(ChangeRequestPredicates.matchSourceOrTargetBranch("branch"))
                .collect(Collectors.toList());

        assertThat(filteredList).hasSize(2);
    }

    @Test
    public void matchSourceAndTargetAndStatusTest() {
        ChangeRequest crOne = mock(ChangeRequest.class);
        doReturn("sourceBranch").when(crOne).getSourceBranch();
        doReturn("targetBranch").when(crOne).getTargetBranch();
        doReturn(ChangeRequestStatus.OPEN).when(crOne).getStatus();

        ChangeRequest crTwo = mock(ChangeRequest.class);
        doReturn("sourceBranch").when(crTwo).getSourceBranch();
        doReturn("targetBranch").when(crTwo).getTargetBranch();
        doReturn(ChangeRequestStatus.REJECTED).when(crTwo).getStatus();

        ChangeRequest crThree = mock(ChangeRequest.class);
        doReturn("sourceBranch3").when(crThree).getSourceBranch();
        doReturn("targetBranch3").when(crThree).getTargetBranch();
        doReturn(ChangeRequestStatus.REJECTED).when(crThree).getStatus();

        List<ChangeRequest> changeRequests = Stream.of(crOne,
                                                       crTwo,
                                                       crThree).collect(Collectors.toList());

        List<ChangeRequest> filteredList = changeRequests.stream()
                .filter(ChangeRequestPredicates.matchSourceAndTargetAndStatus("sourceBranch",
                                                                              "targetBranch",
                                                                              ChangeRequestStatus.OPEN))
                .collect(Collectors.toList());

        assertThat(filteredList).hasSize(1);
    }

    @Test
    public void matchTargetBranchListAndOtherPredicateTest() {
        ChangeRequest crOne = mock(ChangeRequest.class);
        doReturn("sourceBranch1").when(crOne).getSourceBranch();
        doReturn("targetBranch1").when(crOne).getTargetBranch();
        doReturn(ChangeRequestStatus.OPEN).when(crOne).getStatus();

        ChangeRequest crTwo = mock(ChangeRequest.class);
        doReturn("sourceBranch2").when(crTwo).getSourceBranch();
        doReturn("targetBranch2").when(crTwo).getTargetBranch();
        doReturn(ChangeRequestStatus.OPEN).when(crTwo).getStatus();

        ChangeRequest crThree = mock(ChangeRequest.class);
        doReturn("sourceBranch3").when(crThree).getSourceBranch();
        doReturn("targetBranch3").when(crThree).getTargetBranch();
        doReturn(ChangeRequestStatus.REJECTED).when(crThree).getStatus();

        List<ChangeRequest> changeRequests = Stream.of(crOne,
                                                       crTwo,
                                                       crThree).collect(Collectors.toList());

        List<String> targetBranches = Stream.of("targetBranch1",
                                                "targetBranch2",
                                                "targetBranch3").collect(Collectors.toList());

        List<ChangeRequest> filteredList =
                changeRequests.stream().filter(ChangeRequestPredicates
                                                       .matchTargetBranchListAndOtherPredicate(targetBranches,
                                                                                               ChangeRequestPredicates.matchStatus(ChangeRequestStatus.OPEN)))
                        .collect(Collectors.toList());

        assertThat(filteredList).hasSize(2);
    }
}
