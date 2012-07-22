package org.drools.guvnor.server.builder;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.java.nio.file.DirectoryStream;
import org.drools.java.nio.file.Files;
import org.drools.java.nio.file.Path;
import org.drools.lang.dsl.DSLTokenizedMappingFile;

public class DSLLoaderVFS {

    private static final Charset UTF_8 = Charset.forName("UTF-8");
/*    
    public static List<DSLTokenizedMappingFile> loadDSLMappingFiles(Path packageRootDir) {
        return loadDSLMappingFiles(packageRootDir, new BRMSPackageBuilder.DSLErrorEvent() {
            public void recordError(AssetItem asset,
                                    String message) {
                // ignore at this point...
            }
        });
    }*/
/*
    public static List<DSLTokenizedMappingFile> loadDSLMappingFiles(Path packageRootDir,
                                                                    BRMSPackageBuilder.DSLErrorEvent dslErrorEvent) {
        return loadDSLMappingFiles(packageItem.listAssetsWithVersionsSpecifiedByDependenciesByFormat(AssetFormats.DSL), dslErrorEvent);
    }
*/
    public static List<DSLTokenizedMappingFile> loadDSLMappingFiles(Path packageRootDir/*, BRMSPackageBuilder.DSLErrorEvent dslErrorEvent*/) {
        List<DSLTokenizedMappingFile> result = new ArrayList<DSLTokenizedMappingFile>();

        final DirectoryStream<Path> paths = Files.newDirectoryStream(packageRootDir);
        for ( final Path assetPath : paths ) {
            if(assetPath.getFileName().endsWith(AssetFormats.DSL)) {
                addAsset(/*dslErrorEvent, */result, assetPath);
            }
        }
/*        while (assetItemIterator.hasNext()) {
            addAsset(dslErrorEvent, result, assetItemIterator.next());
        }*/

        return result;
    }

    private static void addAsset(/*BRMSPackageBuilder.DSLErrorEvent dslErrorEvent,*/
                                 List<DSLTokenizedMappingFile> result,
                                 Path assetPath) {
        //if (!assetItem.getDisabled()) {
            final DSLTokenizedMappingFile file = new DSLTokenizedMappingFile();
            try {
                List<String> lines = Files.readAllLines( assetPath, UTF_8 );
                final StringBuilder sb = new StringBuilder();
                if (lines != null ){
                    for (final String s : lines) {
                        sb.append(s).append('\n');
                    }
                }

                if (file.parseAndLoad(new StringReader(sb.toString()))) {
                    result.add(file);
                } else {
                    //logErrors(dslErrorEvent, assetItem, file);
                }

            } catch (IOException e) {
                //throw new RulesRepositoryException(e);
            }
        //}
    }
/*
    private static void logErrors(BRMSPackageBuilder.DSLErrorEvent dslErrorEvent, AssetItem assetItem, DSLTokenizedMappingFile file) {
        for (Object o : file.getErrors()) {
        	
        	if(o instanceof DSLMappingParseException){
	            DSLMappingParseException dslMappingParseException = (DSLMappingParseException) o;
	            dslErrorEvent.recordError(assetItem, "Line " + dslMappingParseException.getLine() + " : " + dslMappingParseException.getMessage());
        	}else if(o instanceof Exception){
        		Exception excp = (Exception)o;
        		dslErrorEvent.recordError(assetItem, "Exception "+ excp.getClass()+" "+ excp.getMessage()+" "+excp.getCause());
        	}else{
        		dslErrorEvent.recordError(assetItem, "Uncategorized error "+o);
        	}
        }
    }*/
}