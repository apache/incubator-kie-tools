/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.workbench.model;

import java.util.Collection;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * Describes the current interception rules for a splash screen, including the user's current preference for whether
 * or not the screen should be displayed next time one of its interception points is matched.
 * <p>
 * All implementations of this interface must be marked as {@link Portable}.
 */
public interface SplashScreenFilter {

    String getName();

    void setName( final String name );

    boolean displayNextTime();

    void setDisplayNextTime( final boolean value );

    Collection<String> getInterceptionPoints();

    void setInterceptionPoints( final Collection<String> places );

}
