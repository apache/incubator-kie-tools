package org.kie.workbench.common.screens.javaeditor.client.type;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.screens.javaeditor.client.resources.JavaEditorResources;
import org.kie.workbench.common.screens.javaeditor.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.javaeditor.type.JavaResourceTypeDefinition;
import org.uberfire.client.workbench.type.ClientResourceType;

@ApplicationScoped
public class JavaResourceType
        extends JavaResourceTypeDefinition
        implements ClientResourceType {

    private static final Image IMAGE = new Image( JavaEditorResources.INSTANCE.images().typeJava() );

    @Override
    public IsWidget getIcon() {
        return IMAGE;
    }

    @Override
    public String getDescription() {
        String desc = Constants.INSTANCE.javaResourceTypeDescription();
        if ( desc == null || desc.isEmpty() ) return super.getDescription();
        return desc;
    }
}
