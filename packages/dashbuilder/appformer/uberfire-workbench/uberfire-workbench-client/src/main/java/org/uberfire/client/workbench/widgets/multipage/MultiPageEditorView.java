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

public interface MultiPageEditorView extends IsWidget {

    void clear();

    void addPage(final int index, final Page page);

    void disablePage(final int index);

    void enablePage(final int index);

    /**
     * Returns index of the editor page with 'title'
     * @param title of the page (Model, Overview ...)
     * @return index, starting from 0
     * @throws IllegalArgumentException if the 'title' is not found
     */
    int getPageIndex(final String title);
}
