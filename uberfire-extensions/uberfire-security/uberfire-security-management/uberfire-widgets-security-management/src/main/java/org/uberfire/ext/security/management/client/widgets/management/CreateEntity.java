/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.client.widgets.management;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

public class CreateEntity implements IsWidget {

    public interface View extends UberView<CreateEntity> {
        void show(final String legend, String placeholder);
        void setValidationState(ValidationState state);
        void clear();
    }

    public View view;
    ClientUserSystemManager userSystemManager;
    String identifier;

    @Inject
    public CreateEntity(final ClientUserSystemManager userSystemManager, final View view) {
        this.userSystemManager = userSystemManager;
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }
    
    void onEntityIdentifierChanged(final String value) {
        setNoneValidationState();
        this.identifier = value;
    }
    
    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void show(final String legend, String placeholder) {
        clear();
        view.show(legend, placeholder);
    }
    
    public void setErrorState() {
        setErrorValidationState();
    }
    
    public String getEntityIdentifier() {
        if (identifier != null && identifier.trim().length() > 0) {
            setNoneValidationState();
            return identifier;
        } else {
            setErrorValidationState();
            return null;
        }
    }

    public void clear() {
        identifier = null;
        setNoneValidationState();
        view.clear();
    }

    private void setNoneValidationState() {
        view.setValidationState(ValidationState.NONE);
    }
    
    private void setErrorValidationState() {
        view.setValidationState(ValidationState.ERROR);
    }
    
}
