package org.dashbuilder.renderer.chartjs.lib;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import org.dashbuilder.renderer.chartjs.lib.event.*;
import org.dashbuilder.renderer.chartjs.lib.options.*;
import org.dashbuilder.renderer.chartjs.lib.resources.ChartStyle;
import org.dashbuilder.renderer.chartjs.lib.resources.Resources;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all chart widgets<br/>
 * Class describes generic behavior of all chart widgets
 */
public abstract class Chart extends SimplePanel implements HasAnimationCompleteHandlers, HasClickHandlers,HasAnimation, HasDataSelectionEventHandlers, IsResponsive{

    private static Resources resources;

    protected LegendOption options = LegendOption.get();
    protected JavaScriptObject nativeCanvas;
	private CanvasElement canvas;
	protected ChartStyle style;
    protected List<AnimationCallback> callbackList = new ArrayList<AnimationCallback>();
	
	
	static{
		resources = GWT.create(Resources.class);
	}
	
	/**
	 * This constructor creates new chart instance with custom {@link ChartStyle}
	 * @param style - new CssResource used for styling charts
	 */
	public Chart(ChartStyle style){
		//setChartStyle(style);
        registerNativeAnimationHandlers();
		canvas = Document.get().createCanvasElement();
		getElement().appendChild(canvas);
        sinkEvents(Event.ONCLICK);
        addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent clickEvent) {
                JavaScriptObject obj = clickEvent.getNativeEvent().cast();

                JavaScriptObject data = getClickPoints(obj, nativeCanvas);
                if (data != null)
                    DataSelectionEvent.fire(Chart.this, Chart.this, data);
            }
        });
	}
	
	/**
	 * Constructor creates chart with default style
	 */
	public Chart() {
		this(resources.chartStyle());
	}

    private native JavaScriptObject getClickPoints(JavaScriptObject event, JavaScriptObject canvas)/*-{
        if(canvas == null || event == null)
            return null;
        try {
            return canvas.getPointsAtEvent(event);
        }
        catch(e){
            //exception occurred when added additional ClickHandler which destroys chart before processing.
            return null;
        }
    }-*/;

	/**
	 * Set new style to the char widget. New style will be injected automatically.<br/>
	 * NOTICE: new style will be applied after re-drawing of chart<br/>
	 * @param style
	 */
	public void setChartStyle(ChartStyle style){
		style.ensureInjected();
		setStylePrimaryName(style.chart());
	}

    protected void processEvents(JavaScriptObject object){
        this.nativeCanvas = object;
    }

	@Override
	protected void onAttach() {
		ChartJs.ensureInjected();
		super.onAttach();
		draw();
	}

	/**
	 * Method re-drawing chart widget without re-requesting data from data provider.<br/>
	 * To update data call {@link #reload()} method instead
	 */
	public abstract void update();
	
	/**
	 * Method requesting data from data provider and re-drawing chart.
	 */
	public abstract void reload();
	
	/**
	 * Method preparing data and invoking native draw method<br/>
	 * This method should not be overridden by sub-classes
	 */
	protected abstract void draw();
	
	/**
	 * Method sets pixel width of chart area
	 * @param width - width in pixels
	 * TODO: replace it with generic {@link #setWidth(String)} and {@link #setSize(String, String)}
	 */
	public void setPixelWidth(int width) {
		canvas.setWidth(width);
	}

    public void setWidth(String width) {
        canvas.getStyle().setProperty("width", width);
    }

    public void setHeight(String height){
        canvas.getStyle().setProperty("height", height);
    }

	/**
	 * Method sets pixel height of chart area
	 * @param height - height in pixels
	 * TODO: replace it with generic {@link #setHeight(String)} and {@link #setSize(String, String)}
	 */
	public void setPixelHeight(int height) {
		canvas.setHeight(height);
	}
	
	@Override
	public void addAnimationCompleteHandler(AnimationCompleteHandler handler) {
		addHandler(handler, AnimationCompleteEvent.getType());
	}

    /**
     * Creates snapshot of current state of chart as image
     * @return Image object or null if Chart not rendered (or in progress)
     */
    public Image getSnapshot(){
        String code= getBase64Image(nativeCanvas);
        if(code == null)
            return null;
        Image image = new Image(code);
        return image;
    }

    private native String getBase64Image(JavaScriptObject nativeCanvas)/*-{
        if(nativeCanvas != null)
            return nativeCanvas.toBase64Image();
        return null;
    }-*/;

    @Override
    /**
     * Important Note : clickHandler added internally by default to handle DataSelectionEvent.
     * In case external clickHandler destroying chart (eg update() method invoked) this will lead
     * to DataSelectionEvent won't be created
     */
    public HandlerRegistration addClickHandler(ClickHandler clickHandler) {
        return addHandler(clickHandler, ClickEvent.getType());
    }

    @Override
    public HandlerRegistration addDataSelectionHandler(DataSelectionHandler handler) {
        return addHandler(handler, DataSelectionEvent.getType());
    }

    protected JavaScriptObject getNativeCanvas(){
        return nativeCanvas;
    }

    protected CanvasElement getNativeElement(){
        return canvas;
    }

    protected void setNativeCanvas(JavaScriptObject object){
        this.nativeCanvas = object;
        processEvents(object);
    }

    /**
     * Specify should chart be animated or not
     * Default value is <code>true</code>
     * @param enabled
     */
    public void setAnimationEnabled(boolean enabled){
        if(!enabled) //"animation" : false interpreted by chart.js as "true"
            options.clearProperty(ANIMATION);
        else
            options.setProperty(ANIMATION, enabled);
    }

    /**
     * Specify animation easing
     * Default value is {@link org.dashbuilder.renderer.chartjs.lib.options.Type#EASE_OUT_QUART}
     * @param type
     */
    public void setAnimationType(Type type){
        if(type == null)
            options.clearProperty(ANIMATION_EASING);
        else
            options.setProperty(ANIMATION_EASING, type.getValue());
    }

    /**
     * Add animation callback to handle animation state changes
     * @param callback
     */
    public void addAnimationCallback(AnimationCallback callback){
        if(callback != null)
            callbackList.add(callback);
    }

    @Override
    public void setAnimationSteps(int steps) {
        if(steps <= 0)
            throw new IndexOutOfBoundsException("Number of animation steps should be positive. Found '"+steps+"'");

        options.setProperty(ANIMATION_STEPS, steps);
    }

    /**
     * Method returns custom options for chart
     * @return
     */
    protected JavaScriptObject constructOptions(){
        return options;
    }

    protected native void registerNativeAnimationHandlers()/*-{
        options = this.@org.dashbuilder.renderer.chartjs.lib.Chart::constructOptions()();
        self = this;
        options.onAnimationProgress = function(progress){
            self.@org.dashbuilder.renderer.chartjs.lib.Chart::onAnimationProgress(D)(progress);
            return;
        }
        options.onAnimationComplete = function(){
            self.@org.dashbuilder.renderer.chartjs.lib.Chart::onAnimationComplete()();
            return;
        }
    }-*/;

    protected void onAnimationProgress(double progress){
        for(AnimationCallback callback : callbackList){
            if(callback != null)
                callback.onProgress(progress);
        }
    }

    protected void onAnimationComplete(){
        for(AnimationCallback callback : callbackList){
            if(callback != null)
                callback.onAnimationComplete();
        }
    }

    @Override
    public void setResponsive(boolean responsive){
        if(!responsive)
            options.clearProperty(RESPONSIVE);
        else
            options.setProperty(RESPONSIVE, true);
    }

    @Override
    public void setMaintainAspectRatio(boolean aspectRatio){
        if(!aspectRatio)
            options.clearProperty(MAINTAIN_ASPECT_RATIO);
        else
            options.setProperty(MAINTAIN_ASPECT_RATIO, true);
    }

    public void setLegendTemplate(String template) {
        options.setLegendTemplate(template);
    }
}
