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

package org.guvnor.structure.backend.repositories;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.guvnor.structure.contributors.Contributor;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.uberfire.spaces.Space;
import org.uberfire.spaces.SpacesAPI;

import static java.util.Collections.unmodifiableCollection;

/**
 * Definition of the bootstrap repository
 */
public class SystemRepository extends GitRepository {

    private static final String ALIAS = "system";

    private static final Collection<String> groups = new ArrayList<String>(1) {{
        add("admin");
    }};

    public static final SystemRepository SYSTEM_REPO = new SystemRepository(ALIAS,
                                                                            SpacesAPI.DEFAULT_SPACE);

    private final Map<String, Object> environment = new HashMap<String, Object>();

    private SystemRepository(final String alias,
                             Space space) {
        super(alias,
              space);
        environment.put("init",
                        Boolean.TRUE);
    }

    @Override
    public Map<String, Object> getEnvironment() {
        return environment;
    }

    @Override
    public void addEnvironmentParameter(final String key,
                                        final Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getUri() {
        String alias = SpacesAPI.sanitizeFileSystemName(getAlias());
        return SpacesAPI.resolveFileSystemPath(SpacesAPI.Scheme.DEFAULT,
                                               getSpace(),
                                               alias).toString();
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public Collection<String> getGroups() {
        return unmodifiableCollection(groups);
    }

    @Override
    public Collection<Contributor> getContributors() {
        throw new UnsupportedOperationException();
    }
}
