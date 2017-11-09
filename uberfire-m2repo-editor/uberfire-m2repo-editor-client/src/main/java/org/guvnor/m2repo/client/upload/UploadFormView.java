/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.m2repo.client.upload;

import com.google.web.bindery.event.shared.HandlerRegistration;
import org.guvnor.m2repo.client.upload.UploadFormView.Presenter;
import org.gwtbootstrap3.client.shared.event.ModalHideHandler;
import org.gwtbootstrap3.client.ui.base.form.AbstractForm;
import org.uberfire.client.mvp.UberView;

public interface UploadFormView extends UberView<Presenter> {

    interface Presenter {

        void handleSubmitComplete(AbstractForm.SubmitCompleteEvent event);

        boolean isFileNameValid();
    }

    String getFileName();

    void showSelectFileUploadWarning();

    void showUnsupportedFileTypeWarning();

    void showUploadedSuccessfullyMessage();

    void showInvalidJarNoPomWarning();

    void showInvalidPomWarning();

    void showUploadFailedError(String message);

    void showGAVInputs();

    void hideGAVInputs();

    void showUploadingBusy();

    void hideUploadingBusy();

    void show();

    void hide();

    HandlerRegistration addHideHandler(final ModalHideHandler modalHideHandler);
}
