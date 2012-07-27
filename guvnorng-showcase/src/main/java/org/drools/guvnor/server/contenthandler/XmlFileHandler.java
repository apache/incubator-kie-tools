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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.RuleContentText;
import org.drools.repository.AssetItem;
import org.drools.repository.ModuleItem;

import com.google.gwt.user.client.rpc.SerializationException;


public class XmlFileHandler extends PlainTextContentHandler {
    public void retrieveAssetContent(Asset asset, ModuleItem pkg, AssetItem item)
            throws SerializationException {
        if (item.getContent() != null) {
            RuleContentText text = new RuleContentText();
            text.content = item.getContent();
            asset.setContent( text );
        }
    }

    public void storeAssetContent(Asset asset, AssetItem repoAsset) throws SerializationException {

        RuleContentText text = (RuleContentText) asset.getContent();

        try {
            InputStream input = new ByteArrayInputStream(text.content.getBytes("UTF-8"));
            repoAsset.updateBinaryContentAttachment(input);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
