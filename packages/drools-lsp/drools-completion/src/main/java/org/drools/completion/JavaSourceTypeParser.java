/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.drools.completion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.drools.drl.parser.antlr4.JavaLexer;
import org.drools.drl.parser.antlr4.JavaParser;

/**
 * Parses {@code .java} source into {@link JavaSourceType}s using the ANTLR Java
 * grammar generated into the {@code drools-parser} jar
 * ({@code org.drools.drl.parser.antlr4.JavaParser}). Only top-level types are
 * indexed; nested types are skipped. Best-effort: syntax errors are silenced so
 * partial/edited buffers still yield whatever parsed cleanly, and the parser
 * never throws.
 *
 * <p>Known limits (acceptable for typing/hover/lint): nested types are not
 * indexed; interface member extraction is name-first (fields/constants may be
 * partial); generic type arguments and array dimensions are erased to the raw
 * simple name.
 */
public final class JavaSourceTypeParser {

    private static final Logger logger = Logger.getLogger(JavaSourceTypeParser.class.getName());

    private static final BaseErrorListener SILENT = new BaseErrorListener() {
        @Override
        public void syntaxError(Recognizer<?, ?> r, Object sym, int line, int col,
                                String msg, RecognitionException e) {
        }
    };

    private JavaSourceTypeParser() {
    }

