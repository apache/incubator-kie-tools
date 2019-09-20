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

import java.util.Date;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.i18n.client.DateTimeFormat;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestStatus;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;

@ApplicationScoped
public class ChangeRequestUtils {

    public static final String CHANGE_REQUEST_ID_KEY = "CHANGE_REQUEST_ID";

    private static final String CREATED_DATE_FORMAT = "MMM d, yyyy";

    private final TranslationService ts;

    @Inject
    public ChangeRequestUtils(final TranslationService ts) {
        this.ts = ts;
    }

    public String formatCreatedDate(Date date) {
        return DateTimeFormat.getFormat(CREATED_DATE_FORMAT).format(date);
    }

    public String formatStatus(final ChangeRequestStatus status) {
        switch (status) {
            case ACCEPTED:
                return ts.getTranslation(LibraryConstants.AcceptedStatus);
            case REJECTED:
                return ts.getTranslation(LibraryConstants.RejectedStatus);
            case REVERTED:
                return ts.getTranslation(LibraryConstants.RevertedStatus);
            case REVERT_FAILED:
                return ts.getTranslation(LibraryConstants.RevertFailedStatus);
            case CLOSED:
                return ts.getTranslation(LibraryConstants.ClosedStatus);
            case OPEN:
            default:
                return ts.getTranslation(LibraryConstants.OpenStatus);
        }
    }

    public String formatFilesSummary(final int changedFiles,
                                     final int addedLines,
                                     final int deletedLines) {
        if (changedFiles == 1) {
            if (addedLines == 0) {
                return formatFilesSummaryForOneFileZeroAdditions(deletedLines);
            } else if (addedLines == 1) {
                return formatFilesSummaryForOneFileOneAddition(deletedLines);
            } else {
                return formatFilesSummaryForOneFileManyAdditions(addedLines,
                                                                 deletedLines);
            }
        } else {
            if (addedLines == 0) {
                return formatFilesSummaryForManyFilesZeroAdditions(changedFiles,
                                                                   deletedLines);
            } else if (addedLines == 1) {
                return formatFilesSummaryForManyFilesOneAddition(changedFiles,
                                                                 deletedLines);
            } else {
                return formatFilesSummaryForManyFilesManyAdditions(changedFiles,
                                                                   addedLines,
                                                                   deletedLines);
            }
        }
    }

    private String formatFilesSummaryForOneFileZeroAdditions(final int deletedLines) {
        if (deletedLines == 0) {
            return ts.getTranslation(LibraryConstants.ChangeRequestFilesSummaryOneFile);
        } else if (deletedLines == 1) {
            return ts.getTranslation(LibraryConstants.ChangeRequestFilesSummaryOneFileOneDeletion);
        } else {
            return ts.format(LibraryConstants.ChangeRequestFilesSummaryOneFileManyDeletions,
                             deletedLines);
        }
    }

    private String formatFilesSummaryForOneFileOneAddition(final int deletedLines) {
        if (deletedLines == 0) {
            return ts.getTranslation(LibraryConstants.ChangeRequestFilesSummaryOneFileOneAddition);
        } else if (deletedLines == 1) {
            return ts.getTranslation(LibraryConstants.ChangeRequestFilesSummaryOneFileOneAdditionOneDeletion);
        } else {
            return ts.format(LibraryConstants.ChangeRequestFilesSummaryOneFileOneAdditionManyDeletions,
                             deletedLines);
        }
    }

    private String formatFilesSummaryForOneFileManyAdditions(final int addedLines,
                                                             final int deletedLines) {
        if (deletedLines == 0) {
            return ts.format(LibraryConstants.ChangeRequestFilesSummaryOneFileManyAdditions,
                             addedLines);
        } else if (deletedLines == 1) {
            return ts.format(LibraryConstants.ChangeRequestFilesSummaryOneFileManyAdditionsOneDeletion,
                             addedLines);
        } else {
            return ts.format(LibraryConstants.ChangeRequestFilesSummaryOneFileManyAdditionsManyDeletions,
                             addedLines,
                             deletedLines);
        }
    }

    private String formatFilesSummaryForManyFilesZeroAdditions(final int changedFiles,
                                                               final int deletedLines) {
        if (deletedLines == 0) {
            return ts.format(LibraryConstants.ChangeRequestFilesSummaryManyFiles,
                             changedFiles);
        } else if (deletedLines == 1) {
            return ts.format(LibraryConstants.ChangeRequestFilesSummaryManyFilesOneDeletion,
                             changedFiles);
        } else {
            return ts.format(LibraryConstants.ChangeRequestFilesSummaryManyFilesManyDeletions,
                             changedFiles,
                             deletedLines);
        }
    }

    private String formatFilesSummaryForManyFilesOneAddition(final int changedFiles,
                                                             final int deletedLines) {
        if (deletedLines == 0) {
            return ts.format(LibraryConstants.ChangeRequestFilesSummaryManyFilesOneAddition,
                             changedFiles);
        } else if (deletedLines == 1) {
            return ts.format(LibraryConstants.ChangeRequestFilesSummaryManyFilesOneAdditionOneDeletion,
                             changedFiles);
        } else {
            return ts.format(LibraryConstants.ChangeRequestFilesSummaryManyFilesOneAdditionManyDeletions,
                             changedFiles,
                             deletedLines);
        }
    }

    private String formatFilesSummaryForManyFilesManyAdditions(final int changedFiles,
                                                               final int addedLines,
                                                               final int deletedLines) {
        if (deletedLines == 0) {
            return ts.format(LibraryConstants.ChangeRequestFilesSummaryManyFilesManyAdditions,
                             changedFiles,
                             addedLines);
        } else if (deletedLines == 1) {
            return ts.format(LibraryConstants.ChangeRequestFilesSummaryManyFilesManyAdditionsOneDeletion,
                             changedFiles,
                             addedLines);
        } else {
            return ts.format(LibraryConstants.ChangeRequestFilesSummaryManyFilesManyAdditionsManyDeletions,
                             changedFiles,
                             addedLines,
                             deletedLines);
        }
    }
}
