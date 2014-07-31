package org.kie.workbench.common.screens.javaeditor.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

public interface Constants extends Messages {

    public static final Constants INSTANCE = GWT.create(Constants.class);

    public String javaResourceTypeDescription();

    public String newJavaFile();

}
