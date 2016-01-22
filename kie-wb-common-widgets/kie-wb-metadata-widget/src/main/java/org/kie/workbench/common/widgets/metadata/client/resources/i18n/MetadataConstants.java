/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.widgets.metadata.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

import java.util.Date;

public interface MetadataConstants extends
        Messages {

    public static final MetadataConstants INSTANCE = GWT.create(MetadataConstants.class);

    String Title();

    String Metadata();

    String LastModified();

    String ModifiedByMetaData();

    String NoteMetaData();

    String CreatedOnMetaData();

    String CreatedByMetaData();

    String FormatMetaData();

    String OtherMetaData();

    String AShortDescriptionOfTheSubjectMatter();

    String TypeMetaData();

    String TypeTip();

    String ExternalLinkMetaData();

    String ExternalLinkTip();

    String SourceMetaData();

    String SourceMetaDataTip();

    String VersionHistory();

    String SubjectMetaData();

    String TagsMetaData();

    String AddNewTag();

    String RemoveThisTag();

    String PleaseWait();

    String Refresh();

    String NewItem();

    String Trash();

    String RuleDocHint();

    String Description();

    String documentationDefault();

    String Discussion();

    String AddADiscussionComment();

    String EraseAllComments();

    String EraseAllCommentsWarning();

    String Cancel();

    String smallCommentBy0On1Small( final String author,
            final Date date );

    String VersionHistory1();

    String NoHistory();

    String View();

    String property0ModifiedOn1By23( final String version,
            final String lastModifier,
            final String lastModifiedDate,
            final String lastModifiedComment );

    String URI();

    String UsedInProjects();

    String Preview();

    String LockMetaData();

    String LockedByHint();
    
    String LockedByHintOwned();

    String UnlockedHint();

    String ForceUnlockCaption();

    String ForceUnlockConfirmationTitle();
    
    String ForceUnlockConfirmationText(final String lockedBy);

    String ByAOnB( final String lastContributor, final String format );

    String NoDescription();

}
