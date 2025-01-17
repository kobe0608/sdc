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

package org.openecomp.sdc.be.components.impl;

import fj.data.Either;
import org.openecomp.sdc.be.dao.api.ActionStatus;
import org.openecomp.sdc.be.dao.neo4j.GraphPropertiesDictionary;
import org.openecomp.sdc.be.datamodel.api.CategoryTypeEnum;
import org.openecomp.sdc.be.datamodel.utils.NodeTypeConvertUtils;
import org.openecomp.sdc.be.datatypes.category.MetadataKeyDataDefinition;
import org.openecomp.sdc.be.datatypes.enums.ComponentTypeEnum;
import org.openecomp.sdc.be.datatypes.enums.MetadataKeyEnum;
import org.openecomp.sdc.be.datatypes.enums.NodeTypeEnum;
import org.openecomp.sdc.be.impl.ComponentsUtils;
import org.openecomp.sdc.be.model.category.CategoryDefinition;
import org.openecomp.sdc.be.model.category.GroupingDefinition;
import org.openecomp.sdc.be.model.category.SubCategoryDefinition;
import org.openecomp.sdc.be.model.operations.api.IElementOperation;
import org.openecomp.sdc.be.model.operations.impl.UniqueIdBuilder;
import org.openecomp.sdc.common.log.wrappers.Logger;
import org.openecomp.sdc.common.util.ValidationUtils;
import org.openecomp.sdc.exception.ResponseFormat;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@Component("categoriesImportManager")
public class CategoriesImportManager {

    @javax.annotation.Resource
    private IElementOperation elementOperation;

    @javax.annotation.Resource
    private ComponentsUtils componentsUtils;

    private static final Logger log = Logger.getLogger(CategoriesImportManager.class.getName());

    public Either<Map<String, List<CategoryDefinition>>, ResponseFormat> createCategories(String categoriesTypesYml) {

        Map<String, List<CategoryDefinition>> allCategories = createCategoriesFromYml(categoriesTypesYml);
        return createCategoriesByDao(allCategories);
    }

    private Either<Map<String, List<CategoryDefinition>>, ResponseFormat> createCategoriesByDao(Map<String, List<CategoryDefinition>> allCategories) {
        Map<String, List<CategoryDefinition>> result = new HashMap<>();
        log.debug("createCategoriesByDao: starting to create Categories.");
        for (Map.Entry<String, List<CategoryDefinition>> entry : allCategories.entrySet()) {
            ComponentTypeEnum componentType = ComponentTypeEnum.findByParamName(entry.getKey());
            NodeTypeEnum nodeTypeCategory = NodeTypeConvertUtils.getCategoryNodeTypeByComponentParam(componentType, CategoryTypeEnum.CATEGORY);
            NodeTypeEnum nodeTypeSubCategory = NodeTypeConvertUtils.getCategoryNodeTypeByComponentParam(componentType, CategoryTypeEnum.SUBCATEGORY);
            NodeTypeEnum nodeTypeGroup = NodeTypeConvertUtils.getCategoryNodeTypeByComponentParam(componentType, CategoryTypeEnum.GROUPING);
            log.debug("createCategoriesByDao: creating componentType:{}  nodeTypeCategory:{} nodeTypeSubCategory:{} nodeTypeGroup:{}", componentType, nodeTypeCategory, nodeTypeSubCategory, nodeTypeGroup);
            List<CategoryDefinition> newCategoriesvalue = new ArrayList<>();
            for (CategoryDefinition category : entry.getValue()) {

                Either<CategoryDefinition, ResponseFormat> createdCategoryRes = createCategorieDeo(entry, category, nodeTypeCategory);
                if (createdCategoryRes.isRight()) {
                    return Either.right(createdCategoryRes.right().value());
                }

                CategoryDefinition newcategory = createdCategoryRes.left().value();
                log.debug("createCategoriesByDao: create category was successful {}", newcategory);
                List<SubCategoryDefinition> newsubcategories = new ArrayList<>();
                List<SubCategoryDefinition> subcategories = category.getSubcategories();
                if (subcategories != null) {
                    boolean createdNewSubCategory = isCreatedNewSubCategory(entry, nodeTypeSubCategory, nodeTypeGroup, category, newcategory, newsubcategories, subcategories);
                    if (!createdNewSubCategory) {
                        return Either.right(createdCategoryRes.right().value());
                    }
                    newcategory.setSubcategories(newsubcategories);
                }
                newCategoriesvalue.add(newcategory);
            }
            result.put(entry.getKey(), newCategoriesvalue);
        }
        return Either.left(result);
    }

