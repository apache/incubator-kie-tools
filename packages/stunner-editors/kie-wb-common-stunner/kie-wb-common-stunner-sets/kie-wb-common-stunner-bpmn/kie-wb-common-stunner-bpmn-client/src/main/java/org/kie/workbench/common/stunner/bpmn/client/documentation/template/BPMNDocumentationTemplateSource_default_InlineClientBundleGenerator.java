/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.client.documentation.template;

import org.gwtproject.resources.client.ResourcePrototype;
import org.gwtproject.resources.client.TextResource;

public class BPMNDocumentationTemplateSource_default_InlineClientBundleGenerator implements BPMNDocumentationTemplateSource {
  private static BPMNDocumentationTemplateSource_default_InlineClientBundleGenerator _instance0 = new BPMNDocumentationTemplateSource_default_InlineClientBundleGenerator();
  private void documentationTemplateInitializer() {
    documentationTemplate = new TextResource() {
      // jar:file:/home/treblereel/.m2/repository/org/kie/kogito/kie-wb-common-stunner-bpmn-client/8.2.1-SNAPSHOT/kie-wb-common-stunner-bpmn-client-8.2.1-SNAPSHOT.jar!/org/kie/workbench/common/stunner/bpmn/client/documentation/template/process-documentation-template.html
      public String getText() {
        return "<!--\n  ~ Copyright 2018 Red Hat, Inc. and/or its affiliates.\n  ~\n  ~ Licensed under the Apache License, Version 2.0 (the \"License\");\n  ~ you may not use this file except in compliance with the License.\n  ~ You may obtain a copy of the License at\n  ~\n  ~     http://www.apache.org/licenses/LICENSE-2.0\n  ~\n  ~ Unless required by applicable law or agreed to in writing, software\n  ~ distributed under the License is distributed on an \"AS IS\" BASIS,\n  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n  ~ See the License for the specific language governing permissions and\n  ~ limitations under the License.\n  -->\n\n<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">\n\n    <title>Process Documentation - {{process.general.id}}</title>\n\n    <link type=\"text/css\" rel=\"stylesheet\" href=\"{{&moduleName}}/css/patternfly.min.css\" media=\"print\"/>\n\n    <style>\n        .categoryCaption {\n            padding-left: 5px;\n            vertical-align: top;\n            display: inline-block;\n        }\n\n        .categoryIcon {\n            width: 20px;\n            display: inline-block;\n        }\n\n        .elementIcon {\n            width: 20px;\n            background: transparent !important;\n            vertical-align: top;\n        }\n\n        .diagramImage {\n            width: 100%;\n            height: 100%;\n        }\n\n        #diagramImageDiv > svg {\n            max-width: 100%;\n            max-height: 100%;\n        }\n\n        #pagecontainer {\n            margin-left: 20px;\n        }\n\n        .textContent{\n            white-space:pre; /* or pre-wrap if you want wrapping to still work. */\n        }\n\n    </style>\n</head>\n<body>\n<div id=\"pagecontainer\">\n    <p>\n        <p id=\"pagecontainercore\">\n\n        <p>\n        <h1 class=\"page-header\" id=\"process-documentation\">Process Documentation</h1>\n        </p>\n\n        <h2 id=\"overview\"><span class=\"badge badge-inverse\">1.0</span> Process Overview</h2>\n\n        <p>\n        <h3 id=\"process-info\"><span class=\"badge badge-inverse\">1.1</span> General</h3>\n        </p>\n\n        <!-- Process info -->\n        {{#process}}\n        {{#general}}\n        <p id=\"processinfocontent\">\n        <table class=\"table table-inverse\">\n            <tbody class=\"textContent\">\n            <tr>\n                <td><b>ID</b></td>\n                <td>{{id}}</td>\n            </tr>\n            <tr>\n                <td><b>Package</b></td>\n                <td>{{pkg}}</td>\n            </tr>\n            <tr>\n                <td><b>Name</b></td>\n                <td>{{name}}</td>\n            </tr>\n            <tr>\n                <td><b>Is executable</b></td>\n                <td>{{isExecutable}}</td>\n            </tr>\n            <tr>\n                <td><b>Is AdHoc</b></td>\n                <td>{{isAdhoc}}</td>\n            </tr>\n            <tr>\n                <td><b>Version</b></td>\n                <td>{{version}}</td>\n            </tr>\n            <tr>\n                <td><b>Documentation</b></td>\n                <td>{{&documentation}}</td>\n            </tr>\n            <tr>\n                <td><b>Description</b></td>\n                <td>{{&description}}</td>\n            </tr>\n            </tbody>\n        </table>\n        </p>\n        {{/general}}\n        <!-- End Process info -->\n\n        <!--Imports -->\n        {{#imports}}\n        <p>\n        <h3 id=\"process-imports\"><span class=\"badge badge-inverse\">1.2</span> Imports</h3></p>\n\n        <p id=\"importscontent\">\n\n            <ul class=\"list-group\" {{noImportsHidden}}>\n                <li class=\"list-group-item\">\n                    <span class=\"pull-xs-right\">No imports</span>\n                </li>\n            </ul>\n\n            <table class=\"table table-inverse\" {{importsTableHidden}}>\n                <thead>\n                    <tr {{importsTableHidden}}>\n                        <th width=\"100%\" colspan=\"2\" style=\"text-align: center\">Data Type Imports: {{totalDefaultImports}}</th>\n                    </tr>\n                    <tr {{defaultImportsHidden}}>\n                        <th width=\"100%\" colspan=\"2\">Class Name</th>\n                    </tr>\n                </thead>\n                <tbody {{defaultImportsHidden}}>\n                    {{#defaultImports}}\n                    <tr>\n                        <td colspan=\"2\">{{className}}</td>\n                    </tr>\n                    {{/defaultImports}}\n                </tbody>\n                <thead>\n                    <tr {{importsTableHidden}}>\n                        <th width=\"100%\" colspan=\"2\" style=\"text-align: center\">WSDL Imports: {{totalWSDLImports}}</th>\n                    </tr>\n                    <tr {{wsdlImportsHidden}}>\n                        <th width=\"50%\">Location</th>\n                        <th width=\"50%\">Namespace</th>\n                    </tr>\n                </thead>\n                <tbody {{wsdlImportsHidden}}>\n                    {{#wsdlImports}}\n                    <tr>\n                        <td>{{location}}</td>\n                        <td>{{namespace}}</td>\n                    </tr>\n                    {{/wsdlImports}}\n                </tbody>\n            </table>\n\n        </p>\n        {{/imports}}\n\n        <!-- DataTotal -->\n        {{#dataTotal}}\n        <p id=\"processTotal\">\n        <h3 id=\"process-totals\"><span class=\"badge badge-inverse\">1.3</span> Data Totals</h3></p>\n\n        <p id=\"processdatatotals\">\n        <ul class=\"list-group\">\n            <li class=\"list-group-item\">\n                Variables\n                <span class=\"pull-xs-right\">{{totalVariables}}</span>\n            </li>\n        </ul>\n        </p>\n\n        <!--Variables -->\n        <p>\n        <h3 id=\"process-vars\"><span class=\"badge badge-inverse\">1.4</span> Variables</h3></p>\n\n        <p id=\"processvarcontent\">\n        <table class=\"table table-inverse\">\n            <thead>\n            <tr>\n                <th width=\"10%\">Name</th>\n                <th width=\"50%\">Type</th>\n                <th width=\"40%\">KPI</th>\n\n            </tr>\n            </thead>\n            <tbody>\n\n            {{#tripplets}}\n            <tr>\n                <td>{{name}}</td>\n                <td>{{type}}</td>\n                <td>{{kpi}}</td>\n            </tr>\n            {{/tripplets}}\n\n\n            </tbody>\n        </table>\n        </p>\n\n        <!-- End DataTotal -->\n        {{/dataTotal}}\n\n        <!-- End Process Overview -->\n        {{/process}}\n\n\n        {{#elementsDetails}}\n        <p id=\"elementsDetails\">\n\n        <h2 id=\"element-details\"><span class=\"badge badge-inverse\">2.0</span> Element Details</h2>\n\n        <p>\n        <h3 id=\"element-totals\"><span class=\"badge badge-inverse\">2.1</span> Totals</h3>\n        </p>\n\n        <p id=\"processelementtotals\">\n\n        <ul class=\"list-group\">\n            {{#totals}}\n            <li class=\"list-group-item\">\n                <span class=\"categoryIcon\">{{&typeIcon}}</span>\n                <span class=\"pull-xs-right categoryCaption\">{{type}}</span>\n                <span class=\"pull-xs-right categoryCaption\">{{quantity}}</span>\n            </li>\n            {{/totals}}\n        </ul>\n\n        </p>\n\n        <p>\n        <h3 id=\"elemen-info\"><span class=\"badge badge-inverse\">2.2</span> Elements</h3>\n        </p>\n\n        <p id=\"processelementdetails\">\n\n            {{#totals}}\n\n        <div class=\"list-group\">\n                  <span class=\"list-group-item\">\n <h3 class=\"list-group-item-heading\">{{type}}</h3>\n                    </span>\n        </div>\n\n        {{#elements}}\n\n        <p class=\"list-group-item-text\">\n        <div id=\"_BFEB21E9-AD01-4F45-9D73-70911579BD72\" class=\"panel panel-default\">\n            <table class=\"table table-inverse\">\n\n                <thead class=\"panel-heading\">\n                <tr>\n                    <th class=\"elementIcon\">\n                        {{&icon}}\n                    </th>\n                    <th width=\"50%\">\n                        <b>Name:</b> {{name}}\n                    </th>\n                    <th width=\"50%\">\n                        <b>Type:</b> {{title}}\n                    </th>\n                </tr>\n                </thead>\n\n                <thead>\n                <tr>\n                    <th colspan=\"2\">Property Name</th>\n                    <th>Property Value</th>\n                </tr>\n                </thead>\n                <tbody class=\"textContent\">\n                {{#properties}}\n                <tr>\n                    <td colspan=\"2\">{{key}}</td>\n                    <td>{{&value}}</td>\n                </tr>\n                {{/properties}}\n\n                </tbody>\n            </table>\n        </div>\n        </p>\n        {{/elements}}\n        {{/totals}}\n\n        </p>\n\n        </p>\n        {{/elementsDetails}}\n\n        <div id=\"diagramImageDiv\" class=\"diagramImage\">\n            <h1 id=\"process-image\"><span class=\"badge badge-inverse\">3.0</span> Process Image</h1>\n\n            {{&diagramImage}}\n\n        </div>\n    </div>\n</div>\n\n</body>\n</html>";
      }
      public String getName() {
        return "documentationTemplate";
      }
    }
    ;
  }
  private static class documentationTemplateInitializer {
    static {
      _instance0.documentationTemplateInitializer();
    }
    static TextResource get() {
      return documentationTemplate;
    }
  }
  public TextResource documentationTemplate() {
    return documentationTemplateInitializer.get();
  }
  private static java.util.HashMap<String, ResourcePrototype> resourceMap;
  private static TextResource documentationTemplate;
  
  public ResourcePrototype[] getResources() {
    return new ResourcePrototype[] {
      documentationTemplate(), 
    };
  }
  public ResourcePrototype getResource(String name) {
      if (resourceMap == null) {
        resourceMap = new java.util.HashMap<String, ResourcePrototype>();
        resourceMap.put("documentationTemplate", documentationTemplate());
      }
      return resourceMap.get(name);
  }
}
