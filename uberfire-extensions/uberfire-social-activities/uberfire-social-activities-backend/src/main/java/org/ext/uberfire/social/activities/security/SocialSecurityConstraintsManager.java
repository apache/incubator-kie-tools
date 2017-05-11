/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.ext.uberfire.social.activities.security;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.ext.uberfire.social.activities.model.SocialActivitiesEvent;
import org.ext.uberfire.social.activities.service.SocialSecurityConstraint;

@ApplicationScoped
public class SocialSecurityConstraintsManager {

    @Inject
    private Instance<SocialSecurityConstraint> socialSecurityConstraints;

    public List<SocialActivitiesEvent> applyConstraints(List<SocialActivitiesEvent> events) {
        List<SocialActivitiesEvent> secureEvents = new ArrayList<SocialActivitiesEvent>();

        initConstraints();

        for (SocialActivitiesEvent event : events) {
            if (isAllowed(event)) {
                secureEvents.add(event);
            }
        }

        return secureEvents;
    }

    private void initConstraints() {
        for (SocialSecurityConstraint securityConstraint : getSocialSecurityConstraints()) {
            securityConstraint.init();
        }
    }

    boolean isAllowed(SocialActivitiesEvent event) {
        try {
            for (SocialSecurityConstraint securityConstraint : getSocialSecurityConstraints()) {
                if (securityConstraint.hasRestrictions(event)) {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    Instance<SocialSecurityConstraint> getSocialSecurityConstraints() {
        return socialSecurityConstraints;
    }
}
