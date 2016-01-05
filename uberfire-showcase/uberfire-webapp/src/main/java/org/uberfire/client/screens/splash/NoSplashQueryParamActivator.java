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

package org.uberfire.client.screens.splash;

import javax.inject.Singleton;

import org.jboss.errai.ioc.client.container.BeanActivator;

import com.google.gwt.user.client.Window;

/**
 * A simple example of a bean activator: reports true unless there is a "nosplash" parameter in the location bar.
 */
@Singleton
public class NoSplashQueryParamActivator implements BeanActivator {

    @Override
    public boolean isActivated() {
        return Window.Location.getParameter( "nosplash" ) == null;
    }

}
