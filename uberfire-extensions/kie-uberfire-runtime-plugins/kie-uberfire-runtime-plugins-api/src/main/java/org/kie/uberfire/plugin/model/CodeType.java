package org.kie.uberfire.plugin.model;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public enum CodeType {
    MAIN,
    ON_OPEN, ON_FOCUS, ON_LOST_FOCUS, ON_MAY_CLOSE, ON_CLOSE, ON_STARTUP, ON_SHUTDOWN,
    ON_RENAME, ON_DELETE, ON_COPY, ON_UPDATE,
    ON_CONCURRENT_UPDATE, ON_CONCURRENT_DELETE, ON_CONCURRENT_RENAME, ON_CONCURRENT_COPY,
    TITLE,
    RESOURCE_TYPE,
    PRIORITY,
    BODY_HEIGHT, INTERCEPTION_POINTS,
    PANEL_TYPE
}
