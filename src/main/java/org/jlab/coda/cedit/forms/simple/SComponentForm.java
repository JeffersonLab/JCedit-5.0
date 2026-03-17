
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

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileSystemView;

import org.jlab.coda.cedit.cooldesktop.CDesktopNew;
import org.jlab.coda.cedit.cooldesktop.DrawingCanvas;
import org.jlab.coda.cedit.system.*;
import org.jlab.coda.cedit.util.FormValidator;
import org.jlab.coda.cedit.util.ValidationResult;

/**
 * @author Vardan Gyurjyan
 */
public class SComponentForm extends BaseForm {
    // Constants
    private static final String UNDEFINED_VALUE = "undefined";

    private DrawingCanvas parentCanvas;
    private JCGComponent component;
    private int processID;
    private SComponentForm cForm;
    private SpinnerNumberModel priorityModel;

    private JCGSetup stp = JCGSetup.getInstance();
    private String pName;

    // data members before graphical update
    private String p_userConfig;
    private String p_rol1 = "";
    private String p_rol2 = "";
    private String p_rol1String = "";
    private String p_rol2String = "";
    private int p_priority;
    private boolean p_isRunData;
    private boolean p_isTsCheck;
    private boolean p_isSparsify;
    private boolean p_isLittleEndian;
    private int p_tsSlop;
    private int p_buildThreads;

    private boolean _tsSlop_update = false;
    private boolean _buildThreads_update = false;
    private boolean p_priority_update = false;
    private boolean _rol1_update = false;
    private boolean _rol2_update = false;
    private boolean _rol1us_update = false;
    private boolean _rol2us_update = false;
    private boolean _config_update = false;
    private boolean _rundata_update = false;
    private boolean _sparsify_update = false;
    private boolean _littleEndian_update = false;
    private boolean _ts_update = false;

    private JFileChooser jfc;
    public SComponentForm(DrawingCanvas canvas, JCGComponent comp, boolean editable) {
        parentCanvas = canvas;
        component = comp;

        jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

        initComponents();

        if(!comp.getType().equals(ACodaType.ROC.name())){
            okAllButton.setEnabled(false);
        }
        // recreate processes combo box
        for(JCGProcess pr:component.getPrcesses()){
            processID++;
            addProcessCombo(pr.getName());
        }
        addProcessCombo("New...");


        nameTextField.setText(comp.getName());
        pName = comp.getName();

        typeTextField.setText(comp.getType());


        idTextField.setText(Integer.toString(comp.getId()));


        parseCode(comp);

        configFileTextField.setText(comp.getUserConfig());
        p_userConfig =comp.getUserConfig();

        descriptionTextArea.setText(comp.getDescription());

        // Create priority model based on component type
        priorityModel = createPriorityModelForType(comp.getType());
        if(priorityModel!=null){
            prioritySpinner.setModel(priorityModel);
            if(comp.getPriority()>0){
                int v =  comp.getPriority();
                prioritySpinner.setValue(v);
                p_priority = v;
            }
        }

        switch(ACodaType.getEnum(comp.getType())){
            case USR:
            case SLC:
            case WNC:
            case FILE:
                Rol1Label.setForeground(Color.lightGray);
                Rol1usrStringLabel.setForeground(Color.lightGray);
                Rol2Label.setForeground(Color.lightGray);
                Rol2UsrStringLabel.setForeground(Color.lightGray);

                Rol1TextField.setEditable(false);
                Rol1UserStrTextField.setEditable(false);
                Rol2TextField.setEditable(false);
                Rol2UserStrTextField.setEditable(false);

                runDataCheckBox.setEnabled(false);
                tsCheckBox.setEnabled(false);
                tsSlopSpinner.setEnabled(false);
                buildTreadsSpinner.setEnabled(false);
                sparsifyCheckBox.setEnabled(false);
                endianCheckBox.setEnabled(false);
                label4.setEnabled(false);
                label6.setEnabled(false);
                break;
            case ER:
            case PEB:
            case SEB:
            case EB:
            case VTP:
            case DC:
                Rol1Label.setForeground(Color.lightGray);
                Rol1usrStringLabel.setForeground(Color.lightGray);
                Rol2Label.setForeground(Color.lightGray);
                Rol2UsrStringLabel.setForeground(Color.lightGray);

                Rol1TextField.setEditable(false);
                Rol1UserStrTextField.setEditable(false);
                Rol2TextField.setEditable(false);
                Rol2UserStrTextField.setEditable(false);

                JCGModule m = comp.getModule();
                if(m!=null){
                    if(m.isRunData()) {
                        runDataCheckBox.setSelected(true);
                        p_isRunData = true;
                    } else {
                        runDataCheckBox.setSelected(false);
                        p_isRunData = false;
                    }
                    if(m.isTsCheck()) {
                        tsCheckBox.setSelected(true);
                        p_isTsCheck = true;
                    } else {
                        tsCheckBox.setSelected(false);
                        p_isTsCheck = false;
                    }

                    if(m.isSparsify()) {
                        sparsifyCheckBox.setSelected(true);
                        p_isSparsify = true;
                    } else {
                        sparsifyCheckBox.setSelected(false);
                        p_isSparsify = false;
                    }
                    // endiannes
                    endianCheckBox.setSelected(false);
                    p_isLittleEndian = false;
                    if(m.getChnnels().size() <=0 ){
                        JCGChannel c = new JCGChannel();
                        m.addChnnel(c);
                    } else {
                        for (JCGChannel channel : m.getChnnels()) {
                            if (channel.getEndian().equals("little")) {
                                endianCheckBox.setSelected(true);
                                p_isLittleEndian = true;
                                break;
                            }
                        }
                    }
                    tsSlopSpinner.setValue(m.getTsSlop());
                    p_tsSlop = m.getTsSlop();
                    buildTreadsSpinner.setValue(m.getThreads());
                    p_buildThreads = m.getThreads();
                }
                break;
            case ROC:
            case GT:
            case TS:
                runDataCheckBox.setEnabled(false);
                tsCheckBox.setEnabled(false);
                tsSlopSpinner.setEnabled(false);
                buildTreadsSpinner.setEnabled(false);
                sparsifyCheckBox.setEnabled(false);
                endianCheckBox.setEnabled(false);
                masterRocCheckBox.setEnabled(true);
                label4.setEnabled(false);
                label6.setEnabled(false);
                break;
        }
        if(component.isPreDefined()){
            descriptionTextArea.setEnabled(false);
        }
        setVisible(true);
        if(!editable){
            nameTextField.setEnabled(false);
            prioritySpinner.setEnabled(false);
            Rol1TextField.setEnabled(false);
            Rol1UserStrTextField.setEnabled(false);
            Rol2TextField.setEnabled(false);
            Rol2UserStrTextField.setEnabled(false);
            configFileTextField.setEnabled(false);
            descriptionTextArea.setEnabled(false);
            processComboBox.setEnabled(false);
            runDataCheckBox.setEnabled(false);
            sparsifyCheckBox.setEnabled(false);
            endianCheckBox.setEnabled(false);
            tsCheckBox.setEnabled(false);
            tsSlopSpinner.setEnabled(false);
            buildTreadsSpinner.setEnabled(false);
            okButton.setEnabled(false);
            clearButton.setEnabled(false);
            processButton.setEnabled(false);
            label4.setEnabled(false);
            label6.setEnabled(false);

        }

        cForm = this;
        String predefinedDescription = CDesktopNew.isComponentPredefined(getNameFromTextField(),
                typeTextField.getText().trim(),
                component.getSubType(),
                descriptionTextArea.getText().replace("\\n","\n"));
        if(predefinedDescription.equals("UNDEFINED_VALUE")) {
            descriptionTextArea.setEnabled(true);
        } else {
            descriptionTextArea.setEnabled(false);
            descriptionTextArea.setText(predefinedDescription);
        }
    }

