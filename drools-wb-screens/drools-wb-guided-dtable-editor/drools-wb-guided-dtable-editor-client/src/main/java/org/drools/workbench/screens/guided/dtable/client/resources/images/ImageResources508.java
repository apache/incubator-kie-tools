package org.drools.workbench.screens.guided.dtable.client.resources.images;

import com.google.gwt.user.client.ui.Image;
import org.drools.workbench.screens.guided.dtable.client.resources.Resources;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.Constants;

/**
 * Images that have alt-t4ext set for Section 508 compliance
 */
public class ImageResources508 {

    public static final ImageResources508 INSTANCE = new ImageResources508();

    private ImageResources508() {
    }

    public Image Edit() {
        Image image = new Image( Resources.INSTANCE.images().edit() );
        image.setAltText( Constants.INSTANCE.Edit() );
        return image;
    }

    public Image EditDisabled() {
        Image image = new Image( Resources.INSTANCE.images().editDisabled() );
        image.setAltText( Constants.INSTANCE.EditDisabled() );
        return image;
    }

    public Image Config() {
        Image image = new Image( Resources.INSTANCE.images().config() );
        image.setAltText( Constants.INSTANCE.Config() );
        return image;
    }

    public Image DeleteItemSmall() {
        Image image = new Image( Resources.INSTANCE.itemImages().deleteItemSmall() );
        image.setAltText( Constants.INSTANCE.DeleteItem() );
        return image;
    }

    public Image NewItem() {
        Image image = new Image( Resources.INSTANCE.itemImages().newItem() );
        image.setAltText( Constants.INSTANCE.NewItem() );
        return image;
    }

}
