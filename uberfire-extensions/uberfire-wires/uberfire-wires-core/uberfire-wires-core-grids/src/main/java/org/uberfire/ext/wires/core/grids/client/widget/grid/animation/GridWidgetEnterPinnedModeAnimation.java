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

import java.util.List;
import java.util.Set;

import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.IAnimation;
import com.ait.lienzo.client.core.animation.IAnimationCallback;
import com.ait.lienzo.client.core.animation.IAnimationHandle;
import com.ait.lienzo.client.core.animation.TimedAnimation;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Transform;
import com.google.gwt.user.client.Command;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;

/**
 * An animation to reposition the ViewPort so the given GridWidget is centred
 * horizontally and GridWidget header aligned with the top of the ViewPort.
 */
public class GridWidgetEnterPinnedModeAnimation extends TimedAnimation {

    public GridWidgetEnterPinnedModeAnimation(final GridWidget gridWidget,
                                              final Set<GridWidget> gridWidgets,
                                              final Set<IPrimitive<?>> gridWidgetConnectors,
                                              final Command onStartCommand,
                                              final List<Command> onEnterPinnedModeCommands) {
        super(500,
              new IAnimationCallback() {

                  private final double endScaleX = 1.0;
                  private final double endScaleY = 1.0;
                  private Point2D delta;
                  private Point2D startTranslation;
                  private double startScaleX;
                  private double startScaleY;
                  private AnimationTweener tweener = AnimationTweener.EASE_OUT;

                  @Override
                  public void onStart(final IAnimation animation,
                                      final IAnimationHandle handle) {
                      final Viewport vp = gridWidget.getViewport();
                      if (vp.getTransform() == null) {
                          vp.setTransform(new Transform());
                      }
                      startScaleX = vp.getTransform().getScaleX();
                      startScaleY = vp.getTransform().getScaleY();
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
                      transform.reset();

                      final Point2D frameLocation = startTranslation.add(delta.mul(pct));
                      final double frameScaleX = startScaleX + (endScaleX - startScaleX) * pct;
                      final double frameScaleY = startScaleY + (endScaleY - startScaleY) * pct;
                      transform.scale(frameScaleX,
                                      frameScaleY).translate(frameLocation.getX(),
                                                             frameLocation.getY());

                      hideGridWidgets(pct);
                      hideGridWidgetConnectors(pct);

                      gridWidget.getLayer().batch();
                  }

                  @Override
                  public void onClose(final IAnimation animation,
                                      final IAnimationHandle handle) {
                      for (GridWidget gw : gridWidgets) {
                          gw.setVisible(false);
                      }
                      for (IPrimitive<?> p : gridWidgetConnectors) {
                          p.setVisible(false);
                      }
                      gridWidget.getLayer().setListening(true);
                      gridWidget.getLayer().batch();

                      onEnterPinnedModeCommands.forEach(Command::execute);
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

                  private void hideGridWidgets(final double pct) {
                      for (GridWidget gw : gridWidgets) {
                          gw.setAlpha(1.0 - pct);
                      }
                  }

                  private void hideGridWidgetConnectors(final double pct) {
                      for (IPrimitive<?> p : gridWidgetConnectors) {
                          p.setAlpha(1.0 - pct);
                      }
                  }
              });
    }
}
