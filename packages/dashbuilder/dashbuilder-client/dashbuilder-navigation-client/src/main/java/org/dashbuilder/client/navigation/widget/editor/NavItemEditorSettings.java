/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.client.navigation.widget.editor;

import java.util.HashMap;
import java.util.Map;

import org.dashbuilder.navigation.NavItem;

public class NavItemEditorSettings {

    private int maxLevels = -1;
    private String literalGroup = "Group";
    private String literalPerspective = "Perspective";
    private String literalDivider = "Divider";
    private boolean newDividerEnabled = true;
    private boolean newGroupEnabled = true;
    private boolean newPerspectiveEnabled = true;
    private boolean gotoPerspectiveEnabled = false;
    private boolean perspectiveContextEnabled = true;
    private boolean onlyRuntimePerspectives = true;
    private Map<String, Integer> maxLevelsMap = new HashMap<>();
    private Map<String, Flags> flagsMap = new HashMap<>();

    public class Flags {

        public static final String NEW_GROUP = "newGroup";
        public static final String NEW_PERSPECTIVE = "newPerspective";
        public static final String NEW_DIVIDER = "newDivider";
        public static final String ONLY_RUNTIME_PERSPECTIVES = "onlyRuntimePerspectives";
        public static final String GOTO_PERSPECTIVE = "gotoPerspective";
        public static final String PERSPECTIVE_CONTEXT = "perspectiveContext";

        Map<String, Boolean> flagMap = new HashMap<>();
        Map<String, Boolean> recursiveMap = new HashMap<>();
        String lastProp = null;

        void setFlag(String prop, boolean enabled) {
            flagMap.put(prop, enabled);
            lastProp = prop;
        }

        boolean isEnabled(String prop) {
            if (flagMap.containsKey(prop)) {
                return flagMap.get(prop);
            } else {
                return false;
            }
        }

        public void applyToAllChildren() {
            if (lastProp != null) {
                recursiveMap.put(lastProp, true);
            }
        }
    }

    public void setLiteralGroup(String literalGroup) {
        this.literalGroup = literalGroup;
    }

    public void setLiteralPerspective(String literalPerspective) {
        this.literalPerspective = literalPerspective;
    }

    public void setLiteralDivider(String literalDivider) {
        this.literalDivider = literalDivider;
    }

    public String getLiteralGroup() {
        return literalGroup;
    }

    public String getLiteralPerspective() {
        return literalPerspective;
    }

    public String getLiteralDivider() {
        return literalDivider;
    }

    private Flags setNavItemPropertyEnabled(String navItemId, String prop, boolean enabled) {
        Flags config = flagsMap.get(navItemId);
        if (config == null) {
            flagsMap.put(navItemId, config = new Flags());
        }
        config.setFlag(prop, enabled);
        return config;
    }

    private Boolean isNavItemPropertyEnabled(NavItem navItem, String prop) {
        Flags config = flagsMap.get(navItem.getId());
        if (config != null && config.flagMap.get(prop) != null) {
            return config.flagMap.get(prop);
        }
        return isNavAncestorPropertyEnabled(navItem.getParent(), prop);
    }

    private Boolean isNavAncestorPropertyEnabled(NavItem navItem, String prop) {
        if (navItem == null) {
            return null;
        }
        Flags config = flagsMap.get(navItem.getId());
        if (config != null && config.flagMap.get(prop) != null && config.recursiveMap.get(prop) != null && config.recursiveMap.get(prop)) {
            return config.flagMap.get(prop);
        } else {
            return isNavAncestorPropertyEnabled(navItem.getParent(), prop);
        }
    }

    public boolean isNewGroupEnabled() {
        return newGroupEnabled;
    }

    public NavItemEditorSettings setNewGroupEnabled(boolean newGroupEnabled) {
        this.newGroupEnabled = newGroupEnabled;
        return this;
    }

    public boolean isNewGroupEnabled(NavItem navItem) {
        Boolean enabled = isNavItemPropertyEnabled(navItem, Flags.NEW_GROUP);
        return enabled != null ? enabled : newGroupEnabled;
    }

    public Flags setNewGroupEnabled(String navItemId, boolean enabled) {
        return setNavItemPropertyEnabled(navItemId, Flags.NEW_GROUP, enabled);
    }

    public boolean isNewPerspectiveEnabled() {
        return newPerspectiveEnabled;
    }

