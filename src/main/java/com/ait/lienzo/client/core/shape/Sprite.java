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

package com.ait.lienzo.client.core.shape;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.shape.json.IFactory;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.client.core.types.SpriteMap;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.google.gwt.json.client.JSONObject;

public class Sprite extends Shape<Sprite>
{
    public Sprite(String url, double rate, String name, SpriteMap smap)
    {
        super(ShapeType.SPRITE);

        setURL(url).setFrameRate(rate).setSpriteMapName(name).setSpriteMap(smap);
    }

    public Sprite(JSONObject node, ValidationContext ctx) throws ValidationException
    {
        super(ShapeType.SPRITE, node, ctx);
    }

    @Override
    public IFactory<Sprite> getFactory()
    {
        return new SpriteFactory();
    }

    public String getURL()
    {
        return getAttributes().getURL();
    }

    public Sprite setURL(String url)
    {
        getAttributes().setURL(url);

        return this;
    }

    public double getFrameRate()
    {
        return getAttributes().getFrameRate();
    }

    public Sprite setFrameRate(double rate)
    {
        getAttributes().setFrameRate(rate);

        return this;
    }

    public SpriteMap getSpriteMap()
    {
        return getAttributes().getSpriteMap();
    }

    public Sprite setSpriteMap(SpriteMap smap)
    {
        getAttributes().setSpriteMap(smap);

        return this;
    }

    public String getSpriteMapName()
    {
        return getAttributes().getSpriteMapName();
    }

    public Sprite setSpriteMapName(String name)
    {
        getAttributes().setSpriteMapName(name);

        return this;
    }

    public Sprite run()
    {
        return this;
    }

    public Sprite pause()
    {
        return this;
    }

    public boolean isRunning()
    {
        return false;
    }

    @Override
    protected boolean prepare(Context2D context, Attributes attr, double alpha)
    {
        return false;
    }

    public static class SpriteFactory extends ShapeFactory<Sprite>
    {
        public SpriteFactory()
        {
            super(ShapeType.SPRITE);

            addAttribute(Attribute.URL, true);

            addAttribute(Attribute.FRAME_RATE, true);

            addAttribute(Attribute.SPRITE_MAP, true);

            addAttribute(Attribute.SPRITE_MAP_NAME, true);
        }

        @Override
        public Sprite create(JSONObject node, ValidationContext ctx) throws ValidationException
        {
            return new Sprite(node, ctx);
        }
    }
}
