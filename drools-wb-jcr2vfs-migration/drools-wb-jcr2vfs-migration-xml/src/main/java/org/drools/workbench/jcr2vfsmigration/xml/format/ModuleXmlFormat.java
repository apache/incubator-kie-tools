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

import org.drools.workbench.jcr2vfsmigration.xml.model.Module;
import org.drools.workbench.jcr2vfsmigration.xml.model.ModuleType;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ModuleXmlFormat implements XmlFormat<Module> {

    public static final String MODULE = "module";
    public static final String MODULE_UUID = "uuid";
    public static final String MODULE_TYPE = "type";
    public static final String MODULE_NAME = "name";

    @Override
    public void format( StringBuilder sb, Module module ) {
        if ( sb == null || module == null ) throw new IllegalArgumentException( "No output or Module specified" );

        sb.append( "<" ).append( MODULE ).append( ">" );
        sb.append( "<" ).append( MODULE_UUID ).append( ">" ).append( module.getUuid() ).append( "</" ).append( MODULE_UUID ).append( ">" );
        sb.append( "<" ).append( MODULE_TYPE ).append( ">" ).append( module.getType() ).append( "</" ).append( MODULE_TYPE ).append( ">" );
        sb.append( "<" ).append( MODULE_NAME ).append( ">" ).append( module.getName() ).append( "</" ).append( MODULE_NAME ).append( ">" );
        sb.append( "</" ).append( MODULE ).append( ">" );
        System.out.format( "Module [%s] exported. %n", module.getName() );
    }

    @Override
    public Module parse( Node moduleNode ) {
        if ( moduleNode == null || !MODULE.equals( moduleNode.getNodeName() ) ) throw new IllegalArgumentException( "No input module node specified for parsing" );

        String name = null;
        String uuid = null;
        ModuleType type = null;

        NodeList moduleAttributes = moduleNode.getChildNodes();
        for ( int i = 0; i < moduleAttributes.getLength(); i++ ) {
            Node attributeNode = moduleAttributes.item( i );
            String nodeContent = attributeNode.getTextContent();
            if ( MODULE_NAME.equalsIgnoreCase( attributeNode.getNodeName() ) ) {
                name = nodeContent;
            } else if ( MODULE_UUID.equalsIgnoreCase( attributeNode.getNodeName() ) ) {
                uuid = nodeContent;
            } else if ( MODULE_TYPE.equalsIgnoreCase( attributeNode.getNodeName() ) ) {
                type = ModuleType.getByName( nodeContent );
            }
        }
        return new Module( type, uuid, name );
    }
}
