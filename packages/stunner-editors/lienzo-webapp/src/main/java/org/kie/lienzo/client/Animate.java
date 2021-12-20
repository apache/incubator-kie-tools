package org.kie.lienzo.client;

import com.ait.lienzo.client.core.animation.AbstractRadialPositioningCalculator;
import com.ait.lienzo.client.core.animation.AnimationCallback;
import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationProperty;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.IAnimation;
import com.ait.lienzo.client.core.animation.IAnimationHandle;
import com.ait.lienzo.client.core.animation.IPositioningCalculator;
import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickHandler;
import com.ait.lienzo.client.core.shape.Bow;
import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Spline;
import com.ait.lienzo.client.core.shape.Star;
import com.ait.lienzo.client.core.types.LinearGradient;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.core.types.Shadow;
import com.ait.lienzo.client.core.util.Geometry;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.shared.core.types.IColor;
import com.ait.lienzo.shared.core.types.LineCap;
import com.ait.lienzo.shared.core.types.LineJoin;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;

import static com.ait.lienzo.client.core.animation.AnimationProperty.Properties.ALPHA;
import static com.ait.lienzo.client.core.animation.AnimationProperty.Properties.DASH_OFFSET;
import static com.ait.lienzo.client.core.animation.AnimationProperty.Properties.FILL_COLOR;
import static com.ait.lienzo.client.core.animation.AnimationProperty.Properties.INNER_RADIUS;
import static com.ait.lienzo.client.core.animation.AnimationProperty.Properties.OUTER_RADIUS;
import static com.ait.lienzo.client.core.animation.AnimationProperty.Properties.POSITIONING;
import static com.ait.lienzo.client.core.animation.AnimationProperty.Properties.ROTATION_DEGREES;
import static com.ait.lienzo.client.core.animation.AnimationProperty.Properties.SCALE;
import static com.ait.lienzo.client.core.animation.AnimationProperty.Properties.X;
import static com.ait.lienzo.client.core.animation.AnimationProperty.Properties.Y;

public class Animate extends BaseExample implements Example {

    private boolean m_to_yellow = true;

    public Animate(final String title) {
        super(title);
    }

