/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.widgets.core.client.editors.texteditor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.mvp.AbstractActivity;

// TODO: [CAPONETTO] Remove this class and transfer the logic to TextEditorPresenter.
@ApplicationScoped
@Named(TextEditorPresenter.IDENTIFIER)
public class TextEditorPresenterActivity extends AbstractActivity {

    @Inject
    private TextEditorPresenter realPresenter;

    @Override
    public void onClose() {
        super.onClose();
        realPresenter.onClose();
    }

    @Override
    public void onOpen() {
        super.onOpen();
        realPresenter.onOpen();
    }

    @Override
    public IsWidget getWidget() {
        return realPresenter.getWidget();
    }

    @Override
    public String getIdentifier() {
        return realPresenter.getIdentifier();
    }
}