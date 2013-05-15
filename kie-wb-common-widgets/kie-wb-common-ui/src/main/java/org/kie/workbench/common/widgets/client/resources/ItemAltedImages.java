package org.kie.workbench.common.widgets.client.resources;

import com.google.gwt.user.client.ui.Image;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;

public class ItemAltedImages {

    public static ItemAltedImages INSTANCE = new ItemAltedImages();

    public Image NewItem() {
        Image image = new Image(ItemImages.INSTANCE.newItem());
        image.setAltText( CommonConstants.INSTANCE.NewItem());
        return image;
    }
}
