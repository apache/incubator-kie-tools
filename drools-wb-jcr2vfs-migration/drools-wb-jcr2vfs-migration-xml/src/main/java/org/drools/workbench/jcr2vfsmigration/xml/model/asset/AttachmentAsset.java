/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.jcr2vfsmigration.xml.model.asset;

import java.util.Date;

public class AttachmentAsset extends BaseXmlAsset {

    private String originalFormat;
    private String attachmentFileName;

    public AttachmentAsset( String name,
                            String format,
                            String lastContributor,
                            String checkinComment,
                            Date lastModified,
                            String attachmentFileName ) {
        // double format param is a patch in order to save the original format (i.e. the one that came from the
        // JCR asset), so that the original extension can be preserved.
        this( name, format, lastContributor, checkinComment, lastModified, format, attachmentFileName );
    }

    public AttachmentAsset( String name,
            String format,
            String lastContributor,
            String checkinComment,
            Date lastModified,
            String originalFormat,
            String attachmentFileName ) {

        super( name, format, lastContributor, checkinComment, lastModified );
        this.originalFormat = originalFormat;
        this.attachmentFileName = attachmentFileName;
    }

    public String getOriginalFormat() {
        return originalFormat;
    }

    public String getAttachmentFileName() {
        return attachmentFileName;
    }
}
