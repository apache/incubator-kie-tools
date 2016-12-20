/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.wbtest.client.headfoot;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.ioc.client.container.BeanActivator;

import com.google.gwt.user.client.Window.Location;

/**
 * Bean activator that disables the header and footer beans if the request contains the parameter
 * disableHeadersAndFooters=true.
 */
@ApplicationScoped
public class HeaderFooterActivator implements BeanActivator {

    public static final String DISABLE_PARAM = "disableHeadersAndFooters";

    @Override
    public boolean isActivated() {
        String disabled = Location.getParameter( DISABLE_PARAM );
        if ( disabled == null ) {
            return true;
        }
        return !Boolean.valueOf( disabled );
    }

}
