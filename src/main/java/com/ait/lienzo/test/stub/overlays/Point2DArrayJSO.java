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

package com.ait.lienzo.test.stub.overlays;

import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.annotation.StubClass;

/**
 * Stub for class <code>com.ait.lienzo.client.core.types.Point2DArray$Point2DArrayJSO</code>.
 *
 * @author Roger Martinez
 * @since 1.0
 *
 */
@StubClass("com.ait.lienzo.client.core.types.Point2DArray$Point2DArrayJSO")
public class Point2DArrayJSO extends JsArray<Point2D.Point2DJSO>
{
    protected Point2DArrayJSO()
    {
    }

    public void pop()
    {
        this.pop();
    }

    public static Point2DArrayJSO make()
    {
        return new Point2DArrayJSO();
    }

    public Point2DArrayJSO noAdjacentPoints()
    {
        final Point2DArrayJSO no = Point2DArrayJSO.make();
        final int sz = this.length();
        if (sz < 1)
        {
            return no;
        }
        Point2D.Point2DJSO p1 = this.get(0);
        no.push(Point2D.Point2DJSO.make(p1.getX(), p1.getY()));
        if (sz < 2)
        {
            return no;
        }
        for (int i = 1; i < sz; i++)
        {
            final Point2D.Point2DJSO p2 = this.get(i);
            if (!((p1.getX() == p2.getX()) && (p1.getY() == p2.getY())))
            {
                no.push(Point2D.Point2DJSO.make(p2.getX(), p2.getY()));
            }
            p1 = p2;
        }
        return no;
    }

    public Point2DArrayJSO copy()
    {
        final Point2DArrayJSO no = Point2DArrayJSO.make();
        final int sz = this.length();
        if (sz < 1)
        {
            return no;
        }
        for (int i = 0; i < sz; i++)
        {
            final Point2D.Point2DJSO p = get(i);
            no.push(Point2D.Point2DJSO.make(p.getX(), p.getY()));
        }
        return no;
    }
}
