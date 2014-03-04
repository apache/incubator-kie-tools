package org.drools.workbench.screens.guided.template.client.type;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.screens.guided.template.client.resources.GuidedTemplateEditorResources;
import org.drools.workbench.screens.guided.template.client.resources.i18n.GuidedTemplateEditorConstants;
import org.drools.workbench.screens.guided.template.type.GuidedRuleTemplateResourceTypeDefinition;
import org.uberfire.client.workbench.type.ClientResourceType;

@ApplicationScoped
public class GuidedRuleTemplateResourceType
        extends GuidedRuleTemplateResourceTypeDefinition
        implements ClientResourceType {

    private static final Image IMAGE = new Image( GuidedTemplateEditorResources.INSTANCE.images().typeGuidedRuleTemplate() );

    @Override
    public IsWidget getIcon() {
        return IMAGE;
    }

    @Override
    public String getDescription() {
        String desc = GuidedTemplateEditorConstants.INSTANCE.guidedTemplateResourceTypeDescription();
        if ( desc == null || desc.isEmpty() ) return super.getDescription();
        return desc;
    }
}
