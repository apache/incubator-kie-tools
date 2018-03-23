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

package com.ait.lienzo.client.core;

import java.util.Objects;

import com.google.gwt.core.client.JavaScriptObject;

public class Path2D
{
    private final NativePath2D m_path;

    private boolean            m_closed = false;

    public static final native boolean isSupported()
    /*-{
		if ($wnd.Path2D) {
			return true;
		}
		return false;
    }-*/;

    public static final native boolean isEllipseSupported()
    /*-{
		if ($wnd.Path2D) {
			var path = new $wnd.Path2D();

			return !!path.ellipse;
		}
		return false;
    }-*/;

    public Path2D(final NativePath2D path)
    {
        m_path = path;
    }

    public Path2D()
    {
        this(NativePath2D.make());
    }

    public Path2D(final Path2D path)
    {
        this(NativePath2D.make(Objects.requireNonNull(path).getNativePath2D()));
    }

    public Path2D(final String path)
    {
        this(NativePath2D.make(Objects.requireNonNull(path)));
    }

    public NativePath2D getNativePath2D()
    {
        return m_path;
    }

    public boolean isClosed()
    {
        return m_closed;
    }

    public Path2D setClosed(final boolean closed)
    {
        m_closed = closed;

        return this;
    }

    public Path2D beginPath()
    {
        if (null != m_path)
        {
            m_path.beginPath();
        }
        return this;
    }

    public Path2D closePath()
    {
        if (null != m_path)
        {
            m_path.closePath();
        }
        return setClosed(true);
    }

    public Path2D rect(final double x, final double y, final double w, final double h)
    {
        if (null != m_path)
        {
            m_path.rect(x, y, w, h);
        }
        return this;
    }

    public Path2D moveTo(final double x, final double y)
    {
        if (null != m_path)
        {
            m_path.moveTo(x, y);
        }
        return this;
    }

    public Path2D lineTo(final double x, final double y)
    {
        if (null != m_path)
        {
            m_path.lineTo(x, y);
        }
        return this;
    }

    public Path2D quadraticCurveTo(final double cpx, final double cpy, final double x, final double y)
    {
        if (null != m_path)
        {
            m_path.quadraticCurveTo(cpx, cpy, x, y);
        }
        return this;
    }

    public Path2D arc(final double x, final double y, final double radius, final double startAngle, final double endAngle)
    {
        if (null != m_path)
        {
            m_path.arc(x, y, radius, startAngle, endAngle);
        }
        return this;
    }

    public Path2D arc(final double x, final double y, final double radius, final double startAngle, final double endAngle, final boolean antiClockwise)
    {
        if (null != m_path)
        {
            m_path.arc(x, y, radius, startAngle, endAngle, antiClockwise);
        }
        return this;
    }

    public Path2D arcTo(final double x1, final double y1, final double x2, final double y2, final double radius)
    {
        if (null != m_path)
        {
            m_path.arcTo(x1, y1, x2, y2, radius);
        }
        return this;
    }

    public Path2D bezierCurveTo(final double cp1x, final double cp1y, final double cp2x, final double cp2y, final double x, final double y)
    {
        if (null != m_path)
        {
            m_path.bezierCurveTo(cp1x, cp1y, cp2x, cp2y, x, y);
        }
        return this;
    }

    public Path2D ellipse(final double x, final double y, final double radiusX, final double radiusY, final double rotation, final double startAngle, final double endAngle, final boolean antiClockwise)
    {
        if (null != m_path)
        {
            m_path.ellipse(x, y, radiusX, radiusY, rotation, startAngle, endAngle, antiClockwise);
        }
        return this;
    }

    public Path2D ellipse(final double x, final double y, final double radiusX, final double radiusY, final double rotation, final double startAngle, final double endAngle)
    {
        if (null != m_path)
        {
            m_path.ellipse(x, y, radiusX, radiusY, rotation, startAngle, endAngle);
        }
        return this;
    }

    public static final class NativePath2D extends JavaScriptObject
    {
        protected NativePath2D()
        {
        }

        static final native NativePath2D make()
        /*-{
			if ($wnd.Path2D) {
				return new $wnd.Path2D();
			}
			return null;
        }-*/;

        static final native NativePath2D make(NativePath2D path)
        /*-{
			if ($wnd.Path2D && path) {
				return new $wnd.Path2D(path);
			}
			return null;
        }-*/;

        static final native NativePath2D make(String path)
        /*-{
			if ($wnd.Path2D && path) {
				return new $wnd.Path2D(path);
			}
			return null;
        }-*/;

        public final native void beginPath()
        /*-{
			if (this.beginPath) {
				this.beginPath();
			}
        }-*/;

        public final native void closePath()
        /*-{
			this.closePath();
        }-*/;

        public final native void rect(double x, double y, double w, double h)
        /*-{
			this.rect(x, y, w, h);
        }-*/;

        public final native void moveTo(double x, double y)
        /*-{
			this.moveTo(x, y);
        }-*/;

        public final native void lineTo(double x, double y)
        /*-{
			this.lineTo(x, y);
        }-*/;

        public final native void quadraticCurveTo(double cpx, double cpy, double x, double y)
        /*-{
			this.quadraticCurveTo(cpx, cpy, x, y);
        }-*/;

        public final native void arc(double x, double y, double radius, double startAngle, double endAngle)
        /*-{
			this.arc(x, y, radius, startAngle, endAngle, false);
        }-*/;

        public final native void arc(double x, double y, double radius, double startAngle, double endAngle, boolean antiClockwise)
        /*-{
			this.arc(x, y, radius, startAngle, endAngle, antiClockwise);
        }-*/;

        public final native void arcTo(double x1, double y1, double x2, double y2, double radius)
        /*-{
			this.arcTo(x1, y1, x2, y2, radius);
        }-*/;

        public final native void bezierCurveTo(double cp1x, double cp1y, double cp2x, double cp2y, double x, double y)
        /*-{
			this.bezierCurveTo(cp1x, cp1y, cp2x, cp2y, x, y);
        }-*/;

        public final native void ellipse(double x, double y, double radiusX, double radiusY, double rotation, double startAngle, double endAngle)
        /*-{
			if (this.ellipse) {
				this.ellipse(x, y, radiusX, radiusY, rotation, startAngle,
						endAngle, false);
			}
        }-*/;

        public final native void ellipse(double x, double y, double radiusX, double radiusY, double rotation, double startAngle, double endAngle, boolean antiClockwise)
        /*-{
			if (this.ellipse) {
				this.ellipse(x, y, radiusX, radiusY, rotation, startAngle,
						endAngle, antiClockwise);
			}
        }-*/;
    }
}
