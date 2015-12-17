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

package org.kie.workbench.common.screens.social.hp.predicate;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
import org.kie.uberfire.social.activities.service.SocialPredicate;
import org.kie.workbench.common.screens.social.hp.model.HomePageTypes;

@Portable
public class UserTimeLineFileChangesPredicate implements SocialPredicate<SocialActivitiesEvent> {

    @Override
    public boolean test( SocialActivitiesEvent socialActivitiesEvent ) {
        return socialActivitiesEvent.getType().equals( HomePageTypes.RESOURCE_ADDED_EVENT.name() )
                || socialActivitiesEvent.getType().equals( HomePageTypes.RESOURCE_UPDATE_EVENT.name() );
    }
}