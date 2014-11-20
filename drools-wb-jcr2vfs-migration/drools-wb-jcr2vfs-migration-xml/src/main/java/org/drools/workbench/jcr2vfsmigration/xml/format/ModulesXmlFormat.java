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

import org.drools.workbench.jcr2vfsmigration.xml.model.Module;
import org.drools.workbench.jcr2vfsmigration.xml.model.Modules;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ModulesXmlFormat implements XmlFormat<Modules> {

    public static final String MODULES = "modules";

    private ModuleXmlFormat moduleXmlFormat;

    public ModulesXmlFormat() {
        this.moduleXmlFormat = new ModuleXmlFormat();
    }

    @Override
    public void format( StringBuilder sb, Modules modules ) {
        if ( sb == null || modules == null ) throw new IllegalArgumentException( "No output or Modules specified" );

        sb.append( "<" ).append( MODULES ).append( ">" );
        moduleXmlFormat.format( sb, modules.getGlobalModule() );
        for ( Iterator<Module> it = modules.getModules().iterator(); it.hasNext(); ) {
            moduleXmlFormat.format( sb, it.next() );
        }
        sb.append( "</" ).append( MODULES ).append( ">" );
    }

    @Override
    public Modules parse( Node modulesNode ) {
        if ( modulesNode == null || !MODULES.equals( modulesNode.getNodeName() ) ) throw new IllegalArgumentException( "No input modules node specified for parsing" );

        Module global = null;
        Collection<Module> normal = new ArrayList<Module>( 5 );

        NodeList moduleNodes = modulesNode.getChildNodes();
        for ( int i = 0; i < moduleNodes.getLength(); i++ ) {
            Node moduleNode = moduleNodes.item( i );
            if ( moduleNode != null ) {
                Module module = moduleXmlFormat.parse( moduleNode );
                switch ( module.getType() ) {
                    case GLOBAL: global = module; break;
                    default: normal.add( module );
                }
            }
        }
        return new Modules( global, normal );
    }
}
