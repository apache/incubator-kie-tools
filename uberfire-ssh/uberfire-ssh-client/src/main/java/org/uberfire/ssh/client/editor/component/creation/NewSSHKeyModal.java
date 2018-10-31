/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ssh.client.editor.component.creation;

import java.util.Optional;
import java.util.stream.Stream;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.uberfire.mvp.Command;
import org.uberfire.ssh.client.resources.i18n.AppformerSSHConstants;
import org.uberfire.ssh.service.shared.editor.SSHKeyEditorService;

@Dependent
public class NewSSHKeyModal implements NewSSHKeyModalView.Presenter {

    private static final String[] SUPPORTED_FORMATS = new String[]{"ssh-rsa", "ssh-dss", "ecdsa-sha2-nistp256", "ecdsa-sha2-nistp384", "ecdsa-sha2-nistp521"};

    private final NewSSHKeyModalView view;
    private final Caller<SSHKeyEditorService> serviceCaller;
    private final TranslationService translationService;

    private NewSSHKeyModalHandler handler;

    @Inject
    public NewSSHKeyModal(final NewSSHKeyModalView view, final Caller<SSHKeyEditorService> serviceCaller, final TranslationService translationService) {
        this.view = view;
        this.serviceCaller = serviceCaller;
        this.translationService = translationService;

        view.init(this);
    }

    public void init(NewSSHKeyModalHandler handler) {
        PortablePreconditions.checkNotNull("handler", handler);

        this.handler = handler;
    }

    public void show() {
        view.show();
    }

    @Override
    public void notifyCancel() {
        view.hide();
    }

    @Override
    public void notifyAdd(String name, String key) {

        view.resetValidation();

        boolean nameValid = validateName(name);

        boolean keyValid = validateKey(key);

        if (nameValid && keyValid) {
            serviceCaller.call((RemoteCallback<Void>) response -> {
                handler.onAddKey();
            }, (ErrorCallback<Message>) (message, throwable) -> {
                view.setKeyValidationError(translationService.getTranslation(AppformerSSHConstants.ValidationKeyFormatError));
                return false;
            }).addKey(name, key);
        }
    }

    private boolean validateKey(final String keyContent) {
        Optional<String> optional = Optional.ofNullable(validateEmptyString(keyContent, AppformerSSHConstants.NewSSHKeyModalViewImplKey));

        if (optional.isPresent()) {
            view.setKeyValidationError(optional.get());
            return false;
        }

        Optional<String> encoding = Stream.of(SUPPORTED_FORMATS).filter(keyContent::startsWith)
                .findAny();

        if (!encoding.isPresent()) {
            view.setKeyValidationError(translationService.getTranslation(AppformerSSHConstants.ValidationKeyFormatError));
            return false;
        }

        if(handler.existsKey(keyContent)) {
            view.setKeyValidationError(translationService.getTranslation(AppformerSSHConstants.ValidationKeyAlreadyExists));
            return false;
        }

        return true;
    }

    private boolean validateName(String name) {
        Optional<String> optional = Optional.ofNullable(validateEmptyString(name, AppformerSSHConstants.NewSSHKeyModalViewImplName));

        if (optional.isPresent()) {
            view.setNameValidationError(optional.get());
            return false;
        }

        if(handler.existsKeyName(name)) {
            view.setNameValidationError(translationService.format(AppformerSSHConstants.ValidationKeyNameAlreadyExists, name));
            return false;
        }
        return true;
    }

    private String validateEmptyString(String str, String fieldNameKey) {
        if (str == null || str.isEmpty()) {
            String fieldName = translationService.getTranslation(fieldNameKey);

            return translationService.format(AppformerSSHConstants.ValidationCannotBeEmpty, fieldName);
        }
        return null;
    }

    public void hide() {
        view.hide();
    }
}