    public void run() {
        final LinearGradient lgradient = new LinearGradient(0, 0, 200, 0);

        lgradient.addColorStop(0.0, ColorName.WHITE);

        lgradient.addColorStop(0.1, ColorName.SALMON);

        lgradient.addColorStop(0.9, ColorName.DARKRED);

        lgradient.addColorStop(1.0, ColorName.WHITE);

        final Rectangle rectangle = new Rectangle(200, 300).setX(50).setY(400).setFillGradient(lgradient).setDraggable(true).setShadow(new Shadow(ColorName.BLACK, 10, 5, 5)).setStrokeColor(ColorName.BLACK).setStrokeWidth(10).setLineJoin(LineJoin.ROUND);

        final Rectangle rectcolor = new Rectangle(200, 150).setX(300).setY(550).setStrokeColor(ColorName.BLACK).setStrokeWidth(10).setLineJoin(LineJoin.ROUND).setFillColor(ColorName.HOTPINK);

        final Bow bow1 = new Bow(80, 100, Geometry.toRadians(0), Geometry.toRadians(270)).setFillColor(ColorName.HOTPINK).setStrokeColor(ColorName.BLACK).setStrokeWidth(2).setDraggable(true).setX(150).setY(150).setShadow(new Shadow(ColorName.BLACK.getColor().setA(0.5), 5, 5, 5));

        layer.add(flippy(bow1, ColorName.HOTPINK));

        final int x = 500;

        final int y = 400;

        final Star sun = new Star(13, 70, 100).setX(x).setY(y).setStrokeColor(ColorName.RED).setStrokeWidth(3).setFillColor(ColorName.YELLOW).setAlpha(0.75);

        final IPositioningCalculator orbit = new AbstractRadialPositioningCalculator() {
            @Override
            public double getX(final double percent) {
                return sun.getX();
            }

            @Override
            public double getY(final double percent) {
                return sun.getY();
            }

            @Override
            public double getRadius(final double percent) {
                return sun.getOuterRadius() + 100;
            }
        };
        final Circle earth = new Circle(50).setX(x + 100 + 100).setY(y).setStrokeColor(ColorName.BLACK).setStrokeWidth(2).setFillColor(ColorName.DEEPSKYBLUE).setShadow(new Shadow(ColorName.BLACK, 10, 5, 5));

        final Circle moon = new Circle(20).setX(x + 100 + 100 + 50 + 40).setY(y).setStrokeColor(ColorName.BLACK).setStrokeWidth(2).setFillColor(ColorName.DARKGRAY).setShadow(new Shadow(ColorName.BLACK, 10, 5, 5));

        sun.addNodeMouseClickHandler(new NodeMouseClickHandler() {
            @Override
            public void onNodeMouseClick(final NodeMouseClickEvent event) {
                sun.getLayer().setListening(false);

                final IPositioningCalculator calc = new AbstractRadialPositioningCalculator() {
                    @Override
                    public double getX(final double percent) {
                        return earth.getX();
                    }

                    @Override
                    public double getY(final double percent) {
                        return earth.getY();
                    }

                    @Override
                    public double getRadius(final double percent) {
                        return earth.getRadius() + 40;
                    }

                    @Override
                    public double getMultiplier(final double percent) {
                        return 5;
                    }
                };
                sun.animate(AnimationTweener.LINEAR, AnimationProperties.toPropertyList(X(x + 500), Y(y - 50), ROTATION_DEGREES(360 * 3), INNER_RADIUS(70 * 1.5), OUTER_RADIUS(100 * 1.5)), 8000, new AnimationCallback() {
                    @Override
                    public void onStart(final IAnimation animation, final IAnimationHandle handle) {
                        doRotation(earth, moon, orbit, calc);
                    }

                    @Override
                    public void onClose(final IAnimation animation, final IAnimationHandle handle) {
                        sun.animate(AnimationTweener.LINEAR, AnimationProperties.toPropertyList(X(x), Y(y), ROTATION_DEGREES(0), INNER_RADIUS(70), OUTER_RADIUS(100)), 8000, new AnimationCallback() {
                            @Override
                            public void onStart(final IAnimation animation, final IAnimationHandle handle) {
                                doRotation(earth, moon, orbit, calc);
                            }

                            @Override
                            public void onClose(final IAnimation animation, final IAnimationHandle handle) {
                                sun.getLayer().setListening(true);

                                sun.getLayer().draw();
                            }
                        });
                    }
                });
            }
        });
        layer.add(rectangle);

        rectangle.addNodeMouseClickHandler(new NodeMouseClickHandler() {
            @Override
            public void onNodeMouseClick(final NodeMouseClickEvent event) {
                rectangle.animate(AnimationTweener.LINEAR, AnimationProperties.toPropertyList(ALPHA(1, 0)), 1000, new AnimationCallback() {
                    @Override
                    public void onClose(final IAnimation animation, final IAnimationHandle handle) {
                        rectangle.animate(AnimationTweener.LINEAR, AnimationProperties.toPropertyList(ALPHA(0, 1)), 1000);
                    }
                });
            }
        });
        layer.add(rectcolor);

        rectcolor.addNodeMouseClickHandler(new NodeMouseClickHandler() {
            @Override
            public void onNodeMouseClick(final NodeMouseClickEvent event) {
                rectcolor.animate(AnimationTweener.LINEAR, AnimationProperties.toPropertyList(FILL_COLOR(m_to_yellow ? ColorName.YELLOW : ColorName.HOTPINK)), 1000, new AnimationCallback() {
                    @Override
                    public void onClose(final IAnimation animation, final IAnimationHandle handle) {
                        m_to_yellow = !m_to_yellow;
                    }
                });
            }
        });
        final Point2DArray points = Point2DArray.fromArrayOfPoint2D(new Point2D(300, 100), new Point2D(400, 200), new Point2D(250, 300), new Point2D(600, 100), new Point2D(650, 150));

        final Spline spline = new Spline(points).setStrokeColor(ColorName.BLUE).setStrokeWidth(7).setLineCap(LineCap.ROUND).setDashArray(15, 15);

        spline.addNodeMouseClickHandler(new NodeMouseClickHandler() {
            @Override
            public void onNodeMouseClick(final NodeMouseClickEvent event) {
                spline.animate(AnimationTweener.LINEAR, AnimationProperties.toPropertyList(DASH_OFFSET(300)), 5000, new AnimationCallback() {
                    @Override
                    public void onClose(final IAnimation animation, final IAnimationHandle handle) {
                        spline.animate(AnimationTweener.LINEAR, AnimationProperties.toPropertyList(DASH_OFFSET(0)), 5000);
                    }
                });
            }
        });
        layer.add(spline);

        for (int i = 0; i < points.size(); i++) {
            final Point2D p = points.get(i);

            final Circle c = new Circle(10).setFillColor(ColorName.BLACK).setAlpha(0.5).setX(p.getX()).setY(p.getY());

            layer.add(c);
        }

        layer.add(sun);

        layer.add(earth);

        layer.add(moon);
    }

