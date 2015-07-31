package org.drools.workbench.screens.dtablexls.client.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.ui.VerticalPanel;
import org.gwtbootstrap3.client.ui.Radio;
import org.uberfire.workbench.type.ResourceTypeDefinition;

public class FileExtensionSelector
        extends VerticalPanel {

    private static final String FILE_TYPE = "FILE_TYPE";
    private final Map<String, ResourceTypeDefinition> resourceTypes = new HashMap<String, ResourceTypeDefinition>();
    private final List<Radio> radioButtonList = new ArrayList<Radio>();

    public FileExtensionSelector( final ResourceTypeDefinition... resourceTypes ) {

        setResourceTypes( resourceTypes );
        addRadioButtons();
    }

    private void addRadioButtons() {
        boolean first = true;
        for ( String resourceSuffix : this.resourceTypes.keySet() ) {
            Radio radioButton = new Radio( FILE_TYPE );
            radioButton.setText( resourceSuffix );

            radioButton.setValue( first );
            first = false;

            radioButtonList.add( radioButton );
            add( radioButton );
        }
    }

    private void setResourceTypes( final ResourceTypeDefinition[] resourceTypes ) {
        for ( ResourceTypeDefinition resourceType : resourceTypes ) {
            this.resourceTypes.put( resourceType.getSuffix(),
                                    resourceType );
        }
    }

    public ResourceTypeDefinition getResourceType() {
        for ( Radio radioButton : radioButtonList ) {
            if ( radioButton.getValue() ) {
                return this.resourceTypes.get( radioButton.getText() );
            }
        }
        return null;
    }
}
