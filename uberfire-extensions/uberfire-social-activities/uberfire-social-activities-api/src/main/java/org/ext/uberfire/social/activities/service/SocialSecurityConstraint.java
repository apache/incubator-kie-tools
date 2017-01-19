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
package org.ext.uberfire.social.activities.service;

import org.ext.uberfire.social.activities.model.SocialActivitiesEvent;

/**
 * This interface defines a SocialSecurityConstraint.
 * Each implementation of this class will be applied by
 * SocialSecurityConstraintsManager each time
 * that a social timeline is requested.
 * Be advised that these constraints are not always applied
 * in CDI RequestScoped, so pay attention when you want to use a
 * RequestScoped bean in this implementation (like User interface).
 */
public interface SocialSecurityConstraint {

    boolean hasRestrictions( SocialActivitiesEvent event );

    void init();

}
