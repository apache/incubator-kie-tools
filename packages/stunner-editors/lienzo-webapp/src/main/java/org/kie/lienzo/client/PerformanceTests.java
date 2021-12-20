package org.kie.lienzo.client;

import java.util.function.Supplier;

import com.ait.lienzo.client.core.animation.AnimationCallback;
import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationProperty;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.IAnimation;
import com.ait.lienzo.client.core.animation.IAnimationHandle;
import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.widget.panel.LienzoPanel;
import com.ait.lienzo.shared.core.types.Color;
import com.google.gwt.core.client.Duration;
import com.google.gwt.dom.client.Style.Display;
import elemental2.dom.CSSProperties;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import org.kie.lienzo.client.util.FPSCounter;
import org.kie.lienzo.client.util.Util;

public class PerformanceTests extends BaseExample implements Example {

    private static final int TOTAL_CIRCLES = 100;

    private HTMLButtonElement decreaseCountButton;
    private HTMLDivElement countText;
    private HTMLButtonElement increaseCountButton;
    private HTMLButtonElement runTestButton;
    private HTMLDivElement timeText;
    private HTMLButtonElement startAnimationButton;
    private HTMLButtonElement stopAnimationButton;
    private int count = TOTAL_CIRCLES;
    private Shape[] instances;
    private IAnimationHandle[] handles;
    private boolean isAnimationEnabled = false;
    private FPSCounter fpsCounter;

    private static Supplier<Shape> shapeBuilder =
            () -> new Circle(10).setStrokeColor(Color.getRandomHexColor()).setStrokeWidth(2).setFillColor(Color.getRandomHexColor()).setDraggable(true);

    public PerformanceTests(String title) {
        super(title);
    }

    @Override
    public void init(LienzoPanel panel,
                     HTMLDivElement topDiv) {
        super.init(panel, topDiv);

        HTMLDivElement fpsDiv = (HTMLDivElement) DomGlobal.document.createElement("div");
        HTMLDivElement buttonsDiv = (HTMLDivElement) DomGlobal.document.createElement("div");
        buttonsDiv.style.display = Display.INLINE_FLEX.getCssName();
        topDiv.style.display = Display.INLINE.getCssName();
        topDiv.appendChild(fpsDiv);
        topDiv.appendChild(buttonsDiv);
        heightOffset = 30;

        fpsCounter = FPSCounter.toElement(displayer -> {
            displayer.style.cssFloat = "right";
            fpsDiv.appendChild(displayer);
        }).start();

        decreaseCountButton = createButton("[-]", this::decreaseInstanceCount);
        buttonsDiv.appendChild(decreaseCountButton);

        countText = createText("" + count);
        buttonsDiv.appendChild(countText);

        increaseCountButton = createButton("[+]", this::increaseInstanceCount);
        buttonsDiv.appendChild(increaseCountButton);

        runTestButton = createButton("Run", this::run);
        buttonsDiv.appendChild(runTestButton);

        timeText = createText("" + count);
        timeText.style.color = "blue";
        buttonsDiv.appendChild(timeText);

        startAnimationButton = createButton("Start", this::startAnimation);
        startAnimationButton.style.marginLeft = CSSProperties.MarginLeftUnionType.of("25px");
        buttonsDiv.appendChild(startAnimationButton);
        stopAnimationButton = createButton("Stop", this::stopAnimation);
        buttonsDiv.appendChild(stopAnimationButton);
        updateAnimationButtonsState();

        width = 300;
        height = 300;
    }

    private void decreaseInstanceCount() {
        setCountIncrement(-1 * getCountInc());
    }

    private void increaseInstanceCount() {
        setCountIncrement(getCountInc());
    }

    private void updateAnimationButtonsState() {
        startAnimationButton.disabled = isAnimationEnabled;
        stopAnimationButton.disabled = !isAnimationEnabled;
    }

    private void stopAnimation() {
        isAnimationEnabled = false;
        if (null != handles) {
            for (int i = 0; i < handles.length; i++) {
                IAnimationHandle handle = handles[i];
                handle.stop();
                handles[i] = null;
            }
            handles = null;
        }
        updateAnimationButtonsState();
    }

    private void startAnimation() {
        isAnimationEnabled = true;
        handles = new IAnimationHandle[instances.length];
        int i = 0;
        for (Shape instance : instances) {
            animateInstance(i, instance);
            i++;
        }
        updateAnimationButtonsState();
    }

    private IAnimationHandle animateInstance(final int i,
                                             final Shape shape) {
        final double[] location = Util.getRandomLocation(shape, width, height, leftPadding, topPadding, rightPadding, bottomPadding);
        // DomGlobal.console.log("Shape animated to [" + location[0] + ", " + location[1] + "]");
        IAnimationHandle handle = shape.animate(AnimationTweener.LINEAR,
                                                AnimationProperties.toPropertyList(AnimationProperty.Properties.X(location[0]),
                                                                                   AnimationProperty.Properties.Y(location[1])),
                                                500,
                                                new AnimationCallback() {
                                                    @Override
                                                    public void onStart(IAnimation animation, IAnimationHandle handle) {
                                                        super.onStart(animation, handle);
                                                    }

                                                    @Override
                                                    public void onFrame(IAnimation animation, IAnimationHandle handle) {
                                                        super.onFrame(animation, handle);
                                                    }

                                                    @Override
                                                    public void onClose(IAnimation animation, IAnimationHandle handle) {
                                                        super.onClose(animation, handle);
                                                        if (isAnimationEnabled) {
                                                            animateInstance(i, shape);
                                                        }
                                                    }
                                                });
        handles[i] = handle;
        return handle;
    }

    private void setCountIncrement(int value) {
        if (count + value > 0) {
            count = count + value;
            countText.textContent = "" + count;
            run();
        }
    }

    @Override
    public void run() {
        boolean wasAnimationEnabled = isAnimationEnabled;

        clear();

        Duration duration = new Duration();

        instances = new Shape[count];
        for (int i = 0; i < count; i++) {
            final Shape shape = shapeBuilder.get();
            instances[i] = shape;
            setRandomLocation(shape);
            layer.add(shape);
        }

        layer.draw();

        int ellapsed = duration.elapsedMillis();
        console.log("Rendering Test completed - #[" + count + "] took [" + ellapsed + "ms]");
        timeText.textContent = "[ " + ellapsed + "ms ]";

        if (wasAnimationEnabled) {
            startAnimation();
        }
    }

    @Override
    public void onResize() {
        super.onResize();
        for (Shape shape : instances) {
            setRandomLocation(shape);
        }
    }

    private void clear() {
        stopAnimation();
        if (null != instances) {
            for (Shape instance : instances) {
                instance.removeFromParent();
            }
            instances = null;
        }
        layer.clear();
        layer.draw();
    }

    private int getCountInc() {
        return count / 4;
    }

    @Override
    public void destroy() {
        super.destroy();
        clear();
        fpsCounter.destroy();
        fpsCounter = null;
        decreaseCountButton.remove();
        countText.remove();
        increaseCountButton.remove();
        runTestButton.remove();
        timeText.remove();
        startAnimationButton.remove();
        stopAnimationButton.remove();
    }
}