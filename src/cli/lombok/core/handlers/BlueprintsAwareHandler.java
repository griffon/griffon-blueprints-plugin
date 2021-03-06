/*
 * Copyright 2012-2013 the original author or authors.
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

package lombok.core.handlers;

import lombok.ast.Expression;
import lombok.ast.IMethod;
import lombok.ast.IType;

import static lombok.ast.AST.*;

/**
 * @author Andres Almiray
 */
public abstract class BlueprintsAwareHandler<TYPE_TYPE extends IType<? extends IMethod<?, ?, ?, ?>, ?, ?, ?, ?, ?>> extends AbstractHandler<TYPE_TYPE> implements BlueprintsAwareConstants {
    private Expression<?> defaultBlueprintsProviderInstance() {
        return Call(Name(DEFAULT_BLUEPRINTS_PROVIDER_TYPE), "getInstance");
    }

    public void addBlueprintsProviderField(final TYPE_TYPE type) {
        addField(type, BLUEPRINTS_PROVIDER_TYPE, BLUEPRINTS_PROVIDER_FIELD_NAME, defaultBlueprintsProviderInstance());
    }

    public void addBlueprintsProviderAccessors(final TYPE_TYPE type) {
        type.editor().injectMethod(
            MethodDecl(Type(VOID), METHOD_SET_BLUEPRINTS_PROVIDER)
                .makePublic()
                .withArgument(Arg(Type(BLUEPRINTS_PROVIDER_TYPE), PROVIDER))
                .withStatement(
                    If(Equal(Name(PROVIDER), Null()))
                        .Then(Block()
                            .withStatement(Assign(Field(BLUEPRINTS_PROVIDER_FIELD_NAME), defaultBlueprintsProviderInstance())))
                        .Else(Block()
                            .withStatement(Assign(Field(BLUEPRINTS_PROVIDER_FIELD_NAME), Name(PROVIDER)))))
        );

        type.editor().injectMethod(
            MethodDecl(Type(BLUEPRINTS_PROVIDER_TYPE), METHOD_GET_BLUEPRINTS_PROVIDER)
                .makePublic()
                .withStatement(Return(Field(BLUEPRINTS_PROVIDER_FIELD_NAME)))
        );
    }

    public void addBlueprintsContributionMethods(final TYPE_TYPE type) {
        delegateMethodsTo(type, METHODS, Field(BLUEPRINTS_PROVIDER_FIELD_NAME));
    }
}
