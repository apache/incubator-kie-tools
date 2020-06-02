/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.util.Bpmn2ResourceImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.xmi.XMLLoad;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.XMLSave;
import org.eclipse.emf.ecore.xmi.impl.XMLLoadImpl;

public class JBPMBpmn2Resource extends Bpmn2ResourceImpl {

    public HashMap xmlNameToFeatureMap = new HashMap();

    public JBPMBpmn2Resource(URI uri) {
        super(uri);
        this.getDefaultLoadOptions().put(XMLResource.OPTION_DEFER_IDREF_RESOLUTION,
                                         true);
        this.getDefaultLoadOptions().put(XMLResource.OPTION_DISABLE_NOTIFY,
                                         true);
        this.getDefaultLoadOptions().put(XMLResource.OPTION_USE_XML_NAME_TO_FEATURE_MAP,
                                         xmlNameToFeatureMap);
        // Switch off DTD external entity processing
        Map parserFeatures = new HashMap();
        parserFeatures.put("http://xml.org/sax/features/external-general-entities",
                           false);
        parserFeatures.put("http://xml.org/sax/features/external-parameter-entities",
                           false);
        this.getDefaultLoadOptions().put(XMLResource.OPTION_PARSER_FEATURES,
                                         parserFeatures);
        this.getDefaultSaveOptions().put(XMLResource.OPTION_ENCODING,
                                         "UTF-8");
        this.getDefaultSaveOptions().put(XMLResource.OPTION_SKIP_ESCAPE,
                                         true);
        this.getDefaultSaveOptions().put(XMLResource.OPTION_PROCESS_DANGLING_HREF,
                                         XMLResource.OPTION_PROCESS_DANGLING_HREF_DISCARD);
    }

    @Override
    protected XMLSave createXMLSave() {
        prepareSave();
        return new JBPMXMLSave(createXMLHelper()) {
            @Override
            protected boolean shouldSaveFeature(EObject o,
                                                EStructuralFeature f) {
                if (Bpmn2Package.eINSTANCE.getDocumentation_Text().equals(f)) {
                    return false;
                }
                if (Bpmn2Package.eINSTANCE.getFormalExpression_Body().equals(f)) {
                    return false;
                }
                return super.shouldSaveFeature(o,
                                               f);
            }
        };
    }

    @Override
    protected XMLLoad createXMLLoad() {
        return new XMLLoadImpl(createXMLHelper()) {

            @Override
            public void load(XMLResource resource,
                             InputStream inputStream,
                             Map<?, ?> options) throws IOException {
                try {
                    super.load(resource,
                               inputStream,
                               options);
                } catch (Exception e) {
                    DiagnosticWrappedException error = new DiagnosticWrappedException(e);
                    resource.getErrors().add(error);
                }
            }
        };
    }

    class DiagnosticWrappedException extends WrappedException implements Diagnostic {

        private static final long serialVersionUID = 1L;
        private String location;
        private int column;
        private int line;

        public DiagnosticWrappedException(Exception exception) {
            super(exception);
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getLocation() {
            return location;
        }

        public void setColumn(int column) {
            this.column = column;
        }

        public int getColumn() {
            return column;
        }

        public void setLine(int line) {
            this.line = line;
        }

        public int getLine() {
            return line;
        }
    }
}
