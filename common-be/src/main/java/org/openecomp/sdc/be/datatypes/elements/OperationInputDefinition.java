/*
 * Copyright © 2016-2018 European Support Limited
 *
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
 */
package org.openecomp.sdc.be.datatypes.elements;


import com.fasterxml.jackson.annotation.JsonCreator;

import org.openecomp.sdc.be.datatypes.enums.JsonPresentationFields;


public class OperationInputDefinition extends InputDataDefinition {
    private String source;
    private String sourceProperty;
    private String toscaDefaultValue;

    @JsonCreator
    public OperationInputDefinition() {
        super();
    }

    public OperationInputDefinition(String name, InputDataDefinition inputDefinition, String source, String sourceProperty) {
        super(inputDefinition);
        setName(name);
        setSource(source);
        setSourceProperty(sourceProperty);
    }

    public OperationInputDefinition(String name, String property, Boolean mandatory, String type) {
        super();
        setName(name);
        setInputId(property);
        setRequired(mandatory);
        setType(type);
    }

    public String getLabel() {
        return (String) getToscaPresentationValue(JsonPresentationFields.ARTIFACT_LABEL);
    }

    public void setLabel(String name) {
        setToscaPresentationValue(JsonPresentationFields.ARTIFACT_LABEL, name);
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSourceProperty() {
        return sourceProperty;
    }

    public void setSourceProperty(String sourceProperty) {
        this.sourceProperty = sourceProperty;
    }

    public String getToscaDefaultValue() {
        return toscaDefaultValue;
    }

    public void setToscaDefaultValue(String toscaDefaultValue) {
        this.toscaDefaultValue = toscaDefaultValue;
    }
}
