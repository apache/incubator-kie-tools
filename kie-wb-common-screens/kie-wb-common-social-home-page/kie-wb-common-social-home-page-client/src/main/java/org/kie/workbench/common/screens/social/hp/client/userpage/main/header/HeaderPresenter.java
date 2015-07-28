/*
 * Copyright 2015 JBoss Inc
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

package org.kie.workbench.common.screens.social.hp.client.userpage.main.header;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.kie.uberfire.social.activities.client.widgets.userbox.UserBoxView;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.uberfire.mvp.ParameterizedCommand;

@Dependent
public class HeaderPresenter {

    public View getView() {
        return view;
    }

    @Inject
    private View view;

    public void clear() {
        view.clear();
    }

    public void noConnection() {
        view.noConnection();
    }

    public interface View extends IsWidget {

        void addConnection( SocialUser follower,
                            UserBoxView.RelationType relationType,
                            Image connection,
                            ParameterizedCommand<String> clickCommand,
                            ParameterizedCommand<String> followUnfollowCommand );

        void clear();

        void noConnection();
    }

    @PostConstruct
    public void setup() {
    }

    public void addConnection( SocialUser follower,
                               UserBoxView.RelationType relationType,
                               Image connection,
                               ParameterizedCommand<String> clickCommand,
                               ParameterizedCommand<String> followUnfollowCommand ) {
        view.addConnection( follower, relationType, connection, clickCommand, followUnfollowCommand );
    }

}
