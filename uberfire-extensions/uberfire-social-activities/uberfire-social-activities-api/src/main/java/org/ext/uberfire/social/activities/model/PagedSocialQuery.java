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

package org.ext.uberfire.social.activities.model;

import java.io.Serializable;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class PagedSocialQuery implements Serializable {

    private List<SocialActivitiesEvent> socialEvents;
    private SocialPaged socialPaged;

    public PagedSocialQuery() {

    }

    public PagedSocialQuery( List<SocialActivitiesEvent> socialEvents,
                             SocialPaged socialPaged ) {
        this.socialEvents = socialEvents;
        this.socialPaged = socialPaged;
    }

    public SocialPaged socialPaged(){
        return socialPaged;
    }

    public List<SocialActivitiesEvent> socialEvents() {
        return socialEvents;
    }
}