    private boolean isCreatedNewSubCategory(Entry<String, List<CategoryDefinition>> entry, NodeTypeEnum nodeTypeSubCategory, NodeTypeEnum nodeTypeGroup, CategoryDefinition category, CategoryDefinition newcategory, List<SubCategoryDefinition> newsubcategories, List<SubCategoryDefinition> subcategories) {
        for (SubCategoryDefinition subcategory : subcategories) {
            Either<SubCategoryDefinition, ResponseFormat> createdSubCategory = createSubCategorieDeo(entry, newcategory, subcategory, nodeTypeSubCategory);
            if (createdSubCategory.isRight()) {
                return false;
            }
            SubCategoryDefinition newsubcategory = createdSubCategory.left().value();
            List<GroupingDefinition> groupings = subcategory.getGroupings();
            if (groupings != null) {
                List<GroupingDefinition> newgroupings = new ArrayList<>();
                for (GroupingDefinition grouping : groupings) {
                    Either<GroupingDefinition, ResponseFormat> createdGrouping = createGroupingDeo(entry, grouping, subcategory, category, nodeTypeGroup);
                    if (createdGrouping.isRight()) {
                        return false;
                    }
                    newgroupings.add(createdGrouping.left().value());
                }
                newsubcategory.setGroupings(newgroupings);
            }
            newsubcategories.add(newsubcategory);
        }
        return true;
    }

    private Either<GroupingDefinition, ResponseFormat> createGroupingDeo(Map.Entry<String, List<CategoryDefinition>> entry, GroupingDefinition grouping, SubCategoryDefinition subcategory, CategoryDefinition category, NodeTypeEnum nodeTypeGroup) {

        log.debug("createGroupingDeo: creating grouping  {}", grouping);
        Either<GroupingDefinition, ActionStatus> createdGrouping = elementOperation.createGrouping(subcategory.getUniqueId(), grouping, nodeTypeGroup);
        if (createdGrouping.isRight()) {
            if (ActionStatus.COMPONENT_GROUPING_EXISTS_FOR_SUB_CATEGORY == createdGrouping.right().value()) {
                log.debug(" create grouping for {}  group {} already exists ", entry.getKey(), grouping.getName());
                String groupingId = UniqueIdBuilder.buildGroupingUid(grouping.getUniqueId(), grouping.getNormalizedName());
                createdGrouping = elementOperation.getGroupingUniqueForType(nodeTypeGroup, groupingId);
                if (createdGrouping.isRight()) {
                    log.debug("failed to get grouping that exists groupingId: {} type: {}", groupingId, nodeTypeGroup);
                    return Either.right(componentsUtils.getResponseFormat(createdGrouping.right().value()));
                }
            }
            log.debug("Failed to create groupingcategory for {}  category {} subcategory {} grouping {} error {}", entry.getKey(),
                    category.getName(), subcategory.getName(), grouping != null ? grouping.getName() : null,
                            createdGrouping != null && createdGrouping.right() != null ? createdGrouping.right().value() : null);

            return Either.right(componentsUtils.getResponseFormat(createdGrouping.right().value()));
        } else {
            log.debug("createGroupingDeo: create Grouping was successful {}", createdGrouping.left().value());
        }
        return Either.left(createdGrouping.left().value());

    }

