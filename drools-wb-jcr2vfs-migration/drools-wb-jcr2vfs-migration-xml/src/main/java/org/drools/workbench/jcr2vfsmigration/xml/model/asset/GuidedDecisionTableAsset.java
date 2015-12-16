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

public class GuidedDecisionTableAsset extends BaseXmlAsset {

    private String content;
    private String extendedRule;

    public GuidedDecisionTableAsset( String name,
                                     String format,
                                     String lastContributor,
                                     String checkinComment,
                                     Date lastModified,
                                     String content,
                                     String extendedRule ) {

        super( name, format, lastContributor, checkinComment, lastModified );
        this.content = content;
        this.extendedRule = extendedRule;
    }

    public String getContent() {
        return content;
    }

    public String getExtendedRule() {
        return extendedRule;
    }
}
