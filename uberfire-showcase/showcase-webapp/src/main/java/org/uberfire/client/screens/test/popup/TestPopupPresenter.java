/*
 * Copyright 2012 JBoss Inc
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

package org.uberfire.client.screens.test.popup;

import javax.inject.Inject;

import org.uberfire.client.annotations.OnReveal;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * Test Pop-up Presenter
 */
@WorkbenchPopup(identifier = "TestPopup")
public class TestPopupPresenter {

    public interface View
        extends
        IsWidget {
    }

    @Inject
    public View view;

    public TestPopupPresenter() {
    }

    @WorkbenchPartView
    public PopupPanel getView() {
        return (PopupPanel) view;
    }

    @OnStart
    public void OnStart() {
        System.out.println( "OnStart" );
    }

    @OnReveal
    public void onReveal() {
        System.out.println( "OnReveal" );
    }

}