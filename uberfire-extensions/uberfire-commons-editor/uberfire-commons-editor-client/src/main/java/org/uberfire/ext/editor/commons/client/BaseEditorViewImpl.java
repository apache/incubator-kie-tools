/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.editor.commons.client;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import org.uberfire.ext.editor.commons.client.resources.i18n.CommonConstants;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;

public abstract class BaseEditorViewImpl
        extends Composite
        implements BaseEditorView {

    protected EditorTitle title = new EditorTitle();

    @Override
    public void alertReadOnly() {
        Window.alert( CommonConstants.INSTANCE.CantSaveReadOnly() );
    }

    @Override
    public void showLoading() {
        showBusyIndicator( CommonConstants.INSTANCE.Loading() );
    }

    @Override
    public void showSaving() {
        showBusyIndicator( CommonConstants.INSTANCE.Saving() );
    }

    @Override
    public EditorTitle getTitleWidget() {
        return title;
    }

    @Override
    public void refreshTitle( final String value ) {
        title.setText( value );
    }

    @Override
    public void showBusyIndicator( String message ) {
        BusyPopup.showMessage( message );
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }

    @Override
    public boolean confirmClose() {
        return Window.confirm( CommonConstants.INSTANCE.DiscardUnsavedData() );
    }
}
