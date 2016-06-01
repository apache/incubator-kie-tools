/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.editor.menu;

import com.google.gwt.user.client.ui.HasEnabled;

public interface EditMenuView extends BaseMenuView<EditMenuBuilder>,
                                      HasEnabled {

    interface Presenter extends BaseMenuView.BaseMenuPresenter {

        void onCut();

        void onCopy();

        void onPaste();

        void onDeleteSelectedCells();

        void onDeleteSelectedColumns();

        void onDeleteSelectedRows();

        void onOtherwiseCell();

    }

    void setOtherwiseCell( final boolean otherwise );

    void enableCutMenuItem( final boolean enabled );

    void enableCopyMenuItem( final boolean enabled );

    void enablePasteMenuItem( final boolean enabled );

    void enableDeleteCellMenuItem( final boolean enabled );

    void enableDeleteColumnMenuItem( final boolean enabled );

    void enableDeleteRowMenuItem( final boolean enabled );

    void enableOtherwiseCellMenuItem( final boolean enabled );

}
