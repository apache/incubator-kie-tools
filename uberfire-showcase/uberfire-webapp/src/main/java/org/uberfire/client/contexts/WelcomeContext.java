package org.uberfire.client.contexts;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import org.uberfire.client.annotations.WorkbenchContext;
import org.uberfire.client.annotations.WorkbenchPartTitle;

@Dependent
@WorkbenchContext(identifier = "welcomeContext")
public class WelcomeContext extends Composite {

    interface ViewBinder
            extends
            UiBinder<SimplePanel, WelcomeContext> {

    }

    private static ViewBinder uiBinder = GWT.create( ViewBinder.class );

    @PostConstruct
    public void init() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "My Custom Context";
    }

}
