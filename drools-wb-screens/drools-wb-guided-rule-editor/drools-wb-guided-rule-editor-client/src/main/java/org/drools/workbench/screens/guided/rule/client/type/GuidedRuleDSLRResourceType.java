package org.drools.workbench.screens.guided.rule.client.type;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.screens.guided.rule.client.resources.GuidedRuleEditorResources;
import org.drools.workbench.screens.guided.rule.type.GuidedRuleDSLRResourceTypeDefinition;
import org.uberfire.client.workbench.type.ClientResourceType;

@ApplicationScoped
public class GuidedRuleDSLRResourceType
        extends GuidedRuleDSLRResourceTypeDefinition
        implements ClientResourceType {

    private static final Image IMAGE = new Image( GuidedRuleEditorResources.INSTANCE.images().typeGuidedRule() );

    @Override
    public IsWidget getIcon() {
        return IMAGE;
    }

    @Override
    public String getDescription() {
        String desc = GuidedRuleEditorResources.CONSTANTS.guidedRuleDSLResourceTypeDescription();
        if ( desc == null || desc.isEmpty() ) return super.getDescription();
        return desc;
    }
}
