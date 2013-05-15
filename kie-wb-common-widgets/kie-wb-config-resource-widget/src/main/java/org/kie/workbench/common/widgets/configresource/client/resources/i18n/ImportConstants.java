package org.kie.workbench.common.widgets.configresource.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

/**
 *
 */
public interface ImportConstants extends
                           Messages {

    public static final ImportConstants INSTANCE = GWT.create( ImportConstants.class );

    String OK();

    String NewItem();

    String Trash();

    String Cancel();

    String ImportedTypes();

    String FactTypesJarTip();

    String AreYouSureYouWantToRemoveThisFactType();

    String ChooseAFactType();

    String loadingList();

    String ChooseClassType();

    String TypesInThePackage();

    String IfNoTypesTip();

    String EnteringATypeClassName();

    String EnterTypeNameTip();

    String advancedClassName();

    String AdvancedView();

    String SwitchToTextModeEditing();

    String SwitchToAdvancedTextModeForPackageEditing();

    String BasicView();

    String SwitchToGuidedModeEditing();

    String CanNotSwitchToBasicView();

    String CanNotSwitchToBasicViewDeclaredTypes();

    String CanNotSwitchToBasicViewFunctions();

    String CanNotSwitchToBasicViewRules();

    String SwitchToGuidedModeForPackageEditing();

    String ImportsSection();

    String NewItemDisabled();

    String Home();

    String New();

    String PleaseSetAName();

    String Imports();
}
