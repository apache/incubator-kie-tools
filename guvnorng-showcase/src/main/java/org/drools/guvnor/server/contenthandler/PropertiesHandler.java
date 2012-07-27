/*
 * Copyright 2005 JBoss Inc
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.drools.guvnor.client.editors.PropertiesHolder;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.server.util.PropertiesPersistence;
import org.drools.repository.AssetItem;

import com.google.gwt.user.client.rpc.SerializationException;

/**
 * Handle *.properties file as a content for rule asset instead of a binary
 * attachment
 */
public class PropertiesHandler extends ContentHandler {
    public void retrieveAssetContent(Asset asset,
                                     AssetItem item) throws SerializationException {
        if ( item.getContent() != null ) {
            asset.setContent( PropertiesPersistence.getInstance().unmarshal(
                                                                             item.getContent() ) );
        }
    }

    public void storeAssetContent(Asset asset,
                                  AssetItem repoAsset)
                                                      throws SerializationException {
        PropertiesHolder holder = (PropertiesHolder) asset.getContent();
        String toSave = PropertiesPersistence.getInstance().marshal( holder );

        InputStream input = null;
        try {
            try {
                input = new ByteArrayInputStream( toSave.getBytes( "UTF-8" ) );
                repoAsset.updateBinaryContentAttachment( input );
            } finally {
                if ( input != null ) {
                    input.close();
                }
            }
        } catch ( UnsupportedEncodingException e ) {
            e.printStackTrace();
            throw new RuntimeException( e ); // TODO: ?
        } catch ( IOException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }
}
