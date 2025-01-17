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

package org.onap.sdc.backend.ci.tests.config;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Config {

    private static final String SDC_DEFAULT_CONFIG_FILE = "src/test/resources/ci/conf/sdc-conf.yaml";
    private String downloadAutomationFolder;
    private boolean systemUnderDebug;
    private boolean rerun;
    private String reportDBhost;
    private int reportDBport;

    private String browser;
    private String catalogBeHost;
    private String esHost;
    private String esPort;
    private String neoHost;
    private String neoPort;
    private String disributionClientHost;
    private String disributionClientPort;
    private boolean isDistributionClientRunning;


    private String errorConfigurationFile;
    private String resourceConfigDir;
    private String importResourceConfigDir;
    private String importResourceTestsConfigDir;

    private String catalogFeHost;
    private String catalogFePort;
    private String catalogBePort;
    private String catalogBeTlsPort;

    private String onboardingBeHost;
    private String onboardingBePort;

    private String neoDBusername;
    private String neoDBpassword;

    private List<String> packages;
    private List<String> bugs;
    private List<String> resourcesNotToDelete;
    private List<String> resourceCategoriesNotToDelete;
    private List<String> serviceCategoriesNotToDelete;
    private boolean stopOnClassFailure = false;

    private String outputFolder;
    private String reportName;
    private String url;
    private String remoteTestingMachineIP;
    private String remoteTestingMachinePort;
    private boolean remoteTesting;

    private String cassandraHost;
    private String cassandraAuditKeySpace;
    private String cassandraArtifactKeySpace;
    private boolean cassandraAuthenticate;
    private String cassandraUsername;
    private String cassandraPassword;
    private boolean cassandraSsl;
    private String cassandraTruststorePath;
    private String cassandraTruststorePassword;
    private boolean captureTraffic;
    private boolean useBrowserMobProxy;
    private String sdcHttpMethod;
    private String localDataCenter;
    private boolean uiSimulator;

    public String getLocalDataCenter() {
        return localDataCenter;
    }

    public void setLocalDataCenter(String localDataCenter) {
        this.localDataCenter = localDataCenter;
    }

    private static Config configIt = null;

    private static final Yaml yaml = new Yaml();

    private Config() {
        super();
    }

    public String getOnboardingBePort() {
        return onboardingBePort;
    }

    public void setOnboardingBePort(String onboardingBePort) {
        this.onboardingBePort = onboardingBePort;
    }

    public String getOnboardingBeHost() {
        return onboardingBeHost;
    }

    public void setOnboardingBeHost(String onboardingBeHost) {
        this.onboardingBeHost = onboardingBeHost;
    }

    public static class TestPackages {

        List<String> packages;
        List<String> bugs;

        public List<String> getPackages() {
            return packages;
        }

        public void setPackages(List<String> packages) {
            this.packages = packages;
        }

        public List<String> getBugs() {
            return bugs;
        }

        public void setBugs(List<String> bugs) {
            this.bugs = bugs;
        }

        @Override
        public String toString() {
            return "TestPackages [packages=" + packages + ", bugs=" + bugs + "]";
        }

    }

    public synchronized static Config instance() {
        if (configIt == null) {
            try {
                configIt = init();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return configIt;
    }

    private static Config init() throws IOException {

        String configFile = System.getProperty("config.resource");
        if (configFile == null) {
            configFile = SDC_DEFAULT_CONFIG_FILE;
        }

        final File file = new File(configFile);
        if (!file.exists()) {
            throw new RuntimeException("The config file " + configFile + " cannot be found.");
        }

        final Config config;
        InputStream in = null;
        try {

            in = Files.newInputStream(Paths.get(configFile));

            config = yaml.loadAs(in, Config.class);

            setPackagesAndBugs(configFile, config);

        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return config;
    }

    public boolean isUiSimulator() {
        return uiSimulator;
    }

    public void setUiSimulator(boolean uiSimulator) {
        this.uiSimulator = uiSimulator;
    }

    private static void setPackagesAndBugs(String path, Config config) throws IOException {

        int separator = Math.max(path.lastIndexOf("\\"), path.lastIndexOf("/"));
        String dirPath = path.substring(0, separator + 1);
        String packagesFile = dirPath + File.separator + "sdc-packages.yaml";
        File file = new File(packagesFile);
        if (false == file.exists()) {
            throw new RuntimeException("The config file " + packagesFile + " cannot be found.");
        }

        TestPackages testPackages = null;
        InputStream in = null;
        try {

            in = Files.newInputStream(Paths.get(packagesFile));

            testPackages = yaml.loadAs(in, TestPackages.class);

            List<String> bugs = testPackages.getBugs();
            List<String> packages = testPackages.getPackages();

            config.setBugs(bugs);
            config.setPackages(packages);

        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    String configurationFile;

    public boolean getSystemUnderDebug() {
        return systemUnderDebug;
    }

    public void setSystemUnderDebug(boolean systemUnderDebug) {
        this.systemUnderDebug = systemUnderDebug;
    }

    public String getSdcHttpMethod() {
        return sdcHttpMethod;
    }

    public void setSdcHttpMethod(String sdcHttpMethod) {
        this.sdcHttpMethod = sdcHttpMethod;
    }

    public boolean getRerun() {
        return rerun;
    }

    public void setRerun(boolean rerun) {
        this.rerun = rerun;
    }

    public String getReportDBhost() {
        return reportDBhost;
    }

    public void setReportDBhost(String reportDBhost) {
        this.reportDBhost = reportDBhost;
    }

    public int getReportDBport() {
        return reportDBport;
    }

    public void setReportDBport(int reportDBport) {
        this.reportDBport = reportDBport;
    }

    public String getBrowser() {
        return browser;
    }

    public boolean getUseBrowserMobProxy() {
        return useBrowserMobProxy;
    }

    public void setUseBrowserMobProxy(boolean useBrowserMobProxy) {
        this.useBrowserMobProxy = useBrowserMobProxy;
    }


    public boolean getCaptureTraffic() {
        return captureTraffic;
    }

    public void setCaptureTraffic(boolean captureTraffic) {
        this.captureTraffic = captureTraffic;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getConfigurationFile() {
        return configurationFile;
    }

    public void setConfigurationFile(String configurationFile) {
        this.configurationFile = configurationFile;
    }

    public boolean getIsDistributionClientRunning() {
        return isDistributionClientRunning;
    }

    public void setIsDistributionClientRunning(boolean isDistributionClientRunning) {
        this.isDistributionClientRunning = isDistributionClientRunning;
    }

    public String getCatalogBePort() {
        return catalogBePort;
    }

    public String getDisributionClientHost() {
        return disributionClientHost;
    }

    public void setDisributionClientHost(String disributionClientHost) {
        this.disributionClientHost = disributionClientHost;
    }

    public String getDisributionClientPort() {
        return disributionClientPort;
    }

    public void setDisributionClientPort(String disributionClientPort) {
        this.disributionClientPort = disributionClientPort;
    }

    public void setCatalogBePort(String catalogBePort) {
        this.catalogBePort = catalogBePort;
    }

    public String getCatalogFeHost() {
        return catalogFeHost;
    }

    public void setCatalogFeHost(String catalogFeHost) {
        this.catalogFeHost = catalogFeHost;
    }

    public String getCatalogFePort() {
        return catalogFePort;
    }

    public void setCatalogFePort(String catalogFePort) {
        this.catalogFePort = catalogFePort;
    }

    public String getCatalogBeHost() {
        return catalogBeHost;
    }

    public void setCatalogBeHost(String catalogBeHost) {
        this.catalogBeHost = catalogBeHost;
    }

    public String getEsHost() {
        return esHost;
    }

    public void setEsHost(String esHost) {
        this.esHost = esHost;
    }

    public String getEsPort() {
        return esPort;
    }

    public void setEsPort(String esPort) {
        this.esPort = esPort;
    }

    public String getResourceConfigDir() {
        return resourceConfigDir;
    }

    public void setResourceConfigDir(String resourceConfigDir) {
        this.resourceConfigDir = resourceConfigDir;
    }

    public String getOutputFolder() {
        return outputFolder;
    }

    public void setOutputFolder(String outputFolder) {
        this.outputFolder = outputFolder;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getNeoPort() {
        return neoPort;
    }

    public void setNeoPort(String neoPort) {
        this.neoPort = neoPort;
    }

    public String getNeoHost() {
        return neoHost;
    }

    public void setNeoHost(String neoHost) {
        this.neoHost = neoHost;
    }

    public String getNeoDBpassword() {
        return neoDBpassword;
    }

    public String getNeoDBusername() {
        return neoDBusername;
    }

    public void setNeoDBusername(String neoDBusername) {
        this.neoDBusername = neoDBusername;
    }

    public void setNeoDBpassword(String neoDBpassword) {
        this.neoDBpassword = neoDBpassword;
    }

    public List<String> getPackages() {
        return packages;
    }

    public void setPackages(List<String> packages) {
        this.packages = packages;
    }

    public List<String> getBugs() {
        return bugs;
    }

    public void setBugs(List<String> bugs) {
        this.bugs = bugs;
    }

    public boolean isStopOnClassFailure() {
        return stopOnClassFailure;
    }

    public void setStopOnClassFailure(boolean stopOnClassFailure) {
        this.stopOnClassFailure = stopOnClassFailure;
    }

    public String getImportResourceConfigDir() {
        return importResourceConfigDir;
    }

    public void setImportResourceConfigDir(String importResourceConfigDir) {
        this.importResourceConfigDir = importResourceConfigDir;
    }

    public String getImportResourceTestsConfigDir() {
        return importResourceTestsConfigDir;
    }

    public void setImportResourceTestsConfigDir(String importResourceTestsConfigDir) {
        this.importResourceTestsConfigDir = importResourceTestsConfigDir;
    }

    public String getErrorConfigurationFile() {
        return errorConfigurationFile;
    }

    public void setErrorConfigurationFile(String errorConfigurationFile) {
        this.errorConfigurationFile = errorConfigurationFile;
    }

    public String getCatalogBeTlsPort() {
        return catalogBeTlsPort;
    }

    public void setCatalogBeTlsPort(String catalogBeTlsPort) {
        this.catalogBeTlsPort = catalogBeTlsPort;
    }

    public List<String> getResourcesNotToDelete() {
        return resourcesNotToDelete;
    }

    public void setResourcesNotToDelete(List<String> resourcesNotToDelete) {
        this.resourcesNotToDelete = resourcesNotToDelete;
    }

    public List<String> getResourceCategoriesNotToDelete() {
        return resourceCategoriesNotToDelete;
    }

    public void setResourceCategoriesNotToDelete(List<String> resourceCategoriesNotToDelete) {
        this.resourceCategoriesNotToDelete = resourceCategoriesNotToDelete;
    }

    public List<String> getServiceCategoriesNotToDelete() {
        return serviceCategoriesNotToDelete;
    }

    public void setServiceCategoriesNotToDelete(List<String> serviceCategoriesNotToDelete) {
        this.serviceCategoriesNotToDelete = serviceCategoriesNotToDelete;
    }

    public String getCassandraHost() {
        return cassandraHost;
    }

    public void setCassandraHost(String cassandraHost) {
        this.cassandraHost = cassandraHost;
    }

    public String getCassandraAuditKeySpace() {
        return cassandraAuditKeySpace;
    }

    public void setCassandraAuditKeySpace(String cassandraAuditKeySpace) {
        this.cassandraAuditKeySpace = cassandraAuditKeySpace;
    }

    public String getCassandraArtifactKeySpace() {
        return cassandraArtifactKeySpace;
    }

    public void setCassandraArtifactKeySpace(String cassandraArtifactKeySpace) {
        this.cassandraArtifactKeySpace = cassandraArtifactKeySpace;
    }

    public String getDownloadAutomationFolder() {
        return downloadAutomationFolder;
    }

    public void setDownloadAutomationFolder(String downloadAutomationFolder) {
        this.downloadAutomationFolder = downloadAutomationFolder;
    }

    @Override
    public String toString() {
        return "Config [systemUnderDebug=" + systemUnderDebug + ", rerun=" + rerun + ", reportDBhost=" + reportDBhost
                + ", reportDBport=" + reportDBport + ", browser=" + browser + ", catalogBeHost=" + catalogBeHost
                + ", esHost=" + esHost + ", esPort=" + esPort + ", neoHost=" + neoHost + ", neoPort=" + neoPort
                + ", disributionClientHost=" + disributionClientHost + ", disributionClientPort="
                + disributionClientPort + ", isDistributionClientRunning=" + isDistributionClientRunning
                + ", errorConfigurationFile=" + errorConfigurationFile + ", resourceConfigDir=" + resourceConfigDir +
                ", importResourceConfigDir=" + importResourceConfigDir + ", importResourceTestsConfigDir="
                + importResourceTestsConfigDir + ", catalogFeHost="
                + catalogFeHost + ", catalogFePort=" + catalogFePort + ", catalogBePort=" + catalogBePort
                + ", catalogBeTlsPort=" + catalogBeTlsPort + ", neoDBusername=" + neoDBusername + ", neoDBpassword="
                + neoDBpassword + ", packages=" + packages + ", bugs="
                + bugs + ", resourcesNotToDelete=" + resourcesNotToDelete + ", resourceCategoriesNotToDelete="
                + resourceCategoriesNotToDelete + ", serviceCategoriesNotToDelete=" + serviceCategoriesNotToDelete
                + ", stopOnClassFailure=" + stopOnClassFailure + ", outputFolder=" + outputFolder + ", reportName="
                + reportName + ", url=" + url + ", remoteTestingMachineIP=" + remoteTestingMachineIP
                + ", remoteTestingMachinePort=" + remoteTestingMachinePort + ", remoteTesting=" + remoteTesting
                + ", cassandraHost=" + cassandraHost + ", cassandraAuditKeySpace=" + cassandraAuditKeySpace
                + ", cassandraArtifactKeySpace=" + cassandraArtifactKeySpace + ", cassandraAuthenticate="
                + cassandraAuthenticate + ", cassandraUsername=" + cassandraUsername + ", cassandraPassword="
                + cassandraPassword + ", cassandraSsl=" + cassandraSsl + ", cassandraTruststorePath="
                + cassandraTruststorePath + ", cassandraTruststorePassword=" + cassandraTruststorePassword
                + ", captureTraffic=" + captureTraffic
                + ", useBrowserMobProxy=" + useBrowserMobProxy + ", configurationFile=" + configurationFile
                + ", downloadAutomationFolder=" + downloadAutomationFolder + "]";
    }

    public boolean isRemoteTesting() {
        return remoteTesting;
    }

    public void setRemoteTesting(boolean remoteTesting) {
        this.remoteTesting = remoteTesting;
    }

    public String getUrl() {
        try {
            return url;
        } catch (Exception e) {
            return null;
        }
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRemoteTestingMachineIP() {
        return remoteTestingMachineIP;
    }

    public void setRemoteTestingMachineIP(String remoteTestingMachineIP) {
        this.remoteTestingMachineIP = remoteTestingMachineIP;
    }

    public String getRemoteTestingMachinePort() {
        return remoteTestingMachinePort;
    }

    public void setRemoteTestingMachinePort(String remoteTestingMachinePort) {
        this.remoteTestingMachinePort = remoteTestingMachinePort;
    }

    public boolean getCassandraAuthenticate() {
        return cassandraAuthenticate;
    }

    public void setCassandraAuthenticate(boolean cassandraAuthenticate) {
        this.cassandraAuthenticate = cassandraAuthenticate;
    }

    public String getCassandraUsername() {
        return cassandraUsername;
    }

    public void setCassandraUsername(String cassandraUsername) {
        this.cassandraUsername = cassandraUsername;
    }

    public String getCassandraPassword() {
        return cassandraPassword;
    }

    public void setCassandraPassword(String cassandraPassword) {
        this.cassandraPassword = cassandraPassword;
    }

    public boolean getCassandraSsl() {
        return cassandraSsl;
    }

    public void setCassandraSsl(boolean cassandraSsl) {
        this.cassandraSsl = cassandraSsl;
    }

    public String getCassandraTruststorePath() {
        return cassandraTruststorePath;
    }

    public void setCassandraTruststorePath(String cassandraTruststorePath) {
        this.cassandraTruststorePath = cassandraTruststorePath;
    }

    public String getCassandraTruststorePassword() {
        return cassandraTruststorePassword;
    }

    public void setCassandraTruststorePassword(String cassandraTruststorePassword) {
        this.cassandraTruststorePassword = cassandraTruststorePassword;
    }

}
