package org.drools.guvnor.server.util;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "dataset")
public class PayloadCollection {

    List<PayloadEntry> payloadEntries;
    String ref;


    public PayloadCollection() {
    }

    public PayloadCollection(String ref, List<PayloadEntry> payloadEntries) {
        this.ref = ref;
        this.payloadEntries = payloadEntries;
    }

    @XmlElement(name = "data")
    public List<PayloadEntry> getPayload() {
        return payloadEntries;
    }

    public void setPayload(List<PayloadEntry> payloadEntries) {
        this.payloadEntries = payloadEntries;
    }

    @XmlAttribute
    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }
}