    // BaseForm implementation

    @Override
    protected boolean validateForm() {
        String _name = getNameFromTextField();

        // Validate component name (checks empty, underscore, duplicates)
        ValidationResult nameValidation = FormValidator.validateComponentName(_name, parentCanvas);
        if (!nameValidation.isValid()) {
            nameValidation.showDialogIfInvalid(cForm);
            return false;
        }

        // Check if predefined
        String tp = typeTextField.getText().trim();
        if (!pName.equals(_name)) {
            if (CDesktopNew.isComponentPredefined(tp, _name)) {
                showError("Component with the name = " + _name + " is predefined", "Error");
                return false;
            }
        }

        return true;
    }

    @Override
    protected void saveForm() {
        String _name = getNameFromTextField();
        String tp = typeTextField.getText().trim();

        // Assign unique ID if name changed
        if (!pName.equals(_name)) {
            idTextField.setText(Integer.toString(CDesktopNew.assignUniqueId(tp)));
        }

        // Update component info
        updateComponentInfo();
    }

    public String getComponentName(){
        return component.getName();
    }

    public String getComponentType(){
        return component.getType();
    }

    /**
     * Parses the following code structure and sets rol1 and rol2 text fields
     * {/primary-list.so myString1} {/secondary-list.so myString2}
     *
     * @param comp  JCGComponent object
     */
    private void parseCode(JCGComponent comp){

        if(comp.getRol1()!=null){
            Rol1TextField.setText(comp.getRol1());
            p_rol1 = comp.getRol1();
        }else {
            Rol1TextField.setText("");
        }
        if(comp.getRol1UsrString()!=null &&
                !comp.getRol1UsrString().equals("")){
            Rol1UserStrTextField.setText(comp.getRol1UsrString());
            p_rol1String = comp.getRol1UsrString();
        }else {
            Rol1UserStrTextField.setText("UNDEFINED_VALUE");
        }
        if(comp.getRol2()!=null){
            Rol2TextField.setText(comp.getRol2());
            p_rol2 = comp.getRol2();
        }else {
            Rol2TextField.setText("");
        }
        if(comp.getRol2UsrString()!=null &&
                !comp.getRol2UsrString().equals("")){
            Rol2UserStrTextField.setText(comp.getRol2UsrString());
            p_rol2String = comp.getRol2UsrString();
        }else {
            Rol2UserStrTextField.setText("UNDEFINED_VALUE");
        }

        masterRocCheckBox.setSelected(comp.isMaster());

    }

