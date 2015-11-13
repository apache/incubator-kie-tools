/*
 * Copyright 2015 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.plugin.model;

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
    PANEL_TYPE;

}
