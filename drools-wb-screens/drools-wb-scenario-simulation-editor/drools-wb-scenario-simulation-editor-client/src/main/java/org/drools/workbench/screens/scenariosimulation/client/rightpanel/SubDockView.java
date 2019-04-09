/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.scenariosimulation.client.rightpanel;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.mvp.HasPresenter;

public interface SubDockView<T extends SubDockView.Presenter>
        extends IsWidget,
                HasPresenter<T> {


    T getPresenter();


    interface Presenter {

        /**
         * Set the <code>ObservablePath</code> of the currently shown editor view
         * @param path
         */
        void setCurrentPath(ObservablePath path);

        /**
         * Verify if the current <code>SubDock</code> already shows data of the currently shown <b>editor</b>.
         * This is used to avoid unneeded re-population of the <code>SubDock</code>
         * @param path
         * @return <code>true</code> if given <code>ObservablePath</code> is equals to the current path of this <code>SubDock</code>
         */
        boolean isCurrentlyShow(ObservablePath path);

    }
}
