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

/*import javax.jcr.Node;
import javax.jcr.NodeIterator;*/
import java.util.Iterator;
import java.util.List;

/**
 * This iterates over nodes and produces AssetItem's.
 * Also allows "skipping" of results to jump to certain items,
 * as per JCRs "skip".
 * <p/>
 * JCR iterators are/can be lazy, so this makes the most of it for large
 * numbers of assets.
 */
public class AssetItemIterator
        implements
        Iterator<AssetItem> {

    //protected NodeIterator it;
    protected List<org.uberfire.java.nio.file.Path> assetItemPaths;
    //protected RulesRepository rulesRepository;
    protected Iterator<org.uberfire.java.nio.file.Path> it;

    public AssetItemIterator(List<org.uberfire.java.nio.file.Path> assetItemPaths) {
        //this.it = nodes;
        //this.rulesRepository = repo;
        this.assetItemPaths = assetItemPaths;
        it = this.assetItemPaths.iterator();
    }

    public AssetItemIterator(AssetItemIterator assetItemIterator) {
        //this.it = assetItemIterator.it;
        //this.rulesRepository = assetItemIterator.rulesRepository;
        this.assetItemPaths = assetItemIterator.assetItemPaths;
    }

    public boolean hasNext() {
        return it.hasNext();
    }

    public AssetItem next() {
    	return new AssetItem(it.next());
/*        
        return new AssetItem(rulesRepository,
                it.nextNode());*/
    }

    public void remove() {
        throw new UnsupportedOperationException("You can't remove an asset this way.");
    }

    /**
     * @param i The number of assets to skip.
     */
    public void skip(long i) {
    	//JLIU: TODO:

        //it.skip(i);
    }

    /**
     * @return the size of the underlying iterator's potential data set.
     *         May be -1 if not known.
     */
    //NOTE this may return -1 as per JCR2.0 when precise count is not available due to performance reasons. 
    //See http://markmail.org/message/mxmk5hkxrdtcc3hl to understand how an actual row count can be ensured
    //All queries have had the explicit "order by" added, however if you find something somewhere that 
    //still returns -1 you'll need to add the "order by" if required.
    public long getSize() {   	
        return assetItemPaths.size();
    }

    /**
     * Get the position in the result set.
     */
    public long getPosition() {
    	//JLIU: TODO:
    	return 0;
    	
        //return it.getPosition();
    }


}
