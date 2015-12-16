/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.jcr2vfsmigration.xml;

import java.io.Writer;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.drools.workbench.jcr2vfsmigration.xml.model.Categories;

public class XMLWriter {

    private JAXBContext jaxbContext;

    public XMLWriter() {
        try {
            this.jaxbContext = JAXBContext.newInstance("org.drools.workbench.jcr2vfsMigration.xml.jaxb.Categories.class");
        } catch ( JAXBException e ) {
            e.printStackTrace();
        }
    }

    public void categoriesToXml(Categories categories, Writer writer) {
        try {
            Marshaller m = jaxbContext.createMarshaller();
            m.marshal( categories, writer );
        } catch ( JAXBException e ) {
            e.printStackTrace();
        }
    }
}
