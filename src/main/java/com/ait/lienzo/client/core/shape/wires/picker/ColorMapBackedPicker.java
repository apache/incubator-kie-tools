package com.ait.lienzo.client.core.shape.wires.picker;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.BackingColorMapUtils;
import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.types.ColorKeyRotor;
import com.ait.lienzo.client.core.types.ImageData;
import com.ait.lienzo.client.core.types.PathPartEntryJSO;
import com.ait.lienzo.client.core.types.PathPartList;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;
import com.ait.tooling.nativetools.client.collection.NFastDoubleArrayJSO;
import com.ait.tooling.nativetools.client.collection.NFastStringMap;

public class ColorMapBackedPicker
{

    public static final int DOCKING_BORDER_WIDTH = 40;

    private final ImageData imageData;

    private final NFastStringMap<PickerPart> colorMap = new NFastStringMap<>();

    public static final ColorKeyRotor colorKeyRotor = new ColorKeyRotor();

    private final boolean addHotspots;

    private Layer layer;

    public ColorMapBackedPicker(Layer layer, NFastArrayList<WiresShape> shapes, ScratchPad scratchPad, WiresShape shapeToSkip, boolean addHotspots)
    {
        this.layer = layer;
        this.addHotspots = addHotspots;
        scratchPad.clear();

        Context2D ctx = scratchPad.getContext();

        addShapes(ctx, shapes, shapeToSkip);

        this.imageData = ctx.getImageData(0, 0, scratchPad.getWidth(), scratchPad.getHeight());
    }

    private void addShapes(Context2D ctx, NFastArrayList<WiresShape> shapes, WiresShape shapeToSkip)
    {
        for (int j = 0; j < shapes.size(); j++)
        {
            WiresShape prim = shapes.get(j);
            if (prim == shapeToSkip)
            {
                continue;
            }
            drawShape(ctx, colorKeyRotor.next(), new PickerPart(prim, PickerPart.ShapePart.BODY));
            if (addHotspots)
            {
                drawShape(ctx, colorKeyRotor.next(), new PickerPart(prim, PickerPart.ShapePart.BORDER));
            }

            if (prim.getChildShapes() != null)
            {
                addShapes(ctx, prim.getChildShapes(), shapeToSkip);
            }
        }
    }

    private void drawShape(Context2D ctx, String color, PickerPart pickerPart)
    {
        colorMap.put(color, pickerPart);
        MultiPath multiPath = pickerPart.getShape().getPath();
        NFastArrayList<PathPartList> listOfPaths = multiPath.getPathPartListArray();

        for (int k = 0; k < listOfPaths.size(); k++)
        {
            PathPartList path = listOfPaths.get(k);

            if (PickerPart.ShapePart.BODY.equals(pickerPart.getShapePart()))
            {
                ctx.setStrokeWidth(multiPath.getStrokeWidth());
            }
            else
            {
                ctx.setStrokeWidth(DOCKING_BORDER_WIDTH);
            }
            ctx.setStrokeColor(color);
            if (PickerPart.ShapePart.BODY.equals(pickerPart.getShapePart()))
            {
                ctx.setFillColor(color);
            }
            ctx.beginPath();

            Point2D absLoc = multiPath.getAbsoluteLocation();
            double offsetX = absLoc.getX();
            double offsetY = absLoc.getY();

            ctx.moveTo(offsetX, offsetY);

            boolean closed = false;
            for (int i = 0; i < path.size(); i++)
            {
                PathPartEntryJSO entry = path.get(i);
                NFastDoubleArrayJSO points = entry.getPoints();

                switch (entry.getCommand())
                {
                    case PathPartEntryJSO.MOVETO_ABSOLUTE:
                    {
                        ctx.moveTo(points.get(0) + offsetX, points.get(1) + offsetY);
                        break;
                    }
                    case PathPartEntryJSO.LINETO_ABSOLUTE:
                    {
                        points = entry.getPoints();
                        double x0 = points.get(0) + offsetX;
                        double y0 = points.get(1) + offsetY;
                        ctx.lineTo(x0, y0);
                        break;
                    }
                    case PathPartEntryJSO.CLOSE_PATH_PART:
                    {
                        if (PickerPart.ShapePart.BODY.equals(pickerPart.getShapePart()))
                        {
                            ctx.closePath();
                            closed = true;
                        }
                        break;
                    }
                    case PathPartEntryJSO.CANVAS_ARCTO_ABSOLUTE:
                    {
                        points = entry.getPoints();

                        double x0 = points.get(0) + offsetX;
                        double y0 = points.get(1) + offsetY;

                        double x1 = points.get(2) + offsetX;
                        double y1 = points.get(3) + offsetY;
                        double r = points.get(4);
                        ctx.arcTo(x0, y0, x1, y1, r);

                    }
                    break;
                }
            }

            if (!closed)
            {
                ctx.closePath();
            }

            if (PickerPart.ShapePart.BODY.equals(pickerPart.getShapePart()))
            {
                ctx.fill();
            }
            ctx.stroke();
        }
    }

    public PickerPart findShapeAt(int x, int y)
    {
        String color = BackingColorMapUtils.findColorAtPoint(imageData, x, y);
        if (color != null)
        {
            PickerPart pickerPart = colorMap.get(color);
            if (pickerPart != null)
            {
                return pickerPart;
            }
        }
        return null;
    }
}
