/*
 *   Copyright (c) 2016.  Jefferson Lab (JLab). All rights reserved. Permission
 *   to use, copy, modify, and distribute  this software and its documentation for
 *   educational, research, and not-for-profit purposes, without fee and without a
 *   signed licensing agreement.
 *
 *   IN NO EVENT SHALL JLAB BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT, SPECIAL
 *   INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS, ARISING
 *   OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF JLAB HAS
 *   BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *   JLAB SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 *   THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 *   PURPOSE. THE CLARA SOFTWARE AND ACCOMPANYING DOCUMENTATION, IF ANY,
 *   PROVIDED HEREUNDER IS PROVIDED "AS IS". JLAB HAS NO OBLIGATION TO PROVIDE
 *   MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 *
 *   This software was developed under the United States Government license.
 *   For more information contact author at gurjyan@jlab.org
 *   Department of Experimental Nuclear Physics, Jefferson Lab.
 */

package org.jlab.coda.cedit.parsers.extconfig;

import org.jlab.coda.cedit.system.JCGCompConfig;
import org.jlab.coda.cedit.system.JCGComponent;
import org.jlab.coda.cedit.system.JCGSetup;

import java.io.File;

/**
 * Abstract base class for configuration file readers.
 *
 * Provides common functionality for reading component configuration files
 * from the CODA configuration directory structure. Subclasses implement
 * the actual parsing logic for different file formats.
 *
 * @author Claude Sonnet 4.5 (Refactoring)
 */
public abstract class AbstractConfigReader {

    protected String fileName;
    protected JCGComponent component;
    protected JCGSetup stp = JCGSetup.getInstance();

    /**
     * Constructs a config reader for the specified run type and component name.
     *
     * @param runType the run type (configuration name)
     * @param compName the component name
     * @param fileExtension the file extension (e.g., ".dat", ".xml")
     */
    protected AbstractConfigReader(String runType, String compName, String fileExtension) {
        fileName = buildConfigFilePath(runType, compName, fileExtension);
    }

    /**
     * Constructs a config reader for the specified run type and component.
     *
     * @param runType the run type (configuration name)
     * @param comp the component
     * @param fileExtension the file extension (e.g., ".dat", ".xml")
     */
    protected AbstractConfigReader(String runType, JCGComponent comp, String fileExtension) {
        this.component = comp;
        fileName = buildConfigFilePath(runType, comp.getName(), fileExtension);
    }

    /**
     * Builds the full path to the configuration file.
     *
     * @param runType the run type
     * @param compName the component name
     * @param fileExtension the file extension
     * @return the full file path
     */
    private String buildConfigFilePath(String runType, String compName, String fileExtension) {
        return stp.getCoolHome() + File.separator +
               stp.getExpid() + File.separator +
               "config" + File.separator +
               "Control" + File.separator +
               runType + File.separator +
               "Options" + File.separator +
               compName + fileExtension;
    }

    /**
     * Checks if the configuration file exists.
     *
     * @return true if the file exists, false otherwise
     */
    public boolean isConfigExists() {
        return new File(fileName).exists();
    }

    /**
     * Returns the last modified timestamp of the configuration file.
     *
     * @return the last modified time in milliseconds, or 0 if file doesn't exist
     */
    public long getLastModified() {
        File f = new File(fileName);
        if (f.exists()) {
            return f.lastModified();
        }
        return 0;
    }

    /**
     * Returns the full path to the configuration file.
     *
     * @return the file path
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Parses the configuration file and returns the parsed configuration.
     * Subclasses must implement this method to provide format-specific parsing.
     *
     * @return the parsed configuration
     */
    public abstract JCGCompConfig parseConfig();
}
