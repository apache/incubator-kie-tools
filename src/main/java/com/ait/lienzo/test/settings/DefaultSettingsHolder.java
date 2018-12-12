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
import com.ait.lienzo.test.translator.LienzoStubTranslatorInterceptor;
import com.ait.lienzo.test.translator.StripFinalModifiersTranslatorInterceptor;

/**
 * This class contains the default framework's settings.
 *
 * You can add your custom settings, if necessary, by using the
 * annotation <code>com.ait.lienzo.test.annotation.Settings</code>
 * on your test classes.
 *
 * @See com.ait.lienzo.test.annotation.Settings
 *
 * @author Roger Martinez
 * @since 1.0
 *
 */
@com.ait.lienzo.test.annotation.Settings (

        /*  Note: Class 'com.ait.lienzo.client.core.shape.Node' is already available for being mocked due to
                  the use of the translator 'com.ait.lienzo.test.translator.LienzoNodeTranslatorInterceptor'.
                  No need to add it explicitly here. */
        mocks = {
                "com.ait.lienzo.client.core.shape.Shape",
                "com.ait.lienzo.client.core.shape.Viewport"
        },

        stubs = {
                com.ait.lienzo.test.stub.overlays.BoundingBoxJSO.class,
                com.ait.lienzo.test.stub.overlays.TransformJSO.class,
                com.ait.lienzo.test.stub.overlays.ShadowJSO.class,
                com.ait.lienzo.test.stub.overlays.NObjectJSO.class,
                com.ait.lienzo.test.stub.overlays.Point2DJSO.class,
                com.ait.lienzo.test.stub.overlays.DragBoundsJSO.class,
                com.ait.lienzo.test.stub.overlays.JsArray.class,
                com.ait.lienzo.test.stub.overlays.JsArrayMixed.class,
                com.ait.lienzo.test.stub.overlays.PathPartListJSO.class,
                com.ait.lienzo.test.stub.overlays.PathPartEntryJSO.class,
                com.ait.lienzo.test.stub.overlays.Point2DArrayJSO.class,
                com.ait.lienzo.test.stub.overlays.NArrayBaseJSO.class,
                com.ait.lienzo.test.stub.overlays.NFastPrimitiveArrayBaseJSO.class,
                com.ait.lienzo.test.stub.overlays.NFastDoubleArrayJSO.class,
                com.ait.lienzo.test.stub.overlays.OptionalNodeFields.class,
                com.ait.lienzo.test.stub.overlays.OptionalShapeFields.class,
                com.ait.lienzo.test.stub.overlays.OptionalGroupOfFields.class,
                com.ait.lienzo.test.stub.Attributes.class,
                com.ait.lienzo.test.stub.NFastArrayList.class,
                com.ait.lienzo.test.stub.NFastStringMap.class,
        },

        jsoStubs = {
                "com.ait.lienzo.client.core.types.FillGradient$GradientJSO",
                "com.ait.lienzo.client.core.types.ImageData",
                "com.ait.lienzo.client.core.types.SpriteBehaviorMap$SpriteBehaviorMapJSO",
                "com.ait.lienzo.client.core.types.SpriteBehaviorMap$BoundingBoxArrayJSO",
                "com.ait.lienzo.client.core.types.TextMetrics",
                "com.ait.lienzo.client.core.shape.AbstractPathClipper$PathClipperJSO",
                "com.ait.lienzo.client.core.shape.Node$OptionalFields",
                "com.ait.lienzo.client.core.Path2D$NativePath2D",
                "com.ait.lienzo.client.core.NativeContext2D",
                "com.ait.lienzo.client.core.image.filter.ImageDataFilterCommonOps",
                "com.ait.lienzo.client.core.image.filter.ImageDataFilter$FilterTableArray",
                "com.ait.lienzo.client.core.image.filter.ImageDataFilter$FilterTableArray",
                "com.ait.tooling.nativetools.client.collection.NFastBooleanArrayJSO",
                "com.ait.tooling.nativetools.client.collection.NFastIntegerArrayJSO",
                "com.ait.tooling.nativetools.client.collection.NFastStringArrayJSO",
                "com.ait.tooling.nativetools.client.NUtils$NativeOps",
                "com.ait.tooling.nativetools.client.collection.NFastStringHistogram$NFastStringHistogramJSO",
                "com.ait.tooling.nativetools.client.collection.NFastStringSet$NFastStringSetJSO",
                "com.ait.tooling.nativetools.client.util.Performance$PerformanceEntryJSO",
                "com.ait.tooling.nativetools.client.util.Performance$PerformanceEntryListJSO",
                "com.ait.tooling.nativetools.client.util.Performance$PerformanceJSO",
                "com.ait.tooling.nativetools.client.webworker.WebWorker$WebWorkerJSO",
                "com.ait.tooling.nativetools.client.usermedia.UserMediaStream",
                "com.ait.tooling.nativetools.client.NObjectBaseJSO"
        },

        jsoMocks = {},

        translators = {

                LienzoStubTranslatorInterceptor.class,
                LienzoJSOStubTranslatorInterceptor.class,
                LienzoJSOMockTranslatorInterceptor.class,
                CanvasSupportTranslatorInterceptor.class,
                StripFinalModifiersTranslatorInterceptor.class,
                LienzoNodeTranslatorInterceptor.class,
                GWTTranslatorInterceptor.class
        },

        logEnabled = false

        )
public final class DefaultSettingsHolder
{
    public static final DefaultSettingsHolder INSTANCE = new DefaultSettingsHolder();

    private DefaultSettingsHolder()
    {
    }
}