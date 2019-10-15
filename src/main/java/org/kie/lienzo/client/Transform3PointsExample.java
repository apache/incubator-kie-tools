package org.kie.lienzo.client;

import com.ait.lienzo.client.core.animation.IAnimation;
import com.ait.lienzo.client.core.animation.IAnimationCallback;
import com.ait.lienzo.client.core.animation.IAnimationHandle;
import com.ait.lienzo.client.core.animation.TimedAnimation;
import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Slice;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.core.types.Shadow;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.widget.panel.LienzoPanel;
import com.ait.lienzo.shared.core.types.ColorName;
import elemental2.dom.HTMLDivElement;
import org.kie.lienzo.client.BaseExample;

public class Transform3PointsExample extends BaseExample implements Example
{
    private static final int         WIDTH       = 200;
    private static final int         DX          = 20;
    private static final int         DY          = 20;
    private static final String      TEXT_FONT   = "oblique normal bold";
    private static final int         TEXT_SIZE   = 16;

    private              SliceGroup  node;

    private              DragPoint[] fromPoints;
    private              DragPoint[] toPoints;
    private              Button      animateButton;
    private              Text[]      animateText = new Text[3];
    private              Button      resetButton;
    private              Text[]      resetText   = new Text[3];

    private Transform originalTransform;
    private Transform fromTransform;
    private Transform toTransform;
    private              double[]    transform   = new double[6];

    public Transform3PointsExample(final String title)
    {
        super(title);
    }

    @Override
    public void init(final LienzoPanel panel, final HTMLDivElement topDiv) {
        super.init(panel, topDiv);

        node = new SliceGroup(WIDTH, "red");
        // Use a Transform to position the node, rather than using X,Y attributes,
        // because we're going to animate the transform (below)
        originalTransform = new Transform().translate(DX, DY);
        //originalTransform.

        node.setTransform(originalTransform);

        layer.add(node);

        fromPoints = new DragPoint[3];
        fromPoints[0] = new DragPoint("1", "blue", DX, DY + WIDTH);
        layer.add(fromPoints[0]);
        fromPoints[1] = new DragPoint("2", "blue", DX, DY);
        layer.add(fromPoints[1]);
        fromPoints[2] = new DragPoint("3", "blue", DX + WIDTH, DY);
        layer.add(fromPoints[2]);

        toPoints = new DragPoint[3];
        toPoints[0] = new DragPoint("1'", "red", 525, 325);
        layer.add(toPoints[0]);
        toPoints[1] = new DragPoint("2'", "red", 600, 350);
        layer.add(toPoints[1]);
        toPoints[2] = new DragPoint("3'", "red", 560, 250);
        layer.add(toPoints[2]);

        animateButton = new Button("Animate");
        animateButton.setX(200).setY(430);
        animateButton.addNodeMouseClickHandler((e) -> animate());
        animateButton.addNodeTouchStartHandler((e) -> animate());
        layer.add(animateButton);

        resetButton = new Button("Reset");
        resetButton.setX(400).setY(430).setVisible(false);
        resetButton.addNodeMouseClickHandler( (e) -> reset() );
        resetButton.addNodeTouchStartHandler( (e) -> reset() );
        layer.add(resetButton);

        animateText[0] = new Text("Drag the red target points to the desired location and click 'Animate'.", TEXT_FONT, TEXT_SIZE);
        animateText[0].setX(10).setY(500).setFillColor("black");
        layer.add(animateText[0]);
        animateText[1] = new Text("It will calculate the Transform that projects the blue (source) points onto the red (target) points,", TEXT_FONT, TEXT_SIZE);
        animateText[1].setX(10).setY(525).setFillColor("black");
        layer.add(animateText[1]);
        animateText[2] = new Text("and animate the transition.", TEXT_FONT, TEXT_SIZE);
        animateText[2].setX(10).setY(550).setFillColor("black");
        layer.add(animateText[2]);

        resetText[0] = new Text("Click 'Reset' to restart the demo.", TEXT_FONT, TEXT_SIZE);
        resetText[0].setX(10).setY(500).setFillColor("black").setVisible(false);
        layer.add(resetText[0]);
        resetText[1] = new Text("Note that the (blue) source points don't necessarily need to be on the shape.", TEXT_FONT, TEXT_SIZE);
        resetText[1].setX(10).setY(525).setFillColor("black").setVisible(false);
        layer.add(resetText[1]);
        resetText[2] = new Text("Try moving them before animating the next time.", TEXT_FONT, TEXT_SIZE);
        resetText[2].setX(10).setY(550).setFillColor("black").setVisible(false);
        layer.add(resetText[2]);
    }

    @Override public void run()
    {

    }

