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

package org.ext.uberfire.social.activities.adapters;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;

import org.ext.uberfire.social.activities.model.SocialActivitiesEvent;
import org.ext.uberfire.social.activities.model.SocialUser;
import org.ext.uberfire.social.activities.service.SocialCommandUserFilter;

@ApplicationScoped
public class SampleUserCommand implements SocialCommandUserFilter {

    @Override
    public List<SocialActivitiesEvent> execute(String parameterValue,
                                               List<SocialActivitiesEvent> events) {
        List<SocialActivitiesEvent> newList = new ArrayList<SocialActivitiesEvent>();
        for (SocialActivitiesEvent event : events) {
            String name = event.getSocialUser().getUserName();
            SocialUser socialUser = new SocialUser(name.toUpperCase());
            SocialActivitiesEvent socialEvent = new SocialActivitiesEvent(socialUser,
                                                                          event.getType(),
                                                                          event.getTimestamp());
            newList.add(socialEvent);
        }
        return newList;
    }

    @Override
    public String getCommandName() {
        return "sampleUserCommand";
    }
}
