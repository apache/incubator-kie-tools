/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.ext.uberfire.social.activities.server;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.commons.cluster.ClusterServiceFactory;

@ApplicationScoped
public class SocialConfiguration {

    @Inject
    @Named("clusterServiceFactory")
    private ClusterServiceFactory clusterServiceFactory;

    private Boolean socialEnable;

    @PostConstruct
    public void setup() {
        final String property = System.getProperty( "org.kie.uberfire.social.activities.enable" );
        if ( property != null ) {
            socialEnable = Boolean.parseBoolean( property );
        } else {
            socialEnable = Boolean.TRUE;
        }
    }

    public Boolean isSocialEnable() {
        return socialEnable;
    }

}
