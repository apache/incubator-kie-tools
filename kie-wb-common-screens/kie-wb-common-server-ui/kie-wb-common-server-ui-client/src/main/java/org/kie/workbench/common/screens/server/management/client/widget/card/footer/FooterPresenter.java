package org.kie.workbench.common.screens.server.management.client.widget.card.footer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;

@Dependent
public class FooterPresenter {

    public interface View extends IsWidget {

        void setup( final String url,
                    final String version );
    }

    private final View view;

    @Inject
    public FooterPresenter( final View view ) {
        this.view = view;
    }

    public View getView() {
        return view;
    }

    public void setup( final String url,
                       final String version ) {
        view.setup( url, version );
    }

}
