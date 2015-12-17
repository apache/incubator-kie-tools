/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.projecteditor.client.forms;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.services.shared.kmodule.KSessionModel;

public interface KSessionsPanelView
        extends IsWidget {

    public interface Presenter {

        void onAdd();

        void onDefaultChanged(KSessionModel model);

        void onOptionsSelectedForKSessions(KSessionModel kSessionModel);

        void onDelete(KSessionModel kSessionModel);

        void onRename(KSessionModel model, String name);
    }

    void makeReadOnly();

    void makeEditable();

    void setPresenter(Presenter presenter);

    void setItemList(List<KSessionModel> list);

    void showOptionsPopUp(KSessionModel kSessionModel);

    void refresh();

    void showXsdIDError();

}
