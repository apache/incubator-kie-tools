/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.workbench.jcr2vfsmigration.export;

import org.assertj.core.api.Assertions;
import org.drools.guvnor.client.rpc.Module;
import org.drools.repository.AssetItem;
import org.drools.workbench.jcr2vfsmigration.jcrExport.asset.ExportContext;
import org.drools.workbench.jcr2vfsmigration.jcrExport.asset.PlainTextAssetWithPackagePropertyExporter;
import org.drools.workbench.jcr2vfsmigration.xml.model.asset.PlainTextAsset;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Calendar;

public class PlainTextAssetWithPackagePropertyExporterTest {

    private PlainTextAssetWithPackagePropertyExporter exporter = new PlainTextAssetWithPackagePropertyExporter();

    @Test
    // BZ-1319568
    public void testExportSingleStandaloneRule() {
        String technicalRule = "dialect \"mvel\"\n" +
                "agenda-group \"RULE GROUP myGroup\"\n" +
                "when\n" +
                "    list : ArrayList( )\n" +
                "then\n" +
                "    list.add(1)\n" +
                "    drools.setFocus(\"some-rule-group\");";

        for (String assetType : Arrays.asList("drl", "dslr")) {
            AssetItem assetItem = createAssetItemMock(technicalRule, assetType);

            Module jcrModule = Mockito.mock(Module.class);

            ExportContext exportContext = Mockito.mock(ExportContext.class);
            Mockito.when(exportContext.getJcrAssetItem()).thenReturn(assetItem);
            Mockito.when(exportContext.getJcrModule()).thenReturn(jcrModule);

            PlainTextAsset asset = exporter.export(exportContext);
            String expectedContentAfterMigration = "rule \"dummy-technical-rule\"\n\n" +
                    "dialect \"mvel\"\n" +
                    "agenda-group \"RULE GROUP myGroup\"\n" +
                    "when\n" +
                    "    list : ArrayList( )\n" +
                    "then\n" +
                    "    list.add(1)\n" +
                    "    drools.setFocus(\"some-rule-group\");\n\n" +
                    "end";
            Assertions.assertThat(asset.getContent()).isEqualTo(expectedContentAfterMigration);
        }
    }

    @Test
    public void testExportWholeRuleFile() {
        String technicalRule = "package org.rules\n\n" +
                "rule \"rule1\"\n" +
                "dialect \"mvel\"\n" +
                "agenda-group \"RULE GROUP myGroup\"\n" +
                "when\n" +
                "    list : ArrayList( )\n" +
                "then\n" +
                "    list.add(1)\n" +
                "    drools.setFocus(\"some-rule-group\");";

        for (String assetType : Arrays.asList("drl", "dslr")) {
            AssetItem assetItem = createAssetItemMock(technicalRule, assetType);

            ExportContext exportContext = Mockito.mock(ExportContext.class);
            Mockito.when(exportContext.getJcrAssetItem()).thenReturn(assetItem);

            PlainTextAsset asset = exporter.export(exportContext);
            // the content is expected to be migrated as is + new line character
            String expectedContentAfterMigration = technicalRule + "\n";
            Assertions.assertThat(asset.getContent()).isEqualTo(expectedContentAfterMigration);
        }
    }

    private AssetItem createAssetItemMock(String content, String type) {
        AssetItem assetItem = Mockito.mock(AssetItem.class);
        Mockito.when(assetItem.getContent()).thenReturn(content);
        Mockito.when(assetItem.getFormat()).thenReturn(type);
        Mockito.when(assetItem.getName()).thenReturn("dummy-technical-rule");
        Mockito.when(assetItem.getLastContributor()).thenReturn("admin");
        Mockito.when(assetItem.getCheckinComment()).thenReturn("comment");
        Mockito.when(assetItem.getLastModified()).thenReturn(Calendar.getInstance());
        return assetItem;
    }
}
