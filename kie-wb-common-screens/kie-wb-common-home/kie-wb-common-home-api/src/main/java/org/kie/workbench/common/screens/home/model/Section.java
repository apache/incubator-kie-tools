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
import java.util.Collections;
import java.util.List;

import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.security.authz.RuntimeFeatureResource;
import org.uberfire.security.authz.RuntimeResource;

import static java.util.Collections.*;

/**
 * A Section on the Home Page
 */
public class Section implements RuntimeFeatureResource {

    private final String heading;
    private final List<SectionEntry> entries = new ArrayList<SectionEntry>();
    private Collection<String> roles = new ArrayList<String>();

    public Section( final String heading ) {
        this.heading = PortablePreconditions.checkNotNull( "heading",
                                                           heading );
    }

    public String getHeading() {
        return heading;
    }

    public void addEntry( final SectionEntry entry ) {
        entries.add( PortablePreconditions.checkNotNull( "entry",
                                                         entry ) );
    }

    public List<SectionEntry> getEntries() {
        return Collections.unmodifiableList( entries );
    }

    public void setRoles(Collection<String> roles) {
        this.roles = PortablePreconditions.checkNotNull("roles", roles);
    }

    @Override
    public String getSignatureId() {
        return getClass().getName() + "#" + heading;
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