    public void addProcessCombo(String name){
        for(int i=0;i<processComboBox.getItemCount();i++) {
            if(processComboBox.getItemAt(i).equals(name)) return;
        }
        processComboBox.addItem(name);
    }

    public void removeProcessCombo(String name){
        for(int i=0; i<processComboBox.getItemCount();i++){
            if(processComboBox.getItemAt(i).equals(name)){
                processComboBox.removeItemAt(i);
                return;
            }
        }
    }


    public String getNameFromTextField(){
        String s =  nameTextField.getText().trim();
        return s.replace("_",".");
    }


    public boolean isComponentDefinedOnCanvas(String name){
        int i=0;
        for(JCGComponent c:parentCanvas.getGCMPs().values()){
            if(c.getName().equals(name)) {
                i = i+1;
            }
        }
        return i > 1;
    }

    private void updateComponentInfo(){
        if(!getNameFromTextField().equals("")){

//            pName = component.getName();

            if(!getNameFromTextField().equals(pName)){
                parentCanvas.linkDelete2(pName);
            }

            component.setName(getNameFromTextField());
            typeTextField.setText(typeTextField.getText().trim().toUpperCase());
            component.setType(typeTextField.getText().trim());

            component.setId(Integer.parseInt(idTextField.getText().trim()));

            int priorityRange = 100;
            if(component.getType().equals(ACodaType.USR.name())) {
                priorityRange = 1000;
            }
            if((Integer)prioritySpinner.getValue() < ACodaType.getEnum(component.getType()).priority() ||
                    (Integer)prioritySpinner.getValue() > ACodaType.getEnum(component.getType()).priority()+priorityRange){
                component.setPriority(ACodaType.getEnum(component.getType()).priority());
            } else {
                component.setPriority((Integer)prioritySpinner.getValue());
            }
            component.setRol1(Rol1TextField.getText().trim());
            component.setRol1UsrString(Rol1UserStrTextField.getText().trim());
            component.setRol2(Rol2TextField.getText().trim());
            component.setRol2UsrString(Rol2UserStrTextField.getText().trim());

            String t = configFileTextField.getText().trim();
            if(t.equals("")) {
                component.setUserConfig("UNDEFINED_VALUE");
            } else {
                component.setUserConfig(configFileTextField.getText().trim());
            }
            component.setDescription(descriptionTextArea.getText().replace("\\n","\n"));

            if(runDataCheckBox.isEnabled()){
                component.getModule().setRunData(runDataCheckBox.isSelected());
            }
            if(tsCheckBox.isEnabled()){
                component.getModule().setTsCheck(tsCheckBox.isSelected());
            }

            if(sparsifyCheckBox.isEnabled()){
                component.getModule().setSparsify(sparsifyCheckBox.isSelected());
            }

            if (endianCheckBox.isEnabled()) {
                if(endianCheckBox.isSelected()) {
                    if (component.getModule().getChnnels().size() <=0){
                        JCGChannel c = new JCGChannel();
                        component.getModule().addChnnel(c);
                    }
                    for (JCGChannel channel : component.getModule().getChnnels()) {
                        channel.setEndian("little");
                    }
                } else {
                    if (component.getModule().getChnnels().size() <=0){
                        JCGChannel c = new JCGChannel();
                        component.getModule().addChnnel(c);
                    }
                    for (JCGChannel channel : component.getModule().getChnnels()) {
                        channel.setEndian("big");
                    }

                }
            }

            if(tsSlopSpinner.isEnabled()){
                component.getModule().setTsSlop((Integer)tsSlopSpinner.getValue());
            }
            if(buildTreadsSpinner.isEnabled()){
                component.getModule().setThreads((Integer)buildTreadsSpinner.getValue());
            }

            if(masterRocCheckBox.isEnabled()){

                if(masterRocCheckBox.isSelected()){
                    // reset all already defined components isMaster
                    CDesktopNew.resetMaster(parentCanvas);
                    component.setMaster(true);
                    component.setPriority(ACodaType.TS.priority());
                }
//                else {
//                    component.setMaster(false);
//                    component.setPriority(ACodaType.getEnum(component.getType()).priority());
//                }
            }

            updateInMemory(pName);

            parentCanvas.repaint();
        }
    }

    public void updateInMemory(String pName){

        //@todo debug printouts
//        parentCanvas.dumpGCMPs();

        for(JCGComponent com:parentCanvas.getGCMPs().values()){
            for(JCGLink l:com.getLnks()){
                if(l.getSourceComponentName().equals(pName)){
                    l.setSourceComponentName(component.getName());
                    l.setSourceComponentType(component.getType());
                    l.setName(l.getSourceComponentName()+"_"+l.getDestinationComponentName());

                } else if(l.getDestinationComponentName().equals(pName)){
                    l.setDestinationComponentName(component.getName());
                    l.setDestinationComponentType(component.getType());
                    l.setName(l.getSourceComponentName()+"_"+l.getDestinationComponentName());
                    if(parentCanvas.getComp(l.getDestinationComponentName())!=null){
                        for(JCGTransport tr: parentCanvas.getComp(l.getDestinationComponentName()).getTrnsports()){
                            tr.setEtName("/tmp/et_" + stp.getExpid() + "_" +l.getDestinationComponentName());
                        }
                    }
                }
            }
        }

        if(!parentCanvas.getGCMPs().containsKey(component.getName())) {
            parentCanvas.getGCMPs().remove(pName);
        }
        parentCanvas.addgCmp(component);
    }

