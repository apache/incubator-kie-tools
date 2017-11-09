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

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.m2repo.client.event.M2RepoSearchEvent;
import org.gwtbootstrap3.client.shared.event.ModalHideEvent;
import org.gwtbootstrap3.client.shared.event.ModalHideHandler;
import org.gwtbootstrap3.client.ui.base.form.AbstractForm;
import org.jboss.errai.ioc.client.container.SyncBeanManager;

import static org.guvnor.m2repo.model.HTMLFileManagerFields.UPLOAD_MISSING_POM;
import static org.guvnor.m2repo.model.HTMLFileManagerFields.UPLOAD_OK;
import static org.guvnor.m2repo.model.HTMLFileManagerFields.UPLOAD_UNABLE_TO_PARSE_POM;
import static org.guvnor.m2repo.utils.FileNameUtilities.isValid;

@Dependent
public class UploadFormPresenter implements UploadFormView.Presenter {

    private Event<M2RepoSearchEvent> searchEvent;

    private UploadFormView view;

    @Inject
    public UploadFormPresenter(final UploadFormView view,
                               final Event<M2RepoSearchEvent> searchEvent,
                               final SyncBeanManager iocManager) {
        this.view = view;
        //When pop-up is closed destroy bean to avoid memory leak
        view.addHideHandler(new ModalHideHandler() {
            @Override
            public void onHide(ModalHideEvent hideEvent) {
                iocManager.destroyBean(UploadFormPresenter.this);
            }
        });
        this.searchEvent = searchEvent;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    @Override
    public void handleSubmitComplete(final AbstractForm.SubmitCompleteEvent event) {
        view.hideUploadingBusy();
        if (UPLOAD_OK.equalsIgnoreCase(event.getResults())) {
            view.showUploadedSuccessfullyMessage();
            view.hideGAVInputs();
            fireSearchEvent();
            view.hide();
        } else if (UPLOAD_MISSING_POM.equalsIgnoreCase(event.getResults())) {
            view.showInvalidJarNoPomWarning();
            view.showGAVInputs();
        } else if (UPLOAD_UNABLE_TO_PARSE_POM.equalsIgnoreCase(event.getResults())) {
            view.showInvalidPomWarning();
            view.hide();
        } else {
            view.showUploadFailedError(event.getResults());
            view.hideGAVInputs();
            view.hide();
        }
    }

    @Override
    public boolean isFileNameValid() {
        String fileName = view.getFileName();
        if (fileName == null || "".equals(fileName)) {
            view.showSelectFileUploadWarning();
            return false;
        } else if (!(isValid(fileName))) {
            view.showUnsupportedFileTypeWarning();
            return false;
        } else {
            view.showUploadingBusy();
            return true;
        }
    }

    public void showView() {
        view.show();
    }

    public void fireSearchEvent() {
        searchEvent.fire(new M2RepoSearchEvent());
    }
}
