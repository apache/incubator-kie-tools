/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.plugin.client.security;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.client.authz.PerspectiveAction;
import org.uberfire.ext.plugin.model.Activity;
import org.uberfire.ext.plugin.model.PluginType;
import org.uberfire.security.Resource;
import org.uberfire.security.ResourceRef;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.model.ActivityResourceType;

@ApplicationScoped
public class PluginController {

    private AuthorizationManager authorizationManager;
    private User user;

    @Inject
    public PluginController(AuthorizationManager authorizationManager,
                            User user) {
        this.authorizationManager = authorizationManager;
        this.user = user;
    }

    public boolean canCreatePerspectives() {
        return authorizationManager.authorize(ActivityResourceType.PERSPECTIVE,
                                              PerspectiveAction.CREATE,
                                              user);
    }

    public boolean canRead(Activity activity) {
        if (PluginType.PERSPECTIVE.equals(activity.getType())
                || PluginType.PERSPECTIVE_LAYOUT.equals(activity.getType())) {

            Resource ref = new ResourceRef(activity.getName(),
                                           ActivityResourceType.PERSPECTIVE);
            return authorizationManager.authorize(ref,
                                                  PerspectiveAction.READ,
                                                  user);
        }
        return true;
    }

    public boolean canUpdate(Activity activity) {
        if (PluginType.PERSPECTIVE.equals(activity.getType())
                || PluginType.PERSPECTIVE_LAYOUT.equals(activity.getType())) {

            Resource ref = new ResourceRef(activity.getName(),
                                           ActivityResourceType.PERSPECTIVE);
            return authorizationManager.authorize(ref,
                                                  PerspectiveAction.UPDATE,
                                                  user);
        }
        return true;
    }

    public boolean canDelete(Activity activity) {
        if (PluginType.PERSPECTIVE.equals(activity.getType())
                || PluginType.PERSPECTIVE_LAYOUT.equals(activity.getType())) {

            Resource ref = new ResourceRef(activity.getName(),
                                           ActivityResourceType.PERSPECTIVE);
            return authorizationManager.authorize(ref,
                                                  PerspectiveAction.DELETE,
                                                  user);
        }
        return true;
    }
}