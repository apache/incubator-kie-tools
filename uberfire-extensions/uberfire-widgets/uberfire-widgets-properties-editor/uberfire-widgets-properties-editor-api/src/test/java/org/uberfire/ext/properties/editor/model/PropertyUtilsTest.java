/*
 * Copyright 2015 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.properties.editor.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;


public class PropertyUtilsTest {


    @Test
    public void convertMapToCategoryNullCase() {
        assertNull( PropertyUtils.convertMapToCategory( null ) );
        assertNull( PropertyUtils.convertMapToCategory( new HashMap<String, List<String>>() ) );
    }

    @Test
    public void convertMapToCategory() {

        HashMap<String, List<String>> categoryMap = new HashMap<String, List<String>>();
        List<String> categoryFields = new ArrayList<String>();
        categoryFields.add( "field1" );
        categoryFields.add( "field2" );
        categoryMap.put( "Category1", categoryFields );

        PropertyEditorCategory category = PropertyUtils.convertMapToCategory( categoryMap );
        assertNotNull( category );
        assertEquals( category.getFields().size(), 2 );
        assertEquals( category.getFields().get(0).getLabel(), "field1" );
        assertEquals( category.getFields().get( 1 ).getLabel(), "field2" );
    }


}
