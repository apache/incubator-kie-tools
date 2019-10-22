package org.kie.lienzo.client;

import com.ait.lienzo.client.core.image.Image;
import com.ait.lienzo.client.core.image.ImageStrip;
import com.ait.lienzo.client.core.image.ImageStrips;
import com.ait.lienzo.client.core.shape.IPrimitive;

/*
    Some improvement I've in mind, but cannot find the right methods in the Elemental2 API...
        // OLD stuff
        // on image.onLoad -> synchronous load & decode (not fast)

        // NEW stuff
        // instead, we can use the async APIs:
        // - 1) fetch data (blob from the network)
        // - 2) callback once decode is being done on the background (by the browser)
        as:
        fetch(url).then(response => {
            response.blob().then(blob => {
            createImageBitmap(blob).then(image => {
                // use the image...
            });
        });
 */
public class ImageStripExample extends BaseExample implements Example {

    private static final int STRIP_SIZE = 86;
    private static final int STRIP_ICON_SIZE = 16;
    private static final int STRIP_ICON_PADDING = 5;
    private static final String STRIP_NAME = "bpmn-icons";
    private static final String STRIP_IMAGE_PATH = "images/bpmn-icons-strip.png";

    public ImageStripExample(final String title) {
        super(title);
    }

    @Override
    public void run() {
        ImageStrip bpmnStrip = new ImageStrip(STRIP_NAME,
                                              STRIP_IMAGE_PATH,
                                              STRIP_ICON_SIZE,
                                              STRIP_ICON_SIZE,
                                              STRIP_ICON_PADDING,
                                              ImageStrip.Orientation.VERTICAL);

        // TODO: Destroy strip as well
        ImageStrips.get().register(bpmnStrip,
                                   this::onStripRegistered);

        layer.draw();
    }


    private void onStripRegistered() {

        int initial_x = 100;
        int max_x = 630;
        double x = initial_x;
        double y = 150;

        for (int i = 0; i < STRIP_SIZE; i++) {
            layer.add(new Image(STRIP_NAME, i)
                              .setX(x)
                              .setY(y));
            double next = x + STRIP_ICON_SIZE + STRIP_ICON_PADDING + 5;
            if (next > max_x) {
                x = initial_x;
                y += 50;
            } else {
                x = next;
            }
        }

        layer.draw();
    }

    private double x = 150;
    private double y = 150;
    private double inc = 100;

    private void add(IPrimitive<?> prim) {
        layer.add(prim.setX(x).setY(y));
        y += inc;
    }
}
