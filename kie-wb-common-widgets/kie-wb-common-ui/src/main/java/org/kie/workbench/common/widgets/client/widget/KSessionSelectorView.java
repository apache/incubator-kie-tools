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

package org.kie.workbench.common.widgets.client.widget;

import java.util.List;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.view.client.SelectionChangeEvent;

public interface KSessionSelectorView
        extends IsWidget,
                SelectionChangeEvent.HasSelectionChangedHandlers {

    void setPresenter( final KSessionSelector presenter );

    void setSelected( final String kbase,
                      final String ksession );

    void addKBase( final String name );

    void setKSessions( final List<String> ksessions );

    void showWarningSelectedKSessionDoesNotExist();

    String getSelectedKSessionName();

}
