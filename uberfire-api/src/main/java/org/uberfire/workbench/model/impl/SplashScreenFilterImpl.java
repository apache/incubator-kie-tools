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

package org.uberfire.workbench.model.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.workbench.model.SplashScreenFilter;

/**
 * Default implementation of SplashScreenFilter
 */
@Portable
public class SplashScreenFilterImpl implements SplashScreenFilter {

    private String name;
    private boolean displayNextTime;
    private Collection<String> interceptionPoints = new ArrayList<String>();

    public SplashScreenFilterImpl() {
    }

    public SplashScreenFilterImpl( final String name,
                                   final boolean displayNextTime,
                                   final Collection<String> interceptionPoints ) {
        this.name = name;
        this.displayNextTime = displayNextTime;
        this.interceptionPoints.addAll( interceptionPoints );
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName( final String name ) {
        this.name = name;
    }

    @Override
    public boolean displayNextTime() {
        return displayNextTime;
    }

    @Override
    public void setDisplayNextTime( final boolean value ) {
        this.displayNextTime = value;
    }

    @Override
    public Collection<String> getInterceptionPoints() {
        return interceptionPoints;
    }

    @Override
    public void setInterceptionPoints( final Collection<String> places ) {
        interceptionPoints.clear();
        interceptionPoints.addAll( places );
    }
}
