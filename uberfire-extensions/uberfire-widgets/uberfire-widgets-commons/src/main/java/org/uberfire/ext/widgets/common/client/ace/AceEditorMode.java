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
 * Enumeration for ACE editor modes.
 * Note that the corresponding .js file must be loaded
 * before a mode can be set.
 */
public enum AceEditorMode {
	/** ABAP (Advanced Business Application Programming). */
	ABAP("abap"),
	/** Actionscript. */
	ACTIONSCRIPT("actionscript"),
	/** Ada. */
	ADA("ada"),
	/** Apache configuration. */
	APACHE_CONF("apache_conf"),
	/** Applescript. */
	APPLESCRIPT("applescript"),
	/** ASCIIDOC. */
	ASCIIDOC("asciidoc"),
	/** Assembly (x86). */
	ASSEMBLY_X86("assembly_x86"),
	/** Auto Hotkey. */
	AUTOHOTKEY("autohotkey"),
	/** Batch file. */
	BATCHFILE("batchfile"),
	/** c9search */
	C9SEARCH("c9search"),
	/** C/C++. */
	C_CPP("c_cpp"),
	/** Cirru, indentation-based grammar for languages. */
	CIRRU("cirru"),
	/** Clojure. */
	CLOJURE("clojure"),
	/** COBOL. */
	COBOL("cobol"),
	/** Coffee. */
	COFFEE("coffee"),
	/** ColdFusion. */
	COLDFUSION("coldfusion"),
	/** C#. */
	CSHARP("csharp"),
	/** CSS. */
	CSS("css"),
	/* Curly. */
	CURLY("curly"),
	/** Dart. */
	DART("Dart"),
	/** Diff. */
	DIFF("diff"),
	/** Django. */
	DJANGO("django"),
	/** D. */
	D("d"),
	/** Docker files. */
	DOCKERFILE("dockerfile"),
	/** Dot. */
	DOT("dot"),
	/** EJS (Embedded Javascript). */
	EJS("ejs"),
	/** Erlang. */
	ERLANG("erlang"),
	/** Forth. */
	FORTH("forth"),
	/** FTL. */
	FTL("ftl"),
	/** Gherkin: Business Readable, Domain Specific Language. */
	GHERKIN("gherkin"),
	/** GLSL (OpenGL Shading Language). */
	GLSL("glsl"),
	/** Go (http://golang.org/). */
	GOLANG("golang"),
	/** Groovy. */
	GROOVY("groovy"),
	/** HAML. */
	HAML("haml"),
	/** Handlebars.js: Minimal Templating on Steroids. */
	HANDLEBARS("handlebars"),
	/** Haskell. */
	HASKELL("haskell"),
	/** Haxe. */
	HAXE("haxe"),
	/** HTML. */
	HTML("html"),
	/** HTML completions. */
	HTML_COMPLETIONS("html_completions"),
	/** HTML (Ruby). */
	HTML_RUBY("html_ruby"),
	/** Ini file. */
	INI("ini"),
	/** JADE. */
	JADE("jade"),
	/** Jack. */
	JACK("jack"),
	/** JAVA. */
	JAVA("java"),
	/** Javascript. */
	JAVASCRIPT("javascript"),
	/** JSONiq, the JSON Query Language. */
	JSONIQ("jsoniq"),
	/** JSON. */
	JSON("json"),
	/** JSP, Java Server Pages. */
	JSP("jsp"),
	/** JSX. */
	JSX("jsx"),
	/** Julia. */
	JULIA("julia"),
	/** LaTeX. */
	LATEX("latex"),
	/** Less. */
	LESS("less"),
	/** Liquid. */
	LIQUID("liquid"),
	/** LISP. */
	LISP("lisp"),
	/** Livescript. */
	LIVESCRIPT("livescript"),
	/** LogiQL. */
	LOGIQL("logiql"),
	/** LSL. */
	LSL("lsl"),
	/** Lua. */
	LUA("lua"),
	/** Luapage. */
	LUAPAGE("luapage"),
	/** Lucene. */
	LUCENE("lucene"),
	/** Makefile. */
	MAKEFILE("makefile"),
	/** Markdown. */
	MARKDOWN("markdown"),
	/** Matlab. */
	MATLAB("matlab"),
	/** Mel. */
	MEL("mel"),
	/** MUSHCode (High Rules). */
	MUSHCODE_HIGH_RULES("mushcode_high_rules"),
	/** MUSHCode. */
	MUSHCODE("mushcode"),
	/** MySQL. */
	MYSQL("mysql"),
	/** Nix. */
	NIX("nix"),
	/** Objective C. */
	OBJECTIVEC("objectivec"),
	/** OCaml. */
	OCAML("ocaml"),
	/** Pascal. */
	PASCAL("pascal"),
	/** Perl. */
	PERL("perl"),
	/** PgSQL. */
	PGSQL("pgsql"),
	/** PHP. */
	PHP("php"),
	/** Plain text. */
	PLAIN_TEXT("plain_text"),
	/** PowerShell. */
	POWERSHELL("powershell"),
	/** Prolog. */
	PROLOG("prolog"),
	/** Java properties file. */
	PROPERTIES("properties"),
	/** Protocol Buffers - Google's data interchange format. */
	PROTOBUF("protobuf"),
	/** Python. */
	PYTHON("python"),
	/** RDoc (Ruby documentation). */
	RDOC("rdoc"),
	/** RHTML. */
	RHTML("rhtml"),
	/** R. */
	R("r"),
	/** Ruby. */
	RUBY("ruby"),
	/** Rust. */
	RUST("rust"),
	/** SASS. */
	SASS("sass"),
	/** Scad. */
	SCAD("scad"),
	/** Scala. */
	SCALA("scala"),
	/** Scheme. */
	SCHEME("scheme"),
	/** SCSS. */
	SCSS("scss"),
	/** Sh (Bourne shell). */
	SH("sh"),
	/** Sjs. */
	SJS("sjs"),
	/** Smarty (PHP template engine). */
	SMARTY("smarty"),
	/** Snippets. */
	SNIPPETS("snippets"),
	/** Soy template. */
	SOY_TEMPLATE("soy_template"),
	/** Space. */
	SPACE("space"),
	/** SQL. */
	SQL("sql"),
	/** Stylus. */
	STYLUS("stylus"),
	/** SVG. */
	SVG("svg"),
	/** Tcl. */
	TCL("tcl"),
	/** TeX. */
	TEX("tex"),
	/** Text. */
	TEXT("text"),
	/** Textile. */
	TEXTILE("textile"),
	/** TOML. */
	TOML("toml"),
	/** TWIG. */
	TWIG("twig"),
	/** TypeScript. */
	TYPESCRIPT("typescript"),
	/** Vala. */
	VALA("vala"),
	/** VBScript. */
	VBSCRIPT("vbscript"),
	/** Velocity. */
	VELOCITY("velocity"),
	/** Verilog. */
	VERILOG("verilog"),
	/** VHDL. */
	VHDL("vhdl"),
	/** XML. */
	XML("xml"),
	/** XQuery. */
	XQUERY("xquery"),
	/** YAML. */
	YAML("yaml");
	
	private final String name;
	
	private AceEditorMode(String name) {
		this.name = name;
	}
	
	/**
	 * @return mode name (e.g., "java" for Java mode)
	 */
	public String getName() {
		return name;
	}
}
