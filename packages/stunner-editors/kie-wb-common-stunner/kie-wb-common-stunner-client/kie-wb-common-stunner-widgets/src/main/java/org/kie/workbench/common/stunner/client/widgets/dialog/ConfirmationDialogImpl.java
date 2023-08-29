/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.client.widgets.dialog;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.canvas.ConfirmationDialog;
import org.uberfire.ext.editor.commons.client.file.popups.elemental2.Elemental2Modal;
import org.uberfire.mvp.Command;

@Dependent
public class ConfirmationDialogImpl extends Elemental2Modal<ConfirmationDialogImpl.View> implements ConfirmationDialog {

    static final String WIDTH = "500px";

    @Inject
    public ConfirmationDialogImpl(final View view) {
        super(view);
    }

    @Override
    public void show(final String title,
                     final String boldDescription,
                     final String question,
                     final Command onYesAction,
                     final Command onNoAction) {
        getView().init(this);
        getView().initialize(title, boldDescription, question, onYesAction, onNoAction);
        superSetup();
        setModalWidth();
        show();
    }

    void setModalWidth() {
        setWidth(WIDTH);
    }

    public interface View extends Elemental2Modal.View<ConfirmationDialogImpl> {

        void initialize(final String title,
                        final String boldDescription,
                        final String question,
                        final Command onConfirmAction,
                        final Command onCancelAction);
    }
}
