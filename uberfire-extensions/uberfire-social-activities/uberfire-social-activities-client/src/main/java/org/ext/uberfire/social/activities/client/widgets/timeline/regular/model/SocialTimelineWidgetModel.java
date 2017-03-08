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

package org.ext.uberfire.social.activities.client.widgets.timeline.regular.model;

import java.util.List;
import java.util.Map;

import org.ext.uberfire.social.activities.client.widgets.item.model.LinkCommandParams;
import org.ext.uberfire.social.activities.model.SocialEventType;
import org.ext.uberfire.social.activities.model.SocialUser;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.mvp.ParameterizedCommand;

public class SocialTimelineWidgetModel {

    private String maxResults;
    private SocialEventType socialEventType;
    private SocialUser socialUser;
    private PlaceManager placeManager;
    private List<ClientResourceType> resourceTypes;
    private Map<String, String> globals;
    private String drlName;

    private ParameterizedCommand<String> userClickCommand;

    private ParameterizedCommand<String> followUnfollowCommand;
    private ParameterizedCommand<LinkCommandParams> linkCommand;

    public SocialTimelineWidgetModel(SocialUser socialUser,
                                     PlaceManager placeManager,
                                     List<ClientResourceType> resourceTypes) {
        this.socialUser = socialUser;
        this.placeManager = placeManager;
        this.resourceTypes = resourceTypes;
    }

    public SocialTimelineWidgetModel(SocialEventType socialEventType,
                                     SocialUser socialUser,
                                     PlaceManager placeManager) {
        this.socialEventType = socialEventType;
        this.socialUser = socialUser;
        this.placeManager = placeManager;
    }

    public SocialTimelineWidgetModel withFollowUnfollowCommand(ParameterizedCommand<String> parameterizedCommand) {
        followUnfollowCommand = parameterizedCommand;
        return this;
    }

    public SocialTimelineWidgetModel withUserClickCommand(ParameterizedCommand<String> parameterizedCommand) {
        userClickCommand = parameterizedCommand;
        return this;
    }

    public SocialTimelineWidgetModel withLinkCommand(ParameterizedCommand<LinkCommandParams> linkCommand) {
        this.linkCommand = linkCommand;
        return this;
    }

    public SocialTimelineWidgetModel droolsQuery(Map<String, String> globals,
                                                 String drlName,
                                                 String maxResults) {
        this.globals = globals;
        this.drlName = drlName;
        this.maxResults = maxResults;
        return this;
    }

    public boolean isDroolsQuery() {
        return this.drlName != null;
    }

    public SocialEventType getSocialEventType() {
        return socialEventType;
    }

    public SocialUser getSocialUser() {
        return socialUser;
    }

    public PlaceManager getPlaceManager() {
        return placeManager;
    }

    public Map<String, String> getGlobals() {
        return globals;
    }

    public String getDrlName() {
        return drlName;
    }

    public String getMaxResults() {
        return maxResults;
    }

    public List<ClientResourceType> getResourceTypes() {
        return resourceTypes;
    }

    public ParameterizedCommand<String> getUserClickCommand() {
        return userClickCommand;
    }

    public ParameterizedCommand<String> getFollowUnfollowCommand() {
        return followUnfollowCommand;
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