    public NavItemEditorSettings setNewPerspectiveEnabled(boolean newPerspectiveEnabled) {
        this.newPerspectiveEnabled = newPerspectiveEnabled;
        return this;
    }

    public boolean isNewPerspectiveEnabled(NavItem navItem) {
        Boolean enabled = isNavItemPropertyEnabled(navItem, Flags.NEW_PERSPECTIVE);
        return enabled != null ? enabled : newPerspectiveEnabled;
    }

    public Flags setNewPerspectiveEnabled(String navItemId, boolean enabled) {
        return setNavItemPropertyEnabled(navItemId, Flags.NEW_PERSPECTIVE, enabled);
    }

    public boolean isNewDividerEnabled() {
        return newDividerEnabled;
    }

    public NavItemEditorSettings setNewDividerEnabled(boolean newDividerEnabled) {
        this.newDividerEnabled = newDividerEnabled;
        return this;
    }

    public boolean isNewDividerEnabled(NavItem navItem) {
        Boolean enabled = isNavItemPropertyEnabled(navItem, Flags.NEW_DIVIDER);
        return enabled != null ? enabled : newDividerEnabled;
    }

    public Flags setNewDividerEnabled(String navItemId, boolean enabled) {
        return setNavItemPropertyEnabled(navItemId, Flags.NEW_DIVIDER, enabled);
    }

    public boolean onlyRuntimePerspectives(NavItem navItem) {
        Boolean enabled = isNavItemPropertyEnabled(navItem, Flags.ONLY_RUNTIME_PERSPECTIVES);
        return enabled != null ? enabled : onlyRuntimePerspectives;
    }

    public Flags setOnlyRuntimePerspectives(String navItemId, boolean enabled) {
        return setNavItemPropertyEnabled(navItemId, Flags.ONLY_RUNTIME_PERSPECTIVES, enabled);
    }

    public boolean isGotoPerspectiveEnabled() {
        return gotoPerspectiveEnabled;
    }

    public NavItemEditorSettings setGotoPerspectiveEnabled(boolean gotoPerspectiveEnabled) {
        this.gotoPerspectiveEnabled = gotoPerspectiveEnabled;
        return this;
    }

    public boolean isGotoPerspectiveEnabled(NavItem navItem) {
        Boolean enabled = isNavItemPropertyEnabled(navItem, Flags.GOTO_PERSPECTIVE);
        return enabled != null ? enabled : gotoPerspectiveEnabled;
    }

    public Flags setGotoPerspectiveEnabled(String navItemId, boolean enabled) {
        return setNavItemPropertyEnabled(navItemId, Flags.GOTO_PERSPECTIVE, enabled);
    }

    public boolean isPerspectiveContextEnabled() {
        return perspectiveContextEnabled;
    }

    public NavItemEditorSettings setPerspectiveContextEnabled(boolean perspectiveContextEnabled) {
        this.perspectiveContextEnabled = perspectiveContextEnabled;
        return this;
    }

    public boolean isPerspectiveContextEnabled(NavItem navItem) {
        Boolean enabled = isNavItemPropertyEnabled(navItem, Flags.PERSPECTIVE_CONTEXT);
        return enabled != null ? enabled : perspectiveContextEnabled;
    }

    public Flags setPerspectiveContextEnabled(String navItemId, boolean enabled) {
        return setNavItemPropertyEnabled(navItemId, Flags.PERSPECTIVE_CONTEXT, enabled);
    }

    public int getMaxLevels() {
        return maxLevels;
    }

    public NavItemEditorSettings setMaxLevels(String navItemId, int maxLevels) {
        maxLevelsMap.put(navItemId, maxLevels);
        return this;
    }

    public int getMaxLevels(String navItemId) {
        if (!maxLevelsMap.containsKey(navItemId)) {
            return -1;
        } else {
            return maxLevelsMap.get(navItemId);
        }
    }

    public NavItemEditorSettings setMaxLevels(int maxLevels) {
        this.maxLevels = maxLevels;
        return this;
    }

    public boolean isOnlyRuntimePerspectives() {
        return onlyRuntimePerspectives;
    }

    public NavItemEditorSettings setOnlyRuntimePerspectives(boolean onlyRuntimePerspectives) {
        this.onlyRuntimePerspectives = onlyRuntimePerspectives;
        return this;
    }
}
