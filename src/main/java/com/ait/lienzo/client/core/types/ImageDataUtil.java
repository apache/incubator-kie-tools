package com.ait.lienzo.client.core.types;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.util.ScratchPad;

import elemental2.dom.ImageData;

public class ImageDataUtil
{

    private ImageDataUtil()
    {

    }

    /**
     * Offsets for each color use RGBA ordering.
     */
    public static final  int        OFFSET_RED   = 0;

    public static final  int        OFFSET_GREEN = 1;

    public static final  int        OFFSET_BLUE  = 2;

    public static final  int        OFFSET_ALPHA = 3;

    private static final ScratchPad SCRATCH      = new ScratchPad(1, 1);

    /**
     * ImageData can't be cloned or deep-copied, it's an internal data structure and has some CRAZY crap in it, this is cheeeeeezy, but hey, it works, and it's portable!!!
     */
    public static final ImageData copy(ImageData image)
    {
        final Context2D context = new ScratchPad(image.width, image.height).getContext();

        context.putImageData(image, 0, 0);

        return context.getImageData(0, 0, image.width, image.height);
    }

    public static ImageData create(ImageData imageData)
    {
        return SCRATCH.getContext().createImageData(imageData);
    }

    /**
     * Returns the alpha value at position (x,y).
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the alpha value at position (x,y), or 0 if not in the image
     * @see #setAlphaAt(ImageData, int, int, int)
     * @see #getColorAt(ImageData, int, int, int)
     */
    public static final int getAlphaAt(ImageData image, final int x, final int y)
    {
        return getColorAt(image, x, y, OFFSET_ALPHA);
    }

    /**
     * Returns the blue value at position (x,y).
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the blue value at position (x,y), or 0 if not in the image
     * @see #setBlueAt(ImageData,int, int, int)
     * @see #getColorAt(ImageData, int, int, int)
     */
    public static final int getBlueAt(ImageData image, final int x, final int y)
    {
        return getColorAt(image, x, y, OFFSET_BLUE);
    }

    /**
     * Returns the green value at position (x,y).
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the green value at position (x,y), or 0 if not in the image
     * @see #setGreenAt(ImageData, int, int, int)
     * @see #getColorAt(ImageData, int, int, int)
     */
    public static final int getGreenAt(ImageData image, final int x, final int y)
    {
        return getColorAt(image, x, y, OFFSET_GREEN);
    }


    /**
     * Returns the red value at position (x,y).
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the red value at position (x,y), or 0 if not in the image
     * @see #setRedAt(ImageData, int, int, int)
     * @see #getColorAt(ImageData, int, int, int)
     */
    public static final int getRedAt(ImageData image, final int x, final int y)
    {
        return getColorAt(image, x, y, OFFSET_RED);
    }

    /**
     * Sets the alpha value at position (x,y).
     *
     * @param alpha the alpha value
     * @param x the x coordinate
     * @param y the y coordinate
     * @see #getAlphaAt(ImageData, int, int)
     * @see #getColorAt(ImageData, int, int, int)
     */
    public static final void setAlphaAt(ImageData image, final int alpha, final int x, final int y)
    {
        setColorAt(image, alpha, x, y, OFFSET_ALPHA);
    }

    /**
     * Sets the blue value at position (x,y).
     *
     * @param blue the blue value
     * @param x the x coordinate
     * @param y the y coordinate
     * @see #getBlueAt(ImageData, int, int)
     * @see #getColorAt(ImageData, int, int, int)
     */
    public static final void setBlueAt(ImageData image, final int blue, final int x, final int y)
    {
        setColorAt(image, blue, x, y, OFFSET_BLUE);
    }

    /**
     * Sets the green value at position (x,y).
     *
     * @param green the green value
     * @param x the x coordinate
     * @param y the y coordinate
     * @see #getGreenAt(ImageData, int, int)
     * @see #getColorAt(ImageData, int, int, int)
     */
    public static final void setGreenAt(ImageData image, final int green, final int x, final int y)
    {
        setColorAt(image, green, x, y, OFFSET_GREEN);
    }

    /**
     * Sets the red value at position (x,y).
     *
     * @param red the red value
     * @param x the x coordinate
     * @param y the y coordinate
     * @see #getRedAt(ImageData, int, int)
     * @see #getColorAt(ImageData, int, int, int)
     */
    public static final void setRedAt(ImageData image, final int red, final int x, final int y)
    {
        setColorAt(image, red, x, y, OFFSET_RED);
    }

    /**
     * Returns the color value at position (x,y) with the specified offset.
     *
     * Colors are stored in RGBA format, where the offset determines the color
     * channel (R, G, B, or A). The values are stored in row-major order. If the
     * specified location is not in the image, 0 is returned.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param offset the color offset
     * @return the color value at position (x,y), or 0 if not in the image
     * @see #setColorAt(ImageData, int, int, int, int)
     */
    private static int getColorAt(ImageData image, int x, int y, int offset)
    {
        if(image != null && image.data != null) {
            return (int) image.data.getAt(4 * (x + y * image.width) + offset).doubleValue();
        }
        return 0;
    }

    /**
     * Sets the color value at position (x,y) with the specified offset.
     *
     * Colors are stored in RGBA format, where the offset determines the color
     * (R, G, B, or A.) The values are stored in row-major order.
     *
     * @param color the color (in the range 0...255)
     * @param x the x coordinate
     * @param y the y coordinate
     * @param offset the color offset
     * @see #getColorAt(ImageData, int, int, int)
     */
    private static void setColorAt(ImageData image, int color, int x, int y, int offset)
    {
        image.data.setAt(4 * (x + y * image.width) + offset, (double) color);
    }
}
