/*-
 * ============LICENSE_START=======================================================
 * SDC
 * ================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.openecomp.sdc.be.components.validation;

import com.google.common.collect.ImmutableSet;
import fj.data.Either;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.openecomp.sdc.be.components.impl.ResponseFormatManager;
import org.openecomp.sdc.be.components.impl.utils.NodeFilterConstraintAction;
import org.openecomp.sdc.be.dao.api.ActionStatus;
import org.openecomp.sdc.be.datamodel.utils.ConstraintConvertor;
import org.openecomp.sdc.be.datatypes.elements.SchemaDefinition;
import org.openecomp.sdc.be.datatypes.enums.NodeFilterConstraintType;
import org.openecomp.sdc.be.impl.ComponentsUtils;
import org.openecomp.sdc.be.model.CapabilityDefinition;
import org.openecomp.sdc.be.model.Component;
import org.openecomp.sdc.be.model.ComponentInstance;
import org.openecomp.sdc.be.model.ComponentInstanceProperty;
import org.openecomp.sdc.be.model.PropertyDefinition;
import org.openecomp.sdc.be.model.jsonjanusgraph.operations.ToscaOperationFacade;
import org.openecomp.sdc.be.model.tosca.ToscaPropertyType;
import org.openecomp.sdc.be.ui.model.UIConstraint;
import org.openecomp.sdc.exception.ResponseFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

@org.springframework.stereotype.Component("NodeFilterValidator")
public class NodeFilterValidator {

    private static final String SOURCE = "Source";
    public static final Set<String> comparableTypes = ImmutableSet.of(ToscaPropertyType.STRING.getType(),
        ToscaPropertyType.INTEGER.getType(), ToscaPropertyType.FLOAT.getType());
    public static final Set<String> schemableTypes =
        ImmutableSet.of(ToscaPropertyType.MAP.getType(), ToscaPropertyType.LIST.getType());
    public static final Set<String> comparableConstraintsOperators =
        ImmutableSet.of(ConstraintConvertor.GREATER_THAN_OPERATOR, ConstraintConvertor.LESS_THAN_OPERATOR);

    protected final ToscaOperationFacade toscaOperationFacade;
    protected final ComponentsUtils componentsUtils;

    private static final Logger LOGGER = LoggerFactory.getLogger(NodeFilterValidator.class);

    @Autowired
    public NodeFilterValidator(final ToscaOperationFacade toscaOperationFacade,
                               final ComponentsUtils componentsUtils) {
        this.toscaOperationFacade = toscaOperationFacade;
        this.componentsUtils = componentsUtils;
    }

    public Either<Boolean, ResponseFormat> validateComponentInstanceExist(final Component component,
                                                                          final String componentInstanceId) {
        if (component == null || StringUtils.isEmpty(componentInstanceId)) {
            LOGGER.error("Input data cannot be empty");
            return getErrorResponse(ActionStatus.FILTER_NOT_FOUND);
        }
        if (CollectionUtils.isEmpty(component.getComponentInstances()) ||
            component.getComponentInstances().stream()
                .noneMatch(ci -> ci.getUniqueId().equals(componentInstanceId))) {
            LOGGER.error("Component Instance list is empty");
            return getErrorResponse(ActionStatus.FILTER_NOT_FOUND);
        }
        return Either.left(Boolean.TRUE);
    }

    private Either<Boolean, ResponseFormat> getErrorResponse(ActionStatus actionStatus, String... variables) {
        ResponseFormat errorResponse = ResponseFormatManager.getInstance().getResponseFormat(actionStatus, variables);
        return Either.right(errorResponse);
    }

    public Either<Boolean, ResponseFormat> validateFilter(final Component parentComponent,
                                                          final String componentInstanceId,
                                                          final List<String> uiConstraints,
                                                          final NodeFilterConstraintAction action,
                                                          final NodeFilterConstraintType nodeFilterConstraintType) {
        try {
            if (NodeFilterConstraintAction.ADD == action || NodeFilterConstraintAction.UPDATE == action) {
                for (final String uiConstraint : uiConstraints) {
                    final UIConstraint constraint = new ConstraintConvertor().convert(uiConstraint);
                    if (ConstraintConvertor.PROPERTY_CONSTRAINT.equals(constraint.getSourceType())) {
                        final Either<Boolean, ResponseFormat> booleanResponseFormatEither =
                            validatePropertyConstraint(parentComponent, componentInstanceId, constraint);
                        if (booleanResponseFormatEither.isRight()) {
                            return booleanResponseFormatEither;
                        }
                    } else if (ConstraintConvertor.STATIC_CONSTRAINT.equals(constraint.getSourceType())) {
                        Either<Boolean, ResponseFormat> booleanResponseFormatEither;
                        if (NodeFilterConstraintType.PROPERTIES.equals(nodeFilterConstraintType)) {
                            booleanResponseFormatEither = isComponentPropertyFilterValid(
                                parentComponent, componentInstanceId, constraint);
                        } else {
                            booleanResponseFormatEither = isComponentCapabilityPropertyFilterValid(
                                parentComponent, componentInstanceId, constraint);
                        }

                        if (booleanResponseFormatEither.isRight()) {
                            return booleanResponseFormatEither;
                        }
                    }
                }
            }
        } catch (final Exception e) {
            LOGGER.debug("Provided constraint" + uiConstraints, e);
            return Either.right(componentsUtils.getResponseFormat(ActionStatus.CONSTRAINT_FORMAT_INCORRECT));
        }

        return Either.left(true);
    }

    private Either<Boolean, ResponseFormat> isComponentCapabilityPropertyFilterValid(final Component parentComponent,
                                                                                     final String componentInstanceId,
                                                                                     final UIConstraint uiConstraint) {

        return validateStaticValueAndOperatorOfCapabilityProperties(parentComponent, componentInstanceId, uiConstraint);

    }

    private Either<Boolean, ResponseFormat> isComponentPropertyFilterValid(Component parentComponent,
                                                                           String componentInstanceId,
                                                                           UIConstraint constraint) {

        return validateStaticValueAndOperator(parentComponent, componentInstanceId, constraint);
    }

    private Either<Boolean, ResponseFormat> validatePropertyConstraint(final Component parentComponent,
                                                                       final String componentInstanceId,
                                                                       final UIConstraint uiConstraint) {
        String source = SOURCE;
        final Optional<ComponentInstance> optionalComponentInstance;

        final List<PropertyDefinition> propertyDefinitions = parentComponent.getProperties();
        List<? extends PropertyDefinition> sourcePropertyDefinition =
            parentComponent.getName().equals(uiConstraint.getSourceName()) &&
                propertyDefinitions != null ? propertyDefinitions : Collections.emptyList();

        if (sourcePropertyDefinition.isEmpty() && !parentComponent.getName().equals(uiConstraint.getSourceName())) {
            optionalComponentInstance = parentComponent.getComponentInstances().stream()
                .filter(componentInstance -> uiConstraint.getSourceName()
                    .equals(componentInstance
                        .getName()))
                .findFirst();

            if (optionalComponentInstance.isPresent()) {
                final List<ComponentInstanceProperty> componentInstanceProperties =
                    parentComponent.getComponentInstancesProperties()
                        .get(optionalComponentInstance.get().getUniqueId());
                sourcePropertyDefinition =
                    componentInstanceProperties == null ? new ArrayList<>() : componentInstanceProperties;
            }
        }

        if (!CollectionUtils.isEmpty(sourcePropertyDefinition)) {
            final Optional<? extends PropertyDefinition> sourceSelectedProperty = sourcePropertyDefinition.stream()
                .filter(property -> uiConstraint
                    .getValue()
                    .equals(property.getName()))
                .findFirst();

            final Optional<? extends PropertyDefinition> targetComponentInstanceProperty =
                parentComponent.getComponentInstancesProperties().get(componentInstanceId).stream()
                    .filter(property -> uiConstraint.getServicePropertyName().equals(property.getName()))
                    .findFirst();

            source = !targetComponentInstanceProperty.isPresent() ? "Target" : SOURCE;
            if (sourceSelectedProperty.isPresent() && targetComponentInstanceProperty.isPresent()) {
                return validatePropertyData(uiConstraint, sourceSelectedProperty, targetComponentInstanceProperty);
            }
        }

        final String missingProperty =
            source.equals(SOURCE) ? uiConstraint.getValue().toString() : uiConstraint.getServicePropertyName();

        return Either.right(
            componentsUtils.getResponseFormat(ActionStatus.MAPPED_PROPERTY_NOT_FOUND, source, missingProperty));
    }

    private Either<Boolean, ResponseFormat> validatePropertyData(UIConstraint uiConstraint,
                                                                 Optional<? extends PropertyDefinition> sourceSelectedProperty,
                                                                 Optional<? extends PropertyDefinition> targetComponentInstanceProperty) {
        if (sourceSelectedProperty.isPresent() && targetComponentInstanceProperty.isPresent()) {
            final PropertyDefinition sourcePropDefinition = sourceSelectedProperty.get();
            final String sourceType = sourcePropDefinition.getType();
            final PropertyDefinition targetPropDefinition = targetComponentInstanceProperty.get();
            final String targetType = targetPropDefinition.getType();
            if (sourceType.equals(targetType)) {
                if (schemableTypes.contains(sourceType)) {
                    final SchemaDefinition sourceSchemaDefinition = sourcePropDefinition.getSchema();
                    final SchemaDefinition targetSchemaDefinition = targetPropDefinition.getSchema();
                    if (!sourceSchemaDefinition.equals(targetSchemaDefinition)) {
                        return Either
                            .right(componentsUtils.getResponseFormat(ActionStatus.SOURCE_TARGET_SCHEMA_MISMATCH,
                                uiConstraint.getServicePropertyName(), uiConstraint.getValue().toString()));
                    }
                }
                return Either.left(Boolean.TRUE);
            } else {
                return Either.right(componentsUtils.getResponseFormat(ActionStatus.SOURCE_TARGET_PROPERTY_TYPE_MISMATCH,
                    uiConstraint.getServicePropertyName(), uiConstraint.getValue().toString()));
            }
        } else {
            LOGGER.debug(
                "Null value passed to `validatePropertyData` - sourceSelectedProperty: '{}' - targetComponentInstanceProperty: '{}'",
                sourceSelectedProperty, targetComponentInstanceProperty);
            return Either.right(componentsUtils
                .getResponseFormat(ActionStatus.GENERAL_ERROR, uiConstraint.getServicePropertyName(),
                    uiConstraint.getValue().toString()));
        }
    }

    private Either<Boolean, ResponseFormat> validateStaticValueAndOperator(
        final Component parentComponent,
        final String componentInstanceId, final UIConstraint uiConstraint) {
        if (!(Objects.nonNull(uiConstraint) && uiConstraint.getValue() instanceof String)) {
            return Either.left(false);
        }
        //TODO: get capabilities properties when constraint type is capabilities
        final Optional<ComponentInstanceProperty> componentInstanceProperty =
            parentComponent.getComponentInstancesProperties().get(componentInstanceId).stream()
                .filter(property -> uiConstraint.getServicePropertyName().equals(property.getName()))
                .findFirst();

        if (!componentInstanceProperty.isPresent()) {
            return Either.right(componentsUtils.getResponseFormat(ActionStatus.SELECTED_PROPERTY_NOT_PRESENT,
                uiConstraint.getServicePropertyName()));
        }
        if (comparableConstraintsOperators.contains(uiConstraint.getConstraintOperator()) && !comparableTypes.contains(
            componentInstanceProperty.get().getType())) {
            return Either.right(componentsUtils.getResponseFormat(ActionStatus.UNSUPPORTED_OPERATOR_PROVIDED,
                uiConstraint.getServicePropertyName(), uiConstraint.getConstraintOperator()));
        }

        return isValidValueCheck(componentInstanceProperty.get().getType(), String.valueOf(uiConstraint.getValue()),
            uiConstraint.getServicePropertyName());
    }

    private Either<Boolean, ResponseFormat> validateStaticValueAndOperatorOfCapabilityProperties(
        final Component parentComponent, final String componentInstanceId, final UIConstraint uiConstraint) {
        if (!(Objects.nonNull(uiConstraint) && uiConstraint.getValue() instanceof String)) {
            return Either.left(false);
        }
        Optional<ComponentInstanceProperty> optionalComponentInstanceProperty = Optional.empty();
        final Optional<ComponentInstance> optionalComponentInstances = parentComponent.getComponentInstances().stream()
            .filter(componentInstance -> componentInstanceId.equalsIgnoreCase(componentInstance.getUniqueId()))
            .findFirst();
        if (optionalComponentInstances.isPresent()) {
            final Optional<List<CapabilityDefinition>> optionalCapabilityDefinitionList = optionalComponentInstances.get()
                .getCapabilities().values().stream()
                .filter(capabilityDefinitions -> capabilityDefinitions
                    .stream().allMatch(capabilityDefinition -> capabilityDefinition.getProperties() != null))
                .collect(Collectors.toList())
                .stream().filter(capabilityDefinitions -> capabilityDefinitions.stream().allMatch(
                    capabilityDefinition -> capabilityDefinition.getProperties().stream().anyMatch(
                        componentInstanceProperty -> uiConstraint.getServicePropertyName()
                            .equalsIgnoreCase(componentInstanceProperty.getName())))).findFirst();

            if (optionalCapabilityDefinitionList.isPresent()) {
                optionalComponentInstanceProperty = optionalCapabilityDefinitionList.get().stream().findAny()
                    .map(capabilityDefinition -> capabilityDefinition.getProperties().get(0));
            }
        }

        if (optionalComponentInstanceProperty.isEmpty()) {
            return Either.right(componentsUtils.getResponseFormat(ActionStatus.SELECTED_PROPERTY_NOT_PRESENT,
                uiConstraint.getServicePropertyName()));
        }
        if (comparableConstraintsOperators.contains(uiConstraint.getConstraintOperator()) && !comparableTypes.contains(
            optionalComponentInstanceProperty.get().getType())) {
            return Either.right(componentsUtils.getResponseFormat(ActionStatus.UNSUPPORTED_OPERATOR_PROVIDED,
                uiConstraint.getServicePropertyName(), uiConstraint.getConstraintOperator()));
        }

        return isValidValueCheck(optionalComponentInstanceProperty.get().getType(), String.valueOf(uiConstraint.getValue()),
            uiConstraint.getServicePropertyName());
    }

    private Either<Boolean, ResponseFormat> isValidValueCheck(String type, String value, String propertyName) {

        ToscaPropertyType toscaPropertyType = ToscaPropertyType.isValidType(type);
        if (Objects.isNull(toscaPropertyType)) {
            return Either.right(
                componentsUtils.getResponseFormat(ActionStatus.UNSUPPORTED_PROPERTY_TYPE, type, propertyName));
        }
        if (toscaPropertyType.getValidator().isValid(value, null)) {
            return Either.left(Boolean.TRUE);
        }
        return Either.right(
            componentsUtils.getResponseFormat(ActionStatus.UNSUPPORTED_VALUE_PROVIDED, type, propertyName, value));
    }

    public Either<Boolean, ResponseFormat> validateComponentFilter(final Component component,
                                                                   final List<String> uiConstraints,
                                                                   final NodeFilterConstraintAction action) {
        try {
            if (NodeFilterConstraintAction.ADD == action || NodeFilterConstraintAction.UPDATE == action) {
                for (final String uiConstraint : uiConstraints) {
                    final UIConstraint constraint = new ConstraintConvertor().convert(uiConstraint);
                    if (ConstraintConvertor.PROPERTY_CONSTRAINT.equals(constraint.getSourceType())) {
                        final Either<Boolean, ResponseFormat> booleanResponseFormatEither =
                                validateComponentPropertyConstraint(component, constraint);
                        if (booleanResponseFormatEither.isRight()) {
                            return booleanResponseFormatEither;
                        }
                    } else if (ConstraintConvertor.STATIC_CONSTRAINT.equals(constraint.getSourceType())) {
                        final Either<Boolean, ResponseFormat> booleanResponseFormatEither =
                                validateComponentStaticValueAndOperator(component, constraint);
                        if (booleanResponseFormatEither.isRight()) {
                            return booleanResponseFormatEither;
                        }
                    }
                }
            }
        } catch (final Exception e) {
            LOGGER.debug("Provided constraint" + uiConstraints, e);
            return Either.right(componentsUtils.getResponseFormat(ActionStatus.CONSTRAINT_FORMAT_INCORRECT));
        }

        return Either.left(true);
    }

    private Either<Boolean, ResponseFormat> validateComponentPropertyConstraint(final Component component,
                                                                                final UIConstraint uiConstraint) {
        String source = SOURCE;

        final List<PropertyDefinition> propertyDefinitions = component.getProperties();
        List<? extends PropertyDefinition> sourcePropertyDefinition =
                component.getName().equals(uiConstraint.getSourceName()) &&
                        propertyDefinitions != null ? propertyDefinitions : Collections.emptyList();

        if (!CollectionUtils.isEmpty(sourcePropertyDefinition)) {
            final Optional<? extends PropertyDefinition> sourceSelectedProperty = sourcePropertyDefinition.stream()
                    .filter(property -> uiConstraint
                            .getValue()
                            .equals(property.getName()))
                    .findFirst();

            final Optional<? extends PropertyDefinition> targetComponentProperty =
                    component.getProperties().stream()
                            .filter(property -> uiConstraint.getServicePropertyName().equals(property.getName()))
                            .findFirst();

            source = !targetComponentProperty.isPresent() ? "Target" : SOURCE;
            if (sourceSelectedProperty.isPresent() && targetComponentProperty.isPresent()) {
                return validatePropertyData(uiConstraint, sourceSelectedProperty, targetComponentProperty);
            }
        }

        final String missingProperty =
                source.equals(SOURCE) ? uiConstraint.getValue().toString() : uiConstraint.getServicePropertyName();

        return Either.right(
                componentsUtils.getResponseFormat(ActionStatus.MAPPED_PROPERTY_NOT_FOUND, source, missingProperty));
    }

    private Either<Boolean, ResponseFormat> validateComponentStaticValueAndOperator(
            final Component component, final UIConstraint uiConstraint) {
        if (!(Objects.nonNull(uiConstraint) && uiConstraint.getValue() instanceof String)) {
            return Either.left(false);
        }
        final Optional<PropertyDefinition> componentProperty =
                component.getProperties().stream()
                        .filter(property -> uiConstraint.getServicePropertyName().equals(property.getName()))
                        .findFirst();

        if (componentProperty.isEmpty()) {
            return Either.right(componentsUtils.getResponseFormat(ActionStatus.SELECTED_PROPERTY_NOT_PRESENT,
                    uiConstraint.getServicePropertyName()));
        }
        if (comparableConstraintsOperators.contains(uiConstraint.getConstraintOperator()) && !comparableTypes.contains(
                componentProperty.get().getType())) {
            return Either.right(componentsUtils.getResponseFormat(ActionStatus.UNSUPPORTED_OPERATOR_PROVIDED,
                    uiConstraint.getServicePropertyName(), uiConstraint.getConstraintOperator()));
        }

        return isValidValueCheck(componentProperty.get().getType(), String.valueOf(uiConstraint.getValue()),
                uiConstraint.getServicePropertyName());
    }

}
