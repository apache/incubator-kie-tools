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


package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget;

import com.google.gwt.view.client.AbstractDataProvider;
import org.uberfire.client.mvp.UberElement;

public interface MultipleInputComponentView<TYPE> extends UberElement<MultipleInputComponentView.Presenter<TYPE>> {

    int getPageSize();

    void refreshTable();

    void render();

    void enablePromoteButton(boolean enable);

    void enableDegradeButton(boolean enable);

    int getCurrentPage();

    void enableRemoveButton(boolean enable);

    void setReadOnly(boolean readOnly);

    interface Presenter<TYPE> {

        AbstractDataProvider<TableEntry<TYPE>> getProvider();

        EditableColumnGenerator<TYPE> getColumnGenerator();

        void newElement();

        void notifyChange(int index,
                          TYPE value1);

        Integer getPageSize();

        void selectValue(TableEntry<TYPE> entry);

        void removeSelectedValues();

        void promoteSelectedValues();

        void degradeSelectedValues();

        Boolean isSelected(TableEntry<TYPE> object);

        boolean isReadOnly();
    }

}
