package org.drools.workbench.screens.guided.dtable.client.wizard.pages;

import org.drools.workbench.screens.guided.dtable.client.widget.Validator;

/**
 * Views that need a validator
 */
public interface RequiresValidator {

    void setValidator( final Validator validator );

}
