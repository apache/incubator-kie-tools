/*
 * Copyright 2011 JBoss Inc
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
package org.drools.guvnor.server.util;

import org.drools.guvnor.client.rpc.Module;
import org.drools.repository.ModuleItem;

public class ModuleFactory {
    public static Module createModuleWithDependencies(ModuleItem item) {
        Module data = create(item);
        data.setDependencies(item.getDependencies());
        return data;
    }

    public static Module createModuleWithOutDependencies(ModuleItem item) {
        return create(item);
    }

    private static Module create(ModuleItem item) {
        Module data = new Module();
        data.setUuid(item.getUUID());
        data.setHeader(DroolsHeader.getDroolsHeader(item));
        data.setExternalURI(item.getExternalURI());
        data.setCatRules(item.getCategoryRules());
        data.setDescription(item.getDescription());
        data.setArchived(item.isArchived());
        data.setName(item.getName());
        data.setLastModified(item.getLastModified().getTime());
        data.setDateCreated(item.getCreatedDate().getTime());
        data.setCheckinComment(item.getCheckinComment());
        data.setLastContributor(item.getLastContributor());
        data.setState(item.getStateDescription());
        data.setSnapshot(item.isSnapshot());
        data.setVersionNumber(item.getVersionNumber());
        data.setFormat(item.getFormat());
        return data;
    }


}
