package com.ait.lienzo.client.core.animation;


import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import elemental2.dom.FrameRequestCallback;

public class AnimationScheduler
{
    private static AnimationScheduler INSTANCE;

    public static AnimationScheduler get()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new AnimationScheduler();
        }
        return INSTANCE;
    }

    public AnimationHandle requestAnimationFrame(AnimationCallback callback) {
        return requestAnimationFrame(callback,
                                     null);
    }


    public AnimationHandle requestAnimationFrame(AnimationCallback callback,
                                                 Element element) {
        final int id = requestImplNew(callback,
                                      element);
        return () -> cancelImpl(id);
    }

    private static int requestImplNew(AnimationCallback cb,
                                      Element element) {
        FrameRequestCallback callback = p0 -> cb.execute(p0);
        return DomGlobal.requestAnimationFrame(callback,
                                                 element);
    }

    private static void cancelImpl(int id) {
        DomGlobal.cancelAnimationFrame(id);
    }

    /**
     * The callback used when an animation frame becomes available.
     */
    public interface AnimationCallback {
        /**
         * Invokes the command.
         *
         * @param timestamp the current timestamp
         */
        void execute(double timestamp);
    }

    /**
     * A handle to the requested animation frame created by
     * {@link #requestAnimationFrame(AnimationCallback, Element)}.
     */
    public interface AnimationHandle {
        /**
         * Cancel the requested animation frame. If the animation frame is already
         * canceled, do nothing.
         */
        void cancel();
    }


}
