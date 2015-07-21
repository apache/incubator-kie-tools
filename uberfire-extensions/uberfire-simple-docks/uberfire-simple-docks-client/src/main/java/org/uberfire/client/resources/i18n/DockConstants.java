package org.uberfire.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

public interface DockConstants
        extends
        Messages {

    public static final DockConstants INSTANCE = GWT.create( DockConstants.class );

    String OK();

}