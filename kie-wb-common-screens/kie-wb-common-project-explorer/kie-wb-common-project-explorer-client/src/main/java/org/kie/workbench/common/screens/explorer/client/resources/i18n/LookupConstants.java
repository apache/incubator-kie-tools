package org.kie.workbench.common.screens.explorer.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.ConstantsWithLookup;

/**
 * Lookup constants
 */
public interface LookupConstants extends ConstantsWithLookup {

    public static final LookupConstants INSTANCE = GWT.create( LookupConstants.class );

    String rules();

}
