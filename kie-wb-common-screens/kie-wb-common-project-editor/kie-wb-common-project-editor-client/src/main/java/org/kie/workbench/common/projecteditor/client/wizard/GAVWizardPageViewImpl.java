package org.kie.workbench.common.projecteditor.client.wizard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class GAVWizardPageViewImpl
    extends Composite
        implements GAVWizardPageView {


    interface GAVWizardPageViewImplBinder
            extends
            UiBinder<Widget, GAVWizardPageViewImpl> {

    }

    private static GAVWizardPageViewImplBinder uiBinder = GWT.create(GAVWizardPageViewImplBinder.class);

    public GAVWizardPageViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }
}
