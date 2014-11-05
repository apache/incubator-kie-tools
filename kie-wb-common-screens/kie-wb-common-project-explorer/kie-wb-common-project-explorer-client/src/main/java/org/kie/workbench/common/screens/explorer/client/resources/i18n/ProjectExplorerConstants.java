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

package org.kie.workbench.common.screens.explorer.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

/**
 * Explorer I18N constants
 */
public interface ProjectExplorerConstants
        extends
        Messages {

    public static final ProjectExplorerConstants INSTANCE = GWT.create( ProjectExplorerConstants.class );

    public String explorerTitle();

    public String nullEntry();

    public String noItemsExist();

    public String businessView();

    public String technicalView();

    public String organizationalUnits();

    public String repositories();

    public String projects();

    public String organizationalUnitColon();

    public String repositoryColon();

    public String projectColon();

    public String packageColon();

    public String files();

    public String refresh();
    public String projectView();
    public String repositoryView();
    public String showAsFolders();
    public String showAsLinks();

    public String miscellaneous_files();

    String LoadingDotDotDot();

    String downloadRepository();

    String downloadProject();
    
    String openProjectEditor();

}
