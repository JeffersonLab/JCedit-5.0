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

package org.jlab.coda.cedit.forms.simple;

import javax.swing.*;
import java.awt.*;

/**
 * Base class for all JCedit forms, providing common utility methods
 * and standardized behavior.
 *
 * This base class is designed to work alongside JFormDesigner's auto-generated code.
 * It provides utility methods and common functionality without interfering with
 * the component initialization that JFormDesigner manages.
 *
 * Subclasses should:
 * - Call super() in their constructors
 * - Override abstract methods as needed
 * - Keep JFormDesigner-generated code in their initComponents() methods
 *
 * @author Claude Sonnet 4.5 (Refactoring)
 */
public abstract class BaseForm extends JFrame {

    /**
     * Constructs a new BaseForm with default settings.
     * Sets up window properties common to all forms.
     */
    protected BaseForm() {
        // Default window properties
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    /**
     * Constructs a new BaseForm with a specific title.
     *
     * @param title the title for the form window
     */
    protected BaseForm(String title) {
        super(title);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    /**
     * Centers the form on the screen.
     * Should be called after pack() or setSize().
     */
    protected void centerOnScreen() {
        setLocationRelativeTo(null);
    }

    /**
     * Centers the form relative to a parent component.
     *
     * @param parent the parent component to center relative to
     */
    protected void centerOnParent(Component parent) {
        setLocationRelativeTo(parent);
    }

    /**
     * Disposes the form and performs cleanup.
     * Subclasses can override to add custom cleanup logic.
     */
    protected void closeForm() {
        dispose();
    }

    /**
     * Makes the form visible and brings it to front.
     */
    protected void showForm() {
        setVisible(true);
        toFront();
    }

    /**
     * Sets whether the form is modal (blocks input to other windows).
     * Note: This only works if the form is a JDialog, not JFrame.
     * Subclasses using JDialog should override this.
     *
     * @param modal true to make the form modal
     */
    protected void setModal(boolean modal) {
        // Default implementation for JFrame does nothing
        // Subclasses using JDialog should override
    }

    /**
     * Validates all form inputs before submission.
     * Subclasses should override this to implement custom validation logic.
     *
     * @return true if all inputs are valid, false otherwise
     */
    protected abstract boolean validateForm();

    /**
     * Saves the form data to the underlying model.
     * Subclasses should override this to implement save logic.
     */
    protected abstract void saveForm();

    /**
     * Resets the form to its initial state.
     * Subclasses should override this to implement reset logic.
     */
    protected void resetForm() {
        // Default implementation does nothing
        // Subclasses can override to provide specific reset behavior
    }

    /**
     * Shows an error message dialog.
     *
     * @param message the error message to display
     */
    protected void showError(String message) {
        showError(message, "Error");
    }

    /**
     * Shows an error message dialog with a custom title.
     *
     * @param message the error message to display
     * @param title the dialog title
     */
    protected void showError(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Shows a warning message dialog.
     *
     * @param message the warning message to display
     */
    protected void showWarning(String message) {
        showWarning(message, "Warning");
    }

    /**
     * Shows a warning message dialog with a custom title.
     *
     * @param message the warning message to display
     * @param title the dialog title
     */
    protected void showWarning(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Shows an information message dialog.
     *
     * @param message the information message to display
     */
    protected void showInfo(String message) {
        showInfo(message, "Information");
    }

    /**
     * Shows an information message dialog with a custom title.
     *
     * @param message the information message to display
     * @param title the dialog title
     */
    protected void showInfo(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Shows a confirmation dialog with Yes/No options.
     *
     * @param message the confirmation message
     * @return true if user clicked Yes, false otherwise
     */
    protected boolean confirm(String message) {
        return confirm(message, "Confirm");
    }

    /**
     * Shows a confirmation dialog with Yes/No options and custom title.
     *
     * @param message the confirmation message
     * @param title the dialog title
     * @return true if user clicked Yes, false otherwise
     */
    protected boolean confirm(String message, String title) {
        int result = JOptionPane.showConfirmDialog(this, message, title,
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return result == JOptionPane.YES_OPTION;
    }

    /**
     * Enables or disables a group of components.
     *
     * @param enabled true to enable, false to disable
     * @param components the components to enable/disable
     */
    protected void setComponentsEnabled(boolean enabled, Component... components) {
        for (Component component : components) {
            component.setEnabled(enabled);
        }
    }

    /**
     * Sets the foreground color for a group of components.
     *
     * @param color the foreground color to set
     * @param components the components to update
     */
    protected void setForegroundColor(Color color, Component... components) {
        for (Component component : components) {
            component.setForeground(color);
        }
    }

    /**
     * Standard cancel action - closes the form without saving.
     * This can be called from CancelAction implementations.
     */
    protected void handleCancel() {
        closeForm();
    }

    /**
     * Standard OK action - validates and saves the form.
     * This can be called from OkAction implementations.
     *
     * @return true if save was successful, false otherwise
     */
    protected boolean handleOk() {
        if (validateForm()) {
            saveForm();
            return true;
        }
        return false;
    }
}
