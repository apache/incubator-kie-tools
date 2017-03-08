/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.backend.server.plugins;

import javax.servlet.ServletContext;

import org.jboss.errai.bus.server.api.RpcContext;

public class PluginUtils {

    public static String getRealPath(final String path) {
        return getRealPath(RpcContext.getServletRequest().getServletContext(),
                           path);
    }

    public static String getRealPath(final ServletContext servletContext,
                                     final String path) {
        final String realPath = servletContext.getRealPath(path);
        if (realPath == null) {
            return null;
        } else {
            return realPath.replaceAll("\\\\",
                                       "/").replaceAll(" ",
                                                       "%20");
        }
    }
}

