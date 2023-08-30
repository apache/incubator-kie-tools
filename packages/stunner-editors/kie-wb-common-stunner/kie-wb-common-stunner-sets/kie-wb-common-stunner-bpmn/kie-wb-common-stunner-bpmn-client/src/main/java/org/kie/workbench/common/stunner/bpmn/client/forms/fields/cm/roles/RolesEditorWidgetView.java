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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.cm.roles;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.KeyValueRow;

public interface RolesEditorWidgetView extends IsWidget {

    interface Presenter {

        List<KeyValueRow> deserialize(final String s);

        String serialize(final List<KeyValueRow> rows);
    }

    void init(final RolesEditorWidgetView.Presenter presenter);

    void doSave();

    void notifyModelChanged();

    int getRowsCount();

    void setRows(final List<KeyValueRow> rows);

    List<KeyValueRow> getRows();

    RolesListItemWidgetView getWidget(int index);

    void setVisible(final boolean visible);

    void remove(final KeyValueRow row);

    void setReadOnly(final boolean readOnly);

    boolean isDuplicateName(final String name);
}
