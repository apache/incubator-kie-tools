/*
   Copyright (c) 2014,2015,2016 Ahome' Innovation Technologies. All rights reserved.

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

package com.ait.lienzo.tools.client;

import java.util.Objects;
import elemental2.dom.DomGlobal;

// @TODO this class may be redundant, now that you can access Console directly with Elemental2
public final class Console implements ILogging
{
    private static Console INSTANCE;

    public static final Console get()
    {
        if (null == INSTANCE)
        {
            INSTANCE = new Console();
        }
        return INSTANCE;
    }

    private Console()
    {
    }

    private static final void profile_beg_0(String message)
    {
// @FIXME left this commented as elemental2 does not expose profile() this is not a standard, we should probalboy remove this method.
//		if (!!$wnd.console) {
//			if ($wnd.console.profile !== undefined) {
//				$wnd.console.profile(message);
//			}
//		}
        throw new RuntimeException();
    }

    private static final  void profile_end_0()
    {
// @FIXME left this commented as elemental2 does not expose profileEnd() this is not a standard, we should probalboy remove this method.
//		if (!!$wnd.console) {
//			if ($wnd.console.profileEnd !== undefined) {
//				$wnd.console.profileEnd();
//			}
//		}
        throw new RuntimeException();
    }

    private static final void timestamp_0(String message)
    {
// @FIXME left this commented as elemental2 does not expose timestamp() this is not a standard, we should probalboy remove this method.
//        DomGlobal.window.console.timestamp(message);

//		if (!!$wnd.console) {
//			if ($wnd.console.timestamp !== undefined) {
//				$wnd.console.timestamp(message);
//			}
//		}
        throw new RuntimeException();
    }

    public final void clear()
    {
        DomGlobal.window.console.clear();
    }

    public final void count(final String message)
    {
        DomGlobal.window.console.count(message);
    }

    public final void groupBeg(final String message)
    {
        DomGlobal.window.console.group(message);
    }

    public final void groupEnd()
    {
        DomGlobal.window.console.groupEnd();
    }

    public final void profileBeg(final String message)
    {
        profile_beg_0(Objects.requireNonNull(message));
    }

    public final void profileEnd()
    {
        profile_end_0();
    }

    public final void timeBeg(final String message)
    {
        DomGlobal.window.console.time(message);
    }

    public final void timeEnd(final String message)
    {
        DomGlobal.window.console.timeEnd(message);
    }

    public final void timeStamp(final String message)
    {
        timestamp_0(Objects.requireNonNull(message));
    }

    public final void trace(final String message)
    {
        DomGlobal.window.console.trace(message);
    }

    @Override
    public void info(String message)
    {
        DomGlobal.window.console.info(message);
    }

    @Override
    public void severe(String message)
    {
        DomGlobal.window.console.error("SEVERE: " + message);
    }

    @Override
    public final void error(final String message)
    {
        DomGlobal.window.console.error("ERROR: " + message);
    }

    @Override
    public void error(String message, Throwable e)
    {
        DomGlobal.window.console.error("ERROR: " + message + e.getMessage());
    }

    @Override
    public void fine(String message)
    {
        DomGlobal.window.console.debug(message);
    }

    @Override
    public void warn(String message)
    {
        DomGlobal.window.console.warn(message);
    }

    @Override
    public void severe(String message, Throwable e)
    {
        DomGlobal.window.console.error("SEVERE: " + message + e.getMessage());
    }
}