    public static List<JavaSourceType> parse(String source) {
        if (source == null || source.isBlank()) {
            return Collections.emptyList();
        }
        try {
            JavaLexer lexer = new JavaLexer(CharStreams.fromString(source));
            lexer.removeErrorListeners();
            lexer.addErrorListener(SILENT);
            JavaParser parser = new JavaParser(new CommonTokenStream(lexer));
            parser.removeErrorListeners();
            parser.addErrorListener(SILENT);

            JavaParser.CompilationUnitContext cu = parser.compilationUnit();
            if (cu == null) {
                return Collections.emptyList();
            }
            String pkg = (cu.packageDeclaration() != null
                    && cu.packageDeclaration().qualifiedName() != null)
                    ? cu.packageDeclaration().qualifiedName().getText() : "";

            List<JavaSourceType> out = new ArrayList<>();
            for (JavaParser.TypeDeclarationContext td : cu.typeDeclaration()) {
                try {
                    JavaSourceType t = fromTypeDeclaration(td, pkg);
                    if (t != null) {
                        out.add(t);
                    }
                } catch (Exception e) {
                    logger.fine(() -> "Skipping malformed top-level type: " + e.getMessage());
                }
            }
            return out;
        } catch (Exception e) {
            logger.fine(() -> "Failed to parse Java source: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private static JavaSourceType fromTypeDeclaration(JavaParser.TypeDeclarationContext td, String pkg) {
        if (td.classDeclaration() != null) {
            return fromClass(td.classDeclaration(), pkg);
        }
        if (td.enumDeclaration() != null) {
            return fromEnum(td.enumDeclaration(), pkg);
        }
        if (td.interfaceDeclaration() != null) {
            return fromInterface(td.interfaceDeclaration(), pkg);
        }
        if (td.recordDeclaration() != null) {
            return fromRecord(td.recordDeclaration(), pkg);
        }
        return null; // annotation type / bare ';'
    }

    private static JavaSourceType fromClass(JavaParser.ClassDeclarationContext cd, String pkg) {
        String simpleName = cd.identifier().getText();
        String extendsName = extendsSimpleNameOf(cd.typeType());
        List<String> interfaces = (cd.IMPLEMENTS() != null && !cd.typeList().isEmpty())
                ? simplifyAll(cd.typeList(0)) : List.of();

        List<Field> fields = new ArrayList<>();
        List<Field> getters = new ArrayList<>();
        List<String> ctors = new ArrayList<>();
        if (cd.classBody() != null) {
            collectBodyMembers(cd.classBody().classBodyDeclaration(), fields, getters, ctors, simpleName);
        }

        Map<String, Field> members = new LinkedHashMap<>();
        mergeGettersThenFields(members, getters, fields);

        return new JavaSourceType(fqcn(pkg, simpleName), simpleName, false, extendsName, interfaces,
                new ArrayList<>(members.values()), ctors,
                declLine(cd.identifier()), declColumn(cd.identifier()));
    }

    private static JavaSourceType fromEnum(JavaParser.EnumDeclarationContext ed, String pkg) {
        String simpleName = ed.identifier().getText();
        List<String> interfaces = (ed.IMPLEMENTS() != null && ed.typeList() != null)
                ? simplifyAll(ed.typeList()) : List.of();

        Map<String, Field> members = new LinkedHashMap<>();
        if (ed.enumConstants() != null) {
            for (JavaParser.EnumConstantContext ec : ed.enumConstants().enumConstant()) {
                String name = ec.identifier().getText();
                String args = ec.arguments() != null ? argsText(ec.arguments()) : null;
                members.put(name, new Field(name, simpleName, args, Field.Origin.ENUM_CONSTANT));
            }
        }

        List<Field> fields = new ArrayList<>();
        List<Field> getters = new ArrayList<>();
        List<String> ctors = new ArrayList<>();
        if (ed.enumBodyDeclarations() != null) {
            collectBodyMembers(ed.enumBodyDeclarations().classBodyDeclaration(), fields, getters, ctors, simpleName);
        }
        mergeGettersThenFields(members, getters, fields);

        return new JavaSourceType(fqcn(pkg, simpleName), simpleName, true, null, interfaces,
                new ArrayList<>(members.values()), ctors,
                declLine(ed.identifier()), declColumn(ed.identifier()));
    }

    private static JavaSourceType fromInterface(JavaParser.InterfaceDeclarationContext id, String pkg) {
        String simpleName = id.identifier().getText();
        List<String> interfaces = (id.EXTENDS() != null && !id.typeList().isEmpty())
                ? simplifyAll(id.typeList(0)) : List.of();

        List<Field> fields = new ArrayList<>();
        List<Field> getters = new ArrayList<>();
        if (id.interfaceBody() != null) {
            for (JavaParser.InterfaceBodyDeclarationContext ibd : id.interfaceBody().interfaceBodyDeclaration()) {
                try {
                    collectInterfaceMember(ibd, fields, getters);
                } catch (Exception e) {
                    logger.fine(() -> "Skipping interface member in " + simpleName + ": " + e.getMessage());
                }
            }
        }
        Map<String, Field> members = new LinkedHashMap<>();
        mergeGettersThenFields(members, getters, fields);

        return new JavaSourceType(fqcn(pkg, simpleName), simpleName, false, null, interfaces,
                new ArrayList<>(members.values()), List.of(),
                declLine(id.identifier()), declColumn(id.identifier()));
    }

    private static JavaSourceType fromRecord(JavaParser.RecordDeclarationContext rd, String pkg) {
        String simpleName = rd.identifier().getText();
        List<String> interfaces = (rd.IMPLEMENTS() != null && rd.typeList() != null)
                ? simplifyAll(rd.typeList()) : List.of();

        List<JavaParser.RecordComponentContext> components =
                (rd.recordHeader() != null && rd.recordHeader().recordComponentList() != null)
                        ? rd.recordHeader().recordComponentList().recordComponent() : List.of();

        // Records expose components only as accessor methods — there is no
        // separate private field worth modeling — so each component is a
        // single GETTER, consistent with getters beating fields elsewhere.
        Map<String, Field> members = new LinkedHashMap<>();
        List<String> ctorTypes = new ArrayList<>();
        for (JavaParser.RecordComponentContext rc : components) {
            String name = rc.identifier().getText();
            String type = simplify(rc.typeType());
            members.putIfAbsent(name, new Field(name, type, null, Field.Origin.GETTER));
            ctorTypes.add(type);
        }
        String canonicalCtor = simpleName + "(" + String.join(", ", ctorTypes) + ")";

        return new JavaSourceType(fqcn(pkg, simpleName), simpleName, false, null, interfaces,
                new ArrayList<>(members.values()), List.of(canonicalCtor),
                declLine(rd.identifier()), declColumn(rd.identifier()));
    }

    /**
     * Merges getters then fields into {@code into} via {@code putIfAbsent} —
     * a getter beats a same-named field. This mirrors the insertion order
     * {@code ClassMemberIndex.reflectMembers} uses when reflecting a compiled
     * class, so a source-parsed type's member list doesn't reshuffle once a
     * build replaces it with the reflected view.
     */
    private static void mergeGettersThenFields(Map<String, Field> into, List<Field> getters, List<Field> fields) {
        for (Field f : getters) {
            into.putIfAbsent(f.name, f);
        }
        for (Field f : fields) {
            into.putIfAbsent(f.name, f);
        }
    }

    /**
     * Walks a class/enum body's declarations, sorting each into a field,
     * getter, or constructor-signature list. Per-member failures are
     * swallowed so one malformed declaration doesn't drop the rest.
     */
    private static void collectBodyMembers(List<JavaParser.ClassBodyDeclarationContext> decls,
                                            List<Field> fieldsOut, List<Field> gettersOut,
                                            List<String> ctorsOut, String simpleName) {
        for (JavaParser.ClassBodyDeclarationContext cbd : decls) {
            try {
                JavaParser.MemberDeclarationContext md = cbd.memberDeclaration();
                if (md == null) {
                    continue; // static block or bare ';'
                }
                if (md.fieldDeclaration() != null && hasPublicModifier(cbd.modifier())) {
                    JavaParser.FieldDeclarationContext fd = md.fieldDeclaration();
                    String type = simplify(fd.typeType());
                    for (JavaParser.VariableDeclaratorContext vd : fd.variableDeclarators().variableDeclarator()) {
                        String name = vd.variableDeclaratorId().identifier().getText();
                        fieldsOut.add(new Field(name, type, null, Field.Origin.FIELD));
                    }
                } else if (md.methodDeclaration() != null) {
                    JavaParser.MethodDeclarationContext mt = md.methodDeclaration();
                    String property = getterPropertyOf(mt.typeTypeOrVoid(), mt.identifier(), mt.formalParameters());
                    if (property != null) {
                        gettersOut.add(new Field(property, simplify(mt.typeTypeOrVoid().typeType()), null,
                                Field.Origin.GETTER));
                    }
                } else if (md.constructorDeclaration() != null) {
                    ctorsOut.add(constructorSignature(simpleName, md.constructorDeclaration().formalParameters()));
                }
            } catch (Exception e) {
                logger.fine(() -> "Skipping class member in " + simpleName + ": " + e.getMessage());
            }
        }
    }

    /**
     * True when {@code modifiers} includes {@code public} — mirrors
     * reflection's {@code Class#getFields()}, which surfaces public fields
     * only (instance and static alike; static-field inclusion is a
     * documented residual drift, see the roadmap's known limitations).
     */
    private static boolean hasPublicModifier(List<JavaParser.ModifierContext> modifiers) {
        for (JavaParser.ModifierContext modifier : modifiers) {
            JavaParser.ClassOrInterfaceModifierContext coim = modifier.classOrInterfaceModifier();
            if (coim != null && coim.PUBLIC() != null) {
                return true;
            }
        }
        return false;
    }

    /** Extracts a {@code constDeclaration} or no-arg getter from one interface body member, if any. */
    private static void collectInterfaceMember(JavaParser.InterfaceBodyDeclarationContext ibd,
                                                List<Field> fieldsOut, List<Field> gettersOut) {
        JavaParser.InterfaceMemberDeclarationContext imd = ibd.interfaceMemberDeclaration();
        if (imd == null) {
            return; // bare ';'
        }
        if (imd.constDeclaration() != null) {
            JavaParser.ConstDeclarationContext cdecl = imd.constDeclaration();
            String type = simplify(cdecl.typeType());
            for (JavaParser.ConstantDeclaratorContext decl : cdecl.constantDeclarator()) {
                String name = decl.identifier().getText();
                fieldsOut.add(new Field(name, type, null, Field.Origin.FIELD));
            }
        } else if (imd.interfaceMethodDeclaration() != null) {
            JavaParser.InterfaceCommonBodyDeclarationContext body =
                    imd.interfaceMethodDeclaration().interfaceCommonBodyDeclaration();
            String property = getterPropertyOf(body.typeTypeOrVoid(), body.identifier(), body.formalParameters());
            if (property != null) {
                gettersOut.add(new Field(property, simplify(body.typeTypeOrVoid().typeType()), null,
                        Field.Origin.GETTER));
            }
        }
    }

    /**
     * Maps a no-arg {@code getX()}/{@code isX()} declaration to its bean
     * property name (JavaBeans decapitalize rule, mirroring
     * {@code ClassMemberIndex.propertyNameOf}), or {@code null} for
     * non-accessors and {@code getClass}.
     */
    private static String getterPropertyOf(JavaParser.TypeTypeOrVoidContext returnType,
                                            JavaParser.IdentifierContext nameCtx,
                                            JavaParser.FormalParametersContext params) {
        if (returnType == null || returnType.typeType() == null || nameCtx == null) {
            return null; // void return, or malformed
        }
        if (params != null && params.formalParameterList() != null) {
            return null; // getters take no arguments
        }
        String name = nameCtx.getText();
        if (name.startsWith("get") && name.length() > 3 && Character.isUpperCase(name.charAt(3))) {
            if ("getClass".equals(name)) {
                return null;
            }
            return decapitalize(name.substring(3));
        }
        if (name.startsWith("is") && name.length() > 2 && Character.isUpperCase(name.charAt(2))) {
            String returnSimple = simplify(returnType.typeType());
            if ("boolean".equals(returnSimple) || "Boolean".equals(returnSimple)) {
                return decapitalize(name.substring(2));
            }
        }
        return null;
    }

    /** JavaBeans decapitalize: keep as-is when the first two letters are both uppercase. */
    private static String decapitalize(String raw) {
        if (raw.length() > 1 && Character.isUpperCase(raw.charAt(0)) && Character.isUpperCase(raw.charAt(1))) {
            return raw;
        }
        char[] chars = raw.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

    private static String constructorSignature(String simpleName, JavaParser.FormalParametersContext params) {
        List<String> types = new ArrayList<>();
        JavaParser.FormalParameterListContext list = params == null ? null : params.formalParameterList();
        if (list != null) {
            for (JavaParser.FormalParameterContext fp : list.formalParameter()) {
                types.add(simplify(fp.typeType()));
            }
            if (list.lastFormalParameter() != null) {
                types.add(simplify(list.lastFormalParameter().typeType()) + "...");
            }
        }
        return simpleName + "(" + String.join(", ", types) + ")";
    }

    private static List<String> simplifyAll(JavaParser.TypeListContext typeList) {
        List<String> out = new ArrayList<>();
        if (typeList != null) {
            for (JavaParser.TypeTypeContext t : typeList.typeType()) {
                out.add(simplify(t));
            }
        }
        return out;
    }

    /**
     * Simple name of an {@code extends} supertype: the last {@code identifier()}
     * of its {@code classOrInterfaceType()} when present (avoids any ambiguity
     * from generics/arrays in raw text), else the type's text run through
     * {@link #simplify(String)}.
     */
    private static String extendsSimpleNameOf(JavaParser.TypeTypeContext typeType) {
        if (typeType == null) {
            return null;
        }
        JavaParser.ClassOrInterfaceTypeContext coit = typeType.classOrInterfaceType();
        if (coit != null && !coit.identifier().isEmpty()) {
            List<JavaParser.IdentifierContext> ids = coit.identifier();
            return ids.get(ids.size() - 1).getText();
        }
        return simplify(typeType.getText());
    }

    private static String simplify(JavaParser.TypeTypeContext typeType) {
        return typeType == null ? null : simplify(typeType.getText());
    }

    /** Strips generic type arguments and array brackets, then takes the last dotted segment. */
    private static String simplify(String rawType) {
        if (rawType == null) {
            return null;
        }
        String t = rawType;
        int generics = t.indexOf('<');
        if (generics >= 0) {
            t = t.substring(0, generics);
        }
        while (t.endsWith("[]")) {
            t = t.substring(0, t.length() - 2);
        }
        int dot = t.lastIndexOf('.');
        return dot >= 0 ? t.substring(dot + 1) : t;
    }

    /** The raw text between an {@code arguments()} node's parentheses. */
    private static String argsText(JavaParser.ArgumentsContext args) {
        String text = args.getText();
        if (text.length() >= 2 && text.charAt(0) == '(' && text.charAt(text.length() - 1) == ')') {
            return text.substring(1, text.length() - 1);
        }
        return text;
    }

    private static String fqcn(String pkg, String simpleName) {
        return pkg.isEmpty() ? simpleName : pkg + "." + simpleName;
    }

    private static int declLine(JavaParser.IdentifierContext id) {
        return id.getStart().getLine() - 1;
    }

    private static int declColumn(JavaParser.IdentifierContext id) {
        return id.getStart().getCharPositionInLine();
    }
}
