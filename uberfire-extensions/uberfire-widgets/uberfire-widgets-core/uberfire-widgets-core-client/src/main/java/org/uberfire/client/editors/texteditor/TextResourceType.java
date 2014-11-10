package org.uberfire.client.editors.texteditor;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.resources.CoreImages;
import org.uberfire.client.resources.i18n.CoreConstants;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.workbench.type.TextResourceTypeDefinition;

@ApplicationScoped
public class TextResourceType
        extends TextResourceTypeDefinition
        implements ClientResourceType {

    private static final Image IMAGE = new Image( CoreImages.INSTANCE.typeTextFile() );

    @Override
    public IsWidget getIcon() {
        return IMAGE;
    }

    @Override
    public String getDescription() {
        String desc = CoreConstants.INSTANCE.textResourceTypeDescription();
        if ( desc == null || desc.isEmpty() ) return super.getDescription();
        return desc;
    }
}
