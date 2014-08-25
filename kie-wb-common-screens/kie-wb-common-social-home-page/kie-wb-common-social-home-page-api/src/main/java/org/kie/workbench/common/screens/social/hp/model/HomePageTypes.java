package org.kie.workbench.common.screens.social.hp.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.uberfire.social.activities.model.SocialEventType;

@Portable
public enum HomePageTypes implements SocialEventType {

    RESOURCE_ADDED_EVENT, RESOURCE_UPDATE_EVENT, PUBLISH_BATCH_MESSAGES;
}
