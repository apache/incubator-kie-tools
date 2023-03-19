/*
   Copyright (c) 2017 Ahome' Innovation Technologies. All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.ait.lienzo.client.core.shape.guides;

import com.ait.lienzo.client.core.shape.GroupOf;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.shape.Triangle;
import com.ait.lienzo.client.core.shape.storage.IStorageEngine;
import com.ait.lienzo.client.core.shape.storage.PrimitiveFastArrayStorageEngine;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Shadow;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.shared.core.types.GroupType;
import com.ait.lienzo.shared.core.types.IColor;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.shared.core.types.TextBaseLine;
import com.ait.lienzo.tools.client.StringOps;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;

public class ToolTip extends GroupOf<IPrimitive<?>, ToolTip> implements IGuidePrimitive<ToolTip> {

    private static final String FONT_FAMILY = "Verdana";

    private static final String TEXT_FONT_STYLE = "normal";

    private static final String LABL_FONT_STYLE = "bold";

    private static final int FONT_SIZE = 10;

    private static final IColor LABEL_COLOR = ColorName.BLACK;

    private final Rectangle m_body;

    private final Triangle m_tail;

    private final Triangle m_mask;

    private final Text m_text;

    private final Text m_labl;

    private int m_wait;

    private boolean m_show;

    private double m_oldx;

    private double m_oldy;

    private double m_padding;

    private double m_tailValue;

    private String m_textValue;

    private String m_lablValue;

    private RepeatingCommand m_autoHider;

    private static final Shadow SHADOW = new Shadow(ColorName.BLACK.getColor().setA(0.80), 10, 3, 3);

    public ToolTip() {
        super(GroupType.GROUP, new PrimitiveFastArrayStorageEngine());

        m_body = new Rectangle(1, 1).setCornerRadius(5).setStrokeWidth(1).setShadow(SHADOW);

        m_tail = new Triangle(new Point2D(1, 1), new Point2D(1, 1), new Point2D(1, 1)).setStrokeWidth(1).setShadow(SHADOW);

        m_mask = new Triangle(new Point2D(1, 1), new Point2D(1, 1), new Point2D(1, 1));

        m_text = new Text("", FONT_FAMILY, TEXT_FONT_STYLE, FONT_SIZE).setFillColor(LABEL_COLOR).setTextAlign(TextAlign.CENTER).setTextBaseLine(TextBaseLine.MIDDLE);

        m_labl = new Text("", FONT_FAMILY, LABL_FONT_STYLE, FONT_SIZE).setFillColor(LABEL_COLOR).setTextAlign(TextAlign.CENTER).setTextBaseLine(TextBaseLine.MIDDLE);

        add(m_body);

        add(m_tail);

        add(m_mask);

        add(m_text);

        add(m_labl);

        setPadding(12);

        setTailValue(12);

        setVisible(false);

        setListening(false);

        setFillColor(ColorName.WHITESMOKE);

        m_show = true;
    }

    @Override
    public IGuidePrimitive<?> asGuide() {
        return this;
    }

    @Override
    public ToolTip show(final double x, final double y) {
        return show(x, y, false);
    }

    private ToolTip show(final double x, final double y, final boolean force) {
        m_autoHider = null;

        if ((!force) && (!m_show)) {
            return this;
        }
        if (!m_show) {
            hide();
        }
        m_oldx = x;

        m_oldy = y;

        m_show = false;

        double th = 0;

        double tw = 0;

        double lh = 0;

        double lw = 0;

        final double pd = getPadding();

        if ((null == m_textValue) || (m_textValue.isEmpty())) {
            m_text.setText("").setVisible(false);
        } else {
            m_text.setText(m_textValue).setVisible(true);

            final BoundingBox bb = m_text.getBoundingBox();

            tw = bb.getWidth();

            th = bb.getHeight();
        }
        if ((null == m_lablValue) || (m_lablValue.isEmpty())) {
            m_labl.setText("").setVisible(false);
        } else {
            m_labl.setText(m_lablValue).setVisible(true);

            final BoundingBox bb = m_labl.getBoundingBox();

            lw = bb.getWidth();

            lh = bb.getHeight();
        }
        final double p2 = pd * 2;

        final double rw = Math.max(tw, lw) + p2 + 2;

        final double rh = th + lh + p2 + 4;

        final double w2 = rw / 2;

        final double h2 = rh / 2;

        m_body.setWidth(rw).setHeight(rh).setCornerRadius(5);

        final double tv = getTailValue();

        if (tv > 1) {
            m_tail.setPoints(new Point2D(w2 - tv, rh), new Point2D(w2, rh + tv), new Point2D(w2 + tv, rh)).setVisible(true);

            m_mask.setPoints(new Point2D(w2 - tv - 3, rh - 3), new Point2D(w2, rh + tv - 3), new Point2D(w2 + tv + 3, rh - 3)).setVisible(true);
        } else {
            m_tail.setVisible(false);

            m_mask.setVisible(false);
        }
        if (th > 0) {
            m_text.setX(w2);

            if (lh > 0) {
                m_labl.setY(h2 - (th / 2) - 2);

                m_text.setY(h2 + (lh / 2) + 2);
            } else {
                m_text.setY(h2);
            }
        }
        if (lh > 0) {
            m_labl.setX(w2);

            if (th > 0) {
                m_labl.setY(h2 - (th / 2) - 2);

                m_text.setY(h2 + (lh / 2) + 2);
            } else {
                m_labl.setY(h2);
            }
        }
        setX(x - w2);

        setY(y - rh);

        if ((!force) && (getAutoHideTime() > 0)) {
            m_autoHider = new RepeatingCommand() {
                @Override
                public boolean execute() {
                    if ((this == m_autoHider) && (isShowing())) {
                        hide();
                    }
                    return false;
                }
            };
            Scheduler.get().scheduleFixedDelay(m_autoHider, getAutoHideTime());
        }
        setVisible(true);

        return draw();
    }

    @Override
    public boolean isShowing() {
        return (!m_show);
    }

    @Override
    public int getAutoHideTime() {
        return m_wait;
    }

    @Override
    public ToolTip setAutoHideTime(final int time) {
        m_wait = Math.max(time, 0);

        return this;
    }

    public double getPadding() {
        return m_padding;
    }

    public ToolTip setPadding(final double padding) {
        m_padding = Math.max(padding, 2);

        return this;
    }

    public double getTailValue() {
        return m_tailValue;
    }

    public ToolTip setTailValue(final double value) {
        m_tailValue = Math.max(value, 0);

        return this;
    }

    public ToolTip setFillColor(IColor fill) {
        if (null == fill) {
            fill = ColorName.WHITESMOKE;
        }
        return setFillColor(fill.getColorString());
    }

    public ToolTip setFillColor(String fill) {
        if (null == (fill = StringOps.toTrimOrNull(fill))) {
            fill = ColorName.WHITESMOKE.getColorString();
        }
        m_body.setFillColor(fill);

        m_tail.setFillColor(fill);

        m_mask.setFillColor(fill);

        if (isShowing()) {
            return draw();
        }
        return this;
    }

    @Override
    public ToolTip draw() {
        moveToTop();

        return batch();
    }

    @Override
    public ToolTip hide() {
        if (m_show) {
            return this;
        }
        m_show = true;

        setVisible(false);

        return batch();
    }

    public ToolTip setValues(final String text, final String labl) {
        m_textValue = text;

        m_lablValue = labl;

        if (isShowing()) {
            show(m_oldx, m_oldy, true);
        }
        return this;
    }

    @Override
    public IStorageEngine<IPrimitive<?>> getDefaultStorageEngine() {
        return new PrimitiveFastArrayStorageEngine();
    }
}
