/*
 * Copyright 2012 JBoss Inc
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

package org.kie.workbench.common.screens.search.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

/**
 * EnumEditor I18N constants
 */
public interface Constants
        extends
        Messages {

    public static final Constants INSTANCE = GWT.create( Constants.class );

    String Format();

    String Name();

    String CreatedDate();

    String LastModified();

    String Disabled();

    String SearchForm();

    String Source();

    String SourceTip();

    String CreatedBy();

    String CreatedByTip();

    String Description();

    String DescriptionTip();

    String FormatTip();

    String Subject();

    String SubjectTip();

    String Type();

    String TypeTip();

    String LastModifiedBy();

    String LastModifiedByTip();

    String ExternalLink();

    String ExternalLinkTip();

    String CheckinComment();

    String CheckinCommentTip();

    String DateCreated();

    String DateAfterPlaceholder();

    String DateBeforePlaceholder();

    String Search();

    String Clear();

    String QueryResult();

    String AtLeastOneFieldMustBeSet();

    String FindTitle();

    String SearchPlaceholder();

    String SearchResultTitle();
}
