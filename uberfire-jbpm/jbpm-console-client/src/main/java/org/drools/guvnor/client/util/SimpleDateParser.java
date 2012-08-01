/*
 * Copyright 2006 Robert Hanson <iamroberthanson AT gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.guvnor.client.util;

import org.jboss.bpm.console.client.util.regex.Pattern;

import java.util.Date;


/**
 * This is a simple regular expression based parser for date notations.
 * While our aim is to fully support in the future the JDK date parser, currently
 * only numeric notations and literals are supported such as <code>dd/MM/yyyy HH:mm:ss.SSSS</code>.
 * Each entity is parsed with the same number of digits, i.e. for <code>dd</code> two digits will be
 * parsed while for <code>d</code> only one will be parsed.
 * @author <a href="mailto:g.georgovassilis@gmail.com">George Georgovassilis</a>
 *
 */

@SuppressWarnings("deprecation")
public class SimpleDateParser {


	private final static String DAY_IN_MONTH = "d";

	private final static String MONTH = "M";

	private final static String YEAR = "y";

	private final static String LITERAL = "\\";

	private final static int DATE_PATTERN = 0;

	private final static int REGEX_PATTERN = 1;

	private final static int COMPONENT = 2;

	private final static int REGEX = 0;

	private final static int INSTRUCTION = 1;

	private final static String[] TOKENS[] = {
	{ "SSSS", "(\\d\\d\\d\\d)",DateLocale.TOKEN_MILLISECOND },
	{ "SSS", "(\\d\\d\\d)", DateLocale.TOKEN_MILLISECOND },
	{ "SS", "(\\d\\d)", DateLocale.TOKEN_MILLISECOND },
	{ "S", "(\\d)", DateLocale.TOKEN_MILLISECOND },
	{ "ss", "(\\d\\d)", DateLocale.TOKEN_SECOND },
	{ "s", "(\\d)", DateLocale.TOKEN_SECOND },
	{ "mm", "(\\d\\d)", DateLocale.TOKEN_MINUTE },
	{ "m", "(\\d)", DateLocale.TOKEN_MINUTE},
	{ "HH", "(\\d\\d)", DateLocale.TOKEN_HOUR_24},
	{ "H", "(\\d)", DateLocale.TOKEN_HOUR_24 },
	{ "dd", "(\\d\\d)", DateLocale.TOKEN_DAY_OF_MONTH },
	{ "d", "(\\d)", DateLocale.TOKEN_DAY_OF_MONTH },
	{ "MM", "(\\d\\d)", DateLocale.TOKEN_MONTH },
	{ "M", "(\\d)", DateLocale.TOKEN_MONTH },
	{ "yyyy", "(\\d\\d\\d\\d)", DateLocale.TOKEN_YEAR },
	{ "yyy", "(\\d\\d\\d)", DateLocale.TOKEN_YEAR },
	{ "yy", "(\\d\\d)", DateLocale.TOKEN_YEAR },
	{ "y", "(\\d)", DateLocale.TOKEN_YEAR }
	};

	private Pattern regularExpression;

	private String instructions = "";

   private static void _parse(String format, String[] args) {
		if (format.length() == 0)
			return;
		if (format.startsWith("'")){
			format = format.substring(1);
			int end = format.indexOf("'");
			if (end == -1)
				throw new IllegalArgumentException("Unmatched single quotes.");
			args[REGEX]+=Pattern.quote(format.substring(0,end));
			format = format.substring(end+1);
		}
		for (int i = 0; i < TOKENS.length; i++) {
			String[] row = TOKENS[i];
			String datePattern = row[DATE_PATTERN];
			if (!format.startsWith(datePattern))
				continue;
			format = format.substring(datePattern.length());
			args[REGEX] += row[REGEX_PATTERN];
			args[INSTRUCTION] += row[COMPONENT];
			_parse(format, args);
			return;
		}
		args[REGEX] += Pattern.quote(""+format.charAt(0));
		format = format.substring(1);
		_parse(format, args);
	}

	private static void load(Date date, String text, String component) {
		if (component.equals(DateLocale.TOKEN_MILLISECOND)) {
			//TODO: implement
		}

		if (component.equals(DateLocale.TOKEN_SECOND)) {
			date.setSeconds(Integer.parseInt(text));
		}

		if (component.equals(DateLocale.TOKEN_MINUTE)) {
			date.setMinutes(Integer.parseInt(text));
		}

		if (component.equals(DateLocale.TOKEN_HOUR_24)) {
			date.setHours(Integer.parseInt(text));
		}

		if (component.equals(DateLocale.TOKEN_DAY_OF_MONTH)) {
			date.setDate(Integer.parseInt(text));
		}
		if (component.equals(DateLocale.TOKEN_MONTH)) {
			date.setMonth(Integer.parseInt(text)-1);
		}
		if (component.equals(DateLocale.TOKEN_YEAR)) {
			//TODO: fix for short patterns
			date.setYear(Integer.parseInt(text)-1900);
		}

	}

	public SimpleDateParser(String format) {
		String[] args = new String[] { "", "" };
		_parse(format, args);
		regularExpression = new Pattern(args[REGEX]);
		instructions = args[INSTRUCTION];
	}

	public Date parse(String input) {
		Date date = new Date(0, 0, 1, 0, 0, 0);
		String matches[] = regularExpression.match(input);
		if (matches == null)
			throw new IllegalArgumentException(input+" does not match "+regularExpression.pattern());
		if (matches.length-1!=instructions.length())
			throw new IllegalArgumentException("Different group count - "+input+" does not match "+regularExpression.pattern());
		for (int group = 0; group < instructions.length(); group++) {
			String match = matches[group + 1];
			load(date, match, ""+instructions.charAt(group));
		}
		return date;
	}

	public static Date parse(String input, String pattern){
		return new SimpleDateParser(pattern).parse(input);
	}
}
