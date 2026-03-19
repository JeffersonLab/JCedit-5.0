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

package org.jlab.coda.cedit.util;

import org.jlab.coda.cedit.cooldesktop.DrawingCanvas;
import org.jlab.coda.cedit.system.JCGComponent;

import java.util.regex.Pattern;

/**
 * Utility class for common form validation operations.
 *
 * Consolidates validation logic that was previously duplicated across multiple form classes.
 * Provides consistent validation behavior and error messages throughout the application.
 *
 * @author Claude Sonnet 4.5 (Refactoring)
 */
public class FormValidator {

    // Regex patterns for validation
    private static final Pattern IP_ADDRESS_PATTERN =
            Pattern.compile("^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");

    private static final Pattern VALID_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+$");

    /**
     * Validates a component name for creation on the canvas.
     *
     * Checks:
     * - Name is not null or empty
     * - Name doesn't contain underscore (control character)
     *
     * @param name the component name to validate
     * @param canvas the drawing canvas (parameter kept for API compatibility but not used)
     * @return validation result
     */
    public static ValidationResult validateComponentName(String name, DrawingCanvas canvas) {
        if (name == null || name.trim().isEmpty()) {
            return ValidationResult.error("Component name cannot be empty.", "Validation Error");
        }

        if (name.contains("_")) {
            return ValidationResult.warning(
                    "\"_\" is a control character and cannot be used in the name.\n" +
                    "Please change the name of the component.",
                    "Invalid Name");
        }

        // Duplicate component check removed - users can now create components with same names

        return ValidationResult.success();
    }

    /**
     * Validates that a required field is not empty.
     *
     * @param value the field value
     * @param fieldName the name of the field for error messages
     * @return validation result
     */
    public static ValidationResult validateRequired(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            return ValidationResult.error(
                    fieldName + " is required and cannot be empty.",
                    "Required Field");
        }
        return ValidationResult.success();
    }

    /**
     * Validates an IP address format.
     *
     * @param ipAddress the IP address to validate
     * @return validation result
     */
    public static ValidationResult validateIpAddress(String ipAddress) {
        if (ipAddress == null || ipAddress.trim().isEmpty()) {
            return ValidationResult.error("IP address cannot be empty.", "Validation Error");
        }

        if (!IP_ADDRESS_PATTERN.matcher(ipAddress.trim()).matches()) {
            return ValidationResult.error(
                    "Invalid IP address format: " + ipAddress + "\n" +
                    "Expected format: xxx.xxx.xxx.xxx",
                    "Invalid IP Address");
        }

        return ValidationResult.success();
    }

    /**
     * Validates that a numeric value is within a specified range.
     *
     * @param value the value to validate
     * @param min the minimum allowed value (inclusive)
     * @param max the maximum allowed value (inclusive)
     * @param fieldName the name of the field for error messages
     * @return validation result
     */
    public static ValidationResult validateRange(int value, int min, int max, String fieldName) {
        if (value < min || value > max) {
            return ValidationResult.error(
                    fieldName + " must be between " + min + " and " + max + ".\n" +
                    "Provided value: " + value,
                    "Value Out of Range");
        }
        return ValidationResult.success();
    }

    /**
     * Validates that a port number is in valid range (1-65535).
     *
     * @param port the port number
     * @param portName the name of the port for error messages (e.g., "TCP Port")
     * @return validation result
     */
    public static ValidationResult validatePort(int port, String portName) {
        return validateRange(port, 1, 65535, portName);
    }

    /**
     * Validates that a string matches a given pattern.
     *
     * @param value the value to validate
     * @param pattern the regex pattern
     * @param fieldName the name of the field for error messages
     * @param patternDescription description of what the pattern represents
     * @return validation result
     */
    public static ValidationResult validatePattern(String value, Pattern pattern,
                                                    String fieldName, String patternDescription) {
        if (value == null || !pattern.matcher(value).matches()) {
            return ValidationResult.error(
                    fieldName + " does not match required format.\n" +
                    "Expected: " + patternDescription,
                    "Invalid Format");
        }
        return ValidationResult.success();
    }

    /**
     * Checks if a component with the given name is already defined on the canvas.
     *
     * @param name the component name
     * @param canvas the drawing canvas
     * @return true if component exists, false otherwise
     */
    private static boolean isComponentDefinedOnCanvas(String name, DrawingCanvas canvas) {
        if (canvas == null || canvas.getGCMPs() == null) {
            return false;
        }

        for (JCGComponent gc : canvas.getGCMPs().values()) {
            if (gc.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Combines multiple validation results. Returns the first failure encountered,
     * or success if all validations pass.
     *
     * @param results variable number of validation results
     * @return combined validation result
     */
    public static ValidationResult combine(ValidationResult... results) {
        for (ValidationResult result : results) {
            if (!result.isValid()) {
                return result;
            }
        }
        return ValidationResult.success();
    }
}
