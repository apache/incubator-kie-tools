/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.metaDataEditor;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.MetaDataRow;
import org.uberfire.backend.vfs.Path;

public interface MetaDataEditorWidgetView extends IsWidget {

    interface Presenter {

        void doSave();

        void notifyModelChanged();

        List<MetaDataRow> deserializeMetaDataAttributes(final String s);

        String serializeMetaDataAttributes(final List<MetaDataRow> attributeRows);

        void addAttribute();

        boolean isDuplicateAttribute(final String attribute);

        void removeMetaData(final MetaDataRow attributeRow);

        Path getDiagramPath();

        void showErrorMessage(String message);
    }

    void init(final Presenter presenter);

    void doSave();

    int getMetaDataRowsCount();

    void setTableDisplayStyle();

    void setNoneDisplayStyle();

    void setMetaDataRows(final List<MetaDataRow> rows);

    List<MetaDataRow> getMetaDataRows();

    MetaDataListItemWidgetView getMetaDataWidget(final int index);

    void setVisible(final boolean visible);

    boolean isDuplicateAttribute(final String attribute);

    void removeMetaData(final MetaDataRow attributeRow);

    void setReadOnly(final boolean readOnly);
}