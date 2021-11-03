package org.dashbuilder.dataset.editor.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.ConstantsWithLookup;
import com.google.gwt.i18n.client.Messages;

public interface DataSetAuthoringConstants extends Messages {

    public static final DataSetAuthoringConstants INSTANCE = GWT.create(DataSetAuthoringConstants.class);

    String homeTitle();
    String creationWizardTitle();
    String dataSetCount(int count);
    String nextSteps();
    String defineA();
    String newDataSet();
    String toFetchYourDataFromExtSystem();
    String createDataDisplayers();
    String createDashboards();
    String editorTitleGeneric();
    String editorTitle(String name, String type);
    String validationOk();
    String validationFailed();
    String dataSetNotFound();
    String saving();
    String savedOk();
}