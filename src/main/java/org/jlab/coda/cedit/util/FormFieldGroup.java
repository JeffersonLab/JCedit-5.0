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
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class for managing groups of form fields that need to be
 * enabled/disabled together with consistent visual styling.
 *
 * This class helps reduce code duplication in forms where multiple
 * UI components need to be enabled or disabled as a group, often with
 * accompanying color changes to labels.
 *
 * @author Claude Sonnet 4.5 (Refactoring)
 */
public class FormFieldGroup {

    private final List<JComponent> editableComponents;
    private final List<JLabel> labels;
    private final Color enabledLabelColor;
    private final Color disabledLabelColor;

    /**
     * Creates a new FormFieldGroup with default colors (black for enabled, light gray for disabled).
     */
    public FormFieldGroup() {
        this(Color.BLACK, Color.LIGHT_GRAY);
    }

    /**
     * Creates a new FormFieldGroup with custom label colors.
     *
     * @param enabledLabelColor color to use for labels when group is enabled
     * @param disabledLabelColor color to use for labels when group is disabled
     */
    public FormFieldGroup(Color enabledLabelColor, Color disabledLabelColor) {
        this.editableComponents = new ArrayList<>();
        this.labels = new ArrayList<>();
        this.enabledLabelColor = enabledLabelColor;
        this.disabledLabelColor = disabledLabelColor;
    }

    /**
     * Adds an editable component to the group (e.g., JTextField, JSpinner, JComboBox).
     * These components will have their enabled state managed by this group.
     *
     * @param components one or more components to add
     * @return this FormFieldGroup for method chaining
     */
    public FormFieldGroup addEditableComponents(JComponent... components) {
        editableComponents.addAll(Arrays.asList(components));
        return this;
    }

    /**
     * Adds labels to the group. These will have their foreground color changed
     * when the group is enabled or disabled.
     *
     * @param labels one or more labels to add
     * @return this FormFieldGroup for method chaining
     */
    public FormFieldGroup addLabels(JLabel... labels) {
        this.labels.addAll(Arrays.asList(labels));
        return this;
    }

    /**
     * Enables all components in the group.
     * - Editable components will be enabled
     * - Text components will be made editable
     * - Labels will have their foreground set to the enabled color
     */
    public void enable() {
        setEnabled(true);
    }

    /**
     * Disables all components in the group.
     * - Editable components will be disabled
     * - Text components will be made non-editable
     * - Labels will have their foreground set to the disabled color
     */
    public void disable() {
        setEnabled(false);
    }

    /**
     * Sets the enabled state of all components in the group.
     *
     * @param enabled true to enable, false to disable
     */
    public void setEnabled(boolean enabled) {
        // Update editable components
        for (JComponent component : editableComponents) {
            component.setEnabled(enabled);

            // For text components, also set editable state
            if (component instanceof JTextComponent) {
                ((JTextComponent) component).setEditable(enabled);
            }
        }

        // Update label colors
        Color labelColor = enabled ? enabledLabelColor : disabledLabelColor;
        for (JLabel label : labels) {
            label.setForeground(labelColor);
        }
    }

    /**
     * Clears all components and labels from this group.
     */
    public void clear() {
        editableComponents.clear();
        labels.clear();
    }

    /**
     * Returns the number of editable components in this group.
     *
     * @return the component count
     */
    public int getComponentCount() {
        return editableComponents.size();
    }

    /**
     * Returns the number of labels in this group.
     *
     * @return the label count
     */
    public int getLabelCount() {
        return labels.size();
    }

    /**
     * Builder class for fluent construction of FormFieldGroups with many components.
     */
    public static class Builder {
        private final FormFieldGroup group;

        public Builder() {
            this.group = new FormFieldGroup();
        }

        public Builder(Color enabledLabelColor, Color disabledLabelColor) {
            this.group = new FormFieldGroup(enabledLabelColor, disabledLabelColor);
        }

        public Builder addComponents(JComponent... components) {
            group.addEditableComponents(components);
            return this;
        }

        public Builder addLabels(JLabel... labels) {
            group.addLabels(labels);
            return this;
        }

        public FormFieldGroup build() {
            return group;
        }
    }
}
