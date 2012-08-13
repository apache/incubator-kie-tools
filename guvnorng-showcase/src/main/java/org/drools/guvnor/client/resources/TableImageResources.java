package org.drools.guvnor.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface TableImageResources
        extends ClientBundle {

    TableImageResources INSTANCE = GWT.create(TableImageResources.class);

    @Source("images/downArrow.png")
    ImageResource downArrow();

    @Source("images/smallDownArrow.png")
    ImageResource smallDownArrow();

    @Source("images/upArrow.png")
    ImageResource upArrow();

    @Source("images/smallUpArrow.png")
    ImageResource smallUpArrow();

    @Source("images/columnPicker.png")
    ImageResource columnPicker();

}
