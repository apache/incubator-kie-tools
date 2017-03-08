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

package org.ext.uberfire.social.activities.client.widgets.timeline.simple.model;

import java.util.List;

import org.ext.uberfire.social.activities.client.widgets.item.model.LinkCommandParams;
import org.ext.uberfire.social.activities.client.widgets.pagination.Next;
import org.ext.uberfire.social.activities.client.widgets.pagination.Previous;
import org.ext.uberfire.social.activities.model.SocialActivitiesEvent;
import org.ext.uberfire.social.activities.model.SocialEventType;
import org.ext.uberfire.social.activities.model.SocialPaged;
import org.ext.uberfire.social.activities.model.SocialUser;
import org.ext.uberfire.social.activities.service.SocialPredicate;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.mvp.ParameterizedCommand;

public class SimpleSocialTimelineWidgetModel {

    private SocialEventType socialEventType;
    private SocialUser socialUser;
    private SocialPredicate<SocialActivitiesEvent> predicate;
    private PlaceManager placeManager;
    private SocialPaged socialPaged;
    private Previous less;
    private Next more;
    private List<ClientResourceType> resourceTypes;
    private ParameterizedCommand<LinkCommandParams> linkCommand;

    public SimpleSocialTimelineWidgetModel(SocialEventType socialEventType,
                                           SocialPredicate<SocialActivitiesEvent> predicate,
                                           PlaceManager placeManager,
                                           SocialPaged socialPaged) {
        this.socialEventType = socialEventType;
        this.predicate = predicate;
        this.placeManager = placeManager;
        this.socialPaged = socialPaged;
    }

    public SimpleSocialTimelineWidgetModel(SocialUser socialUser,
                                           SocialPredicate<SocialActivitiesEvent> predicate,
                                           PlaceManager placeManager,
                                           SocialPaged socialPaged) {
        this.socialUser = socialUser;
        this.predicate = predicate;
        this.placeManager = placeManager;
        this.socialPaged = socialPaged;
    }

    public SimpleSocialTimelineWidgetModel withPagination(Previous less,
                                                          Next more) {
        this.less = less;
        this.more = more;
        return this;
    }

    public SimpleSocialTimelineWidgetModel withLinkCommand(ParameterizedCommand<LinkCommandParams> linkCommand) {
        this.linkCommand = linkCommand;
        return this;
    }

    public SimpleSocialTimelineWidgetModel withIcons(List<ClientResourceType> resourceTypes) {
        this.resourceTypes = resourceTypes;
        return this;
    }

    public SimpleSocialTimelineWidgetModel withOnlyMorePagination(Next more) {
        this.more = more;
        return this;
    }

    public boolean isSocialTypeWidget() {
        return socialUser == null;
    }

    public SocialUser getSocialUser() {
        return socialUser;
    }

    public SocialPredicate<SocialActivitiesEvent> getPredicate() {
        return predicate;
    }

    public PlaceManager getPlaceManager() {
        return placeManager;
    }

    public SocialPaged getSocialPaged() {
        return socialPaged;
    }

    public SocialEventType getSocialEventType() {
        return socialEventType;
    }

    public Previous getLess() {
        return less;
    }

    public Next getMore() {
        return more;
    }

    public void updateSocialPaged(SocialPaged socialPaged) {
        this.socialPaged = socialPaged;
    }

    public List<ClientResourceType> getResourceTypes() {
        return resourceTypes;
    }

    public ParameterizedCommand<LinkCommandParams> getLinkCommand() {
        if (linkCommand == null) {
            return new ParameterizedCommand<LinkCommandParams>() {
                @Override
                public void execute(LinkCommandParams parameters) {

                }
            };
        }
        return linkCommand;
    }
}