    public void animate()
    {
        Point2DArray src = new Point2DArray();
        for (int i = 0; i < fromPoints.length; i++)
        {
            src.push(new Point2D(fromPoints[i].getX(), fromPoints[i].getY()));
        }
        Point2DArray target = new Point2DArray();
        for (int i = 0; i < toPoints.length; i++)
        {
            target.push(new Point2D(toPoints[i].getX(), toPoints[i].getY()));
        }

        // Calculate the Transform to go from the 3 source points to the 3 target points
        fromTransform = node.getAbsoluteTransform();
        if (fromTransform == null)
        {
            fromTransform = new Transform();
        }

        // Here's the magic. We'll do the math for you!
        toTransform = Transform.create3PointTransform(src, target).multiply(fromTransform);

        TimedAnimation handle = new TimedAnimation(5000, new IAnimationCallback() {

            @Override
            public void onStart(IAnimation animation, IAnimationHandle handle) {
                // Hide the Animate button and associated text
                animateButton.setVisible(false);
                for (int i = 0; i < animateText.length; i++)
                    animateText[i].setVisible(false);
                repaint();
            }

            @Override
            public void onFrame(IAnimation animation, IAnimationHandle handle) {
                // Calculate the transform between the fromTransform and the toTransform.
                // If percent=0, transform will result in the fromTransform.
                // If percent=1, transform will result in the toTransform.
                // Any other percent value will fall somewhere in between (in a linear fashion.)
                for (int i = 0; i < 6; i++) // a Transform matrix has 6 values
                {
                    double d = fromTransform.get(i);
                    transform[i] = d + animation.getPercent() * (toTransform.get(i) - d);
                }

                // Set the Transform on the node
                node.setTransform(Transform.makeFromArray(transform));
                repaint();
            }

            @Override
            public void onClose(IAnimation animation, IAnimationHandle handle) {
                // Show the Reset button and associated text
                resetButton.setVisible(true);
                for (int i = 0; i < resetText.length; i++)
                {
                    resetText[i].setVisible(true);
                }
                repaint();
            }

        });
        handle.setNode(node);
        handle.run(); // start the animation
    }

    private void repaint()
    {
        layer.getScene().draw();
    }

    protected void reset()
    {
        animateButton.setVisible(true);
        for (int i = 0; i < animateText.length; i++)
            animateText[i].setVisible(true);

        resetButton.setVisible(false);
        for (int i = 0; i < resetText.length; i++)
            resetText[i].setVisible(false);

        // Put the node back in its original location
        node.setTransform(originalTransform);

        // Put the blue source points back
        fromPoints[0].setX(DX).setY(DY + WIDTH);
        fromPoints[1].setX(DX).setY(DY);
        fromPoints[2].setX(DX + WIDTH).setY(DY);

        repaint();
    }

    // A SliceGroup is a Group with a Rectangle and some Slices and Text inside the Rectangle.
    // One Slice is scaled and one is rotated.
    public static class SliceGroup extends Group
    {
        private double width;

        public SliceGroup(double w, String color)
        {
            width = w;

            add(new Rectangle(w, w).setStrokeColor(color));

            double r = w/4;
            Slice s = new Slice(r, 0, Math.PI / 2, true);
            s.setX(r).setY(r);
            s.setFillColor(color);
            s.setDraggable(true);
            add(s);

            Text t = new Text("Slices", "oblique normal bold", w/15);
            t.setX(r * 0.6).setY(r * 0.8);
            t.setFillColor(ColorName.YELLOW);
            add(t);

            s = new Slice(r, 0.75 * Math.PI, 3 * Math.PI / 2, true);
            s.setX(3 * r).setY(r);
            s.setScale(0.5);
            s.setFillColor(color);
            s.setDraggable(true);
            add(s);

            s = new Slice(r, 0, Math.PI);
            s.setX(r).setY(3 * r);
            s.setRotation(Math.PI / 4);
            s.setFillColor(color);
            s.setDraggable(true);
            add(s);
        }

        public SliceGroup addSliceGroup(String color)
        {
            SliceGroup s = new SliceGroup(width, color);
            s.setRotation(-Math.PI / 2);
            s.setScale(0.5);
            double r = width / 4;
            s.setX(r*2).setY(r*4);

            add(s);

            return s;
        }
    }

    public static class DragPoint extends Group
    {
        public DragPoint(String label, String color, int x, int y)
        {
            Circle c = new Circle(10);
            c.setFillColor(color);
            c.setStrokeColor(ColorName.BLACK);
            c.setStrokeWidth(1);
            add(c);

            Text text = new Text(label);
            text.setFillColor(ColorName.WHITE);
            text.setFontSize(8);
            text.setX(-4);
            text.setY(3);
            add(text);

            setX(x);
            setY(y);
            setDraggable(true);
        }
    }

    public class Button extends Group
    {
        public Button(String label)
        {
            Rectangle r = new Rectangle(100, 30, 5);
            r.setFillColor("green");
            r.setShadow(new Shadow(ColorName.DARKGREEN.getValue(), 6, 6, 6));
            add(r);

            Text text = new Text(label);
            text.setFillColor("white");
            text.setFontSize(16);
            text.setX(15);
            text.setY(20);
            text.setListening(false);

            add(text);
        }
    }
}
