package org.uberfire.client.editors.texteditor;

import javax.enterprise.context.ApplicationScoped;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.shared.workbench.type.TextResourceTypeDefinition;

@ApplicationScoped
public class TextResourceType
        extends TextResourceTypeDefinition
        implements ClientResourceType {

    private final Icon icon = new Icon( IconType.FILE_ALT );

    @Override
    public IsWidget getIcon() {
        return icon;
    }
}
