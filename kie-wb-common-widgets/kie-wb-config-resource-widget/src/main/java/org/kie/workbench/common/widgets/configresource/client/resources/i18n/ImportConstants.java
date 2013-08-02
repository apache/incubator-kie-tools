package org.kie.workbench.common.widgets.configresource.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

/**
 *
 */
public interface ImportConstants extends
                                 Messages {

    public static final ImportConstants INSTANCE = GWT.create( ImportConstants.class );

    String NewItem();

    String ChooseAFactType();

    String Imports();

    String addImportPopupTitle();

    String importTypeIsMandatory();

    String noImportsDefined();

    String remove();

    String promptForRemovalOfImport0( String importType );

    String importType();

    String noTypesAvailable();

}
