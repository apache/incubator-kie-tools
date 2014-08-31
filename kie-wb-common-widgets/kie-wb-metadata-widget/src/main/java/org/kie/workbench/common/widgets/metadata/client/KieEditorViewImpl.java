/*
 * Copyright 2014 JBoss Inc
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

package org.kie.workbench.common.widgets.metadata.client;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import org.kie.uberfire.client.common.BusyPopup;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;

public abstract class KieEditorViewImpl
        extends Composite
        implements KieEditorView {

    protected KieEditorTitle title = new KieEditorTitle();

    @Override
    public void alertReadOnly() {
        Window.alert(CommonConstants.INSTANCE.CantSaveReadOnly());
    }

    @Override
    public void showLoading() {
        showBusyIndicator(CommonConstants.INSTANCE.Loading());
    }

    @Override
    public void showSaving() {
        showBusyIndicator(CommonConstants.INSTANCE.Saving());
    }

    @Override
    public KieEditorTitle getTitleWidget() {
        return title;
    }

    @Override
    public void refreshTitle(String fileName, String description) {
        title.setText(fileName, description);
    }

    @Override
    public void showBusyIndicator(String message) {
        BusyPopup.showMessage(message);
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }

}
