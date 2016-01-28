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

package org.kie.workbench.common.screens.explorer.client.widgets.navigator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Pull;

public class NavigatorExpandCollapseButton extends Composite {

    public enum Mode {
        EXPANDED, COLLAPSED
    }

    private Mode mode;

    private Button button;

    public NavigatorExpandCollapseButton( final Mode mode ) {
        this.mode = mode;
        configureButton();
    }

    private void configureButton() {
        this.button = GWT.create( Button.class );
        initWidget( button );
        button.setPull( Pull.RIGHT );
        button.getElement().getStyle().setMarginTop( 10, Style.Unit.PX );
        button.getElement().getStyle().setMarginBottom( 10, Style.Unit.PX );
        button.getElement().getStyle().setMarginRight( 10, Style.Unit.PX );

        if ( mode.equals( Mode.COLLAPSED )) {
            button.setIcon( IconType.CHEVRON_DOWN );
        } else {
            button.setIcon( IconType.CHEVRON_UP );
        }
    }

    public void addClickHandler( final ClickHandler clickHandler ) {
        button.addClickHandler( clickHandler );
    }

    public void invertMode() {
        if ( mode.equals( Mode.COLLAPSED )) {
            this.mode = Mode.EXPANDED;
            button.setIcon( IconType.CHEVRON_UP );
        } else {
            this.mode = Mode.COLLAPSED;
            button.setIcon( IconType.CHEVRON_DOWN );
        }
    }

    public boolean isExpanded() {
        return Mode.EXPANDED.equals( this.mode );
    }
}
