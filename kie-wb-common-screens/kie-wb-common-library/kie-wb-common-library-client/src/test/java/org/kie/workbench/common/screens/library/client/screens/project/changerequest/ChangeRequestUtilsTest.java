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

package org.kie.workbench.common.screens.library.client.screens.project.changerequest;

import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestStatus;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ChangeRequestUtilsTest {

    private ChangeRequestUtils utils;

    @Mock
    private TranslationService ts;

    @Before
    public void setUp() {
        this.utils = new ChangeRequestUtils(ts);
    }

    @Test
    public void formatStatusAcceptedTest() {
        utils.formatStatus(ChangeRequestStatus.ACCEPTED);

        verify(ts).getTranslation(LibraryConstants.AcceptedStatus);
    }

    @Test
    public void formatStatusRejectedTest() {
        utils.formatStatus(ChangeRequestStatus.REJECTED);

        verify(ts).getTranslation(LibraryConstants.RejectedStatus);
    }

    @Test
    public void formatStatusClosedTest() {
        utils.formatStatus(ChangeRequestStatus.CLOSED);

        verify(ts).getTranslation(LibraryConstants.ClosedStatus);
    }

    @Test
    public void formatStatusRevertedTest() {
        utils.formatStatus(ChangeRequestStatus.REVERTED);

        verify(ts).getTranslation(LibraryConstants.RevertedStatus);
    }

    @Test
    public void formatStatusRevertFailedTest() {
        utils.formatStatus(ChangeRequestStatus.REVERT_FAILED);

        verify(ts).getTranslation(LibraryConstants.RevertFailedStatus);
    }

    @Test
    public void formatStatusOpenTest() {
        utils.formatStatus(ChangeRequestStatus.OPEN);

        verify(ts).getTranslation(LibraryConstants.OpenStatus);
    }

    @Test
    public void formatFilesSummary1File0Add0DeleteTest() {
        utils.formatFilesSummary(1, 0, 0);

        verify(ts).getTranslation(LibraryConstants.ChangeRequestFilesSummaryOneFile);
    }

    @Test
    public void formatFilesSummary1File0Add1DeleteTest() {
        utils.formatFilesSummary(1, 0, 1);

        verify(ts).getTranslation(LibraryConstants.ChangeRequestFilesSummaryOneFileOneDeletion);
    }

    @Test
    public void formatFilesSummary1File0AddNDeleteTest() {
        utils.formatFilesSummary(1, 0, 10);

        verify(ts).format(LibraryConstants.ChangeRequestFilesSummaryOneFileManyDeletions, 10);
    }

    @Test
    public void formatFilesSummary1File1Add0DeleteTest() {
        utils.formatFilesSummary(1, 1, 0);

        verify(ts).getTranslation(LibraryConstants.ChangeRequestFilesSummaryOneFileOneAddition);
    }

    @Test
    public void formatFilesSummary1File1Add1DeleteTest() {
        utils.formatFilesSummary(1, 1, 1);

        verify(ts).getTranslation(LibraryConstants.ChangeRequestFilesSummaryOneFileOneAdditionOneDeletion);
    }

    @Test
    public void formatFilesSummary1File1AddNDeleteTest() {
        utils.formatFilesSummary(1, 1, 10);

        verify(ts).format(LibraryConstants.ChangeRequestFilesSummaryOneFileOneAdditionManyDeletions, 10);
    }

    @Test
    public void formatFilesSummary1FileNAdd0DeleteTest() {
        utils.formatFilesSummary(1, 20, 0);

        verify(ts).format(LibraryConstants.ChangeRequestFilesSummaryOneFileManyAdditions, 20);
    }

    @Test
    public void formatFilesSummary1FileNAdd1DeleteTest() {
        utils.formatFilesSummary(1, 20, 1);

        verify(ts).format(LibraryConstants.ChangeRequestFilesSummaryOneFileManyAdditionsOneDeletion, 20);
    }

    @Test
    public void formatFilesSummary1FileNAddNDeleteTest() {
        utils.formatFilesSummary(1, 20, 10);

        verify(ts).format(LibraryConstants.ChangeRequestFilesSummaryOneFileManyAdditionsManyDeletions, 20, 10);
    }

    @Test
    public void formatFilesSummaryNFile0Add0DeleteTest() {
        utils.formatFilesSummary(30, 0, 0);

        verify(ts).format(LibraryConstants.ChangeRequestFilesSummaryManyFiles, 30);
    }

    @Test
    public void formatFilesSummaryNFile0Add1DeleteTest() {
        utils.formatFilesSummary(30, 0, 1);

        verify(ts).format(LibraryConstants.ChangeRequestFilesSummaryManyFilesOneDeletion, 30);
    }

    @Test
    public void formatFilesSummaryNFile0AddNDeleteTest() {
        utils.formatFilesSummary(30, 0, 10);

        verify(ts).format(LibraryConstants.ChangeRequestFilesSummaryManyFilesManyDeletions, 30, 10);
    }

    @Test
    public void formatFilesSummaryNFile1Add0DeleteTest() {
        utils.formatFilesSummary(30, 1, 0);

        verify(ts).format(LibraryConstants.ChangeRequestFilesSummaryManyFilesOneAddition, 30);
    }

    @Test
    public void formatFilesSummaryNFile1Add1DeleteTest() {
        utils.formatFilesSummary(30, 1, 1);

        verify(ts).format(LibraryConstants.ChangeRequestFilesSummaryManyFilesOneAdditionOneDeletion, 30);
    }

    @Test
    public void formatFilesSummaryNFile1AddNDeleteTest() {
        utils.formatFilesSummary(30, 1, 10);

        verify(ts).format(LibraryConstants.ChangeRequestFilesSummaryManyFilesOneAdditionManyDeletions, 30, 10);
    }

    @Test
    public void formatFilesSummaryNFileNAdd0DeleteTest() {
        utils.formatFilesSummary(30, 20, 0);

        verify(ts).format(LibraryConstants.ChangeRequestFilesSummaryManyFilesManyAdditions, 30, 20);
    }

    @Test
    public void formatFilesSummaryNFileNAdd1DeleteTest() {
        utils.formatFilesSummary(30, 20, 1);

        verify(ts).format(LibraryConstants.ChangeRequestFilesSummaryManyFilesManyAdditionsOneDeletion, 30, 20);
    }

    @Test
    public void formatFilesSummaryNFileNAddNDeleteTest() {
        utils.formatFilesSummary(30, 20, 10);

        verify(ts).format(LibraryConstants.ChangeRequestFilesSummaryManyFilesManyAdditionsManyDeletions, 30, 20, 10);
    }
}
