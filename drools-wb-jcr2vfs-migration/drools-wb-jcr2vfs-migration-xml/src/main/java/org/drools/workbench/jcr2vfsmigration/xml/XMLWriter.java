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
