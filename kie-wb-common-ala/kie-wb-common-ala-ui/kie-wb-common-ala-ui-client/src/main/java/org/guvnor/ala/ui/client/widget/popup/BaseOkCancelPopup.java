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

package org.guvnor.ala.ui.client.widget.popup;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;

public abstract class BaseOkCancelPopup {

    public interface View
            extends UberElement<BaseOkCancelPopup>,
                    HasBusyIndicator {

        void show(final String title);

        void hide();

        void setContent(final HTMLElement element);
    }

    protected final View view;

    public BaseOkCancelPopup(final View view) {
        this.view = view;
    }

    protected void init() {
        view.init(this);
    }

    protected void show(final String title) {
        view.show(title);
    }

    protected abstract void onOK();

    protected void onCancel() {
        view.hide();
    }
}