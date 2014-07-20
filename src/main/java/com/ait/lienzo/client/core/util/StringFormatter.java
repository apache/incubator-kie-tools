/*
   Copyright (c) 2014 Ahome' Innovation Technologies. All rights reserved.

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

package com.ait.lienzo.client.core.util;

public final class StringFormatter
{
    private StringFormatter()
    {
    }

    /**
     * Simple formatter. Replaces strings of the form "{0}", "{1}" etc. with the
     * corresponding parameters.
     * 
     * @param format String with "{0}", "{1}" placeholders.
     * @param params First parameter will be placed in "{0}" placeholder.
     * 
     * @return Formatted string
     */
    public static final String format(String format, Object... params)
    {
        StringBuilder b = new StringBuilder();

        int p = 0;

        while (true)
        {
            int i = format.indexOf('{', p);

            if (i == -1)
            {
                break; // done
            }
            int i2 = format.indexOf('}', i + 1);

            if (i2 == -1)
            {
                break; // done
            }
            if (p != i)
            {
                b.append(format.substring(p, i));
            }
            String nstr = format.substring(i + 1, i2);

            try
            {
                int n = Integer.parseInt(nstr);

                if (n >= 0 && n < params.length)
                {
                    b.append(params[n]);
                }
                else
                {
                    b.append('{').append(nstr).append('}');
                }
            }
            catch (NumberFormatException e)
            {
                b.append('{').append(nstr).append('}');
            }
            p = i2 + 1;
        }
        b.append(format.substring(p));

        return b.toString();
    }
}
