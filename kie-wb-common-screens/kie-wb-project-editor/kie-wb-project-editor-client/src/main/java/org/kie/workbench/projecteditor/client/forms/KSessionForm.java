/*
 * Copyright 2012 JBoss Inc
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

package org.kie.workbench.projecteditor.client.forms;

import com.google.gwt.user.client.ui.Widget;
import org.kie.guvnor.project.model.ClockTypeOption;
import org.kie.guvnor.project.model.KSessionModel;
import org.kie.workbench.projecteditor.client.widgets.Form;

import javax.inject.Inject;

public class KSessionForm
        implements Form<KSessionModel>, KSessionFormView.Presenter {

    private final KSessionFormView view;
    private KSessionModel model;

    @Inject
    public KSessionForm(KSessionFormView view) {
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void setModel(KSessionModel model) {
        this.model = model;

        view.setName(model.getName());

        switch (model.getClockType()) {
            case PSEUDO:
                view.selectPseudo();
                break;
            case REALTIME:
                view.selectRealtime();
                break;
        }
    }

    @Override
    public void makeReadOnly() {
        view.makeReadOnly();
    }

    @Override
    public void onRealtimeSelect() {
        model.setClockType( ClockTypeOption.REALTIME);
    }

    @Override
    public void onPseudoSelect() {
        model.setClockType(ClockTypeOption.PSEUDO);
    }
}
