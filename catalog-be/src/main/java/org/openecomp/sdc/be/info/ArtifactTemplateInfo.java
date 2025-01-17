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

package org.openecomp.sdc.be.info;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import fj.data.Either;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.openecomp.sdc.be.config.ArtifactConfiguration;
import org.openecomp.sdc.be.config.BeEcompErrorManager;
import org.openecomp.sdc.be.config.ComponentType;
import org.openecomp.sdc.be.config.ConfigurationManager;
import org.openecomp.sdc.be.dao.api.ActionStatus;
import org.openecomp.sdc.be.datatypes.enums.NodeTypeEnum;
import org.openecomp.sdc.be.impl.ComponentsUtils;
import org.openecomp.sdc.be.model.ArtifactType;
import org.openecomp.sdc.common.api.ArtifactGroupTypeEnum;
import org.openecomp.sdc.common.api.ArtifactTypeEnum;
import org.openecomp.sdc.common.log.wrappers.Logger;
import org.openecomp.sdc.exception.ResponseFormat;

@Getter
@Setter
@NoArgsConstructor
public class ArtifactTemplateInfo {
    private static final Logger log = Logger.getLogger(ArtifactTemplateInfo.class);
    private static final Gson gson = new Gson();
    public static final String CSAR_ARTIFACT = "artifacts";

    private static final String ARTIFACT_TEMPLATE_TYPE = "type";
    private static final String FILE_NAME = "fileName";
    private static final String ARTIFACT_TEMPLATE_ENV = "env";
    private static final String IS_BASE = "isBase";
    private static final String CSAR_HEAT = "HEAT";
    private static final String CSAR_HELM = "HELM";
    private static final String CSAR_NETWORK = "network";
    private static final String CSAR_VOLUME = "volume";
    private static final String CSAR_NESTED = "nested";
    private static final String DESC = "description";

    private String type;
    private String fileName;
    private String env;
    private boolean base;
    private String groupName;
    private String description;

    private List<ArtifactTemplateInfo> relatedArtifactsInfo;

    public ArtifactTemplateInfo(String type, String fileName, String env, List<ArtifactTemplateInfo> relatedArtifactsInfo) {
        this.type = type;
        this.fileName = fileName;
        this.env = env;
        this.relatedArtifactsInfo = relatedArtifactsInfo;
    }

