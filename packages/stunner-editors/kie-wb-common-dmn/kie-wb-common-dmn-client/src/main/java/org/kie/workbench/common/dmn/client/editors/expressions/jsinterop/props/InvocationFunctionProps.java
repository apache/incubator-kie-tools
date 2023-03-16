package org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props;

import jsinterop.annotations.JsType;

@JsType
public class InvocationFunctionProps {

    public final String id;
    public final String name;

    public InvocationFunctionProps(final String id, final String name) {
        this.id = id;
        this.name = name;
    }
}