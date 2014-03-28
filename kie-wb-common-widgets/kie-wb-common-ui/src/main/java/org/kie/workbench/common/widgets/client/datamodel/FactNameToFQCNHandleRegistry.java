package org.kie.workbench.common.widgets.client.datamodel;

import java.util.HashMap;
import java.util.Map;

public class FactNameToFQCNHandleRegistry {

    Map<String, String> map = new HashMap<String, String>();

    public void add(String mfClassName_typeName, String mfClassName_qualifiedType) {
        map.put(mfClassName_typeName, mfClassName_qualifiedType);
    }

    public String get(String factName) {
        return map.get(factName);
    }

    public boolean contains(String mfTypeName) {
        return map.keySet().contains(mfTypeName);
    }
}
