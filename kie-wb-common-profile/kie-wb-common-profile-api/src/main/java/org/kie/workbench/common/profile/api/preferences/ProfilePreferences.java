/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.profile.api.preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.uberfire.preferences.shared.PropertyFormType;
import org.uberfire.preferences.shared.annotations.Property;
import org.uberfire.preferences.shared.annotations.WorkbenchPreference;
import org.uberfire.preferences.shared.bean.BasePreference;

import static org.kie.workbench.common.profile.api.preferences.ProfileDefinitions.FORCE_PREFIX;

@WorkbenchPreference(identifier = "ProfilePreferences", bundleKey = "ProfilePreferences.Label")
public class ProfilePreferences implements BasePreference<ProfilePreferences> {
    
    private static Logger logger = LoggerFactory.getLogger(ProfilePreferences.class.getName());

    @Property(bundleKey = "ProfilePreferences.Profiles", 
            helpBundleKey= "ProfilePreferences.Profiles.Help", 
            formType = PropertyFormType.COMBO)
    private Profile profile;

    public ProfilePreferences() {
    }
    
    public ProfilePreferences(Profile profile) {
        this.profile = profile;
    }

    @Override
    public ProfilePreferences defaultValue(ProfilePreferences defaultValue) {
        String profileStr = System.getProperty("org.kie.workbench.profile", Profile.FULL.name());
        Profile defaultProfile = Profile.FULL;
        try {
            if (profileStr.startsWith(FORCE_PREFIX)) {
                profileStr = profileStr.substring(FORCE_PREFIX.length());
            }
            defaultProfile = Profile.valueOf(profileStr);
        } catch(IllegalArgumentException e) {
            logger.warn("Not able to load profile {}. Loading FULL profile.", profileStr);
        }
        defaultValue.setProfile(defaultProfile);
        return defaultValue;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }
   
}