    private Either<SubCategoryDefinition, ResponseFormat> createSubCategorieDeo(Map.Entry<String, List<CategoryDefinition>> entry, CategoryDefinition newcategory, SubCategoryDefinition subcategory, NodeTypeEnum nodeTypeSubCategory) {
        log.debug("createSubCategorieDeo: creating subcategory  {}", subcategory);
        Either<SubCategoryDefinition, ActionStatus> createdSubCategory = elementOperation.createSubCategory(newcategory.getUniqueId(), subcategory, nodeTypeSubCategory);
        if (createdSubCategory.isRight() && ActionStatus.COMPONENT_SUB_CATEGORY_EXISTS_FOR_CATEGORY == createdSubCategory.right().value()) {
            log.debug(" create subcategory for {}  category {} subcategory {} already exists, updating", entry.getKey(), newcategory.getName(), subcategory.getName());
            String subCategoryId = UniqueIdBuilder.buildSubCategoryUid(newcategory.getUniqueId(), subcategory.getNormalizedName());
            createdSubCategory = elementOperation.updateSubCategory(subCategoryId, subcategory, nodeTypeSubCategory);
        }
        if (createdSubCategory.isRight()) {
            log.debug("Failed to create subcategory for {}  category: {} subcategory: {} error {}", entry.getKey(), newcategory.getName(), subcategory.getName(), createdSubCategory.right().value());
            return Either.right(componentsUtils.getResponseFormat(createdSubCategory.right().value()));
        } else {
            log.debug("createSubCategorieDeo: create subcategory was successful {}", createdSubCategory.left().value());
        }
        return Either.left(createdSubCategory.left().value());
    }

    private Either<CategoryDefinition, ResponseFormat> createCategorieDeo(Map.Entry<String, List<CategoryDefinition>> entry, CategoryDefinition category, NodeTypeEnum nodeTypeCategory) {
        log.debug("createCategorieDeo: creating category {}", category);
        Either<CategoryDefinition, ActionStatus> createdCategory = elementOperation.createCategory(category, nodeTypeCategory);
        if (createdCategory.isRight() && ActionStatus.COMPONENT_CATEGORY_ALREADY_EXISTS == createdCategory.right().value()) {
            log.debug("createCategorieDeo: already exists, updating {}", category);
            createdCategory = elementOperation.updateCategory(category, nodeTypeCategory);
        }
        
        if (createdCategory.isRight()) {
            log.debug("Failed to create category for {}  {} error {}", entry.getKey(), category.getName(), createdCategory.right().value());
            return Either.right(componentsUtils.getResponseFormat(createdCategory.right().value()));
        } else {
            log.debug("createCategorieDeo: create category was successful {}", createdCategory.left().value());
        }
        return Either.left(createdCategory.left().value());
    }

    private Map<String, List<CategoryDefinition>> createCategoriesFromYml(String categoriesTypesYml) {
        Map<String, Object> toscaJson = (Map<String, Object>) new Yaml().load(categoriesTypesYml);
        Map<String, List<CategoryDefinition>> allCategories = new HashMap<>();

        Iterator<Entry<String, Object>> categoryEntryItr = toscaJson.entrySet().iterator();
        while (categoryEntryItr.hasNext()) {
            Entry<String, Object> categoryTypeEntry = categoryEntryItr.next();
            String categoryType = categoryTypeEntry.getKey();
            List<CategoryDefinition> categoriesPerType = null;
            Map<String, Object> categoryPerType = null;
            switch (categoryType) {
            case ComponentTypeEnum.SERVICE_PARAM_NAME:
                categoryPerType = (Map<String, Object>) categoryTypeEntry.getValue();
                categoriesPerType = createServiceCategories(categoryPerType);
                break;
            case ComponentTypeEnum.RESOURCE_PARAM_NAME:
                categoryPerType = (Map<String, Object>) categoryTypeEntry.getValue();
                categoriesPerType = createResourceCategories(categoryPerType);
                break;
            case ComponentTypeEnum.PRODUCT_PARAM_NAME:
                // TODO
                break;
            default:
                log.debug("Not supported category type - {}", categoryType);
                break;
            }
            if (categoriesPerType != null) {
                allCategories.put(categoryType, categoriesPerType);
            }
        }
        return allCategories;
    }

