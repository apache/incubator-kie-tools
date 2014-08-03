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

package com.ait.lienzo.client.core.shape.json;

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
import com.ait.lienzo.client.core.types.NFastStringMap;
import com.ait.lienzo.client.core.util.Console;

/**
 * This class is a central repository for all {@link IJSONSerializable} factories.  
 * If you create a new class and you would like to be able to serialize / deserialize
 * it, you will need to register it here using {@link #registerFactory(String, IFactory)}.
 */
public final class FactoryRegistry
{
    private static FactoryRegistry            s_instance;

    private final NFastStringMap<IFactory<?>> m_factories = new NFastStringMap<IFactory<?>>();

    private FactoryRegistry()
    {
    }

    /**
     * Adds a {@link IFactory} to this registry.
     * <p>
     * Use this when you're creating your own class and you want to be able to deserialize
     * your node from a JSON string via {@link JSONDeserializer#fromString(String)}.
     * 
     * @param factory IFactory
     * @return this FactoryRegistry
     */
    public final FactoryRegistry registerFactory(IFactory<?> factory)
    {
        String type = factory.getTypeName();

        if (null == m_factories.get(type))
        {
            m_factories.put(type, factory);
        }
        else
        {
            Console.log("WARNING: IFactory for " + type + " was already registered. Try prefixing your type names e.g. with 'foo_' to avoid conflicts with the built-in Lienzo nodes.");
        }
        return this;
    }

    /**
     * Returns the {@link IFactory} for the specified type name.
     * 
     * @param typeName
     * @return IFactory
     */
    public final IFactory<?> getFactory(String typeName)
    {
        return m_factories.get(typeName);
    }

    /**
     * Returns the singleton FactoryRegistry.
     * @return FactoryRegistry
     */
    public static final FactoryRegistry getInstance()
    {
        if (null == s_instance)
        {
            // changed to lazy init - DSJ

            s_instance = makeFactoryRegistry();
        }
        return s_instance;
    }

    private static final FactoryRegistry makeFactoryRegistry()
    {
        // Make sure we register the built-in Lienzo types first,
        // so that toolkit users can't override them.

        FactoryRegistry registry = new FactoryRegistry();

        // Primitive types

        registry.registerFactory(new Arc.ArcFactory());

        registry.registerFactory(new Arrow.ArrowFactory());

        registry.registerFactory(new BezierCurve.BezierCurveFactory());

        registry.registerFactory(new Circle.CircleFactory());

        registry.registerFactory(new Ellipse.EllipseFactory());

        registry.registerFactory(new Line.LineFactory());

        registry.registerFactory(new Movie.MovieFactory());

        registry.registerFactory(new Parallelogram.ParallelogramFactory());

        registry.registerFactory(new Picture.PictureFactory());

        registry.registerFactory(new Polygon.PolygonFactory());

        registry.registerFactory(new PolyLine.PolyLineFactory());

        registry.registerFactory(new QuadraticCurve.QuadraticCurveFactory());

        registry.registerFactory(new Rectangle.RectangleFactory());

        registry.registerFactory(new RegularPolygon.RegularPolygonFactory());

        registry.registerFactory(new Slice.SliceFactory());

        registry.registerFactory(new Star.StarFactory());

        registry.registerFactory(new Text.TextFactory());

        registry.registerFactory(new Triangle.TriangleFactory());

        registry.registerFactory(new Spline.SplineFactory());

        registry.registerFactory(new Bow.BowFactory());

        registry.registerFactory(new Ring.RingFactory());

        registry.registerFactory(new Chord.ChordFactory());

        registry.registerFactory(new IsoscelesTrapezoid.IsoscelesTrapezoidFactory());

        registry.registerFactory(new SVGPath.SVGPathFactory());

        // Container Types

        registry.registerFactory(new Group.GroupFactory());

        registry.registerFactory(new Layer.LayerFactory());

        registry.registerFactory(new GridLayer.GridLayerFactory());

        registry.registerFactory(new Scene.SceneFactory());

        registry.registerFactory(new Viewport.ViewportFactory());

        return registry;
    }
}
