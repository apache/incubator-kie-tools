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
import com.ait.lienzo.client.core.shape.storage.PrimitiveFastArrayStorageEngine;
import com.ait.lienzo.client.core.shape.storage.SceneFastArrayStorageEngine;
import com.ait.lienzo.client.core.shape.storage.StorageEngineType;
import com.ait.lienzo.client.core.shape.storage.ViewportFastArrayStorageEngine;
import com.ait.lienzo.shared.core.types.GroupType;
import com.ait.lienzo.shared.core.types.ImageFilterType;
import com.ait.lienzo.shared.core.types.NodeType;
import com.ait.lienzo.shared.core.types.PaletteType;
import com.ait.lienzo.shared.core.types.ShapeType;

final class LienzoCorePlugin extends AbstractLienzoCorePlugin
{
    LienzoCorePlugin()
    {
        addFactorySupplier(ShapeType.ARC, () -> new Arc.ArcFactory());
        addFactorySupplier(ShapeType.ARROW, () -> new Arrow.ArrowFactory());
        addFactorySupplier(ShapeType.BEZIER_CURVE, () -> new BezierCurve.BezierCurveFactory());
        addFactorySupplier(ShapeType.CIRCLE, () -> new Circle.CircleFactory());
        addFactorySupplier(ShapeType.ELLIPSE, () -> new Ellipse.EllipseFactory());
        addFactorySupplier(ShapeType.ELLIPTICAL_ARC, () -> new EllipticalArc.EllipticalArcFactory());
        addFactorySupplier(ShapeType.LINE, () -> new Line.LineFactory());
        addFactorySupplier(ShapeType.MOVIE, () -> new Movie.MovieFactory());
        addFactorySupplier(ShapeType.PARALLELOGRAM, () -> new Parallelogram.ParallelogramFactory());
        addFactorySupplier(ShapeType.PICTURE, () -> new Picture.PictureFactory());
        addFactorySupplier(ShapeType.IMAGE, () -> new Image.ImageFactory());
        addFactorySupplier(ShapeType.POLYGON, () -> new Polygon.PolygonFactory());
        addFactorySupplier(ShapeType.POLYLINE, () -> new PolyLine.PolyLineFactory());
        addFactorySupplier(ShapeType.QUADRATIC_CURVE, () -> new QuadraticCurve.QuadraticCurveFactory());
        addFactorySupplier(ShapeType.RECTANGLE, () -> new Rectangle.RectangleFactory());
        addFactorySupplier(ShapeType.REGULAR_POLYGON, () -> new RegularPolygon.RegularPolygonFactory());
        addFactorySupplier(ShapeType.SLICE, () -> new Slice.SliceFactory());
        addFactorySupplier(ShapeType.STAR, () -> new Star.StarFactory());
        addFactorySupplier(ShapeType.TEXT, () -> new Text.TextFactory());
        addFactorySupplier(ShapeType.TRIANGLE, () -> new Triangle.TriangleFactory());
        addFactorySupplier(ShapeType.SPLINE, () -> new Spline.SplineFactory());
        addFactorySupplier(ShapeType.BOW, () -> new Bow.BowFactory());
        addFactorySupplier(ShapeType.RING, () -> new Ring.RingFactory());
        addFactorySupplier(ShapeType.CHORD, () -> new Chord.ChordFactory());
        addFactorySupplier(ShapeType.ISOSCELES_TRAPEZOID, () -> new IsoscelesTrapezoid.IsoscelesTrapezoidFactory());
        addFactorySupplier(ShapeType.SVG_PATH, () -> new SVGPath.SVGPathFactory());
        addFactorySupplier(ShapeType.SPRITE, () -> new Sprite.SpriteFactory());
        addFactorySupplier(ShapeType.ORTHOGONAL_POLYLINE, () -> new OrthogonalPolyLine.OrthogonaPolylLineFactory());
        addFactorySupplier(ShapeType.MULTI_PATH, () -> new MultiPath.MultiPathFactory());
        addFactorySupplier(GroupType.GROUP, () -> new Group.GroupFactory());
        addFactorySupplier(NodeType.LAYER, () -> new Layer.LayerFactory());
        addFactorySupplier(NodeType.GRID_LAYER, () -> new GridLayer.GridLayerFactory());
        addFactorySupplier(NodeType.SCENE, () -> new Scene.SceneFactory());
        addFactorySupplier(NodeType.VIEWPORT, () -> new Viewport.ViewportFactory());
        addFactorySupplier(PaletteType.PALETTE, () -> new Palette.PaletteFactory());
        addFactorySupplier(PaletteType.PALETTE_ITEM, () -> new PaletteItem.PaletteItemFactory());
        addFactorySupplier(StorageEngineType.PRIMITIVE_FAST_ARRAY_STORAGE_ENGINE, () -> new PrimitiveFastArrayStorageEngine.PrimitiveFastArrayStorageEngineFactory());
        addFactorySupplier(StorageEngineType.SCENE_FAST_ARRAY_STORAGE_ENGINE, () -> new SceneFastArrayStorageEngine.SceneFastArrayStorageEngineFactory());
        addFactorySupplier(StorageEngineType.VIEWPORT_FAST_ARRAY_STORAGE_ENGINE, () -> new ViewportFastArrayStorageEngine.ViewportFastArrayStorageEngineFactory());
        addFactorySupplier(ImageFilterType.AlphaScaleColorImageDataFilterType, () -> new AlphaScaleColorImageDataFilter.AlphaScaleColorImageDataFilterFactory());
        addFactorySupplier(ImageFilterType.AverageGrayScaleImageDataFilterType, () -> new AverageGrayScaleImageDataFilter.AverageGrayScaleImageDataFilterFactory());
        addFactorySupplier(ImageFilterType.BrightnessImageDataFilterType, () -> new BrightnessImageDataFilter.BrightnessImageDataFilterFactory());
        addFactorySupplier(ImageFilterType.BumpImageDataFilterType, () -> new BumpImageDataFilter.BumpImageDataFilterFactory());
        addFactorySupplier(ImageFilterType.ColorDeltaAlphaImageDataFilterType, () -> new ColorDeltaAlphaImageDataFilter.ColorDeltaAlphaImageDataFilterFactory());
        addFactorySupplier(ImageFilterType.ColorLuminosityImageDataFilterType, () -> new ColorLuminosityImageDataFilter.ColorLuminosityImageDataFilterFactory());
        addFactorySupplier(ImageFilterType.ContrastImageDataFilterType, () -> new ContrastImageDataFilter.ContrastImageDataFilterFactory());
        addFactorySupplier(ImageFilterType.DiffusionImageDataFilterType, () -> new DiffusionImageDataFilter.DiffusionImageDataFilterFactory());
        addFactorySupplier(ImageFilterType.EdgeDetectImageDataFilterType, () -> new EdgeDetectImageDataFilter.EdgeDetectImageDataFilterFactory());
        addFactorySupplier(ImageFilterType.EmbossImageDataFilterType, () -> new EmbossImageDataFilter.EmbossImageDataFilterFactory());
        addFactorySupplier(ImageFilterType.ExposureImageDataFilterType, () -> new ExposureImageDataFilter.ExposureImageDataFilterFactory());
        addFactorySupplier(ImageFilterType.GainImageDataFilterType, () -> new GainImageDataFilter.GainImageDataFilterFactory());
        addFactorySupplier(ImageFilterType.GammaImageDataFilterType, () -> new GammaImageDataFilter.GammaImageDataFilterFactory());
        addFactorySupplier(ImageFilterType.HueImageDataFilterType, () -> new HueImageDataFilter.HueImageDataFilterFactory());
        addFactorySupplier(ImageFilterType.ImageDataFilterChainType, () -> new ImageDataFilterChain.ImageDataFilterChainFactory());
        addFactorySupplier(ImageFilterType.InvertColorImageDataFilterType, () -> new InvertColorImageDataFilter.InvertColorImageDataFilterFactory());
        addFactorySupplier(ImageFilterType.LightnessGrayScaleImageDataFilterType, () -> new LightnessGrayScaleImageDataFilter.LightnessGrayScaleImageDataFilterFactory());
        addFactorySupplier(ImageFilterType.LuminosityGrayScaleImageDataFilterType, () -> new LuminosityGrayScaleImageDataFilter.LuminosityGrayScaleImageDataFilterFactory());
        addFactorySupplier(ImageFilterType.PosterizeImageDataFilterType, () -> new PosterizeImageDataFilter.PosterizeImageDataFilterFactory());
        addFactorySupplier(ImageFilterType.RGBIgnoreAlphaImageDataFilterType, () -> new RGBIgnoreAlphaImageDataFilter.RGBIgnoreAlphaImageDataFilterFactory());
        addFactorySupplier(ImageFilterType.SharpenImageDataFilterType, () -> new SharpenImageDataFilter.SharpenImageDataFilterFactory());
        addFactorySupplier(ImageFilterType.SolarizeImageDataFilterType, () -> new SolarizeImageDataFilter.SolarizeImageDataFilterFactory());
        addFactorySupplier(ImageFilterType.StackBlurImageDataFilterType, () -> new StackBlurImageDataFilter.StackBlurImageDataFilterFactory());
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
