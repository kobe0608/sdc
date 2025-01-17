/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Nordix Foundation
 *  ================================================================================
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  SPDX-License-Identifier: Apache-2.0
 *  ============LICENSE_END=========================================================
 */

package org.onap.sdc.frontend.ci.tests.pages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.onap.sdc.frontend.ci.tests.pages.component.workspace.ToscaArtifactsPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Handles the Resource Page Left Side Menu UI actions
 */
public class ResourceLeftSideMenu extends AbstractPageObject {

    private WebElement wrappingElement;

    public ResourceLeftSideMenu(final WebDriver webDriver) {
        super(webDriver);
        timeoutInSeconds = 5;
    }

    @Override
    public void isLoaded() {
        wrappingElement = getWrappingElement();
    }

    /**
     * Gets the enclosing element of the menu.
     *
     * @return the enclosing element
     */
    public WebElement getWrappingElement() {
        return getWait()
            .until(ExpectedConditions.visibilityOfElementLocated(By.className(XpathSelector.MAIN_DIV.getId())));
    }

    /**
     * Clicks on the properties assignment menu item.
     *
     * @return the next page object
     */
    public ResourcePropertiesAssignmentPage clickOnPropertiesAssignmentMenuItem() {
        wrappingElement.findElement(By.xpath(XpathSelector.PROPERTIES_ASSIGNMENT_MENU.getXpath())).click();
        return new ResourcePropertiesAssignmentPage(webDriver);
    }

    /**
     * Clicks on the TOSCA artifacts menu item.
     *
     * @return the next page object
     */
    public ToscaArtifactsPage clickOnToscaArtifactsMenuItem() {
        wrappingElement.findElement(By.xpath(XpathSelector.TOSCA_ARTIFACTS_MENU.getXpath())).click();
        return new ToscaArtifactsPage(webDriver);
    }

    /**
     * Clicks on the TOSCA artifacts menu item.
     *
     * @return the next page object
     */
    public ServiceCreatePage clickOnGeneralMenuItem() {
        wrappingElement.findElement(By.xpath(XpathSelector.GENERAL_MENU.getXpath())).click();
        return new ServiceCreatePage(webDriver);
    }

    /**
     * Enum that contains identifiers and xpath expressions to elements related to the enclosing page object.
     */
    @AllArgsConstructor
    private enum XpathSelector {
        MAIN_DIV("w-sdc-left-sidebar", "//div[@class='%s']"),
        PROPERTIES_ASSIGNMENT_MENU("Properties AssignmentLeftSideMenu", "//*[@data-tests-id='%s']"),
        GENERAL_MENU("GeneralLeftSideMenu", "//*[@data-tests-id='%s']"),
        TOSCA_ARTIFACTS_MENU("TOSCA ArtifactsLeftSideMenu", "//*[@data-tests-id='%s']");

        @Getter
        private final String id;
        private final String xpathFormat;

        public String getXpath() {
            return String.format(xpathFormat, id);
        }
    }

}
