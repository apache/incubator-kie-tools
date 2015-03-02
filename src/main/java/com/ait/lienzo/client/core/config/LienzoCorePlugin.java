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

package com.ait.lienzo.client.core.config;

import com.ait.lienzo.client.core.image.filter.AlphaScaleColorImageDataFilter;
import com.ait.lienzo.client.core.image.filter.AverageGrayScaleImageDataFilter;
import com.ait.lienzo.client.core.image.filter.BrightnessImageDataFilter;
import com.ait.lienzo.client.core.image.filter.BumpImageDataFilter;
import com.ait.lienzo.client.core.image.filter.ColorDeltaAlphaImageDataFilter;
import com.ait.lienzo.client.core.image.filter.ColorLuminosityImageDataFilter;
import com.ait.lienzo.client.core.image.filter.ContrastImageDataFilter;
import com.ait.lienzo.client.core.image.filter.DiffusionImageDataFilter;
import com.ait.lienzo.client.core.image.filter.EdgeDetectImageDataFilter;
import com.ait.lienzo.client.core.image.filter.EmbossImageDataFilter;
import com.ait.lienzo.client.core.image.filter.ExposureImageDataFilter;
import com.ait.lienzo.client.core.image.filter.GainImageDataFilter;
import com.ait.lienzo.client.core.image.filter.GammaImageDataFilter;
import com.ait.lienzo.client.core.image.filter.HueImageDataFilter;
import com.ait.lienzo.client.core.image.filter.ImageDataFilterChain;
import com.ait.lienzo.client.core.image.filter.InvertColorImageDataFilter;
import com.ait.lienzo.client.core.image.filter.LightnessGrayScaleImageDataFilter;
import com.ait.lienzo.client.core.image.filter.LuminosityGrayScaleImageDataFilter;
import com.ait.lienzo.client.core.image.filter.PosterizeImageDataFilter;
import com.ait.lienzo.client.core.image.filter.RGBIgnoreAlphaImageDataFilter;
import com.ait.lienzo.client.core.image.filter.SharpenImageDataFilter;
import com.ait.lienzo.client.core.image.filter.SolarizeImageDataFilter;
import com.ait.lienzo.client.core.image.filter.StackBlurImageDataFilter;
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
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.OrthogonalPolyLine;
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
import com.ait.lienzo.client.core.shape.SimpleArrow;
import com.ait.lienzo.client.core.shape.Slice;
import com.ait.lienzo.client.core.shape.Spline;
import com.ait.lienzo.client.core.shape.Sprite;
import com.ait.lienzo.client.core.shape.Star;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.shape.Triangle;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.shape.grid.Grid;
import com.ait.lienzo.client.core.shape.json.IFactory;
import com.ait.lienzo.shared.core.types.GroupType;
import com.ait.lienzo.shared.core.types.ImageFilterType;
import com.ait.lienzo.shared.core.types.NodeType;
import com.ait.lienzo.shared.core.types.ProxyType;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.ait.lienzo.shared.java.util.function.Supplier;

