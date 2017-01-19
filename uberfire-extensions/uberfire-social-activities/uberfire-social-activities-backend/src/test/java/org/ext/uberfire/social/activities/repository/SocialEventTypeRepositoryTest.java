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

package org.ext.uberfire.social.activities.repository;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.ext.uberfire.social.activities.model.SocialEventType;
import org.ext.uberfire.social.activities.service.SocialAdapter;

import static org.junit.Assert.*;

public class SocialEventTypeRepositoryTest {

    private SocialEventTypeRepository socialEventTypeRepository;

    @Before
    public void setup() {
        socialEventTypeRepository = new SocialEventTypeRepository() {
            @Override
            Map<Class, SocialAdapter> getSocialAdapters() {

                Map<Class, SocialAdapter> adapters = new HashMap<Class, SocialAdapter>();
                adapters.put( SampleSocialUserEventAdapter.class, new SampleSocialUserEventAdapter() );
                return adapters;
            }
        };
        socialEventTypeRepository.setup();
    }

    @Test
    public void findAllTypes() {
        assertFalse( socialEventTypeRepository.findAll().isEmpty() );
    }
    @Test
    public void findType() {
        SocialEventType dummyType = socialEventTypeRepository.findType( "DUMMY_EVENT" );
        assertEquals( "DUMMY_EVENT", dummyType.name() );
    }

}
