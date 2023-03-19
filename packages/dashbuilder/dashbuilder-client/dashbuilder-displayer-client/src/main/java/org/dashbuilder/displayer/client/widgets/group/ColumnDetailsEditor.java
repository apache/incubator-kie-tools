/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.displayer.client.widgets.group;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.common.client.StringUtils;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.group.GroupFunction;
import org.dashbuilder.displayer.client.events.ColumnDetailsChangedEvent;
import org.uberfire.client.mvp.UberView;

@Dependent
public class ColumnDetailsEditor implements IsWidget {

    public interface View extends UberView<ColumnDetailsEditor> {
        void setColumnId(String columnId);
        String getColumnId();
    }

    View view = null;
    GroupFunction column = null;
    DataSetMetadata metadata = null;
    Event<ColumnDetailsChangedEvent> changedEvent = null;

    @Inject
    public ColumnDetailsEditor(View view) {
        this.view = view;
        this.view.init(this);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public GroupFunction getColumn() {
        return column;
    }

    public void init(DataSetMetadata metadata, GroupFunction groupFunction) {
        this.column = groupFunction;
        this.metadata = metadata;

        if (StringUtils.isBlank(column.getColumnId())) {
            view.setColumnId(column.getSourceId());
        }
        else {
            view.setColumnId(column.getColumnId());
        }
    }

    void onColumnNameChanged() {
        String text = view.getColumnId();
        if (!StringUtils.isBlank(text)) {
            column.setColumnId(text);
            changedEvent.fire(new ColumnDetailsChangedEvent(column));
        }
    }
}
