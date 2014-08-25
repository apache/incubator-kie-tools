package org.kie.workbench.common.screens.social.hp.client.homepage.header;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

@Dependent
public class HeaderPresenter {

    public View getView() {
        return view;
    }

    @Inject
    private View view;

    public interface View extends IsWidget {

        void setOnSelectCommand( ParameterizedCommand<String> onSelectCommand );

        void setViewAllCommand( Command viewAllCommand );

        void setNumberOfItemsLabel( String numberOfItemsLabel );

        void setUpdatesMenuList( List<String> items );
    }

    @PostConstruct
    public void setup() {
    }

    public void setOnSelectCommand( ParameterizedCommand onSelectCommand ) {
        view.setOnSelectCommand( onSelectCommand );
    }

    public void setNumberOfItemsLabel( String numberOfItemsLabel ) {
        view.setNumberOfItemsLabel( numberOfItemsLabel );
    }

    public void setUpdatesMenuList( List<String> items ) {
        view.setUpdatesMenuList( items );
    }

    public void setViewAllCommand( Command viewAllCommand ) {
        view.setViewAllCommand( viewAllCommand );
    }

}
