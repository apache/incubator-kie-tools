package org.kie.workbench.common.stunner.core.client.api;

import jsinterop.annotations.JsType;
import org.kie.workbench.common.stunner.core.api.JsDefinitionManager;

@JsType
public class JsStunnerEditor {

    public JsDefinitionManager definitions;
    public JsStunnerSession session;
    public Object canvas;

}