    public static Either<ArtifactTemplateInfo, ResponseFormat> createArtifactTemplateInfoFromJson(ComponentsUtils componentsUtils, String type, Map<String, Object> o, List<ArtifactTemplateInfo> createdArtifactTemplateInfoList,
            ArtifactTemplateInfo parentArtifact) {
        String content = gson.toJson(o);
        JsonObject jsonElement = new JsonObject();
        ArtifactTemplateInfo resourceInfo = new ArtifactTemplateInfo();

        jsonElement = gson.fromJson(content, jsonElement.getClass());

        Map<String, Object> artifactTemplateMap = ComponentsUtils.parseJsonToObject(jsonElement.toString(), HashMap.class);
        if (artifactTemplateMap.containsKey(ARTIFACT_TEMPLATE_TYPE)) {
            resourceInfo.setType((String) artifactTemplateMap.get(ARTIFACT_TEMPLATE_TYPE));
        }
        if (artifactTemplateMap.containsKey(FILE_NAME)) {
            resourceInfo.setFileName((String) artifactTemplateMap.get(FILE_NAME));
        }
        if (artifactTemplateMap.containsKey(IS_BASE)) {
            resourceInfo.setBase((Boolean) artifactTemplateMap.get(IS_BASE));
        }
        if (artifactTemplateMap.containsKey(ARTIFACT_TEMPLATE_ENV)) {
            Object envObj = artifactTemplateMap.get(ARTIFACT_TEMPLATE_ENV);
            String envStr = "";
            if (envObj instanceof String) {
                envStr = (String) envObj;
            }
            else if (envObj instanceof Map) {
                Map envMap = (Map) envObj;
                if (envMap.containsKey(FILE_NAME)) {
                    envStr = (String) envMap.get(FILE_NAME);
                }
            }
            resourceInfo.setEnv(envStr);
        }
        if (artifactTemplateMap.containsKey(DESC)) {
            resourceInfo.setDescription((String) artifactTemplateMap.get(DESC));
        } else {
            resourceInfo.setDescription((String) artifactTemplateMap.get(FILE_NAME));
        }

        boolean artifactTypeExist = false;
        String correctType;
        if (type.equalsIgnoreCase(CSAR_NESTED)) {
            correctType = ArtifactTypeEnum.HEAT_NESTED.getType();
        }
        else if (type.equalsIgnoreCase(CSAR_VOLUME)) {
            correctType = ArtifactTypeEnum.HEAT_VOL.getType();
        }
        else if (type.equalsIgnoreCase(CSAR_NETWORK)) {
            correctType = ArtifactTypeEnum.HEAT_NET.getType();
        }
        else if (type.equalsIgnoreCase(CSAR_ARTIFACT)){
            if( parentArtifact != null) {
                correctType = ArtifactTypeEnum.HEAT_ARTIFACT.getType();
            }
            else {
                correctType = resourceInfo.type;
            }
        }
        else if (type.equalsIgnoreCase(CSAR_HEAT)) {
            correctType = ArtifactTypeEnum.HEAT.getType();
        }
        else if (type.equalsIgnoreCase(CSAR_HELM)) {
            correctType = ArtifactTypeEnum.HELM.getType();
        }
        else {
            correctType = ArtifactTypeEnum.OTHER.getType();
        }
        Either<List<ArtifactType>, ActionStatus> allArtifactTypes = getDeploymentArtifactTypes(NodeTypeEnum.Resource);

        if (allArtifactTypes.isRight()) {
            BeEcompErrorManager.getInstance().logBeInvalidConfigurationError("Artifact Upload / Update", "artifactTypes", allArtifactTypes.right().value().name());
            return Either.right(componentsUtils.getResponseFormat(ActionStatus.FAILED_RETRIVE_ARTIFACTS_TYPES));
        }

        for (ArtifactType artType : allArtifactTypes.left().value()) {

            if (artType.getName().contains(correctType)) {
                resourceInfo.type = artType.getName();
                artifactTypeExist = true;
                break;
            }
        }

        if (!artifactTypeExist) {
            BeEcompErrorManager.getInstance().logBeInvalidTypeError("Artifact", "-Not supported artifact type ", correctType);
            log.debug("Not supported artifact type = {}" , correctType);
            return Either.right(componentsUtils.getResponseFormat(ActionStatus.ARTIFACT_TYPE_NOT_SUPPORTED, correctType));
        }

        Either<Boolean, ResponseFormat> eitherNeedToCreate = validateEnv(componentsUtils, createdArtifactTemplateInfoList, resourceInfo);
        if (eitherNeedToCreate.isRight()) {
            return Either.right(eitherNeedToCreate.right().value());
        }
        eitherNeedToCreate = validateParentType(componentsUtils, resourceInfo, parentArtifact);
        if (eitherNeedToCreate.isRight()) {
            return Either.right(eitherNeedToCreate.right().value());
        }
        eitherNeedToCreate = validateIsAlreadyExist(componentsUtils, resourceInfo, createdArtifactTemplateInfoList, parentArtifact);
        if (eitherNeedToCreate.isRight()) {
            return Either.right(eitherNeedToCreate.right().value());
        }
        Set<String> keys = o.keySet();
        for (String key : keys) {
            if (o.get(key) instanceof List) {
                List<Map<String, Object>> artifList = (List<Map<String, Object>>) o.get(key);
                for (Map<String, Object> relatedArtifactsMap : artifList) {
                    Either<ArtifactTemplateInfo, ResponseFormat> relatedArtifact = ArtifactTemplateInfo.createArtifactTemplateInfoFromJson(componentsUtils, key, relatedArtifactsMap, createdArtifactTemplateInfoList, resourceInfo);
                    if (relatedArtifact.isRight()) {
                        return relatedArtifact;
                    }
                    if (resourceInfo.relatedArtifactsInfo == null) {
                        resourceInfo.relatedArtifactsInfo = new ArrayList<>();
                    }
                    resourceInfo.relatedArtifactsInfo.add(relatedArtifact.left().value());
                }
            }
        }
        return Either.left(resourceInfo);
    }

