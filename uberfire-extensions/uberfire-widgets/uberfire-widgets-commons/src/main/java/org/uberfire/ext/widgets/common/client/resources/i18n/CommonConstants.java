/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.widgets.common.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

/**
 *
 */
public interface CommonConstants
        extends Messages {

    public static final CommonConstants INSTANCE = GWT.create( CommonConstants.class );

    String OK();

    String YES();

    String NO();

    String Information();

    String Close();

    String Error();

    String Warning();

    String ShowDetail();

    String AbstractTableOpen();

    String AbstractTablePleaseSelectAnItemToDelete();

    String AbstractTableRefreshList();

    String AbstractTableOpenSelected();

    String AbstractTableFileURI();

    String ReOpen();

    String Ignore();

    String ForceSave();

    String Cancel();

    String ConcurrentIssue();

    String ConcurrentUpdate( String identity,
                             String pathURI );

    String ConcurrentRename( String identity,
                             String sourceURI,
                             String targetURI );

    String ConcurrentDelete( String identity,
                             String pathURI );

    String ChooseFile();

    String Upload();

    String More();

    String Active();

    String ExceptionInvalidPath();

    String ExceptionFileAlreadyExists0( final String uri );

    String ExceptionNoSuchFile0( final String uri );

    String ExceptionSecurity0( final String uri );

    String ExceptionGeneric0( final String message );

    String ItemDeletedSuccessfully();

    String DeletePopupTitle();

    String DeletePopupDelete();

    String Version();

    String Items();

    String ColorPickerTitle();

    String Add_New_Filter();

    String Filter_parameters();

    String Filter_Management();

    String Filter_Name();

    String Filter_Must_Have_A_Name();

    String RemoveFilter();

    String CustomFilters();

    String NoCustomFilterAvailable();

    String Refresh();

    String ColumnPickerButtonTooltip();

    String PageSizeSelectorTooltip();

    String Reset();

    String Actions();

    String Basic_Properties();

    String ClickToDisplay0(final String resourceGroup);

    String RemoveTabTitle();

    String RemoveTabConfirm(String tabName);

    String AutoRefresh();

    String Disable_autorefresh();

    String Autorefresh_Disabled();

    String Minutes();

    String Minute();

}
