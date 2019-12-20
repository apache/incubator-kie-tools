/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper;

import java.util.logging.Logger;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.user.client.Command;
import jsinterop.base.JsPropertyMap;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.MainJs;

import static junit.framework.TestCase.fail;

public class DMNUnmarshallerTestBootstrap {

    private static final Logger LOGGER = Logger.getLogger(DMNUnmarshallerTestBootstrap.class.getName());

    /**
     * Bootstrap tests by loading required scripts. Since this runs asynchronously we
     * kick off the actual tests as the final step of the script loading process.
     */
    public void bootstrap(final Runnable tests) {
        LOGGER.info("Entering bootstrap()...");

        final Step step8 = new Step(() -> {
            final JsPropertyMap constructorsMap = MainJs.getConstructorsMap();
            MainJs.initializeJsInteropConstructors(constructorsMap);
            tests.run();
        });
        final Step step7 = new Step(() -> injectJavaScript("MainJs.js", step8));
        final Step step6 = new Step(() -> injectJavaScript("KIE.js", step7));
        final Step step5 = new Step(() -> injectJavaScript("DMN12.js", step6));
        final Step step4 = new Step(() -> injectJavaScript("DMNDI12.js", step5));
        final Step step3 = new Step(() -> injectJavaScript("DI.js", step4));
        final Step step2 = new Step(() -> injectJavaScript("DC.js", step3));
        final Step step1 = new Step(() -> injectJavaScript("Jsonix-all.js", step2));

        step1.onSuccess(null);

        LOGGER.info("Exiting bootstrap()...");
    }

    private void injectJavaScript(final String uri,
                                  final Step callback) {
        LOGGER.info("Attempting to inject: " + uri);
        ScriptInjector
                .fromUrl(uri)
                .setWindow(ScriptInjector.TOP_WINDOW)
                .setCallback(callback)
                .inject();
    }

    private static class Step implements Callback<Void, Exception> {

        private final Command success;

        private Step(final Command success) {
            this.success = success;
        }

        @Override
        public void onFailure(final Exception reason) {
            fail(reason.getMessage());
        }

        @Override
        public void onSuccess(final Void result) {
            success.execute();
        }
    }
}
