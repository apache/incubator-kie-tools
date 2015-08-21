package org.drools.workbench.screens.dtablexls.client.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.gwtbootstrap.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.uberfire.workbench.type.ResourceTypeDefinition;

public class FileExtensionSelector
        extends VerticalPanel {

    private static final String FILE_TYPE = "FILE_TYPE";
    private final Map<String, ResourceTypeDefinition> resourceTypes = new HashMap<String, ResourceTypeDefinition>();
    private final List<RadioButton> radioButtonList = new ArrayList<RadioButton>();

    public FileExtensionSelector( final ResourceTypeDefinition... resourceTypes ) {

        setResourceTypes( resourceTypes );
        addRadioButtons();
    }

    private void addRadioButtons() {
        boolean first = true;
        for (String resourceSuffix : this.resourceTypes.keySet()) {
            RadioButton radioButton = new RadioButton( FILE_TYPE );
            radioButton.setText( resourceSuffix );

            radioButton.setValue( first );
            first = false;

            radioButtonList.add( radioButton );
            add( radioButton );
        }
    }

    private void setResourceTypes( final ResourceTypeDefinition[] resourceTypes ) {
        for (ResourceTypeDefinition resourceType : resourceTypes) {
            this.resourceTypes.put( resourceType.getSuffix(),
                                    resourceType );
        }
    }

    public ResourceTypeDefinition getResourceType() {
        for (RadioButton radioButton : radioButtonList) {
            if ( radioButton.getValue() ) {
                return this.resourceTypes.get( radioButton.getText() );
            }
        }
        return null;
    }
}
