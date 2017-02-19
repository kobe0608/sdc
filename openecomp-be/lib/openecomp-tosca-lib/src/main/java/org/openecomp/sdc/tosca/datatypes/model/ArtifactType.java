/*-
 * ============LICENSE_START=======================================================
 * SDC
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
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

package org.openecomp.sdc.tosca.datatypes.model;

import java.util.List;
import java.util.Map;

public class ArtifactType {

  private String derived_from;
  private String version;
  private String description;
  private String mime_type;
  private List<String> file_ext;
  private Map<String, PropertyDefinition> properties;


  public String getDerived_from() {
    return derived_from;
  }

  public void setDerived_from(String derivedFrom) {
    this.derived_from = derivedFrom;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getMime_type() {
    return mime_type;
  }

  public void setMime_type(String mimeType) {
    this.mime_type = mimeType;
  }

  public List<String> getFile_ext() {
    return file_ext;
  }

  public void setFile_ext(List<String> fileExt) {
    this.file_ext = fileExt;
  }

  public Map<String, PropertyDefinition> getProperties() {
    return properties;
  }

  public void setProperties(Map<String, PropertyDefinition> properties) {
    this.properties = properties;
  }
}
