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

package org.ext.uberfire.social.activities.client.widgets.item.model;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ext.uberfire.social.activities.client.widgets.timeline.regular.model.SocialTimelineWidgetModel;
import org.ext.uberfire.social.activities.client.widgets.timeline.simple.model.SimpleSocialTimelineWidgetModel;
import org.ext.uberfire.social.activities.model.SocialActivitiesEvent.LINK_TYPE;
import org.ext.uberfire.social.activities.model.SocialUser;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.mvp.ParameterizedCommand;

public class SimpleItemWidgetModel {

    private Date timestamp;
    private String linkText;
    private SocialUser socialUser;
    private String description;
    private PlaceManager placeManager;
    private Path linkPath;
    private LINK_TYPE linkType = LINK_TYPE.VFS;
    private String linkURI;
    private String itemDescription;
    private String title;
    private boolean withFileIcon;
    private String eventType;
    private List<ClientResourceType> resourceTypes;
    private ParameterizedCommand<LinkCommandParams> linkCommand;
    private Map<String, String> linkParams = new HashMap<String, String>();

    public SimpleItemWidgetModel(SimpleSocialTimelineWidgetModel model,
                                 String eventType,
                                 Date timestamp,
                                 String linkText,
                                 String linkURI,
                                 LINK_TYPE linkType,
                                 String itemDescription,
                                 SocialUser socialUser) {

        this.socialUser = socialUser;
        this.resourceTypes = model.getResourceTypes();
        this.placeManager = model.getPlaceManager();
        this.eventType = eventType;
        this.timestamp = timestamp;
        this.linkText = linkText;
        this.linkURI = linkURI;
        this.linkType = linkType;
        this.itemDescription = itemDescription;
    }

    public SimpleItemWidgetModel(String eventType,
                                 Date timestamp,
                                 String description,
                                 String itemDescription,
                                 SocialUser socialUser) {
        this.socialUser = socialUser;
        this.eventType = eventType;
        this.timestamp = timestamp;
        this.description = description;
        this.itemDescription = itemDescription;
    }

    public SimpleItemWidgetModel(SocialTimelineWidgetModel model,
                                 String eventType,
                                 Date timestamp,
                                 String linkText,
                                 String linkURI,
                                 LINK_TYPE linkType,
                                 String description,
                                 String itemDescription,
                                 SocialUser socialUser) {
        this.socialUser = socialUser;
        this.placeManager = model.getPlaceManager();
        this.eventType = eventType;
        this.timestamp = timestamp;
        this.linkText = linkText;
        this.linkURI = linkURI;
        this.linkType = linkType;
        this.description = description;
        this.itemDescription = itemDescription;
    }

    public SimpleItemWidgetModel(SocialTimelineWidgetModel model,
                                 String eventType,
                                 Date timestamp,
                                 String description,
                                 String itemDescription) {
        this.socialUser = model.getSocialUser();
        this.eventType = eventType;
        this.timestamp = timestamp;
        this.description = description;
        this.itemDescription = itemDescription;
    }

    public boolean shouldIPrintIcon() {
        return resourceTypes != null && getLinkText() != null;
    }

    public String getDescription() {
        return description;
    }

    public String getLinkText() {
        return cleanLinkText(linkText);
    }

    private String cleanLinkText(String linkText) {
        if (hasExtension(linkText)) {
            return removeExtension(linkText);
        }
        return linkText;
    }

    private String removeExtension(String linkText) {
        return linkText != null ? linkText.substring(0,
                                                     linkText.indexOf(".")) : linkText;
    }

    private boolean hasExtension(String linkText) {
        return linkText != null && linkText.indexOf('.') > 0;
    }

    public SocialUser getSocialUser() {
        return socialUser;
    }

    public PlaceManager getPlaceManager() {
        return placeManager;
    }

    public Path getLinkPath() {
        return linkPath;
    }

    public String getLinkURI() {
        return linkURI;
    }

    public LINK_TYPE getLinkType() {
        return linkType;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ClientResourceType> getResourceTypes() {
        return resourceTypes;
    }

    public String getEventType() {
        return eventType;
    }

    public boolean isVFSLink() {
        return linkType == LINK_TYPE.VFS;
    }

    public SimpleItemWidgetModel withLinkCommand(ParameterizedCommand<LinkCommandParams> linkCommand) {
        this.linkCommand = linkCommand;
        return this;
    }

    public SimpleItemWidgetModel withLinkPath(Path linkPath) {
        this.linkPath = linkPath;
        return this;
    }

    public SimpleItemWidgetModel withLinkParams(Map<String, String> linkParams) {
        if (linkParams != null) {
            this.linkParams.putAll(linkParams);
        }
        return this;
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

    public Map<String, String> getLinkParams() {
        return linkParams;
    }
}
