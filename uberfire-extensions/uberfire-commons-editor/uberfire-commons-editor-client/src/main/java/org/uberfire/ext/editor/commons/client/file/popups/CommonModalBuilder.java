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

package org.uberfire.ext.editor.commons.client.file.popups;

import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;


public class CommonModalBuilder {

    private final BaseModal modal;

    public CommonModalBuilder() {
        modal = new BaseModal();
    }

    public CommonModalBuilder addHeader( String title ) {
        modal.setTitle( title );
        return this;
    }

    public CommonModalBuilder addFooter( ModalFooter footer ) {
        modal.add( footer );
        return this;
    }

    public CommonModalBuilder addBody( HTMLElement element ) {
        modal.add( buildPanel( element, new ModalBody() ) );
        return this;
    }

    public BaseModal build() {
        return modal;
    }

    private FlowPanel buildPanel( HTMLElement element, FlowPanel panel ) {
        panel.add( build( element ) );
        return panel;
    }

    private Widget build( HTMLElement element ) {
        return ElementWrapperWidget.getWidget( element );
    }
}
