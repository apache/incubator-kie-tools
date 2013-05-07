package org.drools.workbench.jcr2vfsmigration.migrater;

public class PackageHeaderInfo {    
    private String header = null;

    public PackageHeaderInfo(String header) {
        this.header = header;
    }
    
    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }
    
}
