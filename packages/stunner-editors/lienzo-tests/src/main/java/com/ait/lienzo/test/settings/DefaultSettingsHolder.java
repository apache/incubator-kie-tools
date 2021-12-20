/*
 * Copyright (c) 2017 Ahome' Innovation Technologies. All rights reserved.
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

package com.ait.lienzo.test.settings;

import com.ait.lienzo.test.translator.CanvasSupportTranslatorInterceptor;
import com.ait.lienzo.test.translator.GWTTranslatorInterceptor;
import com.ait.lienzo.test.translator.LienzoJSOMockTranslatorInterceptor;
import com.ait.lienzo.test.translator.LienzoJSOStubTranslatorInterceptor;
import com.ait.lienzo.test.translator.LienzoNodeTranslatorInterceptor;
import com.ait.lienzo.test.translator.LienzoPrimitiveTranslatorInterceptor;
import com.ait.lienzo.test.translator.LienzoStubTranslatorInterceptor;
import com.ait.lienzo.test.translator.StripFinalModifiersTranslatorInterceptor;

/**
 * This class contains the default framework's settings.
 * <p>
 * You can add your custom settings, if necessary, by using the
 * annotation <code>com.ait.lienzo.test.annotation.Settings</code>
 * on your test classes.
 *
 * @author Roger Martinez
 * @See com.ait.lienzo.test.annotation.Settings
 * @since 1.0
 */
@com.ait.lienzo.test.annotation.Settings(

        /*  Note: Class 'com.ait.lienzo.client.core.shape.Node' is already available for being mocked due to
                  the use of the translator 'com.ait.lienzo.test.translator.LienzoNodeTranslatorInterceptor'.
                  No need to add it explicitly here. */
        mocks = {
                "com.ait.lienzo.client.core.shape.Shape",
                "com.ait.lienzo.client.core.shape.Viewport"
        },

        stubs = {
                com.ait.lienzo.test.stub.UIEvent.class,
                com.ait.lienzo.test.stub.NFastArrayList.class,
                com.ait.lienzo.test.stub.NFastStringMap.class,
                com.ait.lienzo.test.stub.NFastDoubleArray.class,
                com.ait.lienzo.test.stub.Uint8ClampedArray.class,
                com.ait.lienzo.test.stub.LienzoCore.class,
                com.ait.lienzo.test.stub.NFastStringMap.class,
                com.ait.lienzo.test.stub.RootPanel.class,
                com.ait.lienzo.test.stub.TextUtils.class,
                com.ait.lienzo.test.stub.overlays.CanvasRenderingContext2D.class,
                com.ait.lienzo.test.stub.overlays.Context2D.class,
                com.ait.lienzo.test.stub.overlays.Global.class,
                com.ait.lienzo.test.stub.overlays.JSONType.class,
                com.ait.lienzo.test.stub.overlays.JsArray.class,
                com.ait.lienzo.test.stub.overlays.JsMap.class,
                com.ait.lienzo.test.stub.overlays.DomGlobal.class,
                com.ait.lienzo.test.stub.overlays.Window.class,
                com.ait.lienzo.test.stub.overlays.HTMLDocument.class,
                com.ait.lienzo.test.stub.overlays.HTMLCanvasElement.class,
                com.ait.lienzo.test.stub.overlays.HTMLDivElement.class,
                com.ait.lienzo.test.stub.overlays.HTMLElement.class,
                com.ait.lienzo.test.stub.overlays.AddEventListenerOptions.class,
                com.ait.lienzo.test.stub.overlays.ImageData.class,
                com.ait.lienzo.test.stub.overlays.PathPartListJSO.class,
                com.ait.lienzo.test.stub.overlays.Point2DArray.class,
                com.ait.lienzo.test.stub.overlays.ScratchPad.class,
                com.ait.lienzo.test.stub.overlays.Timer.class
        },

        jsoStubs = {
                "com.ait.lienzo.client.core.types.FillGradient$GradientJSO",
                "com.ait.lienzo.client.core.types.ImageData",
                "com.ait.lienzo.client.core.types.TextMetrics",
                "com.ait.lienzo.client.core.shape.AbstractPathClipper$PathClipperJSO",
                "com.ait.lienzo.client.core.NativeContext2D",
                "com.ait.lienzo.client.core.image.filter.ImageDataFilterCommonOps",
                "com.ait.lienzo.client.core.image.filter.ImageDataFilter$FilterTableArray",
                "com.ait.tooling.nativetools.client.collection.NFastBooleanArrayJSO",
                "com.ait.tooling.nativetools.client.collection.NFastIntegerArrayJSO",
                "com.ait.tooling.nativetools.client.collection.NFastStringArrayJSO"
        },

        jsoMocks = {
                "elemental2.dom.CSSProperties$WidthUnionType",
                "elemental2.dom.CSSProperties$HeightUnionType",
                "elemental2.dom.CSSProperties$ZIndexUnionType",
                "com.ait.lienzo.client.core.shape.Node$OptionalFields",
        },

        translators = {
                LienzoStubTranslatorInterceptor.class,
                LienzoJSOStubTranslatorInterceptor.class,
                LienzoJSOMockTranslatorInterceptor.class,
                CanvasSupportTranslatorInterceptor.class,
                StripFinalModifiersTranslatorInterceptor.class,
                LienzoNodeTranslatorInterceptor.class,
                LienzoPrimitiveTranslatorInterceptor.class,
                GWTTranslatorInterceptor.class,
        },

        logEnabled = false

)

public final class DefaultSettingsHolder {

    public static final DefaultSettingsHolder INSTANCE = new DefaultSettingsHolder();

    private DefaultSettingsHolder() {
    }
}