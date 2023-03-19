package org.dashbuilder.dataset.client.resources.bundles;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;

public interface DataSetClientResources
        extends
        ClientBundle {

    DataSetClientResources INSTANCE = GWT.create( DataSetClientResources.class );

    DataSetClientImages images();

}
