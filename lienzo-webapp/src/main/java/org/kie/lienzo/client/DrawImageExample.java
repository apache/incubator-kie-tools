package org.kie.lienzo.client;

import com.ait.lienzo.client.core.image.PictureLoadedHandler;
import com.ait.lienzo.client.core.image.filter.AbstractImageDataFilter;
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
import com.ait.lienzo.client.core.image.filter.ImageDataFilter;
import com.ait.lienzo.client.core.image.filter.InvertColorImageDataFilter;
import com.ait.lienzo.client.core.image.filter.LightnessGrayScaleImageDataFilter;
import com.ait.lienzo.client.core.image.filter.LuminosityGrayScaleImageDataFilter;
import com.ait.lienzo.client.core.image.filter.PosterizeImageDataFilter;
import com.ait.lienzo.client.core.image.filter.RGBIgnoreAlphaImageDataFilter;
import com.ait.lienzo.client.core.image.filter.SharpenImageDataFilter;
import com.ait.lienzo.client.core.image.filter.SolarizeImageDataFilter;
import com.ait.lienzo.client.core.image.filter.StackBlurImageDataFilter;
import com.ait.lienzo.client.core.shape.Picture;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.widget.panel.LienzoPanel;
import com.ait.lienzo.shared.core.types.ColorName;
import com.google.gwt.dom.client.Style.Display;
import elemental2.core.JsArray;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLOptionElement;
import elemental2.dom.HTMLSelectElement;
import org.kie.lienzo.client.util.Util;

import static elemental2.dom.DomGlobal.document;

public class DrawImageExample extends BaseExample implements Example {

    private final String IMAGE_URL = "https://upload.wikimedia.org/wikipedia/commons/thumb/9/91/Peach_poster_rodents.jpg/640px-Peach_poster_rodents.jpg";

    private HTMLInputElement slider = (HTMLInputElement) DomGlobal.document.createElement("input");

    private JsArray<Picture> pictures;

    private HTMLSelectElement select;

    public DrawImageExample(final String title) {
        super(title);
    }

    @Override
    public void init(final LienzoPanel panel, final HTMLDivElement topDiv) {
        super.init(panel, topDiv);
        topDiv.style.display = Display.INLINE_BLOCK.getCssName();

        pictures = new JsArray<Picture>();

        slider.type = "range";
        slider.min = "25";
        slider.max = "100";
        slider.step = "5";
        slider.value = "25";
        slider.oninput = (e) -> {
            double scale = Double.parseDouble(slider.value);
            setScale(scale / 100);
            console.log("scale " + slider.value + "%");
            layer.batch();
            return null;
        };
        topDiv.appendChild(slider);

        select = (HTMLSelectElement) document.createElement("select");
        HTMLOptionElement option = addOption("None", select);
        option.selected = true;
        addOption("Grey Scale", select);
        addOption("Brightness", select);
        addOption("Contrast", select);
        addOption("Alpha Scale Color", select);
        addOption("Color Delta Alpha", select);
        addOption("Color Luminosity", select);
        addOption("Bump", select);
        addOption("Diffusion", select);
        addOption("Edge Detection", select);
        addOption("Emboss", select);
        addOption("Exposure", select);
        addOption("Gain", select);
        addOption("Gamma", select);
        addOption("Hue", select);
        addOption("Invert Color", select);
        addOption("Lightness Grey Scale", select);
        addOption("Luminosity Grey Scale", select);
        addOption("Posterize", select);
        addOption("RGB Ignore", select);
        addOption("Sharpen", select);
        addOption("Solarize", select);
        addOption("Stack Blur", select);
        addOption("None", select);

        topDiv.appendChild(select);

        select.onchange = (e) -> {
            AbstractImageDataFilter filter = null;
            switch (select.value) {
                case "Grey Scale":
                    filter = new AverageGrayScaleImageDataFilter();
                    break;
                case "Alpha Scale Color":
                    filter = new AlphaScaleColorImageDataFilter(ColorName.RED);
                    break;
                case "Color Delta Alpha":
                    filter = new ColorDeltaAlphaImageDataFilter(ColorName.RED, 50);
                    break;
                case "Color Luminosity":
                    filter = new ColorLuminosityImageDataFilter(ColorName.RED);
                    break;
                case "Brightness":
                    filter = new BrightnessImageDataFilter();
                    break;
                case "Contrast":
                    filter = new ContrastImageDataFilter();
                    break;
                case "Bump":
                    filter = new BumpImageDataFilter();
                    break;
                case "Emboss":
                    filter = new EmbossImageDataFilter();
                    break;
                case "Diffusion":
                    filter = new DiffusionImageDataFilter();
                    break;
                case "Edge Detection":
                    filter = new EdgeDetectImageDataFilter();
                    break;
                case "Exposure":
                    filter = new ExposureImageDataFilter();
                    break;
                case "Gain":
                    filter = new GainImageDataFilter();
                    break;
                case "Gamma":
                    filter = new GammaImageDataFilter();
                    break;
                case "Hue":
                    filter = new HueImageDataFilter();
                    break;
                case "Invert Color":
                    filter = new InvertColorImageDataFilter();
                    break;
                case "Lightness Grey Scale":
                    filter = new LightnessGrayScaleImageDataFilter();
                    break;
                case "Luminosity Grey Scale":
                    filter = new LuminosityGrayScaleImageDataFilter();
                    break;
                case "Posterize":
                    filter = new PosterizeImageDataFilter();
                    break;
                case "RGB Ignore":
                    filter = new RGBIgnoreAlphaImageDataFilter();
                    break;
                case "Sharpen":
                    filter = new SharpenImageDataFilter();
                    break;
                case "Solarize":
                    filter = new SolarizeImageDataFilter();
                    break;
                case "Stack Blur":
                    filter = new StackBlurImageDataFilter();
                    break;
                case "None":
                    clearImageDataFilter();
                    break;
            }
            if (filter != null) {
                console.log("Filter Request Start: " + select.value);
                setImageDataFilter(filter);
                console.log("Filter Finished End: " + select.value);
            }
            layer.batch();
            return null;
        };
    }

    private HTMLOptionElement addOption(String filter, HTMLSelectElement select) {
        HTMLOptionElement option = (HTMLOptionElement) document.createElement("option");
        option.label = filter;
        option.value = filter;
        select.add(option);
        return option;
    }

    @Override
    public void run() {
        for (int i = 0; i < 3; i++) {
            final int pos = i;
            PictureLoadedHandler handler = (e) ->
            {
                Picture picture = pictures.getAt(pos);
                picture.setScale(0.25);
                BoundingBox box = picture.getBoundingBox();
                Util.setLocation(picture, width, height, 5, 5, 5, 5);
                console.log("Image Loaded");

                layer.batch();
            };
            Picture picture = new Picture(IMAGE_URL, handler);
            picture.setDraggable(true);
            layer.add(picture);
            pictures.push(picture);
        }
    }

    public void setScale(double scale) {
        for (Picture picture : pictures.asList()) {
            picture.setScale(scale);
        }
    }

    public void setImageDataFilter(ImageDataFilter filter) {
        for (Picture picture : pictures.asList()) {
            picture.getImageProxy().setFilters(filter);
            picture.reFilter((e) -> {
            });
        }
    }

    public void clearImageDataFilter() {
        for (Picture picture : pictures.asList()) {
            picture.getImageProxy().clearFilters();
            picture.reFilter((e) -> {
            });
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        select.remove();
        slider.remove();
        pictures = null;
        console.log("Destroying Draw Image ->");
    }
}
