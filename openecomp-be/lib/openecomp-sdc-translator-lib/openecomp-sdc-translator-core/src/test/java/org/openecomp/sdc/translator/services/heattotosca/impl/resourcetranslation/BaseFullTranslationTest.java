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

package org.openecomp.sdc.translator.services.heattotosca.impl.resourcetranslation;

import static org.junit.Assert.assertEquals;

import org.apache.commons.collections4.MapUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.openecomp.core.translator.api.HeatToToscaTranslator;
import org.openecomp.core.translator.datatypes.TranslatorOutput;
import org.openecomp.core.translator.factory.HeatToToscaTranslatorFactory;
import org.openecomp.core.utilities.file.FileUtils;
import org.openecomp.core.validation.util.MessageContainerUtil;
import org.openecomp.sdc.common.errors.CoreException;
import org.openecomp.sdc.common.errors.ErrorCategory;
import org.openecomp.sdc.common.errors.ErrorCode;
import org.openecomp.sdc.common.togglz.ToggleableFeature;
import org.openecomp.sdc.datatypes.error.ErrorLevel;
import org.openecomp.sdc.datatypes.error.ErrorMessage;
import org.openecomp.sdc.tosca.services.ToscaFileOutputService;
import org.openecomp.sdc.tosca.services.impl.ToscaFileOutputServiceCsarImpl;
import org.openecomp.sdc.translator.TestUtils;
import org.togglz.testing.TestFeatureManager;
import org.togglz.testing.TestFeatureManagerProvider;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class BaseFullTranslationTest {

  public static final String IN_POSTFIX = "/in";
  public static final String OUT_POSTFIX = "/out";

  protected static TestFeatureManager manager;

  @BeforeClass
  public static void enableToggleableFeatures(){
    manager = new TestFeatureManager(ToggleableFeature.class);
    manager.enableAll();
    TestFeatureManagerProvider.setFeatureManager(manager);
  }


  @AfterClass
  public static void disableToggleableFeatures() {
    manager.disableAll();
    manager = null;
    TestFeatureManagerProvider.setFeatureManager(null);
  }

  protected void testTranslationWithInit(String path) throws IOException {
    File translatedZipFile = initTranslatorAndTranslate(path);
    testTranslation(path, translatedZipFile);
  }

  protected File initTranslatorAndTranslate(String path) throws IOException {
    HeatToToscaTranslator heatToToscaTranslator = HeatToToscaTranslatorFactory.getInstance().createInterface();
    return translateZipFile(path, heatToToscaTranslator);
  }

  protected void testTranslation(String basePath, File translatedZipFile) throws IOException {

    URL url = BaseFullTranslationTest.class.getResource(basePath + OUT_POSTFIX);
    Set<String> expectedResultFileNameSet = new HashSet<>();
    Map<String, byte[]> expectedResultMap = new HashMap<>();

    String path = url.getPath();
    File pathFile = new File(path);
    File[] files = pathFile.listFiles();
    Assert.assertNotNull("manifest files is empty", files);
    for (File expectedFile : files) {
      expectedResultFileNameSet.add(expectedFile.getName());
      try (FileInputStream input = new FileInputStream(expectedFile)) {
        expectedResultMap.put(expectedFile.getName(), FileUtils.toByteArray(input));
      }
    }

    try (FileInputStream fis = new FileInputStream(translatedZipFile);
         ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis))) {
      ZipEntry entry;
      String name;
      String expected;
      String actual;

      while ((entry = zis.getNextEntry()) != null) {

        name = entry.getName()
            .substring(entry.getName().lastIndexOf(File.separator) + 1, entry.getName().length());
        if (expectedResultFileNameSet.contains(name)) {
          expected = new String(expectedResultMap.get(name)).trim().replace("\r", "");
          actual = new String(FileUtils.toByteArray(zis)).trim().replace("\r", "");
          assertEquals("difference in file: " + name, expected, actual);

          expectedResultFileNameSet.remove(name);
        }
      }
      if (expectedResultFileNameSet.isEmpty()) {
        expectedResultFileNameSet.forEach(System.out::println);
      }
    }
    assertEquals(0, expectedResultFileNameSet.size());
    translatedZipFile.delete();
  }

  private File translateZipFile(String basePath, HeatToToscaTranslator heatToToscaTranslator) throws IOException {
    URL inputFilesUrl = this.getClass().getResource(basePath + IN_POSTFIX);
    String path = inputFilesUrl.getPath();
    TestUtils.addFilesToTranslator(heatToToscaTranslator, path);
    TranslatorOutput translatorOutput = heatToToscaTranslator.translate();
    Assert.assertNotNull(translatorOutput);
    if (MapUtils.isNotEmpty(translatorOutput.getErrorMessages()) && MapUtils.isNotEmpty(
        MessageContainerUtil
            .getMessageByLevel(ErrorLevel.ERROR, translatorOutput.getErrorMessages()))) {
      throw new CoreException((new ErrorCode.ErrorCodeBuilder()).withMessage(
          "Error in validation " + getErrorAsString(translatorOutput.getErrorMessages()))
          .withId("Validation Error").withCategory(ErrorCategory.APPLICATION).build());
    }

    File file = File.createTempFile("VSP", "zip");

    try (FileOutputStream fos = new FileOutputStream(file)) {
      ToscaFileOutputService toscaFileOutputService = new ToscaFileOutputServiceCsarImpl();
      fos.write(
          toscaFileOutputService.createOutputFile(translatorOutput.getToscaServiceModel(), null));
    }

    return file;
  }

  private String getErrorAsString(Map<String, List<ErrorMessage>> errorMessages) {
    StringBuilder sb = new StringBuilder();
    errorMessages.entrySet().forEach(
        entry -> sb.append("File:").append(entry.getKey()).append(System.lineSeparator())
            .append(getErrorList(entry.getValue())));

    return sb.toString();
  }

  private String getErrorList(List<ErrorMessage> errors) {
    StringBuilder sb = new StringBuilder();
    errors.forEach(
        error -> sb.append(error.getMessage()).append("[").append(error.getLevel()).append("]")
            .append(System.lineSeparator()));
    return sb.toString();
  }

}
