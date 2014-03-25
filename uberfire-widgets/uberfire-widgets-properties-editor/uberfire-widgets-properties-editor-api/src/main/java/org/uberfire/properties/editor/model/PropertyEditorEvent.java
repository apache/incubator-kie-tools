package org.uberfire.properties.editor.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.util.Collections.*;
import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

/**
 * A Property Editor CDI Event handled by Property Editor.
 * Id event its a parameter to identify the originator of the event.
 * A Property Editor event contains one or multiples PropertyEditorCategory.
 */
public class PropertyEditorEvent {

    private List<PropertyEditorCategory> properties = new ArrayList<PropertyEditorCategory>();

    public PropertyEditorEvent( String idEvent,
                                List<PropertyEditorCategory> properties ) {
        checkNotNull( "idEvent", idEvent );
        checkNotNull( "properties", properties );
        for (PropertyEditorCategory property: properties  ){
            property.setIdEvent( idEvent );
        }
        this.properties = properties;
    }

    public PropertyEditorEvent( String idEvent,PropertyEditorCategory property ) {
        checkNotNull( "idEvent", idEvent );
        checkNotNull( "property", property );
        property.setIdEvent( idEvent );
        this.properties.add( property );
    }

    /**
     * Get Properties from a event ordered by priority.
     * @return Categories and fields ordered by priority, lower values toward the beginning
     */
    public List<PropertyEditorCategory> getSortedProperties() {
        sortCategoriesAndFieldsByPriority( properties );
        return properties;
    }

    private static void sortCategoriesAndFieldsByPriority( List<PropertyEditorCategory> properties ) {
        sortCategoriesByPriority( properties );

        sortEditorFieldInfoByPriority( properties );

    }

    private static void sortCategoriesByPriority( List<PropertyEditorCategory> properties ) {
        sort( properties, new Comparator<PropertyEditorCategory>() {
            @Override
            public int compare( final PropertyEditorCategory o1,
                                final PropertyEditorCategory o2 ) {

                if ( o1.getPriority() < o2.getPriority() ) {
                    return -1;
                } else if ( o1.getPriority() > o2.getPriority() ) {
                    return 1;
                } else {
                    return 0;
                }
            }
        } );
    }

    private static void sortEditorFieldInfoByPriority( List<PropertyEditorCategory> properties ) {
        for ( PropertyEditorCategory category : properties ) {
            sort( category.getFields(), new Comparator<PropertyEditorFieldInfo>() {
                @Override
                public int compare( final PropertyEditorFieldInfo o1,
                                    final PropertyEditorFieldInfo o2 ) {

                    if ( o1.getPriority() < o2.getPriority() ) {
                        return -1;
                    } else if ( o1.getPriority() > o2.getPriority() ) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            } );
        }

    }

}
