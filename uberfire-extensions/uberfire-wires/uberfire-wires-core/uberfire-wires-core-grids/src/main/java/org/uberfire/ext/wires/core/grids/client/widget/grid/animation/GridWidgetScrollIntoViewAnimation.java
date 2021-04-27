/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.ext.wires.core.grids.client.widget.grid.animation;

import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.IAnimation;
import com.ait.lienzo.client.core.animation.IAnimationCallback;
import com.ait.lienzo.client.core.animation.IAnimationHandle;
import com.ait.lienzo.client.core.animation.TimedAnimation;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Transform;
import com.google.gwt.user.client.Command;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;

/**
 * An animation to reposition the ViewPort so the given GridWidget is positioned in the top-left of the visible bounds.
 */
public class GridWidgetScrollIntoViewAnimation extends TimedAnimation {

    public GridWidgetScrollIntoViewAnimation(final GridWidget gridWidget,
                                             final Command onStartCommand) {
        super(500,
              new IAnimationCallback() {

                  private Point2D delta;
                  private Point2D startTranslation;
                  private AnimationTweener tweener = AnimationTweener.EASE_OUT;

                  @Override
                  public void onStart(final IAnimation animation,
                                      final IAnimationHandle handle) {
                      final Viewport vp = gridWidget.getViewport();
                      if (vp.getTransform() == null) {
                          vp.setTransform(new Transform());
                      }
                      startTranslation = getViewportTranslation().mul(-1.0);

                      final Point2D endTranslation = new Point2D(gridWidget.getX(),
                                                                 gridWidget.getY()).mul(-1.0);

                      delta = new Point2D(endTranslation.getX() - startTranslation.getX(),
                                          endTranslation.getY() - startTranslation.getY());

                      onStartCommand.execute();

                      gridWidget.getLayer().setListening(false);
                      gridWidget.getLayer().batch();
                  }

                  @Override
                  public void onFrame(final IAnimation animation,
                                      final IAnimationHandle handle) {
                      final double pct = assertPct(animation.getPercent());
                      final Viewport vp = gridWidget.getViewport();
                      final Transform transform = vp.getTransform();
                      final double scaleX = transform.getScaleX();
                      final double scaleY = transform.getScaleY();
                      transform.reset();

                      final Point2D frameLocation = startTranslation.add(delta.mul(pct));
                      transform.scale(scaleX,
                                      scaleY).translate(frameLocation.getX(),
                                                        frameLocation.getY());

                      gridWidget.getLayer().batch();
                  }

                  @Override
                  public void onClose(final IAnimation animation,
                                      final IAnimationHandle handle) {
                      gridWidget.getLayer().setListening(true);
                      gridWidget.getLayer().batch();
                  }

                  private Point2D getViewportTranslation() {
                      final Viewport vp = gridWidget.getViewport();
                      final Transform transform = vp.getTransform();
                      final Transform t = transform.copy().getInverse();
                      final Point2D p = new Point2D(t.getTranslateX(),
                                                    t.getTranslateY());
                      return p;
                  }

                  private double assertPct(final double pct) {
                      if (pct < 0) {
                          return 0;
                      }
                      if (pct > 1.0) {
                          return 1.0;
                      }
                      return tweener.apply(pct);
                  }
              });
    }
}
