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

import javax.swing.*;

/**
 * Represents the result of a validation operation.
 *
 * Contains information about whether validation passed, an error message if it failed,
 * and the severity level for display purposes.
 *
 * @author Claude Sonnet 4.5 (Refactoring)
 */
public class ValidationResult {

    private final boolean valid;
    private final String errorMessage;
    private final String errorTitle;
    private final int messageType;

    private ValidationResult(boolean valid, String errorMessage, String errorTitle, int messageType) {
        this.valid = valid;
        this.errorMessage = errorMessage;
        this.errorTitle = errorTitle;
        this.messageType = messageType;
    }

    /**
     * Creates a successful validation result.
     */
    public static ValidationResult success() {
        return new ValidationResult(true, null, null, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Creates a failed validation result with an error message.
     */
    public static ValidationResult error(String message, String title) {
        return new ValidationResult(false, message, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Creates a failed validation result with a warning message.
     */
    public static ValidationResult warning(String message, String title) {
        return new ValidationResult(false, message, title, JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Returns whether the validation passed.
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Returns the error message, or null if validation passed.
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Returns the error title for display in dialogs.
     */
    public String getErrorTitle() {
        return errorTitle;
    }

    /**
     * Returns the JOptionPane message type constant (ERROR_MESSAGE, WARNING_MESSAGE, etc.).
     */
    public int getMessageType() {
        return messageType;
    }

    /**
     * Shows the validation result in a JOptionPane if validation failed.
     * Does nothing if validation passed.
     *
     * @param parentComponent the parent component for the dialog
     */
    public void showDialogIfInvalid(java.awt.Component parentComponent) {
        if (!valid) {
            JOptionPane.showMessageDialog(parentComponent, errorMessage, errorTitle, messageType);
        }
    }
}
