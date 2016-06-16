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

package org.kie.workbench.common.screens.home.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.mvp.Command;
import org.uberfire.security.Resource;
import org.uberfire.security.ResourceAction;

/**
 * A Section on the Home Page
 */
public class SectionEntry {

    protected String caption;
    protected List<SectionEntry> children = new ArrayList<>();
    protected Command onClickCommand = null;
    protected String permission = null;
    protected Resource resource = null;
    protected ResourceAction resourceAction = null;

    protected SectionEntry(final String caption ) {
        this.caption = PortablePreconditions.checkNotNull("caption", caption);
    }

    protected SectionEntry(final String caption, Command command ) {
        this.caption = PortablePreconditions.checkNotNull("caption", caption);
        this.onClickCommand = command;
    }

    public String getCaption() {
        return caption;
    }

    public Command getOnClickCommand() {
        return onClickCommand;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public ResourceAction getResourceAction() {
        return resourceAction;
    }

    public void setResourceAction(ResourceAction resourceAction) {
        this.resourceAction = resourceAction;
    }

    public void addChild(final SectionEntry entry) {
        children.add(PortablePreconditions.checkNotNull("entry", entry));
    }

    public List<SectionEntry> getChildren() {
        return Collections.unmodifiableList(children);
    }
}
