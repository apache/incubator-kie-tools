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

package org.guvnor.ala.ui.openshift.client.provider;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.guvnor.ala.ui.client.handler.ProviderConfigurationForm;
import org.guvnor.ala.ui.client.util.AbstractHasContentChangeHandlers;
import org.guvnor.ala.ui.client.widget.FormStatus;
import org.guvnor.ala.ui.model.Provider;
import org.guvnor.ala.ui.model.ProviderConfiguration;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.UberElement;

import static org.guvnor.ala.ui.client.util.UIUtil.getStringValue;

@Dependent
public class OpenShiftProviderConfigPresenter
        extends AbstractHasContentChangeHandlers
        implements ProviderConfigurationForm {

    protected static final String MASTER_URL = "kubernetes-master";

    protected static final String USER = "kubernetes-auth-basic-username";

    protected static final String PASSWORD = "kubernetes-auth-basic-password";

    public interface View
            extends UberElement<OpenShiftProviderConfigPresenter> {

        String getProviderName();

        String getMasterURL();

        String getUsername();

        String getPassword();

        void setProviderName(String name);

        void setMasterURL(String masterURL);

        void setUsername(String username);

        void setPassword(String password);

        void disable();

        void enable();

        void setProviderNameStatus(final FormStatus formStatus);

        void setMasterURLStatus(FormStatus error);

        void setUsernameStatus(FormStatus error);

        void setPasswordStatus(FormStatus error);

        void clear();

        String getWizardTitle();
    }

    private final View view;

    @Inject
    public OpenShiftProviderConfigPresenter(final View view) {
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public View getView() {
        return view;
    }

    @Override
    public ProviderConfiguration buildProviderConfiguration() {
        final Map<String, Object> values = new HashMap<>();
        values.put(MASTER_URL,
                   getMasterURL());
        values.put(USER,
                   getUsername());
        values.put(PASSWORD,
                   getPassword());
        return new ProviderConfiguration(getProviderName(),
                                         values);
    }

    @Override
    public void clear() {
        view.clear();
    }

    @Override
    public void load(final Provider provider) {
        clear();
        view.setProviderName(provider.getKey().getId());
        view.setMasterURL(getStringValue(provider.getConfiguration().getValues(),
                                         MASTER_URL));
        view.setUsername(getStringValue(provider.getConfiguration().getValues(),
                                        USER));
        view.setPassword(getStringValue(provider.getConfiguration().getValues(),
                                        PASSWORD));
    }

    public String getProviderName() {
        return view.getProviderName();
    }

    public String getMasterURL() {
        return view.getMasterURL();
    }

    public String getUsername() {
        return view.getUsername();
    }

    public String getPassword() {
        return view.getPassword();
    }

    public void isValid(final Callback<Boolean> callback) {
        boolean isValid = !isEmpty(view.getProviderName()) &&
                !isEmpty(view.getMasterURL()) &&
                !isEmpty(view.getUsername()) &&
                !isEmpty(view.getPassword());
        callback.callback(isValid);
    }

    @Override
    public String getWizardTitle() {
        return view.getWizardTitle();
    }

    @Override
    public void disable() {
        view.disable();
    }

    protected void onProviderNameChange() {
        if (!isEmpty(view.getProviderName())) {
            view.setProviderNameStatus(FormStatus.VALID);
        } else {
            view.setProviderNameStatus(FormStatus.ERROR);
        }
        onContentChange();
    }

    protected void onMasterURLChange() {
        if (!isEmpty(view.getMasterURL())) {
            view.setMasterURLStatus(FormStatus.VALID);
        } else {
            view.setMasterURLStatus(FormStatus.ERROR);
        }
        onContentChange();
    }

    protected void onUserNameChange() {
        if (!isEmpty(view.getUsername())) {
            view.setUsernameStatus(FormStatus.VALID);
        } else {
            view.setUsernameStatus(FormStatus.ERROR);
        }
        onContentChange();
    }

    protected void onPasswordChange() {
        if (!isEmpty(view.getPassword())) {
            view.setPasswordStatus(FormStatus.VALID);
        } else {
            view.setPasswordStatus(FormStatus.ERROR);
        }
        onContentChange();
    }

    private void onContentChange() {
        fireChangeHandlers();
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
