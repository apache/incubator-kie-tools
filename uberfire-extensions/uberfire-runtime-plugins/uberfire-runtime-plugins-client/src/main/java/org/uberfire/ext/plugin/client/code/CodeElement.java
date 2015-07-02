package org.uberfire.ext.plugin.client.code;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.uberfire.ext.plugin.model.CodeType;
import org.uberfire.mvp.ParameterizedCommand;

/**
 * TODO: update me
 */
public interface CodeElement {

    void addNav( final DropDownMenu parent,
                 final Button dropdownButton,
                 final ParameterizedCommand<CodeType> onChange );

    IconType getIcon();

    CodeType getType();
}
