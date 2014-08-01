package org.kie.workbench.common.services.datamodeller.driver;

import org.kie.workbench.common.services.datamodeller.core.JavaTypeInfo;

public class TypeInfoResult extends DriverResult {

    private JavaTypeInfo typeInfo;

    public TypeInfoResult() {
    }

    public TypeInfoResult( JavaTypeInfo typeInfo ) {
        this.typeInfo = typeInfo;
    }

    public JavaTypeInfo getTypeInfo() {
        return typeInfo;
    }

    public void setTypeInfo( JavaTypeInfo typeInfo ) {
        this.typeInfo = typeInfo;
    }
}
