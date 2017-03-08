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

package org.ext.uberfire.social.activities.service;

import java.util.Map;

import org.ext.uberfire.social.activities.model.PagedSocialQuery;
import org.ext.uberfire.social.activities.model.SocialActivitiesEvent;
import org.ext.uberfire.social.activities.model.SocialPaged;
import org.ext.uberfire.social.activities.model.SocialUser;
import org.jboss.errai.bus.server.annotations.Remote;

@Remote
public interface SocialUserTimelinePagedRepositoryAPI {

    PagedSocialQuery getUserTimeline(SocialUser socialUser,
                                     SocialPaged socialPaged);

    PagedSocialQuery getUserTimeline(SocialUser socialUser,
                                     SocialPaged socialPaged,
                                     SocialPredicate<SocialActivitiesEvent> predicate);

    public PagedSocialQuery getUserTimeline(SocialUser socialUser,
                                            SocialPaged socialPaged,
                                            Map commandsMap,
                                            SocialPredicate<SocialActivitiesEvent> predicate);
}