    private static Either<Boolean, ResponseFormat> validateIsAlreadyExist(ComponentsUtils componentsUtils, ArtifactTemplateInfo resourceInfo, List<ArtifactTemplateInfo> createdArtifactTemplateInfoList, ArtifactTemplateInfo parentArtifact) {

        if (parentArtifact == null) {
            if (createdArtifactTemplateInfoList == null || createdArtifactTemplateInfoList.isEmpty())
                return Either.left(true);
            for (ArtifactTemplateInfo createdArtifact : createdArtifactTemplateInfoList) {
                if (createdArtifact.getType().equalsIgnoreCase(resourceInfo.getType()) && createdArtifact.getFileName().equalsIgnoreCase(resourceInfo.getFileName())) {
                    return Either.right(componentsUtils.getResponseFormat(ActionStatus.ARTIFACT_ALRADY_EXIST_IN_MASTER_IN_CSAR, resourceInfo.getFileName(), createdArtifact.type));
                }
            }
            return Either.left(true);
        } else {
            List<ArtifactTemplateInfo> relatedArtifacts = parentArtifact.getRelatedArtifactsInfo();
            if (relatedArtifacts == null || relatedArtifacts.isEmpty())
                return Either.left(true);
            for (ArtifactTemplateInfo relatedArtifact : relatedArtifacts) {
                if (relatedArtifact.getType().equalsIgnoreCase(resourceInfo.getType()) && relatedArtifact.getFileName().equalsIgnoreCase(resourceInfo.getFileName())) {
                    return Either.right(componentsUtils.getResponseFormat(ActionStatus.ARTIFACT_ALRADY_EXIST_IN_MASTER_IN_CSAR, resourceInfo.getFileName(), parentArtifact.getFileName()));
                }
            }
            return Either.left(true);
        }
    }

    private static Either<Boolean, ResponseFormat> validateParentType(ComponentsUtils componentsUtils, ArtifactTemplateInfo resourceInfo, ArtifactTemplateInfo parentArtifact) {

        if (parentArtifact == null) {
            return Either.left(true);
        }
        if (resourceInfo.getType().equalsIgnoreCase(ArtifactTypeEnum.HEAT_ARTIFACT.getType())) {
            return Either.left(true);
        }
        if (resourceInfo.getType().equalsIgnoreCase(ArtifactTypeEnum.HEAT.getType())) {
            return Either.right(componentsUtils.getResponseFormat(ActionStatus.ARTIFACT_NOT_VALID_IN_MASTER, resourceInfo.getFileName(), resourceInfo.getType(), parentArtifact.getFileName(), parentArtifact.getType()));
        }
        if ((resourceInfo.getType().equalsIgnoreCase(ArtifactTypeEnum.HEAT_NET.getType()) || resourceInfo.getType().equalsIgnoreCase(ArtifactTypeEnum.HEAT_VOL.getType()))
                && !parentArtifact.getType().equalsIgnoreCase(ArtifactTypeEnum.HEAT.getType())) {
            return Either.right(componentsUtils.getResponseFormat(ActionStatus.ARTIFACT_NOT_VALID_IN_MASTER, resourceInfo.getFileName(), resourceInfo.getType(), parentArtifact.getFileName(), parentArtifact.getType()));
        }
        if (parentArtifact.getType().equalsIgnoreCase(ArtifactTypeEnum.HEAT_NESTED.getType())) {
            if (resourceInfo.getType().equalsIgnoreCase(ArtifactTypeEnum.HEAT_ARTIFACT.getType()) || resourceInfo.getType().equalsIgnoreCase(ArtifactTypeEnum.HEAT_NESTED.getType())) {
                return Either.left(true);
            }
            return Either.right(componentsUtils.getResponseFormat(ActionStatus.ARTIFACT_NOT_VALID_IN_MASTER, resourceInfo.getFileName(), resourceInfo.getType(), parentArtifact.getFileName(), parentArtifact.getType()));
        }
        if (parentArtifact.getType().equalsIgnoreCase(ArtifactTypeEnum.HEAT.getType()) && resourceInfo.getType().equalsIgnoreCase(ArtifactTypeEnum.HEAT.getType())) {
            return Either.right(componentsUtils.getResponseFormat(ActionStatus.ARTIFACT_NOT_VALID_IN_MASTER, resourceInfo.getFileName(), resourceInfo.getType(), parentArtifact.getFileName(), parentArtifact.getType()));
        }

        if (parentArtifact.getType().equalsIgnoreCase(ArtifactTypeEnum.HEAT.getType())) {
            return Either.left(true);
        }
        return Either.left(true);
    }

