/*
   Copyright (c) 2017 Ahome' Innovation Technologies. All rights reserved.

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

import com.ait.lienzo.client.core.image.Image;
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
import com.ait.lienzo.client.core.palette.Palette;
import com.ait.lienzo.client.core.palette.PaletteItem;
import com.ait.lienzo.client.core.shape.Arc;
import com.ait.lienzo.client.core.shape.Arrow;
import com.ait.lienzo.client.core.shape.BezierCurve;
import com.ait.lienzo.client.core.shape.Bow;
import com.ait.lienzo.client.core.shape.Chord;
import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.Ellipse;
import com.ait.lienzo.client.core.shape.EllipticalArc;
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
import com.ait.lienzo.client.core.shape.Slice;
import com.ait.lienzo.client.core.shape.Spline;
import com.ait.lienzo.client.core.shape.Sprite;
import com.ait.lienzo.client.core.shape.Star;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.shape.Triangle;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.shape.json.IFactory;
import com.ait.lienzo.client.core.shape.storage.PrimitiveFastArrayStorageEngine;
import com.ait.lienzo.client.core.shape.storage.SceneFastArrayStorageEngine;
import com.ait.lienzo.client.core.shape.storage.StorageEngineType;
import com.ait.lienzo.client.core.shape.storage.ViewportFastArrayStorageEngine;
import com.ait.lienzo.shared.core.types.GroupType;
import com.ait.lienzo.shared.core.types.ImageFilterType;
import com.ait.lienzo.shared.core.types.NodeType;
import com.ait.lienzo.shared.core.types.PaletteType;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.ait.tooling.common.api.java.util.function.Supplier;

final class LienzoCorePlugin extends AbstractLienzoCorePlugin
{
    LienzoCorePlugin()
    {
        addFactorySupplier(ShapeType.ARC, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new Arc.ArcFactory();
            }
        });
        addFactorySupplier(ShapeType.ARROW, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new Arrow.ArrowFactory();
            }
        });
        addFactorySupplier(ShapeType.BEZIER_CURVE, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new BezierCurve.BezierCurveFactory();
            }
        });
        addFactorySupplier(ShapeType.CIRCLE, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new Circle.CircleFactory();
            }
        });
        addFactorySupplier(ShapeType.ELLIPSE, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new Ellipse.EllipseFactory();
            }
        });
        addFactorySupplier(ShapeType.ELLIPTICAL_ARC, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new EllipticalArc.EllipticalArcFactory();
            }
        });
        addFactorySupplier(ShapeType.LINE, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new Line.LineFactory();
            }
        });
        addFactorySupplier(ShapeType.MOVIE, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new Movie.MovieFactory();
            }
        });
        addFactorySupplier(ShapeType.PARALLELOGRAM, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new Parallelogram.ParallelogramFactory();
            }
        });
        addFactorySupplier(ShapeType.PICTURE, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new Picture.PictureFactory();
            }
        });
        addFactorySupplier(ShapeType.IMAGE, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new Image.ImageFactory();
            }
        });
        addFactorySupplier(ShapeType.POLYGON, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new Polygon.PolygonFactory();
            }
        });
        addFactorySupplier(ShapeType.POLYLINE, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new PolyLine.PolyLineFactory();
            }
        });
        addFactorySupplier(ShapeType.QUADRATIC_CURVE, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new QuadraticCurve.QuadraticCurveFactory();
            }
        });
        addFactorySupplier(ShapeType.RECTANGLE, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new Rectangle.RectangleFactory();
            }
        });
        addFactorySupplier(ShapeType.REGULAR_POLYGON, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new RegularPolygon.RegularPolygonFactory();
            }
        });
        addFactorySupplier(ShapeType.SLICE, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new Slice.SliceFactory();
            }
        });
        addFactorySupplier(ShapeType.STAR, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new Star.StarFactory();
            }
        });
        addFactorySupplier(ShapeType.TEXT, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new Text.TextFactory();
            }
        });
        addFactorySupplier(ShapeType.TRIANGLE, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new Triangle.TriangleFactory();
            }
        });
        addFactorySupplier(ShapeType.SPLINE, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new Spline.SplineFactory();
            }
        });
        addFactorySupplier(ShapeType.BOW, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new Bow.BowFactory();
            }
        });
        addFactorySupplier(ShapeType.RING, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new Ring.RingFactory();
            }
        });
        addFactorySupplier(ShapeType.CHORD, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new Chord.ChordFactory();
            }
        });
        addFactorySupplier(ShapeType.ISOSCELES_TRAPEZOID, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new IsoscelesTrapezoid.IsoscelesTrapezoidFactory();
            }
        });
        addFactorySupplier(ShapeType.SVG_PATH, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new SVGPath.SVGPathFactory();
            }
        });
        addFactorySupplier(ShapeType.SPRITE, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new Sprite.SpriteFactory();
            }
        });
        addFactorySupplier(ShapeType.ORTHOGONAL_POLYLINE, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new OrthogonalPolyLine.OrthogonaPolylLineFactory();
            }
        });
        addFactorySupplier(ShapeType.MULTI_PATH, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new MultiPath.MultiPathFactory();
            }
        });
        addFactorySupplier(GroupType.GROUP, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new Group.GroupFactory();
            }
        });
        addFactorySupplier(NodeType.LAYER, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new Layer.LayerFactory();
            }
        });
        addFactorySupplier(NodeType.GRID_LAYER, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new GridLayer.GridLayerFactory();
            }
        });
        addFactorySupplier(NodeType.SCENE, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new Scene.SceneFactory();
            }
        });
        addFactorySupplier(NodeType.VIEWPORT, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new Viewport.ViewportFactory();
            }
        });
        addFactorySupplier(PaletteType.PALETTE, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new Palette.PaletteFactory();
            }
        });
        addFactorySupplier(PaletteType.PALETTE_ITEM, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new PaletteItem.PaletteItemFactory();
            }
        });
        addFactorySupplier(StorageEngineType.PRIMITIVE_FAST_ARRAY_STORAGE_ENGINE, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new PrimitiveFastArrayStorageEngine.PrimitiveFastArrayStorageEngineFactory();
            }
        });
        addFactorySupplier(StorageEngineType.SCENE_FAST_ARRAY_STORAGE_ENGINE, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new SceneFastArrayStorageEngine.SceneFastArrayStorageEngineFactory();
            }
        });
        addFactorySupplier(StorageEngineType.VIEWPORT_FAST_ARRAY_STORAGE_ENGINE, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new ViewportFastArrayStorageEngine.ViewportFastArrayStorageEngineFactory();
            }
        });
        addFactorySupplier(ImageFilterType.AlphaScaleColorImageDataFilterType, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new AlphaScaleColorImageDataFilter.AlphaScaleColorImageDataFilterFactory();
            }
        });
        addFactorySupplier(ImageFilterType.AverageGrayScaleImageDataFilterType, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new AverageGrayScaleImageDataFilter.AverageGrayScaleImageDataFilterFactory();
            }
        });
        addFactorySupplier(ImageFilterType.BrightnessImageDataFilterType, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new BrightnessImageDataFilter.BrightnessImageDataFilterFactory();
            }
        });
        addFactorySupplier(ImageFilterType.BumpImageDataFilterType, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new BumpImageDataFilter.BumpImageDataFilterFactory();
            }
        });
        addFactorySupplier(ImageFilterType.ColorDeltaAlphaImageDataFilterType, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new ColorDeltaAlphaImageDataFilter.ColorDeltaAlphaImageDataFilterFactory();
            }
        });
        addFactorySupplier(ImageFilterType.ColorLuminosityImageDataFilterType, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new ColorLuminosityImageDataFilter.ColorLuminosityImageDataFilterFactory();
            }
        });
        addFactorySupplier(ImageFilterType.ContrastImageDataFilterType, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new ContrastImageDataFilter.ContrastImageDataFilterFactory();
            }
        });
        addFactorySupplier(ImageFilterType.DiffusionImageDataFilterType, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new DiffusionImageDataFilter.DiffusionImageDataFilterFactory();
            }
        });
        addFactorySupplier(ImageFilterType.EdgeDetectImageDataFilterType, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new EdgeDetectImageDataFilter.EdgeDetectImageDataFilterFactory();
            }
        });
        addFactorySupplier(ImageFilterType.EmbossImageDataFilterType, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new EmbossImageDataFilter.EmbossImageDataFilterFactory();
            }
        });
        addFactorySupplier(ImageFilterType.ExposureImageDataFilterType, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new ExposureImageDataFilter.ExposureImageDataFilterFactory();
            }
        });
        addFactorySupplier(ImageFilterType.GainImageDataFilterType, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new GainImageDataFilter.GainImageDataFilterFactory();
            }
        });
        addFactorySupplier(ImageFilterType.GammaImageDataFilterType, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new GammaImageDataFilter.GammaImageDataFilterFactory();
            }
        });
        addFactorySupplier(ImageFilterType.HueImageDataFilterType, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new HueImageDataFilter.HueImageDataFilterFactory();
            }
        });
        addFactorySupplier(ImageFilterType.ImageDataFilterChainType, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new ImageDataFilterChain.ImageDataFilterChainFactory();
            }
        });
        addFactorySupplier(ImageFilterType.InvertColorImageDataFilterType, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new InvertColorImageDataFilter.InvertColorImageDataFilterFactory();
            }
        });
        addFactorySupplier(ImageFilterType.LightnessGrayScaleImageDataFilterType, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new LightnessGrayScaleImageDataFilter.LightnessGrayScaleImageDataFilterFactory();
            }
        });
        addFactorySupplier(ImageFilterType.LuminosityGrayScaleImageDataFilterType, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new LuminosityGrayScaleImageDataFilter.LuminosityGrayScaleImageDataFilterFactory();
            }
        });
        addFactorySupplier(ImageFilterType.PosterizeImageDataFilterType, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new PosterizeImageDataFilter.PosterizeImageDataFilterFactory();
            }
        });
        addFactorySupplier(ImageFilterType.RGBIgnoreAlphaImageDataFilterType, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new RGBIgnoreAlphaImageDataFilter.RGBIgnoreAlphaImageDataFilterFactory();
            }
        });
        addFactorySupplier(ImageFilterType.SharpenImageDataFilterType, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new SharpenImageDataFilter.SharpenImageDataFilterFactory();
            }
        });
        addFactorySupplier(ImageFilterType.SolarizeImageDataFilterType, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new SolarizeImageDataFilter.SolarizeImageDataFilterFactory();
            }
        });
        addFactorySupplier(ImageFilterType.StackBlurImageDataFilterType, new Supplier<IFactory<?>>()
        {
            @Override
            public IFactory<?> get()
            {
                return new StackBlurImageDataFilter.StackBlurImageDataFilterFactory();
            }
        });
    }

    @Override
    public final String getNameSpace()
    {
        return "LienzoCore";
    }

    @Override
    public final String getVersion()
    {
        return "2.0.295-RELEASE";
    }
}
