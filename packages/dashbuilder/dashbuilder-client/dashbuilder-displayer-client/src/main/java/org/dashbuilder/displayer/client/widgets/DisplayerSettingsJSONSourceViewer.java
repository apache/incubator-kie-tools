/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.displayer.client.widgets;

import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.common.client.JsonSourceViewer;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.json.DisplayerSettingsJSONMarshaller;

public class DisplayerSettingsJSONSourceViewer extends Composite {

    interface SettingsEditorUIBinder extends UiBinder<Widget, DisplayerSettingsJSONSourceViewer> {}
    private static final SettingsEditorUIBinder uiBinder = GWT.create( SettingsEditorUIBinder.class );

    DisplayerSettings displayerSettings;
    DisplayerSettingsJSONMarshaller jsonMarshaller;

    @UiField
    public JsonSourceViewer jsonSourceViewer;

    @Inject
    public DisplayerSettingsJSONSourceViewer() {
        initWidget( uiBinder.createAndBindUi( this ) );
        jsonMarshaller = DisplayerSettingsJSONMarshaller.get();
    }

    public DisplayerSettings getDisplayerSettings() {
        return displayerSettings;
    }

    public void setDisplayerSettings(DisplayerSettings displayerSettings) {
        jsonSourceViewer.setContent(jsonMarshaller.toJsonObject(displayerSettings));
    }
}
