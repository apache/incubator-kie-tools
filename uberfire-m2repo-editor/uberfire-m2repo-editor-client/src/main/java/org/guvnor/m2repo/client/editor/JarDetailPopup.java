/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.guvnor.m2repo.client.editor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.m2repo.client.resources.i18n.M2RepoEditorConstants;
import org.gwtbootstrap3.client.shared.event.ModalShowEvent;
import org.gwtbootstrap3.client.shared.event.ModalShowHandler;
import org.gwtbootstrap3.client.ui.Pre;
import org.uberfire.client.views.pfly.sys.PatternFlyBootstrapper;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKButton;

/**
 * A popup that shows an error message
 */
public class JarDetailPopup extends BaseModal {

    interface JarDetailPopupWidgetBinder
            extends
            UiBinder<Widget, JarDetailPopup> {

    }

    private static JarDetailPopupWidgetBinder uiBinder = GWT.create(JarDetailPopupWidgetBinder.class);

    @UiField
    protected Pre pom;

    public JarDetailPopup(final String details) {
        PatternFlyBootstrapper.ensurePrettifyIsAvailable();
        setTitle(M2RepoEditorConstants.INSTANCE.JarDetails());
        setHideOtherModals(false);

        setBody(uiBinder.createAndBindUi(this));
        add(new ModalFooterOKButton(new Command() {
            @Override
            public void execute() {
                hide();
            }
        }));

        this.pom.setHTML(details);
        addShowHandler(new ModalShowHandler() {
            @Override
            public void onShow(ModalShowEvent evt) {
                initPrettify();
            }
        });
    }

    public static native void initPrettify() /*-{
        $wnd.prettyPrint();
    }-*/;
}
