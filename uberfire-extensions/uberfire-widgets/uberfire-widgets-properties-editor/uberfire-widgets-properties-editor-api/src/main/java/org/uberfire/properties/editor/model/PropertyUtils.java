package org.uberfire.properties.editor.model;

import java.util.List;
import java.util.Map;


public class PropertyUtils {

    public static PropertyEditorCategory convertMapToCategory( Map<String, List<String>> map ) {
        if ( map != null && !map.keySet().isEmpty() ) {
            String categoryName = map.keySet().iterator().next();
            PropertyEditorCategory category = new PropertyEditorCategory( categoryName );
            List<String> fields = map.get( categoryName );
            for ( String field : fields ) {
                category.withField( new PropertyEditorFieldInfo( field, PropertyEditorType.TEXT ) );
            }

            return category;
        }
        return null;
    }

}
