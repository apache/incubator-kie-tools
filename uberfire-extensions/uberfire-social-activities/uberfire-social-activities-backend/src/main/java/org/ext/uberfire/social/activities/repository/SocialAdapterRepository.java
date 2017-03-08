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
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.ext.uberfire.social.activities.service.SocialAdapter;
import org.ext.uberfire.social.activities.service.SocialAdapterRepositoryAPI;

@ApplicationScoped
public class SocialAdapterRepository implements SocialAdapterRepositoryAPI {

    private Map<Class, SocialAdapter> socialAdapters = new HashMap<Class, SocialAdapter>();

    @Inject
    @Any
    private Instance<SocialAdapter<?>> services;

    @PostConstruct
    public void setup() {
        for (SocialAdapter bean : services) {
            socialAdapters.put(bean.eventToIntercept(),
                               bean);
        }
    }

    @Override
    public Map<Class, SocialAdapter> getSocialAdapters() {
        return socialAdapters;
    }
}
