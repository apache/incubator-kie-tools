/*
   Copyright (c) 2014,2015 Ahome' Innovation Technologies. All rights reserved.

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

package com.ait.lienzo.client.core.types;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;

/**
 * JSO to be used when retrieving the Canvas {@link Text} measurements.
 */
public final class TextMetrics extends JavaScriptObject
{
    public static final TextMetrics make()
    {
        return make(0, 0);
    }

    public static final native TextMetrics make(int w, int h)
    /*-{
        return {width: w, height: h};       
    }-*/;

    protected TextMetrics()
    {
    }
    
    public final String toJSONString()
    {
        return new JSONObject(this).toString();
    }

    /**
     * Sets the text width.
     * 
     * @param height
     */
    public final native void setWidth(double width)
    /*-{
    	this.width = width;
    }-*/;

    /**
     * Return the width of the rendered text.
     * 
     * @return the width of the text
     */
    public final native double getWidth()
    /*-{
    	if (this.width !== undefined) {
    	    return this.width;
    	}
    	return 0;
    }-*/;

    /**
     * Sets the text height.
     * 
     * @param height
     */
    public final native void setHeight(double height)
    /*-{
    	this.height = height;
    }-*/;

    /**
     * Return the height of the rendered text.
     * @return the height of the text
     */
    public final native double getHeight()
    /*-{
    	if (this.height !== undefined) {
    	    return this.height;
    	}
    	return 0;
    }-*/;
}
