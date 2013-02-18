package org.uberfire.client.editors.texteditor;

import javax.enterprise.context.ApplicationScoped;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.file.ResourceType;

@ApplicationScoped
public class TextResourceType implements ResourceType {

    private final Icon icon = new Icon( IconType.FILE_ALT );

    @Override
    public String getDescription() {
        return "Text file";
    }

    @Override
    public IsWidget getIcon() {
        return icon;
    }

    @Override
    public boolean accept( final Path path ) {
        return path.getFileName().endsWith( ".txt" );
    }
}
