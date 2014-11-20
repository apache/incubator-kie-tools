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
package org.drools.workbench.jcr2vfsmigration.vfsImport;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.drools.workbench.jcr2vfsmigration.jcrExport.CategoryExporter;
import org.drools.workbench.jcr2vfsmigration.migrater.util.MigrationPathManager;
import org.drools.workbench.jcr2vfsmigration.util.FileManager;
import org.guvnor.common.services.shared.metadata.CategoriesService;
import org.guvnor.common.services.shared.metadata.model.Categories;
import org.guvnor.common.services.shared.metadata.model.CategoryItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@ApplicationScoped
public class CategoryImporter {

    protected static final Logger logger = LoggerFactory.getLogger(CategoryImporter.class);

    @Inject
    FileManager fileManager;

    @Inject   
    CategoriesService categoriesService;

    @Inject
    protected MigrationPathManager migrationPathManager;
    
    public void importAll() {
        System.out.println( "  Category import started" );

        Document xml = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            xml = db.parse( fileManager.getCategoriesExportFile() );
            Node categoriesNode = xml.getElementsByTagName( CategoryExporter.CATEGORIES ).item( 0 );

            Categories vfsCategories = new Categories();
            loadChildCategories(categoriesNode.getChildNodes(), vfsCategories);

            categoriesService.save(migrationPathManager.generatePathForModule("categories.xml"), vfsCategories,null,"");

        } catch ( Exception e ) {
            e.printStackTrace();
        }

        System.out.println( "  Category import ended" );
    }
    
    private void loadChildCategories(NodeList categoryNodes, CategoryItem vfsCategoryItem) {

        for ( int i = 0; i < categoryNodes.getLength(); i++ ) {
            Node category = categoryNodes.item( i );
            if ( CategoryExporter.CATEGORY.equals( category.getNodeName() ) ) {
                NamedNodeMap attrs = category.getAttributes();
                if ( attrs != null ) {
                    String categoryName = attrs.getNamedItem( CategoryExporter.CATEGORY_NAME ).getNodeValue();
                    CategoryItem categoryItem = vfsCategoryItem.addChildren( categoryName, "" );
                    loadChildCategories( category.getChildNodes(), categoryItem );
                }
            }
        }
    }
}
