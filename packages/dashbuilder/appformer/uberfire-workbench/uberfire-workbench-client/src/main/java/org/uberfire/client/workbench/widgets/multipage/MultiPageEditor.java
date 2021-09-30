/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.workbench.widgets.multipage;

import com.google.gwt.user.client.ui.IsWidget;

public interface MultiPageEditor extends IsWidget {

    void addPage(final Page page);

    void addPage(int index, final Page page);

    void disablePage(int index);

    void enablePage(int index);

    void selectPage(final int index);

    int selectedPage();

    MultiPageEditorView getView();

    void addWidget(final IsWidget widget,
                   final String label);

    void clear();

    void addTabBarWidget(final IsWidget customWidget);
}
