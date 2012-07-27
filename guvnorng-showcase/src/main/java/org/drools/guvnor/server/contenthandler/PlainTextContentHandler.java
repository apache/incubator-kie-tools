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

import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.client.rpc.BuilderResultLine;
import org.drools.guvnor.client.rpc.RuleContentText;
import org.drools.repository.AssetItem;

import com.google.gwt.user.client.rpc.SerializationException;

public abstract class PlainTextContentHandler extends ContentHandler {

    public void retrieveAssetContent(Asset asset,
                                     AssetItem item) throws SerializationException {
        //default to text, goode olde texte, just like mum used to make.
        RuleContentText text = new RuleContentText();
        text.content = item.getContent();
        asset.setContent( text );

    }

    public void storeAssetContent(Asset asset,
                                  AssetItem repoAsset) throws SerializationException {
        repoAsset.updateContent( ((RuleContentText) asset.getContent()).content );

    }

    public BuilderResult validateAsset(AssetItem asset) {

        final String message = validate(asset.getContent());

        return createBuilderResult(message,
                asset.getName(),
                asset.getFormat(),
                asset.getUUID());
    }

    private BuilderResult createBuilderResult(final String message, final String name,
            final String format, final String uuid) {

        if (message.length() == 0) {
            return new BuilderResult();
        } else {
            List<BuilderResultLine> errors = new ArrayList<BuilderResultLine>();

            BuilderResultLine result = new BuilderResultLine().setAssetName(name).setAssetFormat(format).setUuid(uuid).setMessage(message);
            errors.add(result);

            BuilderResult builderResult = new BuilderResult();
            builderResult.addLines(errors);

            return builderResult;
        }
    }

    public String validate(final String content) {
        return "";
    }

}
