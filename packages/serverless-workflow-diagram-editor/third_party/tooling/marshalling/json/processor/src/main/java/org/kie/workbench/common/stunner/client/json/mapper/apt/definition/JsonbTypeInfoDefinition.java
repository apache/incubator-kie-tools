/*
 * Copyright © 2022 Treblereel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.json.mapper.apt.definition;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.UnknownType;
import com.google.auto.common.MoreTypes;
import jakarta.json.bind.annotation.JsonbSubtype;
import jakarta.json.bind.annotation.JsonbTypeInfo;
import org.kie.workbench.common.stunner.client.json.mapper.apt.context.GenerationContext;
import org.kie.workbench.common.stunner.client.json.mapper.internal.Pair;
import org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer.JsonbSubtypeDeserializer;
import org.kie.workbench.common.stunner.client.json.mapper.internal.serializer.JsonbSubtypeSerializer;

public class JsonbTypeInfoDefinition extends FieldDefinition {

    private final Map<String, TypeMirror> types = new HashMap<>();

    private final String typeFieldName;

    public JsonbTypeInfoDefinition(
            JsonbTypeInfo jsonbTypeInfo, TypeMirror property, GenerationContext context) {
        super(property, context);
        this.typeFieldName = jsonbTypeInfo.key();
        for (JsonbSubtype jsonbSubtype : jsonbTypeInfo.value()) {
            getJsonbSubtype(jsonbSubtype);
        }
    }

    @Override
    public Statement getFieldDeserializer(PropertyDefinition field, CompilationUnit cu) {
        ObjectCreationExpr deserializerCreationExpr = getDeserializerCreationExpr(field.getType(), cu);

        return new ExpressionStmt(
                new MethodCallExpr(new NameExpr("bean"), field.getSetter().getSimpleName().toString())
                        .addArgument(
                                new MethodCallExpr(deserializerCreationExpr, "deserialize")
                                        .addArgument(
                                                new MethodCallExpr(new NameExpr("jsonObject"), "getJsonObject")
                                                        .addArgument(new StringLiteralExpr(field.getName())))
                                        .addArgument(new NameExpr("ctx"))));
    }

    @Override
    public Statement getFieldSerializer(PropertyDefinition field, CompilationUnit cu) {
        ObjectCreationExpr serializerCreationExpr = getSerializerCreationExpr(cu);
        return new ExpressionStmt(
                new MethodCallExpr(serializerCreationExpr, "serialize")
                        .addArgument(
                                new MethodCallExpr(
                                        new NameExpr("bean"), field.getGetter().getSimpleName().toString()))
                        .addArgument(new StringLiteralExpr(field.getName()))
                        .addArgument(new NameExpr("generator"))
                        .addArgument(new NameExpr("ctx")));
    }

    public ObjectCreationExpr getSerializerCreationExpr(CompilationUnit cu) {
        cu.addImport(JsonbSubtypeSerializer.class);
        cu.addImport(JsonbSubtypeSerializer.Info.class);

        ClassOrInterfaceType type = new ClassOrInterfaceType();
        type.setName(JsonbSubtypeSerializer.class.getSimpleName());

        ObjectCreationExpr serializerCreationExpr = new ObjectCreationExpr();
        type.setTypeArguments(new UnknownType());
        serializerCreationExpr.setType(type);

        serializerCreationExpr.addArgument(new StringLiteralExpr(typeFieldName));

        types.forEach(
                (alias, mirror) -> {
                    ObjectCreationExpr info = new ObjectCreationExpr();
                    info.setType(
                            new ClassOrInterfaceType()
                                    .setName(JsonbSubtypeSerializer.Info.class.getSimpleName()));
                    info.addArgument(new StringLiteralExpr(alias));
                    info.addArgument(new FieldAccessExpr(new NameExpr(mirror.toString()), "class"));
                    info.addArgument(
                            new ObjectCreationExpr()
                                    .setType(
                                            new ClassOrInterfaceType()
                                                    .setName(
                                                            context
                                                                    .getTypeUtils()
                                                                    .getJsonSerializerImplQualifiedName(
                                                                            MoreTypes.asTypeElement(mirror)))));
                    serializerCreationExpr.addArgument(info);
                });
        return serializerCreationExpr;
    }

    private void getJsonbSubtype(JsonbSubtype jsonbSubtype) {
        try {
            jsonbSubtype.type();
        } catch (MirroredTypeException e) {
            types.put(jsonbSubtype.alias(), e.getTypeMirror());
        }
    }

    public ObjectCreationExpr getDeserializerCreationExpr(TypeMirror fieldType, CompilationUnit cu) {
        cu.addImport(JsonbSubtypeDeserializer.class);
        cu.addImport(Pair.class);

        ClassOrInterfaceType type = new ClassOrInterfaceType();
        type.setName(JsonbSubtypeDeserializer.class.getSimpleName());

        ObjectCreationExpr deserializerCreationExpr = new ObjectCreationExpr();
        type.setTypeArguments(new ClassOrInterfaceType().setName(fieldType.toString()));
        deserializerCreationExpr.setType(type);

        deserializerCreationExpr.addArgument(new StringLiteralExpr(typeFieldName));

        types.forEach(
                (alias, ser) -> {
                    ObjectCreationExpr pairCreationExpr = new ObjectCreationExpr();
                    pairCreationExpr.setType(new ClassOrInterfaceType().setName("Pair"));
                    pairCreationExpr.addArgument(new StringLiteralExpr(alias));
                    pairCreationExpr.addArgument(
                            new ObjectCreationExpr()
                                    .setType(
                                            new ClassOrInterfaceType()
                                                    .setName(
                                                            context
                                                                    .getTypeUtils()
                                                                    .getJsonDeserializerImplQualifiedName(
                                                                            MoreTypes.asTypeElement(ser), cu))));
                    deserializerCreationExpr.addArgument(pairCreationExpr);
                });
        return deserializerCreationExpr;
    }
}
