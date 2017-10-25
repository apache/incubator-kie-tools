/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.ui.client.widget.pipeline.stage;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.guvnor.ala.ui.model.Stage;
import org.uberfire.client.mvp.UberElement;

@Dependent
public class StagePresenter {

    public interface View
            extends UberElement<StagePresenter> {

        void setName(final String name);

        void setDoneState(final String stateLabel);

        void setExecutingState(final String stateLabel);

        void setErrorState(final String stateLabel);

        void setStoppedState(final String stateLabel);
    }

    private final View view;

    @Inject
    public StagePresenter(final View view) {
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public View getView() {
        return view;
    }

    public void setup(final Stage stage) {
        view.setName(stage.getName());
        setState(State.EXECUTING);
    }

    public void setState(final State state) {
        if (state.equals(State.EXECUTING)) {
            view.setExecutingState(State.EXECUTING.name());
        } else if (state.equals(State.DONE)) {
            view.setDoneState(State.DONE.name());
        } else if (state.equals(State.ERROR)) {
            view.setErrorState(State.ERROR.name());
        } else if (state.equals(State.STOPPED)) {
            view.setStoppedState(State.STOPPED.name());
        }
    }
}
