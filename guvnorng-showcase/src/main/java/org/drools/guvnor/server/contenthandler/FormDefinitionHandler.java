package org.drools.guvnor.server.contenthandler;

import org.drools.guvnor.client.rpc.FormContentModel;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.repository.AssetItem;

import com.google.gwt.user.client.rpc.SerializationException;

public class FormDefinitionHandler extends ContentHandler {

    @Override
    public void retrieveAssetContent(Asset asset, AssetItem item) throws SerializationException {
        FormContentModel content = new FormContentModel();
        content.setJson( item.getContent() );
        asset.setContent( content );
    }

    @Override
    public void storeAssetContent(Asset asset, AssetItem repoAsset) throws SerializationException {
        FormContentModel content = (FormContentModel) asset.getContent();
        if ( content != null ) {
            if ( content.getJson() != null ) {
                repoAsset.updateContent( content.getJson() );
            }
        }
    }

}
