package org.dashbuilder.displayer.client.component.function;

import java.util.function.Consumer;

import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import elemental2.core.JsMap;

/**
 * Returns GWT Version
 *
 */
@Dependent
public class GWTVersion implements ExternalComponentFunction {

    @Override
    public void exec(JsMap<String, Object> params, Consumer<Object> onResult) {
        onResult.accept(GWT.getVersion());
    }

}