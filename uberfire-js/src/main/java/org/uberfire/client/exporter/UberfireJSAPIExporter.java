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

package org.uberfire.client.exporter;

import java.util.Collection;
import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.IOCBeanDef;

@ApplicationScoped
public class UberfireJSAPIExporter {

    @AfterInitialization
    public void export() {
        Collection<IOCBeanDef<UberfireJSExporter>> jsAPIs = IOC.getBeanManager().lookupBeans( UberfireJSExporter.class );
        for ( IOCBeanDef<UberfireJSExporter> bean : jsAPIs ) {
            UberfireJSExporter jsAPI = bean.getInstance();
            jsAPI.export();
        }
    }

}
