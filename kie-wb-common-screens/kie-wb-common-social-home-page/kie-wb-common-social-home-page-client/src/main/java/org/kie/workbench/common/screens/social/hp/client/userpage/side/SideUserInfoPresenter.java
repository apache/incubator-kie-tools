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

package org.kie.workbench.common.screens.social.hp.client.userpage.side;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.Button;
import org.kie.uberfire.social.activities.model.SocialUser;

@Dependent
public class SideUserInfoPresenter {

    public interface View extends IsWidget {

        void setUserPanel( Image userImage );

        void setUserInfo( SocialUser socialUser );

        void setupLink( Button anchor );

        void clear();
    }

    @Inject
    private View view;

    public void setup( SocialUser socialUser,
                       Image userImage,
                       Button followUnfollow ) {
        view.clear();
        view.setUserPanel( userImage );
        view.setUserInfo( socialUser );
        view.setupLink( followUnfollow );
    }

    public View getView() {
        return view;
    }


}
