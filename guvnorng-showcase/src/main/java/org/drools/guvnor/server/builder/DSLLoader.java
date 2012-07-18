package org.drools.guvnor.server.builder;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.vfs.Path;
import org.drools.guvnor.backend.VFSService;
import org.drools.java.nio.file.DirectoryStream;
import org.drools.lang.dsl.DSLMappingParseException;
import org.drools.lang.dsl.DSLTokenizedMappingFile;


import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

public class DSLLoader {
    @Inject
    static VFSService vfsService;
    
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

        DirectoryStream<Path> paths = vfsService.newDirectoryStream(packageRootDir);
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
            DSLTokenizedMappingFile file = new DSLTokenizedMappingFile();
            try {
                String content = vfsService.readAllString( assetPath );
                if (file.parseAndLoad(new StringReader(content))) {
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