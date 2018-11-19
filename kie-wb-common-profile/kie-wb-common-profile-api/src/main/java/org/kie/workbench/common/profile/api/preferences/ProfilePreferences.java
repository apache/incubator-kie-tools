package org.kie.workbench.common.profile.api.preferences;

import java.util.logging.Logger;

import org.uberfire.preferences.shared.PropertyFormType;
import org.uberfire.preferences.shared.annotations.Property;
import org.uberfire.preferences.shared.annotations.WorkbenchPreference;
import org.uberfire.preferences.shared.bean.BasePreference;

@WorkbenchPreference(identifier = "ProfilePreferences", bundleKey = "ProfilePreferences.Label")
public class ProfilePreferences implements BasePreference<ProfilePreferences> {
    
    private static final String INITIAL_PROFILE_PROPERTY = "org.kie.workbench.profile";

    private static Logger logger = Logger.getLogger(ProfilePreferences.class.getName());

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
        String profileStr = System.getProperty(INITIAL_PROFILE_PROPERTY, Profile.FULL.name());
        Profile defaultProfile = Profile.FULL;
        try {
            defaultProfile = Profile.valueOf(profileStr);
        } catch(IllegalArgumentException e) {
            logger.warning(() -> "Not able to load profile " + profileStr + ". Loading FULL profile.");
        }
        logger.info("Loaded Profile " + defaultProfile.getName());
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
