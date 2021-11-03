package org.dashbuilder.displayer.client.component.function;

import java.util.Map;
import java.util.function.Consumer;

import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import org.dashbuilder.displayer.external.ExternalComponentFunction;

/**
 * Returns GWT Version
 *
 */
@Dependent
public class GWTVersion implements ExternalComponentFunction {

    @Override
    public void exec(Map<String, Object> params, Consumer<Object> onFinish, Consumer<String> onError) {
        onFinish.accept(GWT.getVersion());
    }

}