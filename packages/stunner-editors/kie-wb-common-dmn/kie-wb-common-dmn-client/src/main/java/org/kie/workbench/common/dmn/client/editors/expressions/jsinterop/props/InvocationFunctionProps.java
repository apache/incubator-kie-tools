package org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props;

import jsinterop.annotations.JsType;

@JsType
public class InvocationFunctionProps {

    public final String id;
    public final String functionName;

    public InvocationFunctionProps(final String id,final String functionName) {
        this.id = id;
        this.functionName = functionName;
    }
}