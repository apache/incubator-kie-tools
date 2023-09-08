/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.uberfire.ext.editor.commons.client.resources;

import org.treblereel.j2cl.processors.common.resources.ResourcePrototype;

public class CommonImagesImpl implements CommonImages {

    public static final CommonImagesImpl INSTANCE = new CommonImagesImpl();

    private java.util.HashMap<String, ResourcePrototype> resourceMap;

    private CommonImagesImpl() {

    }

    private static org.treblereel.j2cl.processors.common.resources.ImageResource edit;

    private void editInitializer() {
                String encoded =  "data:image/gif;base64,R0lGODlhEAAQAMQAAJ6Mn5yLpIWFtAAla+TPlMGqfvu8Z/3iv/vRm5lPG5lSIptaMpthPZhkQ5tqUP///////wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACH5BAEAABAALAAAAAAQABAAAAVPICSOpPg8ZWo2DaqOT2MYjKvGxoEsdonrBkUPJgM6WqkfwuBIDCFKpjNZXDaf0Wsy4EAQCtpkgvAN+wCDQiExvQ0Sb8cTNhgITi/TaU4KAQA7";
        edit =  new org.treblereel.j2cl.processors.common.resources.impl.ImageResourcePrototype(
                            "edit",
                            encoded,
                            16, 16);
    }

    private static class editInitializer {
        static {
            INSTANCE.editInitializer();
        }
        static org.treblereel.j2cl.processors.common.resources.ImageResource get() {
            return edit;
        }
    }

    @Override
    public org.treblereel.j2cl.processors.common.resources.ImageResource edit() {
        return editInitializer.get();
    };

    public ResourcePrototype getResource(String name) {
        if (resourceMap == null) {
            resourceMap = new java.util.HashMap<>();
            resourceMap.put("edit", edit());
        }
        return resourceMap.get(name);
    }
}
