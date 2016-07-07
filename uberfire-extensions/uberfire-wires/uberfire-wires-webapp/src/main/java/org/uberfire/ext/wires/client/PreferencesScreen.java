/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.wires.client;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.ext.preferences.client.form.PreferencesEditorFormPresenter;

@Dependent
@WorkbenchScreen( identifier = "PreferencesScreen" )
public class PreferencesScreen {

    private final SyncBeanManager beanManager;

    private final PreferencesEditorFormPresenter preferencesForm;

    @Inject
    public PreferencesScreen( final SyncBeanManager beanManager,
                              final PreferencesEditorFormPresenter preferencesForm ) {
        this.beanManager = beanManager;
        this.preferencesForm = preferencesForm;
    }

    @PostConstruct
    public void init() {
        preferencesForm.setManagedKeys( managedKeys() );
    }

    @WorkbenchPartView
    public PreferencesEditorFormPresenter.View getView() {
        return preferencesForm.getView();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Preferences";
    }

    protected List<String> managedKeys() {
        return null;
    }
}
