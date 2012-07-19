package org.drools.repository.utils;

import org.drools.repository.AssetItem;

public interface Validator {

    public boolean validate(AssetItem assetItem);

    public String getFormat();
}
