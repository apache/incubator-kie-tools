package org.drools.guvnor.server.builder;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.lang.dsl.DSLMappingParseException;
import org.drools.lang.dsl.DSLTokenizedMappingFile;
import org.drools.repository.AssetItem;
import org.drools.repository.ModuleItem;
import org.drools.repository.RulesRepositoryException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DSLLoader {

    public static List<DSLTokenizedMappingFile> loadDSLMappingFiles(ModuleItem packageItem) {
        return loadDSLMappingFiles(packageItem, new BRMSPackageBuilder.DSLErrorEvent() {
            public void recordError(AssetItem asset,
                                    String message) {
                // ignore at this point...
            }
        });
    }

    public static List<DSLTokenizedMappingFile> loadDSLMappingFiles(ModuleItem packageItem,
                                                                    BRMSPackageBuilder.DSLErrorEvent dslErrorEvent) {
        return loadDSLMappingFiles(packageItem.listAssetsByFormat(AssetFormats.DSL), dslErrorEvent);
    }

    static List<DSLTokenizedMappingFile> loadDSLMappingFiles(Iterator<AssetItem> assetItemIterator, BRMSPackageBuilder.DSLErrorEvent dslErrorEvent) {
        List<DSLTokenizedMappingFile> result = new ArrayList<DSLTokenizedMappingFile>();

        while (assetItemIterator.hasNext()) {
            addAsset(dslErrorEvent, result, assetItemIterator.next());
        }

        return result;
    }

    private static void addAsset(BRMSPackageBuilder.DSLErrorEvent dslErrorEvent,
                                 List<DSLTokenizedMappingFile> result,
                                 AssetItem assetItem) {
        if (!assetItem.getDisabled()) {
            DSLTokenizedMappingFile file = new DSLTokenizedMappingFile();
            try {
                if (file.parseAndLoad(new StringReader(assetItem.getContent()))) {
                    result.add(file);
                } else {
                    logErrors(dslErrorEvent, assetItem, file);
                }

            } catch (IOException e) {
                throw new RulesRepositoryException(e);
            }
        }
    }

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
    }
}