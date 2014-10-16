package org.kie.uberfire.wires.bayesian.network.client.templates;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

@Dependent
@WorkbenchScreen(identifier = "BayesianTemplatesScreen")
public class TemplateScreen extends Composite {

    interface ViewBinder extends UiBinder<Widget, TemplateScreen> {

    }

    private static ViewBinder uiBinder = GWT.create( ViewBinder.class );

    @UiField
    public SimplePanel templates;

    @Inject
    private SyncBeanManager iocManager;

    @PostConstruct
    public void init() {
        initWidget( uiBinder.createAndBindUi( this ) );
        templates.add( iocManager.lookupBean( TemplatesGroup.class ).getInstance() );
    }

    @WorkbenchPartTitle
    @Override
    public String getTitle() {
        return "Templates";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return this;
    }

}
