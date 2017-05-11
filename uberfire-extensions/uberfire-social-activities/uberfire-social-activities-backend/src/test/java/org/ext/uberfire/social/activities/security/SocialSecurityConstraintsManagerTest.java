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
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.enterprise.inject.Instance;

import org.ext.uberfire.social.activities.model.SocialActivitiesEvent;
import org.ext.uberfire.social.activities.model.SocialUser;
import org.ext.uberfire.social.activities.service.SocialSecurityConstraint;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SocialSecurityConstraintsManagerTest {

    private SocialSecurityConstraintsManager socialSecurityConstraintsManager;
    private List<SocialActivitiesEvent> events;
    private SocialActivitiesEvent event1;
    private SocialActivitiesEvent event2;
    private Instance securityConstraints;
    private SocialSecurityConstraint constraint1;
    private SocialSecurityConstraint constraint2;

    @Before
    public void setup() {
        securityConstraints = mock(Instance.class);
        event1 = generateEvent(new SocialUser("admin"));
        event2 = generateEvent(new SocialUser("dora"));
        events = new ArrayList<>();
        events.add(event1);
        events.add(event2);
        constraint1 = mock(SocialSecurityConstraint.class);
        constraint2 = mock(SocialSecurityConstraint.class);
        when(securityConstraints.iterator()).thenReturn(createSecurityConstraintsIterator());
        socialSecurityConstraintsManager = new SocialSecurityConstraintsManager() {
            @Override
            Instance<SocialSecurityConstraint> getSocialSecurityConstraints() {
                Instance<SocialSecurityConstraint> mock = mock(Instance.class);
                when(mock.iterator()).thenReturn(createSecurityConstraintsIterator());
                return mock;
            }
        };
    }

    @Test
    public void applyConstraintsTest() throws Exception {
        final List<SocialActivitiesEvent> secureEvents = socialSecurityConstraintsManager.applyConstraints(events);
        verify(constraint1).init();
        verify(constraint2).init();
        assertEquals(events.size(),
                     secureEvents.size());
    }

    @Test
    public void applyConstraintsWithRestrictionTest() throws Exception {
        when(constraint2.hasRestrictions(event1)).thenReturn(true);

        final List<SocialActivitiesEvent> secureEvents = socialSecurityConstraintsManager.applyConstraints(events);
        assertEquals(1,
                     secureEvents.size());
        assertEquals(event2,
                     secureEvents.get(0));
    }

    @Test
    public void isAllowedShouldReturnFalseWhenASecurityConstraintThrowsException(){
        socialSecurityConstraintsManager = new SocialSecurityConstraintsManager(){
            @Override
            Instance<SocialSecurityConstraint> getSocialSecurityConstraints() {
                throw new RuntimeException();
            }
        };

        assertFalse(socialSecurityConstraintsManager.isAllowed(new SocialActivitiesEvent()));

    }

    private Iterator<SocialSecurityConstraint> createSecurityConstraintsIterator() {
        List<SocialSecurityConstraint> list = new ArrayList<SocialSecurityConstraint>();
        list.add(constraint1);
        list.add(constraint2);
        return list.iterator();
    }

    private SocialActivitiesEvent generateEvent(SocialUser user) {
        return new SocialActivitiesEvent(
                user,
                "",
                new Date());
    }
}