public final class LienzoCorePlugin extends AbstractLienzoCorePlugin
{
    public LienzoCorePlugin()
    {
        add(ShapeType.ARC, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new Arc.ArcFactory();
            }
        });
        add(ShapeType.ARROW, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new Arrow.ArrowFactory();
            }
        });
        add(ShapeType.BEZIER_CURVE, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new BezierCurve.BezierCurveFactory();
            }
        });
        add(ShapeType.CIRCLE, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new Circle.CircleFactory();
            }
        });
        add(ShapeType.ELLIPSE, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new Ellipse.EllipseFactory();
            }
        });
        add(ShapeType.LINE, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new Line.LineFactory();
            }
        });
        add(ShapeType.MOVIE, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new Movie.MovieFactory();
            }
        });
        add(ShapeType.PARALLELOGRAM, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new Parallelogram.ParallelogramFactory();
            }
        });
        add(ShapeType.PICTURE, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new Picture.PictureFactory();
            }
        });
        add(ShapeType.POLYGON, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new Polygon.PolygonFactory();
            }
        });
        add(ShapeType.POLYLINE, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new PolyLine.PolyLineFactory();
            }
        });
        add(ShapeType.QUADRATIC_CURVE, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new QuadraticCurve.QuadraticCurveFactory();
            }
        });
        add(ShapeType.RECTANGLE, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new Rectangle.RectangleFactory();
            }
        });
        add(ShapeType.REGULAR_POLYGON, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new RegularPolygon.RegularPolygonFactory();
            }
        });
        add(ShapeType.SLICE, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new Slice.SliceFactory();
            }
        });
        add(ShapeType.STAR, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new Star.StarFactory();
            }
        });
        add(ShapeType.TEXT, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new Text.TextFactory();
            }
        });
        add(ShapeType.TRIANGLE, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new Triangle.TriangleFactory();
            }
        });
        add(ShapeType.SPLINE, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new Spline.SplineFactory();
            }
        });
        add(ShapeType.BOW, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new Bow.BowFactory();
            }
        });
        add(ShapeType.RING, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new Ring.RingFactory();
            }
        });
        add(ShapeType.CHORD, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new Chord.ChordFactory();
            }
        });
        add(ShapeType.ISOSCELES_TRAPEZOID, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new IsoscelesTrapezoid.IsoscelesTrapezoidFactory();
            }
        });
        add(ShapeType.SVG_PATH, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new SVGPath.SVGPathFactory();
            }
        });
        add(ShapeType.SPRITE, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new Sprite.SpriteFactory();
            }
        });
        add(ShapeType.ORTHOGONAL_POLYLINE, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new OrthogonalPolyLine.OrthogonaPolylLineFactory();
            }
        });
        add(ShapeType.MULTI_PATH, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new MultiPath.MultiPathFactory();
            }
        });
        add(ShapeType.SIMPLE_ARROW, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new SimpleArrow.SimpleArrowFactory();
            }
        });
        add(ProxyType.GRID, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new Grid.GridFactory();
            }
        });
        add(GroupType.GROUP, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new Group.GroupFactory();
            }
        });
        add(NodeType.LAYER, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new Layer.LayerFactory();
            }
        });
        add(NodeType.GRID_LAYER, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new GridLayer.GridLayerFactory();
            }
        });
        add(NodeType.SCENE, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new Scene.SceneFactory();
            }
        });
        add(NodeType.VIEWPORT, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new Viewport.ViewportFactory();
            }
        });
        add(ImageFilterType.AlphaScaleColorImageDataFilterType, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new AlphaScaleColorImageDataFilter.AlphaScaleColorImageDataFilterFactory();
            }
        });
        add(ImageFilterType.AverageGrayScaleImageDataFilterType, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new AverageGrayScaleImageDataFilter.AverageGrayScaleImageDataFilterFactory();
            }
        });
        add(ImageFilterType.BrightnessImageDataFilterType, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new BrightnessImageDataFilter.BrightnessImageDataFilterFactory();
            }
        });
        add(ImageFilterType.BumpImageDataFilterType, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new BumpImageDataFilter.BumpImageDataFilterFactory();
            }
        });
        add(ImageFilterType.ColorDeltaAlphaImageDataFilterType, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new ColorDeltaAlphaImageDataFilter.ColorDeltaAlphaImageDataFilterFactory();
            }
        });
        add(ImageFilterType.ColorLuminosityImageDataFilterType, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new ColorLuminosityImageDataFilter.ColorLuminosityImageDataFilterFactory();
            }
        });
        add(ImageFilterType.ContrastImageDataFilterType, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new ContrastImageDataFilter.ContrastImageDataFilterFactory();
            }
        });
        add(ImageFilterType.DiffusionImageDataFilterType, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new DiffusionImageDataFilter.DiffusionImageDataFilterFactory();
            }
        });
        add(ImageFilterType.EdgeDetectImageDataFilterType, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new EdgeDetectImageDataFilter.EdgeDetectImageDataFilterFactory();
            }
        });
        add(ImageFilterType.EmbossImageDataFilterType, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new EmbossImageDataFilter.EmbossImageDataFilterFactory();
            }
        });
        add(ImageFilterType.ExposureImageDataFilterType, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new ExposureImageDataFilter.ExposureImageDataFilterFactory();
            }
        });
        add(ImageFilterType.GainImageDataFilterType, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new GainImageDataFilter.GainImageDataFilterFactory();
            }
        });
        add(ImageFilterType.GammaImageDataFilterType, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new GammaImageDataFilter.GammaImageDataFilterFactory();
            }
        });
        add(ImageFilterType.HueImageDataFilterType, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new HueImageDataFilter.HueImageDataFilterFactory();
            }
        });
        add(ImageFilterType.ImageDataFilterChainType, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new ImageDataFilterChain.ImageDataFilterChainFactory();
            }
        });
        add(ImageFilterType.InvertColorImageDataFilterType, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new InvertColorImageDataFilter.InvertColorImageDataFilterFactory();
            }
        });
        add(ImageFilterType.LightnessGrayScaleImageDataFilterType, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new LightnessGrayScaleImageDataFilter.LightnessGrayScaleImageDataFilterFactory();
            }
        });
        add(ImageFilterType.LuminosityGrayScaleImageDataFilterType, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new LuminosityGrayScaleImageDataFilter.LuminosityGrayScaleImageDataFilterFactory();
            }
        });
        add(ImageFilterType.PosterizeImageDataFilterType, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new PosterizeImageDataFilter.PosterizeImageDataFilterFactory();
            }
        });
        add(ImageFilterType.RGBIgnoreAlphaImageDataFilterType, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new RGBIgnoreAlphaImageDataFilter.RGBIgnoreAlphaImageDataFilterFactory();
            }
        });
        add(ImageFilterType.SharpenImageDataFilterType, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new SharpenImageDataFilter.SharpenImageDataFilterFactory();
            }
        });
        add(ImageFilterType.SolarizeImageDataFilterType, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new SolarizeImageDataFilter.SolarizeImageDataFilterFactory();
            }
        });
        add(ImageFilterType.StackBlurImageDataFilterType, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new StackBlurImageDataFilter.StackBlurImageDataFilterFactory();
            }
        });
    }

    @Override
    public String getNameSpace()
    {
        return "LienzoCore";
    }
}
