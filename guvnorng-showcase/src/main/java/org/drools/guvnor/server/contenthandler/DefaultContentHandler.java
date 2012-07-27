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

package org.drools.guvnor.server.contenthandler;

import org.drools.guvnor.client.rpc.Asset;
import org.drools.repository.AssetItem;

import com.google.gwt.user.client.rpc.SerializationException;

/**
 * Default ones will store things as an attachment.
 */
public class DefaultContentHandler extends ContentHandler {

    @Override
    public void retrieveAssetContent(Asset asset,
                                     AssetItem item) throws SerializationException {
    }

    @Override
    public void storeAssetContent(Asset asset,
                                  AssetItem repoAsset)
                                                      throws SerializationException {
    }

}
