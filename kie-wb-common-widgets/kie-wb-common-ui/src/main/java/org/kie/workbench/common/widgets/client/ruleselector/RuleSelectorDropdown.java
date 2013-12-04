package org.kie.workbench.common.widgets.client.ruleselector;

import java.util.Collection;

import com.github.gwtbootstrap.client.ui.DropdownButton;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;

public class RuleSelectorDropdown
        extends Composite
        implements IsWidget,
                   HasValueChangeHandlers<String> {

    private DropdownButton dropdownButton = new DropdownButton();

    public RuleSelectorDropdown() {
        addNoneSelectionToDropDown();
        initWidget( dropdownButton );
    }

    private void addNoneSelectionToDropDown() {
        dropdownButton.add( makeNoneLabel() );
    }

    public void setRuleNames( final Collection<String> ruleNames ) {
        for ( final String ruleName : ruleNames ) {
            NavLink label = new NavLink( ruleName );
            label.addClickHandler( new ClickHandler() {
                @Override
                public void onClick( ClickEvent event ) {
                    ValueChangeEvent.fire( RuleSelectorDropdown.this,
                                           ruleName );
                }
            } );

            dropdownButton.add( label );
        }
    }

    private NavLink makeNoneLabel() {
        final NavLink label = new NavLink( CommonConstants.INSTANCE.LineNoneLine() );
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
        return dropdownButton;
    }

    @Override
    public HandlerRegistration addValueChangeHandler( final ValueChangeHandler<String> handler ) {
        return addHandler( handler,
                           ValueChangeEvent.getType() );
    }

}
