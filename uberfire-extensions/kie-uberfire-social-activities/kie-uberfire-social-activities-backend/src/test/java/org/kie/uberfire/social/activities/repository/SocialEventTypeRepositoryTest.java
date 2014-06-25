package org.kie.uberfire.social.activities.repository;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.kie.uberfire.social.activities.model.SocialEventType;
import org.kie.uberfire.social.activities.service.SocialAdapter;

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
