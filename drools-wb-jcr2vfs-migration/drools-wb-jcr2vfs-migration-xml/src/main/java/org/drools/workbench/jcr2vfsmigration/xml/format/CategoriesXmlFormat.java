/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.jcr2vfsmigration.xml.format;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.drools.workbench.jcr2vfsmigration.xml.model.Categories;
import org.drools.workbench.jcr2vfsmigration.xml.model.Category;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CategoriesXmlFormat implements XmlFormat<Categories> {

    public static final String CATEGORIES = "categories";

    private CategoryXmlFormat categoryXmlFormat;

    public CategoriesXmlFormat() {
    }

    @Override
    public void format( StringBuilder sb, Categories categories ) {
        if ( sb == null || categories == null ) throw new IllegalArgumentException( "No output or Categories specified" );

        initialize();
        sb.append( LT ).append( CATEGORIES ).append( GT );
        for ( Iterator<Category> it = categories.getCategories().iterator(); it.hasNext(); ) {
            categoryXmlFormat.format( sb, it.next() );
        }
        sb.append( LT_SLASH ).append( CATEGORIES ).append( GT );
    }

    @Override
    public Categories parse( Node categoriesNode ) {
        if ( categoriesNode == null || !CATEGORIES.equals( categoriesNode.getNodeName() ) ) throw new IllegalArgumentException( "No input categories node specified for parsing" );

        initialize();
        Collection<Category> cCategories = new ArrayList<Category>( 5 );

        NodeList categoryNodes = categoriesNode.getChildNodes();
        for ( int i = 0; i < categoryNodes.getLength(); i++ ) {
            Node categoryNode = categoryNodes.item( i );
            if ( categoryNode != null ) {
                Category category = categoryXmlFormat.parse( categoryNode );
                cCategories.add( category );
            }
        }
        return new Categories( cCategories );
    }

    // Don't do this in constructor: will lead to stackoverflow because of recursive calls
    private void initialize() {
        if ( categoryXmlFormat == null ) categoryXmlFormat = new CategoryXmlFormat();
    }
}
