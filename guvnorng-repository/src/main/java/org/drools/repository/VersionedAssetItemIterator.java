/*
 * Copyright 2010 JBoss Inc
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

package org.drools.repository;

import java.util.HashMap;
import java.util.Map;
/*
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;*/


/**
 * This iterates over nodes and produces AssetItem's.
 * Also allows "skipping" of results to jump to certain items,
 * as per JCRs "skip".
 *
 * JCR iterators are/can be lazy, so this makes the most of it for large
 * numbers of assets.
 */
public class VersionedAssetItemIterator extends AssetItemIterator {
    Map<String, String> dependencyVersionMap = new HashMap<String, String>();
    private boolean returnAssetsWithVersionsSpecifiedByDependencies = false;
           
    public VersionedAssetItemIterator(/*NodeIterator nodes,*/
                            RulesRepository repo,
                            String[] dependencies) {
    	super(repo);
/*        super(nodes, repo);
        //this.dependencies = dependencies;
        for(String dependency : dependencies) {
            String[] decodedPath = ModuleItem.decodeDependencyPath(dependency);
            if(!"LATEST".equals(decodedPath[1])) {
                dependencyVersionMap.put(decodedPath[0], decodedPath[1]);
            }
        }  */      
    }

    public AssetItem next() {
        AssetItem ai = super.next();
        if(returnAssetsWithVersionsSpecifiedByDependencies && dependencyVersionMap.get(ai.getName()) != null) {
            String version = dependencyVersionMap.get(ai.getName());
            return loadAssetWithVersion(ai, version);
        }
        return ai;
    }
    
    public void setReturnAssetsWithVersionsSpecifiedByDependencies(boolean flag) {
        this.returnAssetsWithVersionsSpecifiedByDependencies = flag;
    }
    
    protected AssetItem loadAssetWithVersion(final AssetItem assetItem,
            String version) {
    	//JLIU: TODO:
    	return null;
    	
/*    	long requiredVersion = Long.parseLong(version);
		if (assetItem.isHistoricalVersion()) {			
			long currentVersion = assetItem.getVersionNumber();
			if(requiredVersion == currentVersion) {
				return assetItem;
			} else {
				Node frozenNode = assetItem.getNode();
				try {
					Node headNode = frozenNode.getSession().getNodeByIdentifier(
							frozenNode.getProperty("jcr:frozenUuid")
									.getString());
					AssetHistoryIterator historyIterator = new AssetHistoryIterator(
							assetItem.getRulesRepository(), headNode);
					while (historyIterator.hasNext()) {
						AssetItem historical = historyIterator.next();
						if (requiredVersion == historical.getVersionNumber()) {
							return historical;
						}
					}
				} catch (RepositoryException e) {
					throw new RulesRepositoryException("Unable to load AssetItem[" + assetItem.getName() + "] with specificed version[" + version + "]", e);
				}
			}	
		} else {
			AssetHistoryIterator it = assetItem.getHistory();
			while (it.hasNext()) {
				AssetItem historical = (AssetItem) it.next();
				if (requiredVersion == historical.getVersionNumber()) {
					return historical;
				}
			}
		}
		
		throw new RulesRepositoryException("Unable to load AssetItem[" + assetItem.getName() + "] with specificed version[" + version + "]");*/
    }
}
