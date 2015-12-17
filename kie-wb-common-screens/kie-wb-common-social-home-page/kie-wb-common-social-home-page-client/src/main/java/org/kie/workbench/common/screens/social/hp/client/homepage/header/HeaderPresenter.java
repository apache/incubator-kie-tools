/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

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
