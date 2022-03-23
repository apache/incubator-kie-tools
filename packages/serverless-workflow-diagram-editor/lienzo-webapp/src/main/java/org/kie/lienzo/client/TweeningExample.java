package org.kie.lienzo.client;

import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationProperty;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.IAnimationHandle;
import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.widget.panel.LienzoPanel;
import com.ait.lienzo.shared.core.types.Color;
import com.google.gwt.dom.client.Style.Display;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLOptionElement;
import elemental2.dom.HTMLSelectElement;

import static elemental2.dom.DomGlobal.document;

public class TweeningExample extends BaseExample implements Example {

    private double xMoveBy;
    private Circle circle;
    private int RADIUS = 80;
    private IAnimationHandle handle;
    HTMLSelectElement select;

    public TweeningExample(final String title) {
        super(title);
    }

    @Override
    public void init(final LienzoPanel panel, final HTMLDivElement topDiv) {
        super.init(panel, topDiv);
        topDiv.style.display = Display.INLINE_BLOCK.getCssName();

        select = (HTMLSelectElement) document.createElement("select");
        for (TweenerTypes tweenerTypes : TweenerTypes.values()) {
            addOption(tweenerTypes, select);
        }
        topDiv.appendChild(select);

        select.onchange = (e) -> {
            TweenerTypes tweenerTypes = TweenerTypes.valueOf(select.value);
            tween(tweenerTypes);
            return null;
        };
    }

    private void addOption(TweenerTypes tweener, HTMLSelectElement select) {
        HTMLOptionElement option = (HTMLOptionElement) document.createElement("option");
        option.label = tweener.name();
        option.value = tweener.name();
        select.add(option);
    }

    @Override
    public void destroy() {
        super.destroy();
        select.remove();
    }

    @Override
    public void run() {
        final int x = width - RADIUS * 2;
        final int y = height / 2;
        xMoveBy = width - (RADIUS * 4);

        circle = new Circle(RADIUS);
        circle.setAlpha(0.75).setX(x).setY(y).setStrokeColor(Color.getRandomHexColor()).setStrokeWidth(2).setFillColor(Color.getRandomHexColor());
        layer.add(circle);
    }

    public void tween(TweenerTypes tweenerType) {
        if (handle != null) {
            handle.stop();
            handle = null;
            int x = width - RADIUS * 2;
            int y = height / 2;
            circle.setX(x).setY(y);
        }

        int displace = (int) (circle.getX() - xMoveBy);

        switch (tweenerType) {
            case LINEAR:
                handle = circle.animate(AnimationTweener.LINEAR, new AnimationProperties().push(AnimationProperty.Properties.X(displace)), 4000);
                break;
            case EASE_IN:
                handle = circle.animate(AnimationTweener.EASE_IN, new AnimationProperties().push(AnimationProperty.Properties.X(displace)), 4000);
                break;
            case EASE_OUT:
                handle = circle.animate(AnimationTweener.EASE_IN_OUT, new AnimationProperties().push(AnimationProperty.Properties.X(displace)), 4000);
                break;
            case EASE_IN_OUT:
                handle = circle.animate(AnimationTweener.EASE_IN_OUT, new AnimationProperties().push(AnimationProperty.Properties.X(displace)), 4000);
                break;
            case ELASTIC:
                handle = circle.animate(AnimationTweener.ELASTIC, new AnimationProperties().push(AnimationProperty.Properties.X(displace)), 4000);
                break;
            default:
                break;
        }
    }

    public enum TweenerTypes {
        NONE,
        LINEAR,
        EASE_IN,
        EASE_OUT,
        EASE_IN_OUT,
        ELASTIC;
    }
}
