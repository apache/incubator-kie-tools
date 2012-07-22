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
import org.drools.guvnor.server.builder.ClassLoaderBuilder;
import org.drools.guvnor.server.builder.ClassLoaderBuilderVFS;
import org.drools.guvnor.server.builder.DSLLoader;
import org.drools.guvnor.server.builder.DSLLoaderVFS;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.server.rules.SuggestionCompletionLoader;
import org.drools.java.nio.file.DirectoryStream;
import org.drools.java.nio.file.Files;
import org.drools.java.nio.file.Path;
import org.drools.lang.dsl.DSLTokenizedMappingFile;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * This decorates the suggestion completion loader with BRMS specific stuff.
 */
public class BRMSSuggestionCompletionLoaderVFS extends SuggestionCompletionLoader {

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    public BRMSSuggestionCompletionLoaderVFS() {
        super();
    }

    public BRMSSuggestionCompletionLoaderVFS(ClassLoader classLoader) {
        super(classLoader);
    }

    public SuggestionCompletionEngine getSuggestionEngine(Path packageRootDir,
                                                          String droolsHeader) {

        StringBuilder buf = new StringBuilder();
        
        DirectoryStream<Path> paths = Files.newDirectoryStream(packageRootDir);
        for ( final Path assetPath : paths ) {
            if(assetPath.getFileName().endsWith(AssetFormats.DRL_MODEL)) {
                final List<String> lines = Files.readAllLines( assetPath, UTF_8 );
                final StringBuilder sb = new StringBuilder();
                if (lines != null ){
                    for (final String s : lines) {
                        sb.append(s).append('\n');
                    }
                }


                buf.append(sb.toString());
                buf.append('\n');
            }
        }
        
/*      AssetItemIterator it = packageItem.listAssetsByFormat(AssetFormats.DRL_MODEL);
        while (it.hasNext()) {
            AssetItem as = it.next();
            buf.append(as.getContent());
            buf.append('\n');
        }
*/

        ClassLoaderBuilderVFS classLoaderBuilder = new ClassLoaderBuilderVFS(packageRootDir);

//        String packageName = packageItem.getName();
//        return super.getSuggestionEngine("package " + packageName + "\n\n" + droolsHeader + "\n" + buf.toString(),
        return super.getSuggestionEngine(droolsHeader + "\n" + buf.toString(),
                classLoaderBuilder.getJarInputStreams(),
                getDSLMappingFiles(packageRootDir),
                getDataEnums(packageRootDir));
    }
/*
    public SuggestionCompletionEngine getSuggestionEngine(ModuleItem pkg) {
        return getSuggestionEngine(pkg,
                DroolsHeader.getDroolsHeader(pkg));
    }*/

    @SuppressWarnings("rawtypes")
    private List<String> getDataEnums(Path packageRootDir) {
        List<String> list = new ArrayList<String>();
        DirectoryStream<Path> paths = Files.newDirectoryStream(packageRootDir);
        for ( final Path assetPath : paths ) {
            if(assetPath.getFileName().endsWith(AssetFormats.ENUMERATION)) {
                final List<String> lines = Files.readAllLines( assetPath, UTF_8 );
                final StringBuilder sb = new StringBuilder();
                if (lines != null ){
                    for (final String s : lines) {
                        sb.append(s).append('\n');
                    }
                }
                list.add(sb.toString());
            }
        }
        
/*        Iterator it = pkg.listAssetsByFormat(new String[]{AssetFormats.ENUMERATION});
        List<String> list = new ArrayList<String>();
        while (it.hasNext()) {
            AssetItem item = (AssetItem) it.next();
            list.add(item.getContent());
        }*/
        return list;
    }

    private List<DSLTokenizedMappingFile> getDSLMappingFiles(Path packageRootDir) {
        return DSLLoaderVFS.loadDSLMappingFiles(packageRootDir/*,
                new BRMSPackageBuilder.DSLErrorEvent() {

                    public void recordError(AssetItem asset,
                                            String message) {
                        getErrors().add(asset.getName() + " : " + message);
                    }
                }*/);
    }
}
