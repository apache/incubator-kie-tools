package org.drools.guvnor.client.rpc;


import org.drools.guvnor.shared.api.PortableObject;

import java.io.Serializable;

public class FormContentModel
        implements PortableObject {
    
    private static final long serialVersionUID = 20110725140435L;
    
    private String json;
    
    public FormContentModel() {
        super();
    }
    
    public FormContentModel(String json) {
        this();
        this.json = json;
    }
    
    public String getJson() {
        return json;
    }
    
    public void setJson(String json) {
        this.json = json;
    }
    
    @Override
    public String toString() {
        return "[json="+this.json+"]";
    }
}
