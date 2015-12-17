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
import java.util.Collection;

import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.mvp.Command;
import org.uberfire.security.authz.RuntimeFeatureResource;

import static java.util.Collections.*;

/**
 * A Section Entry within a Section on the Home Page
 */
public class SectionEntry implements RuntimeFeatureResource {

    private final String caption;
    private final Command onClickCommand;
    private Collection<String> roles = new ArrayList<String>();

    public SectionEntry( final String caption,
                         final Command onClickCommand ) {
        this.caption = PortablePreconditions.checkNotNull( "caption",
                                                           caption );
        this.onClickCommand = PortablePreconditions.checkNotNull( "onClickCommand",
                                                                  onClickCommand );
    }

    public String getCaption() {
        return caption;
    }

    public Command getOnClickCommand() {
        return onClickCommand;
    }

    public void setRoles( Collection<String> roles ) {
        this.roles = PortablePreconditions.checkNotNull( "roles", roles );
    }

    @Override
    public String getSignatureId() {
        return getClass().getName() + "#" + caption;
    }

    @Override
    public Collection<String> getRoles() {
        return roles;
    }

    @Override
    public Collection<String> getTraits() {
        return emptyList();
    }
}
