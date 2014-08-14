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

package com.ait.lienzo.client.core.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.ait.lienzo.client.core.shape.Arc;
import com.ait.lienzo.client.core.shape.Arrow;
import com.ait.lienzo.client.core.shape.BezierCurve;
import com.ait.lienzo.client.core.shape.Bow;
import com.ait.lienzo.client.core.shape.Chord;
import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.Ellipse;
import com.ait.lienzo.client.core.shape.GridLayer;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IsoscelesTrapezoid;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.client.core.shape.Movie;
import com.ait.lienzo.client.core.shape.Parallelogram;
import com.ait.lienzo.client.core.shape.Picture;
import com.ait.lienzo.client.core.shape.PolyLine;
import com.ait.lienzo.client.core.shape.Polygon;
import com.ait.lienzo.client.core.shape.QuadraticCurve;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.RegularPolygon;
import com.ait.lienzo.client.core.shape.Ring;
import com.ait.lienzo.client.core.shape.SVGPath;
import com.ait.lienzo.client.core.shape.Scene;
import com.ait.lienzo.client.core.shape.Slice;
import com.ait.lienzo.client.core.shape.Spline;
import com.ait.lienzo.client.core.shape.Star;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.shape.Triangle;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.shape.json.IFactory;

public final class LienzoCorePlugin implements ILienzoPlugin
{
    private final ArrayList<IFactory<?>> m_factories = new ArrayList<IFactory<?>>();

    public LienzoCorePlugin()
    {
    }

    @Override
    public String getNameSpace()
    {
        return "LienzoCore";
    }

    @Override
    public Collection<IFactory<?>> getFactories()
    {
        if (m_factories.isEmpty())
        {
            m_factories.add(new Arc.ArcFactory());

            m_factories.add(new Arrow.ArrowFactory());

            m_factories.add(new BezierCurve.BezierCurveFactory());

            m_factories.add(new Circle.CircleFactory());

            m_factories.add(new Ellipse.EllipseFactory());

            m_factories.add(new Line.LineFactory());

            m_factories.add(new Movie.MovieFactory());

            m_factories.add(new Parallelogram.ParallelogramFactory());

            m_factories.add(new Picture.PictureFactory());

            m_factories.add(new Polygon.PolygonFactory());

            m_factories.add(new PolyLine.PolyLineFactory());

            m_factories.add(new QuadraticCurve.QuadraticCurveFactory());

            m_factories.add(new Rectangle.RectangleFactory());

            m_factories.add(new RegularPolygon.RegularPolygonFactory());

            m_factories.add(new Slice.SliceFactory());

            m_factories.add(new Star.StarFactory());

            m_factories.add(new Text.TextFactory());

            m_factories.add(new Triangle.TriangleFactory());

            m_factories.add(new Spline.SplineFactory());

            m_factories.add(new Bow.BowFactory());

            m_factories.add(new Ring.RingFactory());

            m_factories.add(new Chord.ChordFactory());

            m_factories.add(new IsoscelesTrapezoid.IsoscelesTrapezoidFactory());

            m_factories.add(new SVGPath.SVGPathFactory());

            m_factories.add(new Group.GroupFactory());

            m_factories.add(new Layer.LayerFactory());

            m_factories.add(new GridLayer.GridLayerFactory());

            m_factories.add(new Scene.SceneFactory());

            m_factories.add(new Viewport.ViewportFactory());
        }
        return Collections.unmodifiableCollection(m_factories);
    }
}
