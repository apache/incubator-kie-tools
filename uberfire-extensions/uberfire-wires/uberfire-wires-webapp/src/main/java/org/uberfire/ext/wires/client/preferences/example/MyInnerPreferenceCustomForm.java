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

package org.uberfire.ext.wires.client.preferences.example;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.preferences.client.annotations.PreferenceForm;
import org.uberfire.ext.preferences.client.base.BasePreferenceForm;
import org.uberfire.ext.wires.shared.preferences.bean.MyInnerPreference;

@Dependent
@PreferenceForm("MyInnerPreference")
@WorkbenchScreen(identifier = MyInnerPreferenceCustomForm.IDENTIFIER)
public class MyInnerPreferenceCustomForm extends BasePreferenceForm<MyInnerPreference> {

    public static final String IDENTIFIER = "MyInnerPreferenceCustomForm";
    private final View view;

    @Inject
    public MyInnerPreferenceCustomForm(final View view) {
        this.view = view;
    }

    @Override
    public void init(final MyInnerPreference preference) {
        view.init(this);
    }

    @Override
    public void beforeSave() {
        view.updatePreference(getPreference());
    }

    @Override
    public void onUndo() {
        view.init(this);
    }

    @WorkbenchPartView
    public View getView() {
        return view;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Custom Form for MyInnerPreference";
    }

    public interface View extends UberElement<MyInnerPreferenceCustomForm> {

        void init(final MyInnerPreferenceCustomForm presenter);

        void updatePreference(final MyInnerPreference preference);
    }
}
