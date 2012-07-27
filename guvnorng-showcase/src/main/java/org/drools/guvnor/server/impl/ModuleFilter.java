/*
 * Copyright 2005 JBoss Inc
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

package org.drools.guvnor.server.impl;

import org.drools.guvnor.client.rpc.Module;
/*import org.drools.guvnor.server.security.ModuleUUIDType;
import org.jboss.seam.security.Identity;*/

public class ModuleFilter extends AbstractFilter<Module> {

    public ModuleFilter(/*Identity identity*/) {
        super( Module.class/*, identity */);
    }

    @Override
    protected boolean checkPermission(final Module module,
                                      final String action) {
    	//JLIU: TODO:
    	return true;
    	
/*        return identity.hasPermission( new ModuleUUIDType( module.getUuid() ),
                                                  action );*/
    }

}
