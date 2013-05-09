package org.kie.guvnor.guided.dtable.client.wizard.pages;

import org.kie.guvnor.guided.dtable.client.widget.Validator;

/**
 * Views that need a validator
 */
public interface RequiresValidator {

    void setValidator( final Validator validator );

}
