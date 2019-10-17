package org.kie.lienzo.client;

import java.util.Map;
import java.util.function.Consumer;

import com.ait.lienzo.client.core.mediator.EventFilter;
import com.ait.lienzo.client.core.mediator.MousePanMediator;
import com.ait.lienzo.client.core.mediator.MouseWheelZoomMediator;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.widget.panel.LienzoPanel;
import elemental2.dom.CSSProperties;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLOptionElement;
import elemental2.dom.HTMLSelectElement;
import org.kie.lienzo.client.util.Console;
import org.kie.lienzo.client.util.Util;

import static elemental2.dom.DomGlobal.document;

public abstract class BaseExample implements Example
{
    private String title;
    protected LienzoPanel panel;
    protected Layer layer;

    protected Console console;

    protected int width;
    protected int height;

    protected int leftPadding = 5;
    protected int topPadding = 5;
    protected int rightPadding = 5;
    protected int bottomPadding = 5;

    protected int widthOffset;
    protected int heightOffset;

    protected HTMLDivElement topDiv;

    public BaseExample(final String title)
    {
        this.title = title;
        console = new Console();
    }

    @Override
    public String getTitle()
    {
        return title;
    }

    @Override
    public void init(final LienzoPanel panel, HTMLDivElement topDiv)
    {
        this.topDiv = topDiv;

        this.panel = panel;
        this.layer = new Layer();
        this.panel.add(this.layer);

        width = panel.getWidePx();
        height = panel.getHighPx();

        MouseWheelZoomMediator zoom = new MouseWheelZoomMediator(EventFilter.SHIFT);
        panel.getViewport().pushMediator(zoom);

        MousePanMediator pan = new MousePanMediator(EventFilter.META);
        this.panel.getViewport().pushMediator(pan);
    }

    @Override public int getWidthOffset()
    {
        return widthOffset;
    }

    @Override public int getHeightOffset()
    {
        return heightOffset;
    }

    public void setRandomLocation(Shape shape)
    {
        Util.setLocation(shape, width, height, leftPadding, topPadding, rightPadding, bottomPadding);
    }

    @Override
    public void destroy()
    {
        panel.destroy();
    }


    @Override
    public void onResize()
    {
        width = panel.getWidePx();
        height = panel.getHighPx();
    }

    public static HTMLDivElement createDiv() {
        return (HTMLDivElement) DomGlobal.document.createElement("div");
    }

    public static HTMLDivElement createText(String text) {
        HTMLDivElement div = createDiv();
        div.textContent = text;
        return div;
    }

    public static HTMLButtonElement createButton(String text,
                                                 Runnable clickCallback) {
        HTMLButtonElement button = (HTMLButtonElement) DomGlobal.document.createElement("button");
        button.textContent = text;
        setMargins(button);
        button.onclick = (e) -> {
            clickCallback.run();
            return null;
        };
        return button;
    }

    public static HTMLSelectElement createSelect(Map<String, String> options,
                                                 Consumer<String> selectCallback) {
        HTMLSelectElement select = (HTMLSelectElement) document.createElement("select");
        options.entrySet().forEach(entry -> addOption(select, entry.getKey(), entry.getValue()));
        setMargins(select);
        select.onchange = (e) -> {
            selectCallback.accept(select.value);
            return null;
        };

        return select;
    }

    public static void addOption(HTMLSelectElement select,
                                 String label,
                                 String value) {
        HTMLOptionElement option = (HTMLOptionElement) document.createElement("option");
        option.label = label;
        option.value = value;
        select.add(option);
    }

    private static void setMargins(HTMLElement e) {
        e.style.marginLeft = CSSProperties.MarginLeftUnionType.of("5px");
        e.style.marginRight = CSSProperties.MarginRightUnionType.of("5px");
    }

}
