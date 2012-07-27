/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.guvnor.server.util;

import org.drools.guvnor.client.rpc.Asset;
import org.drools.repository.AssetItem;

public class AssetPopulator {
    public Asset populateFrom(final AssetItem assetItem) {
        Asset ruleAsset = new Asset();
        ruleAsset.setUuid( assetItem.getUUID() );
        ruleAsset.setName( assetItem.getName() );
        ruleAsset.setDescription( assetItem.getDescription() );
        ruleAsset.setLastModified( assetItem.getLastModified().getTime() );
        ruleAsset.setLastContributor( assetItem.getLastContributor() );
        ruleAsset.setState( (assetItem.getState() != null) ? assetItem.getState().getName() : "" );
        ruleAsset.setDateCreated( assetItem.getCreatedDate().getTime() );
        ruleAsset.setCheckinComment( assetItem.getCheckinComment() );
        ruleAsset.setVersionNumber( assetItem.getVersionNumber() );
        ruleAsset.setFormat(assetItem.getFormat());
        ruleAsset.setArchived(assetItem.isArchived());
        return ruleAsset;
    }

}
