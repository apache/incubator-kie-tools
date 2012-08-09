package org.drools.guvnor.server.util;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Simple java to xml conversion for displaying process data
 * within the console.
 */
public class Payload2XML {

    public StringBuffer convert(String refId, Map<String, Object> javaPayload) {

        StringBuffer sb = new StringBuffer();

        try {
            List<Class> clz = new ArrayList<Class>(javaPayload.size() + 2);
            clz.add(PayloadCollection.class);
            clz.add(PayloadEntry.class);

            List<PayloadEntry> data = new ArrayList<PayloadEntry>();

            for (String key : javaPayload.keySet()) {
                Object payload = javaPayload.get(key);
                clz.add(payload.getClass());
                data.add(new PayloadEntry(key, payload));
            }

            PayloadCollection dataset = new PayloadCollection(refId, data);
            JAXBContext jaxbContext = JAXBContext.newInstance(clz.toArray(new Class[]{}));
            ByteArrayOutputStream bout = new ByteArrayOutputStream();

            Marshaller m = jaxbContext.createMarshaller();
            //m.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            m.marshal(dataset, bout);
            sb.append(new String(bout.toByteArray()));

        } catch (JAXBException e) {
            throw new RuntimeException("Payload2XML conversion failed", e);
        }

        return sb;
    }
}
