/**
 * Copyright 2012 JBoss Inc
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

package org.kie.workbench.common.services.datamodeller.codegen;


import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.driver.ModelDriverListener;
import org.kie.workbench.common.services.datamodeller.driver.impl.DataModelOracleDriver;
import org.kie.workbench.common.services.datamodeller.util.FileHashingUtils;

public class FileHashingTest {

    @Test
    public void testFilesHashing() {

        DataModelOracleDriver dataModelOracleDriver = DataModelOracleDriver.getInstance( );
        DataModel dataModel = dataModelOracleDriver.createModel();

        DataObject dataObject;

        final int size = 10;
        for (int i = 1; i < size; i++) {
            dataObject = dataModel.addDataObject("test", "Test"+i);
            for (int j = 1; j < size; j++) {
                dataObject.addProperty("property"+j, "java.lang.String");
            }
        }

        final String[] generatedFiles = new String[size];
        final String[] hashedFiles = new String[size];
        final String[] hashes = new String[size];

        try {
            dataModelOracleDriver.generateModel(dataModel, new ModelDriverListener() {

                int i = 0;

                @Override
                public void assetGenerated(String fileName, String content) {
                    generatedFiles[i] = content.trim();
                    hashedFiles[i] = FileHashingUtils.setFileHashValue(content.trim());
                    hashes[i] = FileHashingUtils.md5Hex(content.trim());
                    if (i > 7) {
                        hashedFiles[i] = hashedFiles[i] + "raka";
                    }
                    i++;
                }
            });
        } catch (Exception e) {

        }

        for (int i = 0; i < size; i++) {
            if (i <= 7) {
                assertEquals("File doesn't verify hash", true, FileHashingUtils.verifiesHash(hashedFiles[i]));
                assertEquals("Extracted hash doesn't equals generated hash", true, hashes[i].equals(FileHashingUtils.extractFileHashValue(hashedFiles[i])));
            } else {
                assertEquals("File should not verify hash", false, FileHashingUtils.verifiesHash(hashedFiles[i]));
            }
        }
    }
}