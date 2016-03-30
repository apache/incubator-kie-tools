/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

import java.util.Date;

import org.drools.workbench.jcr2vfsmigration.xml.model.asset.AssetType;
import org.drools.workbench.jcr2vfsmigration.xml.model.asset.AttachmentAsset;
import org.drools.workbench.jcr2vfsmigration.xml.model.asset.BusinessRuleAsset;
import org.drools.workbench.jcr2vfsmigration.xml.model.asset.DataModelAsset;
import org.drools.workbench.jcr2vfsmigration.xml.model.asset.GuidedDecisionTableAsset;
import org.drools.workbench.jcr2vfsmigration.xml.model.asset.PlainTextAsset;
import org.drools.workbench.jcr2vfsmigration.xml.model.asset.XmlAsset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

public class XmlAssetFormat extends AbstractXmlAssetFormat {

    private static final Logger logger = LoggerFactory.getLogger(XmlAssetFormat.class);

    private static PlainTextAssetFormat ptaf = new PlainTextAssetFormat();
    private static AttachmentAssetFormat aaf = new AttachmentAssetFormat();
    private static BusinessRuleAssetFormat braf = new BusinessRuleAssetFormat();
    private static GuidedDecisionTableAssetFormat gdtaf = new GuidedDecisionTableAssetFormat();
    private static DataModelAssetFormat dmaf = new DataModelAssetFormat();

    @Override
    public String formatAssetAsString( XmlAsset xmlAsset ) {

        switch ( xmlAsset.getAssetType() ) {
            case ENUMERATION:
            case DSL:
            case DSL_TEMPLATE_RULE:
            case RULE_TEMPLATE:
            case FORM_DEFINITION:
            case SPRING_CONTEXT:
            case SERVICE_CONFIG:
            case WORKITEM_DEFINITION:
            case CHANGE_SET:
            case RULE_FLOW_RF:
            case BPMN_PROCESS:
            case BPMN2_PROCESS:
            case FTL:
            case JSON:
            case FW:

            case SCORECARD_GUIDED:

            case TEST_SCENARIO:

            case DRL :
            case FUNCTION: return ptaf.doFormat( ( PlainTextAsset ) xmlAsset );

            case DECISION_SPREADSHEET_XLS:
            case SCORECARD_SPREADSHEET_XLS:
            case PNG:
            case GIF:
            case JPG:
            case PDF:
            case DOC:
            case ODT: return aaf.doFormat( ( AttachmentAsset ) xmlAsset );

            case BUSINESS_RULE: return braf.doFormat( ( BusinessRuleAsset ) xmlAsset );

            case DECISION_TABLE_GUIDED: return gdtaf.doFormat( ( GuidedDecisionTableAsset ) xmlAsset );

            case DRL_MODEL: return dmaf.doFormat( ( DataModelAsset ) xmlAsset );

            case UNSUPPORTED:

            default: {
                logger.info( "      Formatting asset with type " + xmlAsset.getAssetType() + " into attachment asset" );
                return aaf.doFormat( ( AttachmentAsset ) xmlAsset );
            }
        }
    }

    @Override
    public XmlAsset parseStringToXmlAsset( String name,
            String format,
            String lastContributor,
            String checkinComment,
            Date lastModified,
            Node assetNode ) {

        switch ( AssetType.getByType( format ) ) {
            case ENUMERATION:
            case DSL:
            case DSL_TEMPLATE_RULE:
            case RULE_TEMPLATE:
            case FORM_DEFINITION:
            case SPRING_CONTEXT:
            case SERVICE_CONFIG:
            case WORKITEM_DEFINITION:
            case CHANGE_SET:
            case RULE_FLOW_RF:
            case BPMN_PROCESS:
            case BPMN2_PROCESS:
            case FTL:
            case JSON:
            case FW:

            case SCORECARD_GUIDED:

            case TEST_SCENARIO:

            case DRL:
            case FUNCTION: return ptaf.doParse( name, format, lastContributor, checkinComment, lastModified, assetNode );

            case DECISION_SPREADSHEET_XLS:
            case SCORECARD_SPREADSHEET_XLS:
            case PNG:
            case GIF:
            case JPG:
            case PDF:
            case DOC:
            case ODT: return aaf.doParse( name, format, lastContributor, checkinComment, lastModified, assetNode );

            case BUSINESS_RULE: return braf.doParse( name, format, lastContributor, checkinComment, lastModified, assetNode );

            case DECISION_TABLE_GUIDED: return gdtaf.doParse( name, format, lastContributor, checkinComment, lastModified, assetNode );

            case DRL_MODEL: return dmaf.doParse( name, format, lastContributor, checkinComment, lastModified, assetNode );

            case UNSUPPORTED:

            default: {
                logger.info( "      Attempting to parse asset [{}.{}] into attachment asset.", name, format );
                return aaf.doParse( name, format, lastContributor, checkinComment, lastModified, assetNode );
            }
        }
    }
}
