package org.dashbuilder.common.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

/**
 * <p>Common messages.</p>
 *
 * @since 0.3.0 
 */
public interface DashbuilderCommonMessages extends Messages {

    public static final DashbuilderCommonMessages INSTANCE = GWT.create(DashbuilderCommonMessages.class);
    
    String timeout(String seconds);
}
