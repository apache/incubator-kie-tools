/*
 * Copyright 2012 JBoss Inc
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

package org.uberfire.security.server;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.uberfire.security.Resource;
import org.uberfire.security.ResourceManager;
import org.uberfire.security.Role;
import org.uberfire.security.server.util.AntPathMatcher;
import org.yaml.snakeyaml.Yaml;

import static java.util.Collections.*;
import static org.uberfire.commons.util.Preconditions.*;
import static org.uberfire.commons.util.PreconditionsServer.*;
import static org.uberfire.security.server.SecurityConstants.*;

public class URLResourceManager implements ResourceManager {

    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();
    private static final Collection<Class<? extends Resource>> SUPPORTED_TYPES = new ArrayList<Class<? extends Resource>>(1) {{
        add(URLResource.class);
    }};

    private static final String DEFAULT_CONFIG = "exclude:\n" +
            "   - /*.ico\n" +
            "   - /image/**\n" +
            "   - /css/**";

    private String configFile = URL_FILTER_CONFIG_YAML;

    private final Resources resources;
    private final Set<String> excludeCache = new HashSet<String>();

    public URLResourceManager(final String configFile) {
        if (configFile != null && !configFile.isEmpty()) {
            this.configFile = configFile;
        }
        this.resources = loadConfigData();
    }

    private Resources loadConfigData() {
        final Yaml yaml = new Yaml();
        final InputStream stream = URLResourceManager.class.getClassLoader().getResourceAsStream(this.configFile);
        final Map result;
        if (stream != null) {
            result = yaml.loadAs(stream, Map.class);
        } else {
            result = yaml.loadAs(DEFAULT_CONFIG, Map.class);
        }
        return new Resources(result);
    }

    @Override
    public boolean supports(final Resource resource) {
        if (resource instanceof URLResource) {
            return true;
        }
        return false;
    }

    @Override
    public boolean requiresAuthentication(final Resource resource) {
        final URLResource urlResource;
        try {
            urlResource = checkInstanceOf("context", resource, URLResource.class);
        } catch (IllegalArgumentException e) {
            return false;
        }

        if (!excludeCache.contains(urlResource.getURL())) {
            boolean isExcluded = false;
            for (String excluded : resources.getExcludedResources()) {
                if (ANT_PATH_MATCHER.match(excluded, urlResource.getURL())) {
                    isExcluded = true;
                    excludeCache.add(urlResource.getURL());
                    break;
                }
            }
            if (isExcluded) {
                return false;
            }
        } else {
            return false;
        }

        return true;
    }

    public List<Role> getMandatoryRoles(final URLResource urlResource) {
        for (Map.Entry<String, List<Role>> activeFilteredResource : resources.getMandatoryFilteredResources().entrySet()) {
            if (ANT_PATH_MATCHER.match(activeFilteredResource.getKey(), urlResource.getURL())) {
                return activeFilteredResource.getValue();
            }
        }
        return emptyList();
    }

    private static class Resources {

        private final Map<String, List<Role>> filteredResources;
        private final Map<String, List<Role>> mandatoryFilteredResources;
        private final Set<String> excludedResources;

        private Resources(final Map yaml) {
            checkNotNull("yaml", yaml);

            final Object ofilter = yaml.get("filter");
            if (ofilter != null) {
                final List<Map<String, String>> filter = checkInstanceOf("ofilter", ofilter, List.class);

                this.filteredResources = new HashMap<String, List<Role>>(filter.size());
                this.mandatoryFilteredResources = new HashMap<String, List<Role>>(filter.size());
                for (final Map<String, String> activeFilter : filter) {
                    final String pattern = activeFilter.get("pattern");
                    final String access = activeFilter.get("access");
                    checkNotNull("pattern", pattern);
                    final List<Role> roles;
                    if (access != null) {
                        final String[] textRoles = access.split(",");
                        roles = new ArrayList<Role>(textRoles.length);
                        for (final String textRole : textRoles) {
                            roles.add(new Role() {
                                @Override
                                public String getName() {
                                    return textRole;
                                }
                            });
                        }
                        mandatoryFilteredResources.put(pattern, roles);
                    } else {
                        roles = emptyList();
                    }
                    filteredResources.put(pattern, roles);
                }
            } else {
                this.filteredResources = emptyMap();
                this.mandatoryFilteredResources = emptyMap();
            }

            final Object oexclude = yaml.get("exclude");
            final List exclude = checkInstanceOf("exclude", oexclude, List.class);

            this.excludedResources = new HashSet<String>(exclude);
        }

        public Map<String, List<Role>> getFilteredResources() {
            return filteredResources;
        }

        public Set<String> getExcludedResources() {
            return excludedResources;
        }

        public Map<String, List<Role>> getMandatoryFilteredResources() {
            return mandatoryFilteredResources;
        }
    }
}
