/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.widgets.client.ruleselector;

import java.util.Collection;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.DropDown;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;

public class RuleSelectorDropdown
        extends Composite
        implements IsWidget,
                   HasValueChangeHandlers<String> {

    private DropDown dropdown = new DropDown();
    private Button dropdownButton = new Button() {{
        setDataToggle( Toggle.DROPDOWN );
        setToggleCaret( true );
    }};

    private DropDownMenu dropdownMenu = new DropDownMenu();

    public RuleSelectorDropdown() {
        addNoneSelectionToDropDown();
        initWidget( dropdown );
        dropdown.add( dropdownButton );
        dropdown.add( dropdownMenu );
    }

    private void addNoneSelectionToDropDown() {
        dropdownMenu.add( makeNoneLabel() );
    }

    public void setRuleNames( final Collection<String> ruleNames ) {
        for ( final String ruleName : ruleNames ) {
            AnchorListItem label = new AnchorListItem( ruleName );
            label.addClickHandler( new ClickHandler() {
                @Override
                public void onClick( ClickEvent event ) {
                    ValueChangeEvent.fire( RuleSelectorDropdown.this,
                                           ruleName );
                }
            } );

            dropdownMenu.add( label );
        }
    }

    private AnchorListItem makeNoneLabel() {
        final AnchorListItem label = new AnchorListItem( CommonConstants.INSTANCE.LineNoneLine() );
        label.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                ValueChangeEvent.fire(
                        RuleSelectorDropdown.this,
                        "" );
            }
        } );

        return label;
    }

    @Override
    public Widget asWidget() {
        return dropdown;
    }

    @Override
    public HandlerRegistration addValueChangeHandler( final ValueChangeHandler<String> handler ) {
        return addHandler( handler,
                           ValueChangeEvent.getType() );
    }

}
