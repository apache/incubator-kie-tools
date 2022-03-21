/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.widgets.client.popups.alert;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKButton;

@Dependent
@Templated
public class AlertPopupViewImpl extends Composite
        implements AlertPopupView {

    @Inject
    @DataField("alert-message")
    Span alertMessage;

    private final BaseModal modal;

    public AlertPopupViewImpl() {
        super();
        this.modal = new BaseModal();
    }

    @PostConstruct
    void setup() {
        this.modal.setBody( this );
        this.modal.add( new ModalFooterOKButton( modal::hide ) );
    }

    @Override
    public void alert( final String title,
                       final String message ) {
        this.modal.setTitle( title );
        this.alertMessage.setInnerHTML( getSafeHtml( message ).asString() );
        modal.show();
    }

    private SafeHtml getSafeHtml( final String message ) {
        final SafeHtmlBuilder shb = new SafeHtmlBuilder();
        shb.appendEscaped( message );
        return shb.toSafeHtml();
    }

}
