package org.dashbuilder.renderer.chartjs.lib;

import org.dashbuilder.renderer.chartjs.lib.event.AnimationCompleteEvent;
import org.dashbuilder.renderer.chartjs.lib.event.AnimationCompleteHandler;
import org.dashbuilder.renderer.chartjs.lib.event.HasAnimationCompleteHandlers;
import org.dashbuilder.renderer.chartjs.lib.options.AnimationCallback;
import org.dashbuilder.renderer.chartjs.lib.options.HasAnimation;
import org.dashbuilder.renderer.chartjs.lib.options.IsResponsive;
import org.dashbuilder.renderer.chartjs.lib.options.Type;

import java.util.ArrayList;
import java.util.List;

public abstract class ChartWithAnimation<T> extends Chart implements IsResponsive, HasAnimation,HasAnimationCompleteHandlers {

    protected List<AnimationCallback> callbackList = new ArrayList<AnimationCallback>();

    public ChartWithAnimation(){
        super();
        registerNativeAnimationHandlers();
    }


    @Override
    public void setResponsive(boolean responsive){
        if(!responsive)
            options.clearProperty(RESPONSIVE);
        else
            options.setProperty(RESPONSIVE, true);
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

    @Override
    public void addAnimationCompleteHandler(AnimationCompleteHandler handler) {
        addHandler(handler, AnimationCompleteEvent.getType());
    }


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
}
