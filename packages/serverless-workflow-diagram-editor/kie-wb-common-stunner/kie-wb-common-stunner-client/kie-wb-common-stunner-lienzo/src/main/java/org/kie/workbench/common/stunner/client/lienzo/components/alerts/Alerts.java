/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.kie.workbench.common.stunner.client.lienzo.components.alerts;

import java.util.Objects;

import elemental2.dom.HTMLElement;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import org.kie.j2cl.tools.di.core.IsElement;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.Command;

import static org.kie.workbench.common.stunner.core.i18n.CoreTranslationMessages.ERROR;
import static org.kie.workbench.common.stunner.core.i18n.CoreTranslationMessages.ERRORS;
import static org.kie.workbench.common.stunner.core.i18n.CoreTranslationMessages.INFORMATION;
import static org.kie.workbench.common.stunner.core.i18n.CoreTranslationMessages.INFORMATIONS;
import static org.kie.workbench.common.stunner.core.i18n.CoreTranslationMessages.WARNING;
import static org.kie.workbench.common.stunner.core.i18n.CoreTranslationMessages.WARNINGS;

@Dependent
public class Alerts implements IsElement {

    public interface View extends UberView<Alerts> {

        void setInfoText(String text);

        void setInfoTooltip(String text);

        void setInfoEnabled(boolean enabled);

        void setInfoVisible(boolean visible);

        void setWarningsText(String text);

        void setWarningsTooltip(String text);

        void setWarningsEnabled(boolean enabled);

        void setWarningsVisible(boolean visible);

        void setErrorsText(String text);

        void setErrorsTooltip(String text);

        void setErrorsEnabled(boolean enabled);

        void setErrorsVisible(boolean visible);
    }

    private static final String EMPTY = "";
    private static final String SPACE = " ";

    private final View view;
    private final ClientTranslationService clientTranslationService;
    private Command onShowInfos;
    private Command onShowWarnings;
    private Command onShowErrors;

    @Inject
    public Alerts(final View view,
                  final ClientTranslationService clientTranslationService) {
        this.view = view;
        this.clientTranslationService = clientTranslationService;
        this.onShowInfos = () -> {
        };
        this.onShowWarnings = () -> {
        };
        this.onShowErrors = () -> {
        };
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    @Override
    public HTMLElement getElement(){
        return view.getElement();
    }

    @PreDestroy
    public void destroy() {
        onShowInfos = null;
        onShowWarnings = null;
        onShowErrors = null;
    }

    public void setInfo(int informationQty) {
        String qty = String.valueOf(informationQty);
        if (informationQty == 0) {
            view.setInfoText(EMPTY);
            view.setInfoTooltip(EMPTY);
        } else if (informationQty == 1) {
            view.setInfoText(qty);
            view.setInfoTooltip(qty + SPACE + clientTranslationService.getNotNullValue(INFORMATION));
        } else {
            view.setInfoText(qty);
            view.setInfoTooltip(qty + SPACE + clientTranslationService.getNotNullValue(INFORMATIONS));
        }
        view.setInfoEnabled(informationQty > 0);
        view.setInfoVisible(informationQty > 0);
    }

    public void setWarnings(int warningsQty) {
        String qty = String.valueOf(warningsQty);
        if (warningsQty == 0) {
            view.setWarningsText(EMPTY);
            view.setWarningsTooltip(EMPTY);
        } else if (warningsQty == 1) {
            view.setWarningsText(qty);
            view.setWarningsTooltip(qty + SPACE + clientTranslationService.getNotNullValue(WARNING));
        } else {
            view.setWarningsText(qty);
            view.setWarningsTooltip(qty + SPACE + clientTranslationService.getNotNullValue(WARNINGS));
        }
        view.setWarningsEnabled(warningsQty > 0);
        view.setWarningsVisible(warningsQty > 0);
    }

    public void setErrors(int errorsQty) {
        String qty = String.valueOf(errorsQty);
        if (errorsQty == 0) {
            view.setErrorsText(EMPTY);
            view.setErrorsTooltip(EMPTY);
        } else if (errorsQty == 1) {
            view.setErrorsText(qty);
            view.setErrorsTooltip(qty + SPACE + clientTranslationService.getNotNullValue(ERROR));
        } else {
            view.setErrorsText(qty);
            view.setErrorsTooltip(qty + SPACE + clientTranslationService.getNotNullValue(ERRORS));
        }
        view.setErrorsEnabled(errorsQty > 0);
        view.setErrorsVisible(errorsQty > 0);
    }

    public Alerts onShowInfos(final Command onShowInfos) {
        checkNotNull("onShowInfos", onShowInfos);
        this.onShowInfos = onShowInfos;
        return this;
    }

    public Alerts onShowWarnings(final Command onShowWarnings) {
        checkNotNull("onShowWarnings", onShowWarnings);
        this.onShowWarnings = onShowWarnings;
        return this;
    }

    public Alerts onShowErrors(final Command onShowErrors) {
        checkNotNull("onShowErrors", onShowErrors);
        this.onShowErrors = onShowErrors;
        return this;
    }

    void onShowInfos() {
        onShowInfos.execute();
    }

    void onShowWarnings() {
        onShowWarnings.execute();
    }

    void onShowErrors() {
        onShowErrors.execute();
    }

    private static <T> T checkNotNull(String objName, T obj) {
        return Objects.requireNonNull(obj, "Parameter named '" + objName + "' should be not null!");
    }
}
