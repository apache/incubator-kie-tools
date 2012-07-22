/*
 * Copyright 2012 JBoss Inc
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

package org.drools.guvnor.server.util;

import org.drools.guvnor.client.common.AssetFormats;
//import org.drools.guvnor.server.builder.BRMSPackageBuilder;
import org.drools.guvnor.server.builder.BRMSPackageBuilder;
import org.drools.guvnor.server.builder.ClassLoaderBuilder;
import org.drools.guvnor.server.builder.DSLLoader;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.server.rules.SuggestionCompletionLoader;
import org.drools.lang.dsl.DSLTokenizedMappingFile;
import org.drools.repository.AssetItem;
import org.drools.repository.AssetItemIterator;
import org.drools.repository.ModuleItem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This decorates the suggestion completion loader with BRMS specific stuff.
 */
public class BRMSSuggestionCompletionLoader extends SuggestionCompletionLoader {

    public BRMSSuggestionCompletionLoader() {
        super();
    }

    public BRMSSuggestionCompletionLoader(ClassLoader classLoader) {
        super(classLoader);
    }

    public SuggestionCompletionEngine getSuggestionEngine(ModuleItem packageItem,
                                                          String droolsHeader) {

        StringBuilder buf = new StringBuilder();
        AssetItemIterator it = packageItem.listAssetsByFormat(AssetFormats.DRL_MODEL);
        while (it.hasNext()) {
            AssetItem as = it.next();
            buf.append(as.getContent());
            buf.append('\n');
        }


        ClassLoaderBuilder classLoaderBuilder = new ClassLoaderBuilder(packageItem.listAssetsByFormat(AssetFormats.MODEL));

//        String packageName = packageItem.getName();
//        return super.getSuggestionEngine("package " + packageName + "\n\n" + droolsHeader + "\n" + buf.toString(),
        return super.getSuggestionEngine(droolsHeader + "\n" + buf.toString(),
                classLoaderBuilder.getJarInputStreams(),
                getDSLMappingFiles(packageItem),
                getDataEnums(packageItem));
    }

    public SuggestionCompletionEngine getSuggestionEngine(ModuleItem pkg) {
        return getSuggestionEngine(pkg,
                DroolsHeader.getDroolsHeader(pkg));
    }

    @SuppressWarnings("rawtypes")
    private List<String> getDataEnums(ModuleItem pkg) {
        Iterator it = pkg.listAssetsByFormat(new String[]{AssetFormats.ENUMERATION});
        List<String> list = new ArrayList<String>();
        while (it.hasNext()) {
            AssetItem item = (AssetItem) it.next();
            list.add(item.getContent());
        }
        return list;
    }

    private List<DSLTokenizedMappingFile> getDSLMappingFiles(ModuleItem pkg) {
        return DSLLoader.loadDSLMappingFiles(pkg,
                new BRMSPackageBuilder.DSLErrorEvent() {

                    public void recordError(AssetItem asset,
                                            String message) {
                        getErrors().add(asset.getName() + " : " + message);
                    }
                });
    }
}