    private static Either<Boolean, ResponseFormat> validateEnv(ComponentsUtils componentsUtils, List<ArtifactTemplateInfo> createdArtifactTemplateInfoList, ArtifactTemplateInfo resourceInfo) {

        if (createdArtifactTemplateInfoList == null || createdArtifactTemplateInfoList.isEmpty())
            return Either.left(true);
        if (resourceInfo.getType().equalsIgnoreCase(ArtifactTypeEnum.HEAT_NESTED.getType()) || resourceInfo.getType().equalsIgnoreCase(ArtifactTypeEnum.HEAT_ARTIFACT.getType()))
            return Either.left(true);
        for (ArtifactTemplateInfo createdArtifactTemplateInfo : createdArtifactTemplateInfoList) {
            // check if artifact with this name already parsed. If parsed check
            // env name. it must be the same.
            if (resourceInfo.getFileName().equalsIgnoreCase(createdArtifactTemplateInfo.getFileName())) {
                if ((resourceInfo.getEnv() == null || resourceInfo.getEnv().isEmpty()) && (createdArtifactTemplateInfo.getEnv() != null && !createdArtifactTemplateInfo.getEnv().isEmpty())) {
                    log.debug("Artifact  file with name {} type{}  already parsed but with env {}", resourceInfo.getFileName(), resourceInfo.getType(), createdArtifactTemplateInfo.getEnv());
                    return Either.right(componentsUtils.getResponseFormat(ActionStatus.ARTIFACT_NOT_VALID_ENV, resourceInfo.getFileName(), resourceInfo.getType(), resourceInfo.getEnv(), createdArtifactTemplateInfo.getEnv()));
                }
                if (resourceInfo.getEnv() != null && !resourceInfo.getEnv().isEmpty() && createdArtifactTemplateInfo.getEnv() != null && !createdArtifactTemplateInfo.getEnv().isEmpty()
                        && !createdArtifactTemplateInfo.getEnv().equalsIgnoreCase(resourceInfo.getEnv())) {
                    log.debug("Artifact  file with name {} type{} env {} already parsed but with env {}", resourceInfo.getFileName(), resourceInfo.getType(), resourceInfo.getEnv(), createdArtifactTemplateInfo.getEnv());
                    return Either.right(componentsUtils.getResponseFormat(ActionStatus.ARTIFACT_NOT_VALID_ENV, resourceInfo.getFileName(), resourceInfo.getType(), resourceInfo.getEnv(), createdArtifactTemplateInfo.getEnv()));
                }
                if ((resourceInfo.getEnv() != null && !resourceInfo.getEnv().isEmpty()) && (createdArtifactTemplateInfo.getEnv() == null || createdArtifactTemplateInfo.getEnv().isEmpty())) {
                    log.debug("Artifact  file with name {} type{} env {} already parsed but with env {}", resourceInfo.getFileName(), resourceInfo.getType(), resourceInfo.getEnv(), createdArtifactTemplateInfo.getEnv());
                    return Either.right(componentsUtils.getResponseFormat(ActionStatus.ARTIFACT_NOT_VALID_ENV, resourceInfo.getFileName(), resourceInfo.getType(), resourceInfo.getEnv(), createdArtifactTemplateInfo.getEnv()));
                }
            }
            List<ArtifactTemplateInfo> relatedArtifacts = createdArtifactTemplateInfo.getRelatedArtifactsInfo();
            Either<Boolean, ResponseFormat> status = validateEnv(componentsUtils, relatedArtifacts, resourceInfo);
            if (status.isRight())
                return status;
        }
        return Either.left(true);
    }

    private static Either<List<ArtifactType>, ActionStatus> getDeploymentArtifactTypes(final NodeTypeEnum parentType) {

        final List<ArtifactConfiguration> deploymentArtifacts;
        final List<ArtifactConfiguration> artifactConfigurationList =
            ConfigurationManager.getConfigurationManager().getConfiguration().getArtifacts();

        if (parentType == NodeTypeEnum.Service) {
            deploymentArtifacts = artifactConfigurationList.stream()
                .filter(artifactConfiguration ->
                    artifactConfiguration.hasSupport(ArtifactGroupTypeEnum.DEPLOYMENT)
                        && artifactConfiguration.hasSupport(ComponentType.SERVICE))
                .collect(Collectors.toList());
        } else {
            deploymentArtifacts = artifactConfigurationList.stream()
                .filter(artifactConfiguration ->
                    artifactConfiguration.hasSupport(ArtifactGroupTypeEnum.DEPLOYMENT)
                        && artifactConfiguration.hasSupport(ComponentType.RESOURCE))
                .collect(Collectors.toList());
        }
        final List<ArtifactType> artifactTypes = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(deploymentArtifacts)) {
            deploymentArtifacts.forEach(artifactConfiguration -> {
                final ArtifactType artifactType = new ArtifactType();
                artifactType.setName(artifactConfiguration.getType());
                artifactTypes.add(artifactType);
            });
            return Either.left(artifactTypes);
        }

        return Either.right(ActionStatus.GENERAL_ERROR);
    }

    public static int compareByGroupName(ArtifactTemplateInfo art1, ArtifactTemplateInfo art2) {
        return art1.isBase() ? (art2.isBase() ? 0 : -1) : (art2.isBase() ? 1 : 0);
    }
}
