package org.gwtbootstrap3.extras.select.client;

/*
 * #%L
 * GwtBootstrap3
 * %%
 * Copyright (C) 2013 - 2016 GwtBootstrap3
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

/**
 * @author godi
 */
public interface SelectClientBundle extends ClientBundle {

    static final SelectClientBundle INSTANCE = GWT.create(SelectClientBundle.class);

    static final String VERSION = "1.12.4";
    static final String I18N_DIR = "resource/js/i18n.cache." + VERSION + "/";

    @Source("resource/js/bootstrap-select-" + VERSION + ".min.cache.js")
    TextResource select();

    @Source(I18N_DIR + "defaults-ar_AR.min.js")
    TextResource ar();

    @Source(I18N_DIR + "defaults-bg_BG.min.js")
    TextResource bg();

    @Source(I18N_DIR + "defaults-cro_CRO.min.js")
    TextResource cro();

    @Source(I18N_DIR + "defaults-cs_CZ.min.js")
    TextResource cs();

    @Source(I18N_DIR + "defaults-da_DK.min.js")
    TextResource da();

    @Source(I18N_DIR + "defaults-de_DE.min.js")
    TextResource de();

    @Source(I18N_DIR + "defaults-en_US.min.js")
    TextResource en();

    @Source(I18N_DIR + "defaults-es_CL.min.js")
    TextResource es_CL();

    @Source(I18N_DIR + "defaults-es_ES.min.js")
    TextResource es_ES();

    @Source(I18N_DIR + "defaults-eu.min.js")
    TextResource eu();

    @Source(I18N_DIR + "defaults-fa_IR.min.js")
    TextResource fa();

    @Source(I18N_DIR + "defaults-fi_FI.min.js")
    TextResource fi();

    @Source(I18N_DIR + "defaults-fr_FR.min.js")
    TextResource fr();

    @Source(I18N_DIR + "defaults-hu_HU.min.js")
    TextResource hu();

    @Source(I18N_DIR + "defaults-id_ID.min.js")
    TextResource id();

    @Source(I18N_DIR + "defaults-it_IT.min.js")
    TextResource it();

    @Source(I18N_DIR + "defaults-ko_KR.min.js")
    TextResource ko();

    @Source(I18N_DIR + "defaults-lt_LT.min.js")
    TextResource lt();

    @Source(I18N_DIR + "defaults-nb_NO.min.js")
    TextResource nb();

    @Source(I18N_DIR + "defaults-nl_NL.min.js")
    TextResource nl();

    @Source(I18N_DIR + "defaults-pl_PL.min.js")
    TextResource pl();

    @Source(I18N_DIR + "defaults-pt_BR.min.js")
    TextResource pt_BR();

    @Source(I18N_DIR + "defaults-pt_PT.min.js")
    TextResource pt_PT();

    @Source(I18N_DIR + "defaults-ro_RO.min.js")
    TextResource ro();

    @Source(I18N_DIR + "defaults-ru_RU.min.js")
    TextResource ru();

    @Source(I18N_DIR + "defaults-sk_SK.min.js")
    TextResource sk();

    @Source(I18N_DIR + "defaults-sl_SI.min.js")
    TextResource sl();

    @Source(I18N_DIR + "defaults-sv_SE.min.js")
    TextResource sv();

    @Source(I18N_DIR + "defaults-tr_TR.min.js")
    TextResource tr();

    @Source(I18N_DIR + "defaults-ua_UA.min.js")
    TextResource ua();

    @Source(I18N_DIR + "defaults-zh_CN.min.js")
    TextResource zh_CN();

    @Source(I18N_DIR + "defaults-zh_TW.min.js")
    TextResource zh_TW();
}
