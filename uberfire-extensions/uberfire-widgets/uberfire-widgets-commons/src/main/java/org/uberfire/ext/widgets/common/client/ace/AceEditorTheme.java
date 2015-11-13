/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.widgets.common.client.ace;

/**
 * Enumeration for ACE editor themes.
 * Note that the corresponding .js file must be loaded
 * before a theme can be set.
 */
public enum AceEditorTheme {
	AMBIANCE("ambiance"),
	CHAOS("chaos"),
	CHROME("chrome"),
	CLOUD9_DAY("cloud9_day"),
	CLOUD9_NIGHT("cloud9_night"),
	CLOUD9_NIGHT_LOW_COLOR("cloud9_night_low_color"),
	CLOUDS("clouds"),
	CLOUDS_MIDNIGHT("clouds_midnight"),
	COBALT("cobalt"),
	CRIMSON_EDITOR("crimson_editor"),
	DAWN("dawn"),
	DREAMWEAVER("dreamweaver"),
	ECLIPSE("eclipse"),
	GITHUB("github"),
	IDLE_FINGERS("idle_fingers"),
	KATZENMILCH("katzenmilch"),
	KR_THEME("kr_theme"),
	KR("kr"),
	KUROIR("kuroir"),
	MERBIVORE("merbivore"),
	MERBIVORE_SOFT("merbivore_soft"),
	MONO_INDUSTRIAL("mono_industrial"),
	MONOKAI("monokai"),
	PASTEL_ON_DARK("pastel_on_dark"),
	SOLARIZED_DARK("solarized_dark"),
	SOLARIZED_LIGHT("solarized_light"),
	TERMINAL("terminal"),
	TEXTMATE("textmate"),
	TOMORROW_NIGHT_BLUE("tomorrow_night_blue"),
	TOMORROW_NIGHT_BRIGHT("tomorrow_night_bright"),
	TOMORROW_NIGHT_EIGHTIES("tomorrow_night_eighties"),
	TOMORROW_NIGHT("tomorrow_night"),
	TOMORROW("tomorrow"),
	TWILIGHT("twilight"),
	VIBRANT_INK("vibrant_ink"),
	XCODE("xcode");
	
	private final String name;
	
	private AceEditorTheme(String name) {
		this.name = name;
	}
	
	/**
	 * @return the theme name (e.g., "eclipse")
	 */
	public String getName() {
		return name;
	}
}