    private List<CategoryDefinition> createServiceCategories(Map<String, Object> categories) {
        List<CategoryDefinition> categoriesDef = new ArrayList<>();
        String catName = null;
        List<String> icons = null;
        for (Entry<String, Object> entry : categories.entrySet()) {
            CategoryDefinition catDef = new CategoryDefinition();
            Map<String, Object> category = (Map<String, Object>) entry.getValue();
            catName = (String) category.get("name");
            catDef.setName(catName);
            icons = (List<String>) category.get("icons");
            catDef.setIcons(icons);
            String normalizedName = ValidationUtils.normalizeCategoryName4Uniqueness(catName);
            catDef.setNormalizedName(normalizedName);
            final Object useServiceSubstitutionForNestedServicesProperty = category.get("useServiceSubstitutionForNestedServices");
            final boolean useServiceSubstitutionForNestedServices = useServiceSubstitutionForNestedServicesProperty == null ? 
                    false : (Boolean) useServiceSubstitutionForNestedServicesProperty;
            catDef.setUseServiceSubstitutionForNestedServices(useServiceSubstitutionForNestedServices);
            catDef.setMetadataKeys(getMetadataKeys(category));
            categoriesDef.add(catDef);
        }

        return categoriesDef;
    }
    
    private List<MetadataKeyDataDefinition> getMetadataKeys(Map<String, Object> parentObject) {
        Map<String, Object> metadataKeys = (Map<String, Object>) parentObject.getOrDefault(MetadataKeyEnum.METADATA_KEYS.getName(), Collections.EMPTY_MAP);
        List<MetadataKeyDataDefinition> metadataKeyDefs = new ArrayList<>();
        for (Entry<String, Object> metadataKey : metadataKeys.entrySet()) {
            Map<String, Object> metadataKeyInfo = (Map<String, Object>) metadataKey.getValue();
            MetadataKeyDataDefinition metadataKeyDef = new MetadataKeyDataDefinition();
            String metadataKeyName = (String) metadataKeyInfo.get(MetadataKeyEnum.NAME.getName());
            metadataKeyDef.setName(metadataKeyName);
            final Object mandatoryProperty = metadataKeyInfo.get(MetadataKeyEnum.MANDATORY.getName());
            final boolean mandatory = mandatoryProperty == null ? false : (Boolean) mandatoryProperty;
            metadataKeyDef.setMandatory(mandatory);
            List<String> validValues = (List<String>) metadataKeyInfo.get(MetadataKeyEnum.VALID_VALUES.getName());
            metadataKeyDef.setValidValues(validValues);
            metadataKeyDef.setDefaultValue((String) metadataKeyInfo.get(MetadataKeyEnum.DEFAULT.getName()));
            metadataKeyDefs.add(metadataKeyDef);
        }
        return metadataKeyDefs;
    }

    private List<CategoryDefinition> createResourceCategories(Map<String, Object> categoryPerType) {
        List<CategoryDefinition> categroiesDef = new ArrayList<>();
        for (Map.Entry<String, Object> entry : categoryPerType.entrySet()) {
            Map<String, Object> category = (Map<String, Object>) entry.getValue();
            CategoryDefinition catDef = new CategoryDefinition();
            String catName = (String) category.get("name");
            catDef.setName(catName);
            String normalizedName = ValidationUtils.normalizeCategoryName4Uniqueness(catName);
            catDef.setNormalizedName(normalizedName);
            Map<String, Object> subcategories = (Map<String, Object>) category.get("subcategories");
            List<SubCategoryDefinition> subcateDef = new ArrayList<>();
            for (Entry<String, Object> subcategory : subcategories.entrySet()) {
                Map<String, Object> subcategoryInfo = (Map<String, Object>) subcategory.getValue();
                SubCategoryDefinition subDef = new SubCategoryDefinition();
                String subcategoryName = (String) subcategoryInfo.get("name");
                subDef.setName(subcategoryName);
                List<String> subcategoryIcons = (List<String>) subcategoryInfo.get("icons");
                subDef.setIcons(subcategoryIcons);
                normalizedName = ValidationUtils.normalizeCategoryName4Uniqueness(subcategoryName);
                subDef.setNormalizedName(normalizedName);
                subDef.setMetadataKeys(getMetadataKeys(subcategoryInfo));
                subcateDef.add(subDef);
            }

            catDef.setSubcategories(subcateDef);
            categroiesDef.add(catDef);
        }
        return categroiesDef;
    }
}
