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
package org.guvnor.m2repo.client.widgets;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.view.client.HasData;
import org.guvnor.m2repo.model.JarListPageRow;
import org.uberfire.client.mvp.UberView;

public interface ArtifactListView extends UberView<ArtifactListPresenter> {

    void setContentHeight(String s);

    void setup(final ColumnType... columns);

    void addColumn(final Column<JarListPageRow, ?> column,
                   final String caption);

    void addColumn(final Column<JarListPageRow, ?> column,
                   final String caption,
                   final boolean visible);

    void addColumn(final Column<JarListPageRow, ?> column,
                   final String caption,
                   final double width,
                   final Style.Unit unit);

    void addColumn(final Column<JarListPageRow, ?> column,
                   final String caption,
                   final boolean visible,
                   final double width,
                   final Style.Unit unit);

    void showPom(String pomText);

    HasData<JarListPageRow> getDisplay();

    ColumnSortList getColumnSortList();

    String getRefreshNotificationMessage();
}
