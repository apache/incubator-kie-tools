package org.kie.uberfire.social.activities.service;

import java.util.Map;

public interface SocialAdapterRepositoryAPI {

    Map<Class, SocialAdapter> getSocialAdapters();
}
