/*
   Copyright (c) 2014 Ahome' Innovation Technologies. All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.ait.lienzo.client.core.image.filter;

import com.ait.lienzo.client.core.shape.json.IFactory;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.client.core.types.ImageData;
import com.google.gwt.canvas.dom.client.CanvasPixelArray;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;

/**
 * A class that allows for easy creation of a Light Gray Scale Image Filter.
 */
public class EdgeDetectImageDataFilter extends AbstractImageDataFilter<EdgeDetectImageDataFilter>
{
    public EdgeDetectImageDataFilter()
    {
    }

    protected EdgeDetectImageDataFilter(JSONObject node, ValidationContext ctx) throws ValidationException
    {
        super(node, ctx);
    }

    @Override
    public ImageData filter(ImageData source, boolean copy)
    {
        if (null == source)
        {
            return null;
        }
        if (copy)
        {
            source = source.copy();
        }
        if (false == isActive())
        {
            return source;
        }
        final CanvasPixelArray data = source.getData();

        if (null == data)
        {
            return source;
        }
        ImageData result = source.create();

        filter_(data, result.getData(), source.getWidth(), source.getHeight());

        return result;
    }

    private final native void filter_(JavaScriptObject data, JavaScriptObject buff, int w, int h)
    /*-{
        var hmap = [-1, -2, -1, 0, 0, 0, 1, 2, 1];
        var vmap = [-1, 0, 1, -2, 0, 2, -1, 0, 1];
    	for (var y = 0; y < h; y++) {
            for (var x = 0; x < w; x++) {
                var p = (y * w + x) * 4;
                var rh = 0; gh = 0; bh = 0;
                var rv = 0; gv = 0; bv = 0;
                for(var irow = -1; irow <= 1; irow++) {
                    var iy = y + irow;
                    var ioff;
                    if((iy >= 0) && (iy < h)) {
                        ioff = iy * w * 4;
                    } else {
                        ioff = y * w * 4;
                    }
                    var moff = 3 * (irow + 1) + 1;
                    for(var icol = -1; icol <= 1; icol++) {
                        var ix = x + icol;
                        if(!((ix >= 0) && (ix < w))) {
                            ix = x;
                        }
                        ix *= 4;
                        var r = data[ioff + ix + 0];
                        var g = data[ioff + ix + 1];
                        var b = data[ioff + ix + 2];
                        var z = hmap[moff + icol];
                        var v = vmap[moff + icol];
                        rh += ((z * r) | 0);
                        bh += ((z * g) | 0);
                        gh += ((z * b) | 0);
                        rv += ((v * r) | 0);
                        gv += ((v * g) | 0);
                        bv += ((v * b) | 0);
                    }
                }
                buff[p + 0] = ((Math.sqrt(rh * rh + rv * rv) / 1.8) | 0);
                buff[p + 1] = ((Math.sqrt(gh * gh + gv * gv) / 1.8) | 0);
                buff[p + 2] = ((Math.sqrt(bh * bh + bv * bv) / 1.8) | 0);
                buff[p + 3] = data[p + 3];
            }   
        }
    }-*/;

    @Override
    public IFactory<EdgeDetectImageDataFilter> getFactory()
    {
        return new EdgeDetectImageDataFilterFactory();
    }

    public static class EdgeDetectImageDataFilterFactory extends ImageDataFilterFactory<EdgeDetectImageDataFilter>
    {
        public EdgeDetectImageDataFilterFactory()
        {
            super(EdgeDetectImageDataFilter.class.getSimpleName());
        }

        @Override
        public EdgeDetectImageDataFilter create(JSONObject node, ValidationContext ctx) throws ValidationException
        {
            return new EdgeDetectImageDataFilter(node, ctx);
        }
    }
}