    private Group m_shadey = null;

    public Bow flippy(final Bow prim, final IColor color) {
        prim.addNodeMouseClickHandler(new NodeMouseClickHandler() {
            @Override
            public void onNodeMouseClick(final NodeMouseClickEvent event) {
                prim.animate(AnimationTweener.LINEAR, AnimationProperties.toPropertyList(SCALE(1, -1)), 500, new AnimationCallback() {
                    @Override
                    public void onClose(final IAnimation animation, final IAnimationHandle handle) {
                        prim.animate(AnimationTweener.LINEAR, AnimationProperties.toPropertyList(SCALE(1, 1)), 500, new AnimationCallback() {
                            @Override
                            public void onClose(final IAnimation animation, final IAnimationHandle handle) {
                                shadey(prim.getLayer(), color);
                            }
                        });
                    }
                }).run();
            }
        });
        return prim;
    }

    public void shadey(final Layer layer, final IColor color) {
        final Rectangle[] r = new Rectangle[5];

        if (null != m_shadey) {
            layer.remove(m_shadey);

            layer.draw();
        }
        m_shadey = new Group().setX(400).setY(50);

        for (int i = 0; i < 5; i++) {
            r[i] = new Rectangle(900, 40).setFillColor(color).setStrokeColor(ColorName.BLACK).setStrokeWidth(2).setAlpha(0).setX(0).setY((i * 50)).setScale(1, 0).setShadow(new Shadow(ColorName.BLACK.getColor().setA(0.5), 5, 5, 5));

            m_shadey.add(r[i]);
        }
        layer.add(m_shadey);

        final RepeatingCommand command = new RepeatingCommand() {
            @Override
            public boolean execute() {
                forward(0, r);

                return false;
            }
        };
        Scheduler.get().scheduleFixedDelay(command, 100);
    }

    public void forward(final int i, final Rectangle[] r) {
        if (i < 5) {
            final RepeatingCommand command = new RepeatingCommand() {
                @Override
                public boolean execute() {
                    forward(i + 1, r);

                    r[i].addNodeMouseClickHandler(new NodeMouseClickHandler() {
                        @Override
                        public void onNodeMouseClick(final NodeMouseClickEvent event) {
                            r[i].animate(AnimationTweener.LINEAR, AnimationProperties.toPropertyList(AnimationProperty.Properties.X(r[i].getX() + 900)), 300, new AnimationCallback() {
                                @Override
                                public void onClose(final IAnimation animation, final IAnimationHandle handle) {
                                    r[i].setVisible(false);

                                    r[i].getLayer().draw();

                                    for (int j = i + 1; j < 5; j++) {
                                        if (r[j].isVisible()) {
                                            r[j].animate(AnimationTweener.LINEAR, AnimationProperties.toPropertyList(AnimationProperty.Properties.Y(r[j].getY() - 50)), 300);
                                        }
                                    }
                                }
                            });
                        }
                    });
                    return false;
                }
            };
            Scheduler.get().scheduleFixedDelay(command, 100);

            r[i].animate(AnimationTweener.LINEAR, AnimationProperties.toPropertyList(AnimationProperty.Properties.ALPHA(1), AnimationProperty.Properties.SCALE(1, 1)), 300);
        }
    }

    private void doRotation(final Circle earth, final Circle moon, final IPositioningCalculator orbit, final IPositioningCalculator calc) {
        earth.animate(AnimationTweener.LINEAR, AnimationProperties.toPropertyList(POSITIONING(orbit)), 8000, new AnimationCallback() {
            @Override
            public void onStart(final IAnimation animation, final IAnimationHandle handle) {
                moon.animate(AnimationTweener.LINEAR, AnimationProperties.toPropertyList(POSITIONING(calc)), 8000);
            }
        });
    }
}
