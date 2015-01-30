package org.uberfire.ext.apps.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

public interface CommonConstants
        extends
        Messages {

    public static final CommonConstants INSTANCE = GWT.create( CommonConstants.class );

    String CreateDir();

    String DirName();

    String DirNameHolder();

    String InvalidDirName();

    String OK();

    String Cancel();

    String DuplicateDirName();

    String DeleteAppPrompt();

}