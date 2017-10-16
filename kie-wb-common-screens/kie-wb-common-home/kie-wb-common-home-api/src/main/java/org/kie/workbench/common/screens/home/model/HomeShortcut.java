/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.home.model;

import java.util.ArrayList;
import java.util.List;

import org.kie.soup.commons.validation.PortablePreconditions;
import org.uberfire.mvp.Command;
import org.uberfire.security.Resource;
import org.uberfire.security.ResourceAction;
import org.uberfire.security.ResourceRef;
import org.uberfire.security.ResourceType;

public class HomeShortcut {

    private final String iconCss;
    private final String heading;
    private final String subHeading;

    private Command onClickCommand;
    private String permission;
    private Resource resource;
    private ResourceAction resourceAction;

    private List<HomeShortcutLink> links = new ArrayList<>();

    public HomeShortcut(final String iconCss,
                        final String heading,
                        final String subHeading,
                        final Command onClickCommand,
                        final String permission,
                        final String resourceId,
                        final ResourceType resourceType,
                        final ResourceAction resourceAction) {
        this.iconCss = PortablePreconditions.checkNotNull("iconCss",
                                                          iconCss);
        this.heading = PortablePreconditions.checkNotNull("heading",
                                                          heading);
        this.subHeading = PortablePreconditions.checkNotNull("subHeading",
                                                             subHeading);
        this.onClickCommand = PortablePreconditions.checkNotNull("onClickCommand",
                                                                 onClickCommand);
        this.permission = permission;
        if (resourceType != null) {
            this.resource = new ResourceRef(resourceId,
                                            resourceType);
        }
        this.resourceAction = resourceAction;
    }

    public void addLink(final HomeShortcutLink link) {
        links.add(link);
    }

    public String getIconCss() {
        return iconCss;
    }

    public String getHeading() {
        return heading;
    }

    public String getSubHeading() {
        return subHeading;
    }

    public Command getOnClickCommand() {
        return onClickCommand;
    }

    public String getPermission() {
        return permission;
    }

    public Resource getResource() {
        return resource;
    }

    public ResourceAction getResourceAction() {
        return resourceAction;
    }

    public List<HomeShortcutLink> getLinks() {
        return links;
    }
}