    private void tsSlopSpinnerStateChanged(ChangeEvent e) {
        okAllButton.setEnabled(true);
        JTextField tf =
                ((JSpinner.DefaultEditor)tsSlopSpinner.getEditor()).getTextField();
        tf.setBackground(Color.YELLOW);
        _tsSlop_update = true;
    }

    private void buildThreadsSpinnerStateChanged(ChangeEvent e) {
        okAllButton.setEnabled(true);
        JTextField tf =
                ((JSpinner.DefaultEditor)buildTreadsSpinner.getEditor()).getTextField();
        tf.setBackground(Color.YELLOW);
        _buildThreads_update = true;
    }

    private void prioritySpinnerStateChanged(ChangeEvent e) {
        if(!component.isMaster()){
            okAllButton.setEnabled(true);
            JTextField tf =
                    ((JSpinner.DefaultEditor)prioritySpinner.getEditor()).getTextField();
            tf.setBackground(Color.YELLOW);
            p_priority_update = true;
        }
    }

    private void Rol1TextFieldKeyPressed(KeyEvent e) {
        okAllButton.setEnabled(true);
        Rol1TextField.setBackground(Color.YELLOW);
        _rol1_update = true;
        int returnValue = jfc.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jfc.getSelectedFile();
            Rol1TextField.setText(selectedFile.getAbsolutePath());
//            System.out.println(selectedFile.getAbsolutePath());
        }
    }

    private void Rol2TextFieldKeyPressed(KeyEvent e) {
        Rol2TextField.setBackground(Color.YELLOW);
        _rol2_update = true;
        okAllButton.setEnabled(true);
        int returnValue = jfc.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jfc.getSelectedFile();
            Rol2TextField.setText(selectedFile.getAbsolutePath());
//            System.out.println(selectedFile.getAbsolutePath());
        }
    }


    private void Rol1UserStrTextFieldKeyPressed(KeyEvent e) {
        okAllButton.setEnabled(true);
        Rol1UserStrTextField.setBackground(Color.YELLOW);
        _rol1us_update = true;
    }

    private void Rol2UserStrTextFieldKeyPressed(KeyEvent e) {
        okAllButton.setEnabled(true);
        Rol2UserStrTextField.setBackground(Color.YELLOW);
        _rol2us_update = true;
    }

    private void configFileTextFieldKeyPressed(KeyEvent e) {
        okAllButton.setEnabled(true);
        configFileTextField.setBackground(Color.YELLOW);
        _config_update = true;
    }

    private void runDataCheckBoxMouseClicked(MouseEvent e) {
        okAllButton.setEnabled(true);
        runDataCheckBox.setBackground(Color.YELLOW);
        _rundata_update = true;
    }

    private void sparsifyCheckBoxMouseClicked(MouseEvent e) {
        okAllButton.setEnabled(true);
        sparsifyCheckBox.setBackground(Color.YELLOW);
        _sparsify_update = true;

    }

    private void tsCheckBoxMouseClicked(MouseEvent e) {
        okAllButton.setEnabled(true);
        tsCheckBox.setBackground(Color.YELLOW);
        _ts_update = true;
    }

    private void endianCheckBoxMouseClicked(MouseEvent e) {
        okAllButton.setEnabled(true);
        endianCheckBox.setBackground(Color.YELLOW);
        _littleEndian_update = true;

    }

    /**
     * Helper method to determine priority range for a given component type.
     * @param type the ACodaType enum value
     * @return the priority range (maximum offset from base priority)
     */
    /**
     * Determines the priority range for a given component type.
     * Different component types have different priority ranges to prevent conflicts.
     *
     * @param type the component type
     * @return the priority range (number of possible priority values)
     */
    private int getPriorityRangeForType(ACodaType type) {
        switch (type) {
            case USR:
                return 1000;
            case PEB:
            case PAGG:
            case SEB:
            case SAGG:
            case VTP:
                return 50;
            default:
                return 100;
        }
    }

    /**
     * Creates a SpinnerNumberModel for the priority spinner based on component type.
     * @param typeName the component type name
     * @return SpinnerNumberModel configured for the type, or null if type not found
     */
    private SpinnerNumberModel createPriorityModelForType(String typeName) {
        ACodaType type = ACodaType.getEnum(typeName);
        if (type == null) {
            return null;
        }
        int basePriority = type.priority();
        int range = getPriorityRangeForType(type);
        return new SpinnerNumberModel(basePriority, basePriority, basePriority + range, 1);
    }

    /**
     * Resets the priority spinner to the default value for the given component type.
     * @param typeName the component type name
     */
    private void resetPriorityForType(String typeName) {
        ACodaType type = ACodaType.getEnum(typeName);
        if (type != null) {
            prioritySpinner.setValue(type.priority());
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        label1 = new JLabel();
        nameTextField = new JTextField();
        typeTextField = new JTextField();
        label5 = new JLabel();
        configFileLabel = new JLabel();
        configFileTextField = new JTextField();
        idTextField = new JTextField();
        Rol2Label = new JLabel();
        Rol2TextField = new JTextField();
        Rol2UsrStringLabel = new JLabel();
        Rol2UserStrTextField = new JTextField();
        Rol1Label = new JLabel();
        Rol1TextField = new JTextField();
        Rol1usrStringLabel = new JLabel();
        Rol1UserStrTextField = new JTextField();
        label11 = new JLabel();
        label13 = new JLabel();
        panel2 = new JPanel();
        processButton = new JButton();
        processComboBox = new JComboBox<>();
        label2 = new JLabel();
        prioritySpinner = new JSpinner();
        configFileLabel2 = new JLabel();
        scrollPane1 = new JScrollPane();
        descriptionTextArea = new JTextArea();
        label3 = new JLabel();
        runDataCheckBox = new JCheckBox();
        sparsifyCheckBox = new JCheckBox();
        tsCheckBox = new JCheckBox();
        tsSlopSpinner = new JSpinner();
        label4 = new JLabel();
        masterRocCheckBox = new JCheckBox();
        buildTreadsSpinner = new JSpinner();
        label6 = new JLabel();
        endianCheckBox = new JCheckBox();
        okButton = new JButton();
        clearButton = new JButton();
        cancelButton = new JButton();
        separator1 = new JSeparator();
        okAllButton = new JButton();
        action1 = new OkAction();
        action2 = new ClearAction();
        action3 = new CancelAction();
        action4 = new ProcessAction();
        action5 = new OkAllAction();

        //======== this ========
        setTitle("Component");
        var contentPane = getContentPane();

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));

            //======== contentPanel ========
            {

                //---- label1 ----
                label1.setText("Name");

                //---- typeTextField ----
                typeTextField.setEditable(false);

                //---- label5 ----
                label5.setText("Priority");

                //---- configFileLabel ----
                configFileLabel.setText("User Config ");

                //---- configFileTextField ----
                configFileTextField.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        configFileTextFieldKeyPressed(e);
                    }
                });

                //---- idTextField ----
                idTextField.setEditable(false);
                idTextField.setText("auto");

                //---- Rol2Label ----
                Rol2Label.setText("ROL2");

                //---- Rol2TextField ----
                Rol2TextField.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        Rol2TextFieldKeyPressed(e);
                    }
                });

                //---- Rol2UsrStringLabel ----
                Rol2UsrStringLabel.setText("User String");

                //---- Rol2UserStrTextField ----
                Rol2UserStrTextField.setText("UNDEFINED_VALUE");
                Rol2UserStrTextField.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        Rol2UserStrTextFieldKeyPressed(e);
                    }
                });

                //---- Rol1Label ----
                Rol1Label.setText("ROL1");

                //---- Rol1TextField ----
                Rol1TextField.setText("UNDEFINED_VALUE");
                Rol1TextField.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        Rol1TextFieldKeyPressed(e);
                    }
                });

                //---- Rol1usrStringLabel ----
                Rol1usrStringLabel.setText("User String");

                //---- Rol1UserStrTextField ----
                Rol1UserStrTextField.setText("UNDEFINED_VALUE");
                Rol1UserStrTextField.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        Rol1UserStrTextFieldKeyPressed(e);
                    }
                });

                //---- label11 ----
                label11.setText("Type");

                //---- label13 ----
                label13.setText("ID");

                //======== panel2 ========
                {
                    panel2.setBorder(new TitledBorder("Process"));

                    //---- processButton ----
                    processButton.setAction(action4);
                    processButton.setText("Open");
                    processButton.setToolTipText("add, edit or remove processes");

                    //---- processComboBox ----
                    processComboBox.setModel(new DefaultComboBoxModel<>(new String[] {
                        "New..."
                    }));

                    GroupLayout panel2Layout = new GroupLayout(panel2);
                    panel2.setLayout(panel2Layout);
                    panel2Layout.setHorizontalGroup(
                        panel2Layout.createParallelGroup()
                            .addGroup(panel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(processButton)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(processComboBox, GroupLayout.PREFERRED_SIZE, 167, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(22, Short.MAX_VALUE))
                    );
                    panel2Layout.setVerticalGroup(
                        panel2Layout.createParallelGroup()
                            .addGroup(panel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(processButton)
                                    .addComponent(processComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(17, Short.MAX_VALUE))
                    );
                }

                //---- label2 ----
                label2.setText("(optional)");
                label2.setEnabled(false);

                //---- prioritySpinner ----
                prioritySpinner.setModel(new SpinnerNumberModel(0, null, null, 1));
                prioritySpinner.addChangeListener(e -> prioritySpinnerStateChanged(e));

                //---- configFileLabel2 ----
                configFileLabel2.setText("Description");

                //======== scrollPane1 ========
                {
                    scrollPane1.setViewportView(descriptionTextArea);
                }

                //---- label3 ----
                label3.setText("(optional)");
                label3.setEnabled(false);

                //---- runDataCheckBox ----
                runDataCheckBox.setText("RunData");
                runDataCheckBox.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        runDataCheckBoxMouseClicked(e);
                    }
                });

                //---- sparsifyCheckBox ----
                sparsifyCheckBox.setText("Sparsify");
                sparsifyCheckBox.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        sparsifyCheckBoxMouseClicked(e);
                    }
                });

                //---- tsCheckBox ----
                tsCheckBox.setText("tsCheck");
                tsCheckBox.setSelected(true);
                tsCheckBox.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        tsCheckBoxMouseClicked(e);
                    }
                });

                //---- tsSlopSpinner ----
                tsSlopSpinner.setModel(new SpinnerNumberModel(2, 0, 999, 1));
                tsSlopSpinner.addChangeListener(e -> tsSlopSpinnerStateChanged(e));

                //---- label4 ----
                label4.setText("tsSlop");

                //---- masterRocCheckBox ----
                masterRocCheckBox.setText("Master Roc");
                masterRocCheckBox.setEnabled(false);

                //---- buildTreadsSpinner ----
                buildTreadsSpinner.setModel(new SpinnerNumberModel(1, 1, 4, 1));
                buildTreadsSpinner.addChangeListener(e -> buildThreadsSpinnerStateChanged(e));

                //---- label6 ----
                label6.setText("Threads");

                //---- endianCheckBox ----
                endianCheckBox.setText("Little-endian");
                endianCheckBox.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        endianCheckBoxMouseClicked(e);
                    }
                });

                GroupLayout contentPanelLayout = new GroupLayout(contentPanel);
                contentPanel.setLayout(contentPanelLayout);
                contentPanelLayout.setHorizontalGroup(
                    contentPanelLayout.createParallelGroup()
                        .addGroup(contentPanelLayout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(contentPanelLayout.createParallelGroup()
                                .addGroup(contentPanelLayout.createSequentialGroup()
                                    .addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addGroup(GroupLayout.Alignment.LEADING, contentPanelLayout.createSequentialGroup()
                                            .addGroup(contentPanelLayout.createParallelGroup()
                                                .addGroup(contentPanelLayout.createSequentialGroup()
                                                    .addComponent(Rol1Label)
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(Rol1TextField))
                                                .addGroup(contentPanelLayout.createSequentialGroup()
                                                    .addGroup(contentPanelLayout.createParallelGroup()
                                                        .addComponent(label1)
                                                        .addComponent(label5))
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                    .addGroup(contentPanelLayout.createParallelGroup()
                                                        .addGroup(contentPanelLayout.createSequentialGroup()
                                                            .addComponent(prioritySpinner, GroupLayout.PREFERRED_SIZE, 83, GroupLayout.PREFERRED_SIZE)
                                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                            .addComponent(masterRocCheckBox)
                                                            .addGap(0, 0, Short.MAX_VALUE))
                                                        .addComponent(nameTextField)))
                                                .addGroup(contentPanelLayout.createSequentialGroup()
                                                    .addComponent(Rol2Label)
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(Rol2TextField)))
                                            .addGap(28, 28, 28)
                                            .addGroup(contentPanelLayout.createParallelGroup()
                                                .addGroup(contentPanelLayout.createSequentialGroup()
                                                    .addGroup(contentPanelLayout.createParallelGroup()
                                                        .addComponent(Rol1usrStringLabel)
                                                        .addComponent(Rol2UsrStringLabel))
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                    .addGroup(contentPanelLayout.createParallelGroup()
                                                        .addComponent(Rol1UserStrTextField)
                                                        .addComponent(Rol2UserStrTextField)))
                                                .addGroup(contentPanelLayout.createSequentialGroup()
                                                    .addGap(24, 24, 24)
                                                    .addComponent(label11)
                                                    .addGap(63, 63, 63)
                                                    .addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                        .addComponent(typeTextField, GroupLayout.PREFERRED_SIZE, 159, GroupLayout.PREFERRED_SIZE)
                                                        .addGroup(contentPanelLayout.createSequentialGroup()
                                                            .addComponent(label13)
                                                            .addGap(1, 1, 1)
                                                            .addComponent(idTextField, GroupLayout.PREFERRED_SIZE, 87, GroupLayout.PREFERRED_SIZE))))))
                                        .addGroup(GroupLayout.Alignment.LEADING, contentPanelLayout.createSequentialGroup()
                                            .addComponent(configFileLabel)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(configFileTextField)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(label2, GroupLayout.PREFERRED_SIZE, 62, GroupLayout.PREFERRED_SIZE)))
                                    .addGap(12, 12, 12))
                                .addGroup(contentPanelLayout.createSequentialGroup()
                                    .addComponent(panel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addGap(36, 36, 36)
                                    .addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(runDataCheckBox)
                                        .addComponent(sparsifyCheckBox))
                                    .addGap(18, 18, 18)
                                    .addGroup(contentPanelLayout.createParallelGroup()
                                        .addGroup(contentPanelLayout.createSequentialGroup()
                                            .addComponent(tsSlopSpinner, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(label4)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(endianCheckBox)
                                            .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGroup(contentPanelLayout.createSequentialGroup()
                                            .addComponent(tsCheckBox, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addGap(18, 18, 18)
                                            .addComponent(buildTreadsSpinner, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(label6)
                                            .addGap(24, 24, 24))))
                                .addGroup(contentPanelLayout.createSequentialGroup()
                                    .addGroup(contentPanelLayout.createParallelGroup()
                                        .addComponent(configFileLabel2)
                                        .addComponent(label3, GroupLayout.PREFERRED_SIZE, 62, GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(scrollPane1)
                                    .addContainerGap())))
                );
                contentPanelLayout.setVerticalGroup(
                    contentPanelLayout.createParallelGroup()
                        .addGroup(contentPanelLayout.createSequentialGroup()
                            .addGap(11, 11, 11)
                            .addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(nameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(label1)
                                .addComponent(label11)
                                .addComponent(typeTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(contentPanelLayout.createParallelGroup()
                                .addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(label5)
                                    .addComponent(idTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label13))
                                .addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(prioritySpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(masterRocCheckBox)))
                            .addGap(18, 18, 18)
                            .addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(Rol1Label)
                                .addComponent(Rol1usrStringLabel)
                                .addComponent(Rol1UserStrTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(Rol1TextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(contentPanelLayout.createParallelGroup()
                                .addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(Rol2TextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(Rol2Label))
                                .addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(Rol2UsrStringLabel)
                                    .addComponent(Rol2UserStrTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                            .addGap(18, 18, 18)
                            .addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(configFileLabel)
                                .addComponent(configFileTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(label2))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addGroup(contentPanelLayout.createSequentialGroup()
                                    .addComponent(configFileLabel2)
                                    .addGap(1, 1, 1)
                                    .addComponent(label3))
                                .addComponent(scrollPane1))
                            .addGap(18, 18, 18)
                            .addGroup(contentPanelLayout.createParallelGroup()
                                .addGroup(contentPanelLayout.createSequentialGroup()
                                    .addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(runDataCheckBox)
                                        .addComponent(tsCheckBox)
                                        .addComponent(label6)
                                        .addComponent(buildTreadsSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addGap(9, 9, 9)
                                    .addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(sparsifyCheckBox)
                                        .addComponent(tsSlopSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label4)
                                        .addComponent(endianCheckBox)))
                                .addComponent(panel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                );
            }

            //---- okButton ----
            okButton.setAction(action1);
            okButton.setText("Ok");

            //---- clearButton ----
            clearButton.setAction(action2);

            //---- cancelButton ----
            cancelButton.setAction(action3);
            cancelButton.setText("Cancel");

            //---- okAllButton ----
            okAllButton.setAction(action5);
            okAllButton.setText("Apply to All");
            okAllButton.setEnabled(false);

            GroupLayout dialogPaneLayout = new GroupLayout(dialogPane);
            dialogPane.setLayout(dialogPaneLayout);
            dialogPaneLayout.setHorizontalGroup(
                dialogPaneLayout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, dialogPaneLayout.createSequentialGroup()
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(okButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(okAllButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(clearButton, GroupLayout.PREFERRED_SIZE, 76, GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(cancelButton)
                        .addContainerGap())
                    .addComponent(contentPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(separator1, GroupLayout.Alignment.TRAILING)
            );
            dialogPaneLayout.setVerticalGroup(
                dialogPaneLayout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, dialogPaneLayout.createSequentialGroup()
                        .addComponent(contentPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(separator1, GroupLayout.PREFERRED_SIZE, 2, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(dialogPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(clearButton)
                            .addComponent(cancelButton)
                            .addComponent(okAllButton)
                            .addComponent(okButton)))
            );
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(dialogPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(dialogPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JLabel label1;
    private JTextField nameTextField;
    private JTextField typeTextField;
    private JLabel label5;
    private JLabel configFileLabel;
    private JTextField configFileTextField;
    private JTextField idTextField;
    private JLabel Rol2Label;
    private JTextField Rol2TextField;
    private JLabel Rol2UsrStringLabel;
    private JTextField Rol2UserStrTextField;
    private JLabel Rol1Label;
    private JTextField Rol1TextField;
    private JLabel Rol1usrStringLabel;
    private JTextField Rol1UserStrTextField;
    private JLabel label11;
    private JLabel label13;
    private JPanel panel2;
    private JButton processButton;
    private JComboBox<String> processComboBox;
    private JLabel label2;
    private JSpinner prioritySpinner;
    private JLabel configFileLabel2;
    private JScrollPane scrollPane1;
    private JTextArea descriptionTextArea;
    private JLabel label3;
    private JCheckBox runDataCheckBox;
    private JCheckBox sparsifyCheckBox;
    private JCheckBox tsCheckBox;
    private JSpinner tsSlopSpinner;
    private JLabel label4;
    private JCheckBox masterRocCheckBox;
    private JSpinner buildTreadsSpinner;
    private JLabel label6;
    private JCheckBox endianCheckBox;
    private JButton okButton;
    private JButton clearButton;
    private JButton cancelButton;
    private JSeparator separator1;
    private JButton okAllButton;
    private OkAction action1;
    private ClearAction action2;
    private CancelAction action3;
    private ProcessAction action4;
    private OkAllAction action5;
    // JFormDesigner - End of variables declaration  //GEN-END:variables


    private class ProcessAction extends AbstractAction {
        private ProcessAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            putValue(NAME, "Open");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            updateComponentInfo();
            if((processComboBox.getSelectedItem()).equals("New...")){

                // define a default name
                processID++;
                String tmpName = component.getName()+"_process_"+processID;

                // create a process
                JCGProcess gp = new JCGProcess();
                gp.setName(tmpName);

                // start a process form
                ProcessForm pf = new ProcessForm(cForm,parentCanvas,gp, true);
                pf.setVisible(true);
            } else {

                // open existing process in the form
                for(JCGProcess gp:component.getPrcesses()){
                    if((processComboBox.getSelectedItem()).equals(gp.getName())){

                        // start a process form
                        ProcessForm pf = new ProcessForm(cForm,parentCanvas,gp, false);
                        pf.setVisible(true);
                        break;
                    }
                }
            }
        }
    }

    private class CancelAction extends AbstractAction {
        private CancelAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            putValue(NAME, "Cancel");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            handleCancel();
        }
    }

    private class ClearAction extends AbstractAction {
        private ClearAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            putValue(NAME, "Clear");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
//            nameTextField.setText("");
            // Reset priority to default for component type
            resetPriorityForType(component.getType());
            Rol1TextField.setText("");
            Rol1UserStrTextField.setText("UNDEFINED_VALUE");
            Rol2TextField.setText("");
            Rol2UserStrTextField.setText("UNDEFINED_VALUE");
            configFileTextField.setText("");
            runDataCheckBox.setSelected(false);
            tsCheckBox.setSelected(true);
            sparsifyCheckBox.setSelected(false);
            endianCheckBox.setSelected(false);
            tsSlopSpinner.setValue(2);
            buildTreadsSpinner.setValue(2);
        }
    }

    private class OkAction extends AbstractAction {
        private OkAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            putValue(NAME, "Ok");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            if (handleOk()) {
                closeForm();
            }
        }
    }

    private class OkAllAction extends AbstractAction {
        private OkAllAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            putValue(NAME, "Apply to All");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            // Validate using BaseForm method
            if (!validateForm()) {
                return;
            }

            // Save form data
            saveForm();

            // Apply to all components in group
            if (parentCanvas.isGroupMode){
                for(JCGComponent c:parentCanvas.selectedGroup){
                    if(c.getType().equals(component.getType())){

                        if(_rol1_update) c.setRol1(component.getRol1());
                        if(_rol1us_update) c.setRol1UsrString(component.getRol1UsrString());
                        if(_rol2_update) c.setRol2(component.getRol2());
                        if(_rol2us_update) c.setRol2UsrString(component.getRol2UsrString());

                        if(_config_update) c.setUserConfig(component.getUserConfig());
                        if(_rundata_update) c.getModule().setRunData(component.getModule().isRunData());
                        if(_ts_update) c.getModule().setTsCheck(component.getModule().isTsCheck());
                        if(_sparsify_update) c.getModule().setSparsify(component.getModule().isSparsify());

                        if(_littleEndian_update) {
                            for (JCGChannel channel: c.getModule().getChnnels()){
                                for(JCGChannel ch: component.getModule().getChnnels()){
                                        channel.setEndian(ch.getEndian());
                                }
                            }
                        }

                        if(_tsSlop_update) c.getModule().setTsSlop(component.getModule().getTsSlop());
                        if(_buildThreads_update) c.getModule().setThreads(component.getModule().getThreads());

                        if(!component.isMaster()){
                            if (p_priority_update) c.setPriority(component.getPriority());
                        }
                    }
                }
                parentCanvas.groupReset();
            } else {
                for(JCGComponent c:parentCanvas.getGCMPs().values()){
                    if(c.getType().equals(component.getType())){
                        if(_rol1_update) c.setRol1(component.getRol1());
                        if(_rol1us_update) c.setRol1UsrString(component.getRol1UsrString());
                        if(_rol2_update) c.setRol2(component.getRol2());
                        if(_rol2us_update) c.setRol2UsrString(component.getRol2UsrString());

                        if(_config_update) c.setUserConfig(component.getUserConfig());
                        if(_rundata_update) c.getModule().setRunData(component.getModule().isRunData());
                        if(_ts_update) c.getModule().setTsCheck(component.getModule().isTsCheck());
                        if(_sparsify_update) c.getModule().setSparsify(component.getModule().isSparsify());

                        if(_littleEndian_update) {
                            for (JCGChannel channel: c.getModule().getChnnels()){
                                for(JCGChannel ch: component.getModule().getChnnels()){
                                    channel.setEndian(ch.getEndian());
                                }

                            }
                        }

                        if(_tsSlop_update) c.getModule().setTsSlop(component.getModule().getTsSlop());
                        if(_buildThreads_update) c.getModule().setThreads(component.getModule().getThreads());

                        if(!component.isMaster()){
                            if (p_priority_update) c.setPriority(component.getPriority());
                        }
                    }
                }
            }
            dispose();
        }
    }
}
