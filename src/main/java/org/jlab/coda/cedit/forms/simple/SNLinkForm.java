
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
import javax.swing.*;
import javax.swing.border.*;

import org.jlab.coda.cedit.cooldesktop.DrawingCanvas;
import org.jlab.coda.cedit.system.*;
import org.jlab.coda.cedit.system.JCGComponent;
import org.jlab.coda.cedit.system.JCGLink;
import org.jlab.coda.cedit.system.JCGTransport;
import org.jlab.coda.cedit.util.JCUtil;

/**
 * @author Vardan Gyurjyan
 */
public class SNLinkForm extends JFrame {
    private final JCGLink link;
    private final DrawingCanvas canvas;
    private JCGTransport destinationTransport = null;
    private JCGTransport sourceTransport = null;
    private final JCGSetup stp = JCGSetup.getInstance();

    private final ComboBoxModel comboModel;

    public SNLinkForm(DrawingCanvas canvas, JCGLink gl, boolean editable) {
        this.link = gl;
        this.canvas = canvas;


        if (gl.getDestinationComponentType().equals(ACodaType.FILE.name())) {
            comboModel = new DefaultComboBoxModel(new String[]{
                    "File",
                    "Et",
                    "Debug",
                    "None"
            });
        } else if (gl.getDestinationComponentType().equals(ACodaType.EBER.name()))   {
            comboModel = new DefaultComboBoxModel(new String[]{
                    "EmuSocket+Et",
                    "EmuSocket",
                    "None"
            });
        } else if (gl.getDestinationComponentType().equals(ACodaType.PEB.name())) {
            comboModel = new DefaultComboBoxModel(new String[]{
                    "Et",
                    "EmuSocket",
                    "None"
            });
        } else if (gl.getDestinationComponentType().equals(ACodaType.DC.name())) {
            comboModel = new DefaultComboBoxModel(new String[]{
                    "Et",
                    "EmuSocket",
                    "None"
            });
        } else if (gl.getDestinationComponentType().equals(ACodaType.PAGG.name())) {
            comboModel = new DefaultComboBoxModel(new String[]{
                    "TcpStream",
                    "UdpStream",
                    "None"
            });
        } else if (gl.getDestinationComponentType().equals(ACodaType.SAGG.name())) {
            comboModel = new DefaultComboBoxModel(new String[]{
                    "TcpStream",
                    "UdpStream",
                    "None"
            });
        } else {
            comboModel = new DefaultComboBoxModel(new String[]{
                    "Et",
                    "EmuSocket",
                    "TcpStream",
                    "UdpStream",
                    "None"
            });
        }

        initComponents();
        update();
        if (!editable) {

            disableEmu();
            disableEt();
            disableUdp();
            disableFile();
            okButton.setEnabled(false);
            removeButton.setEnabled(false);
            clearButton.setEnabled(false);
        }
    }

    public void enableEtCustomization(boolean b) {
        if (b) {
            etNumberEvents.setEnabled(true);
            etEventSize.setEnabled(true);
            etChunkSize.setEnabled(true);
            inputEtChunkSize.setEnabled(true);
            etWait.setEnabled(true);
            checkBoxEtCreate.setEnabled(true);
            if ((DrawingCanvas.getComp(link.getDestinationComponentName()).getType().equals(ACodaType.PEB.name())) ||
                    (DrawingCanvas.getComp(link.getDestinationComponentName()).getType().equals(ACodaType.SEB.name())) ||
                    (DrawingCanvas.getComp(link.getDestinationComponentName()).getType().equals(ACodaType.EBER.name())) ||
                    (DrawingCanvas.getComp(link.getDestinationComponentName()).getType().equals(ACodaType.ER.name()))
            ) {
                singleEventOutCheckBox.setEnabled(true);
            }
            etDefaultsMenuItem.setEnabled(true);
        } else {
            etNumberEvents.setEnabled(false);
            etEventSize.setEnabled(false);
            etChunkSize.setEnabled(false);
            inputEtChunkSize.setEnabled(false);
            etWait.setEnabled(false);
            checkBoxEtCreate.setEnabled(false);
            singleEventOutCheckBox.setEnabled(false);
            etDefaultsMenuItem.setEnabled(false);
        }
    }

    private void update() {
        String SName = link.getSourceComponentName();
        String DName = link.getDestinationComponentName();
        String StName = SName + "_transport";
        String DtName = DName + "_transport";

        JCGComponent sourceComponent = canvas.getGCMPs().get(SName);
        JCGComponent destinationComponent = canvas.getGCMPs().get(DName);

        // get destination component transport
        if (destinationComponent.getTrnsports() != null &&
                !destinationComponent.getTrnsports().isEmpty()) {

            // destination transport
            for (JCGTransport tr : destinationComponent.getTrnsports()) {
                if (tr.getName().equals(DtName)) {
                    destinationTransport = tr;
                    break;
                }
            }

            if (destinationTransport != null) {
                if (destinationComponent.getType().equals(ACodaType.FILE.name())) {
                    destinationTransport.setEtName("undefined");
                    destinationTransport.setEtSubNet("undefined");
                    destinationTransport.setNoLink(false);
                }
            }

            // source transport
            for (JCGTransport tr : sourceComponent.getTrnsports()) {
                if (tr.getName().equals(StName)) {
                    sourceTransport = tr;
                    break;
                }
            }

        }
        if (sourceTransport == null) {
            // default is EmuSocket for the rest
            sourceTransport = new JCGTransport();
            sourceTransport.setName(StName);
            sourceTransport.setNoLink(false);
            sourceComponent.addTrnsport(sourceTransport);
        }

        // define a default transport for the destination component if it is not defined
        if (destinationTransport == null) {
            if (link.getDestinationComponentType().equals(ACodaType.ER.name())) {
                destinationTransport = new JCGTransport();
                destinationTransport.setName(DtName);
                destinationTransport.setTransClass("Et");
                destinationTransport.setEtName("/tmp/et_" + stp.getExpid() + "_" + DName);
                destinationTransport.setEtEventNum((Integer) etNumberEvents.getValue());
                destinationTransport.setEtEventSize((Integer) etEventSize.getValue() * 1000);
                destinationTransport.setEtChunkSize((Integer) etChunkSize.getValue());
                destinationTransport.setInputEtChunkSize((Integer) inputEtChunkSize.getValue());
                destinationTransport.setEtWait((Integer) etWait.getValue());
                if (checkBoxEtCreate.isSelected()) {
                    destinationTransport.setDestinationEtCreate("true");
                } else {
                    destinationTransport.setDestinationEtCreate("false;");
                }
                if (singleEventOutCheckBox.isSelected()) {
                    destinationTransport.setSingle("true");
                } else {
                    destinationTransport.setSingle("false");
                }
                destinationTransport.setNoLink(false);
            } else {
                // default is EmuSocket for the rest
                destinationTransport = new JCGTransport();
                destinationTransport.setName(DtName);
                destinationTransport.setNoLink(false);
            }
        }


        link.setDestinationTransportName(DtName);
        link.setSourceTransportName(StName);

// fill the form
        sourceComponentTextField.setText(SName);
        destinationComponentTextField.setText(DName);

        transportClassComboBox.setSelectedItem(destinationTransport.getTransClass());
        if(destinationTransport.getEtName().equals("undefined")){
            etNameTextField.setText("/tmp/et_"+stp.getExpid()+"_"+DName);
        } else {
            etNameTextField.setText(destinationTransport.getEtName());
        }
        etHostTextField.setText(destinationTransport.getEtHostName());
        etSubnetTextField.setText(destinationTransport.getEtSubNet());
        etTcpPortSpinner.setValue(destinationTransport.getEtTcpPort());
        etUdpPortSpinner.setValue(destinationTransport.getEtUdpPort());
        mAddressTextField.setText(destinationTransport.getmAddress());
        connectionMethodComboBox.setSelectedItem(destinationTransport.getEtMethodCon());
        fileNameTextField.setText(destinationTransport.getFileName());

        etNumberEvents.setValue(destinationTransport.getEtEventNum());
        etEventSize.setValue(destinationTransport.getEtEventSize() / 1000);
        etChunkSize.setValue(destinationTransport.getEtChunkSize());
        inputEtChunkSize.setValue(destinationTransport.getInputEtChunkSize());
        etWait.setValue(destinationTransport.getEtWait());
        if (destinationTransport.getDestinationEtCreate().equals("true")) {
            checkBoxEtCreate.setSelected(true);
        } else {
            checkBoxEtCreate.setSelected(false);
        }
        if (destinationTransport.getSingle().equals("true")) {
            singleEventOutCheckBox.setSelected(true);
        } else {
            singleEventOutCheckBox.setSelected(false);
        }

        if (destinationTransport.getFileSplit() < 0) {
            fileSplitSpinner.setValue(2000);
        } else {
            fileSplitSpinner.setValue((int) (destinationTransport.getFileSplit() / 10000000));
        }

        fileTypeComboBox.setSelectedItem(destinationTransport.getFileType());

        // emuSocket
        emuPortSpinner.setValue(destinationTransport.getEmuDirectPort());
        emuSocketWaitSpinner.setValue(destinationTransport.getEmuWait());
        emuMaxBufferSpinner.setValue(destinationTransport.getEmuMaxBuffer() / 1000);
        emuSubnetTextField.setText(destinationTransport.getEmuSubNet());
        fpgaLinkIpTextField.setText(destinationTransport.getFpgaLinkIp());

        emuFatPipeCheckBox.setSelected(sourceTransport.isEmuFatPipe());

        // UdpStream
        UdpHostTextField.setText(destinationTransport.getUdpHost());
        UdpPortSpinner.setValue(destinationTransport.getUdpPort());
        UdpBufferSizeSpinner.setValue(destinationTransport.getUdpBufferSize() / 1000);
        UdpFpgaLinkIp.setText(destinationTransport.getUdpFpgaLinkIp());
        UdpStreamsSpinner.setValue(destinationTransport.getUdpStreams());
        UdpUseLoadBalancer.setSelected(destinationTransport.isLB());
        UdpUseErsap.setSelected(destinationTransport.isErsap());

        // tcpStream
        tcpStreamsSpinner.setValue(destinationTransport.getEmuTcpStreams());
        tcpStreamPortSpinner.setValue(destinationTransport.getTcpStreamDirectPort());
        tcpStreamSocketWaitSpinner.setValue(destinationTransport.getTcpStreamWait());
        tcpStreamMaxBufferSpinner.setValue(destinationTransport.getTcpStreamMaxBuffer() / 1000);
        tcpStreamSubnetTextField.setText(destinationTransport.getTcpStreamSubNet());
        tcpStreamFpgaLinkIpTextField.setText(destinationTransport.getTcpStreamFpgaLinkIp());

        checkTrClass();
    }

    private void enableEt() {
        etNameTextField.setEnabled(true);
        etNameLabel.setEnabled(true);
        etConnectionMethodLabel.setEnabled(true);
        connectionMethodComboBox.setEnabled(true);
        etCustomizationMenue.setEnabled(true);

        if (connectionMethodComboBox.getSelectedItem().equals("direct")) {
            etTcpPortLabel.setEnabled(true);
            etTcpPortSpinner.setEnabled(true);
            etHostLabel.setEnabled(true);
            etHostTextField.setEnabled(true);
            etSubnetLabel.setEnabled(true);
            etSubnetTextField.setEnabled(true);

            etMAddressLabel.setEnabled(false);
            mAddressTextField.setEnabled(false);
            etUdpPortLabel.setEnabled(false);
            etUdpPortSpinner.setEnabled(false);
        } else {
            etUdpPortLabel.setEnabled(true);
            etUdpPortSpinner.setEnabled(true);
            etMAddressLabel.setEnabled(true);
            mAddressTextField.setEnabled(true);

            etTcpPortLabel.setEnabled(false);
            etTcpPortSpinner.setEnabled(true);
            etHostLabel.setEnabled(true);
            etHostTextField.setEnabled(false);
            etSubnetLabel.setEnabled(true);
            etSubnetTextField.setEnabled(true);
        }
    }

    private void disableEt() {
        etNameTextField.setEnabled(false);
        etNameLabel.setEnabled(false);
        etHostTextField.setEnabled(false);
        etHostLabel.setEnabled(false);
        etSubnetTextField.setEnabled(false);
        etSubnetLabel.setEnabled(false);
        etTcpPortSpinner.setEnabled(false);
        etTcpPortLabel.setEnabled(false);
        etUdpPortSpinner.setEnabled(false);
        etUdpPortLabel.setEnabled(false);
        mAddressTextField.setEnabled(false);
        etMAddressLabel.setEnabled(false);
        connectionMethodComboBox.setEnabled(false);
        etConnectionMethodLabel.setEnabled(false);

        etNumberEvents.setEnabled(false);
        etEventSize.setEnabled(false);
        etChunkSize.setEnabled(false);
        inputEtChunkSize.setEnabled(false);
        etWait.setEnabled(false);
        checkBoxEtCreate.setEnabled(false);
        singleEventOutCheckBox.setEnabled(false);
        etCustomizationMenue.setEnabled(false);

        enableEtCustomization(false);

    }

    private void enableEmu() {
        emuMaxBufferSpinner.setEnabled(true);
        emuSocketWaitSpinner.setEnabled(true);
        emuPortSpinner.setEnabled(true);
        emuSubnetTextField.setEnabled(true);
        fpgaLinkIpTextField.setEnabled(true);
        emuFatPipeCheckBox.setEnabled(true);
    }

    private void disableEmu() {
        emuMaxBufferSpinner.setEnabled(false);
        emuSocketWaitSpinner.setEnabled(false);
        emuPortSpinner.setEnabled(false);
        emuSubnetTextField.setEnabled(false);
        fpgaLinkIpTextField.setEnabled(false);
        emuFatPipeCheckBox.setEnabled(false);
    }

    private void enableTcpStream() {
        tcpStreamMaxBufferSpinner.setEnabled(true);
        tcpStreamSocketWaitSpinner.setEnabled(true);
        tcpStreamPortSpinner.setEnabled(true);
        tcpStreamSubnetTextField.setEnabled(true);
        tcpStreamFpgaLinkIpTextField.setEnabled(true);
        tcpStreamsSpinner.setEnabled(true);

    }

    private void disableTcpStream() {
        tcpStreamMaxBufferSpinner.setEnabled(false);
        tcpStreamSocketWaitSpinner.setEnabled(false);
        tcpStreamPortSpinner.setEnabled(false);
        tcpStreamSubnetTextField.setEnabled(false);
        tcpStreamFpgaLinkIpTextField.setEnabled(false);
        tcpStreamsSpinner.setEnabled(false);

    }

    private void enableUdp() {
        UdpHostTextField.setEnabled(true);
        UdpUseLoadBalancer.setEnabled(true);
        UdpUseErsap.setEnabled(true);
        UdpPortSpinner.setEnabled(true);
        UdpBufferSizeSpinner.setEnabled(true);
        UdpFpgaLinkIp.setEnabled(true);
        UdpStreamsSpinner.setEnabled(true);

    }

    private void disableUdp() {
        UdpHostTextField.setEnabled(false);
        UdpUseLoadBalancer.setEnabled(false);
        UdpUseErsap.setEnabled(false);
        UdpPortSpinner.setEnabled(false);
        UdpBufferSizeSpinner.setEnabled(false);
        UdpFpgaLinkIp.setEnabled(false);
        UdpStreamsSpinner.setEnabled(false);

    }

    private void enableFile() {
        fileNameTextField.setEnabled(true);
        fileNameLabel.setEnabled(true);
        fileSplitSpinner.setEnabled(true);
        fileSplitLabel.setEnabled(true);
        fileTypeComboBox.setEnabled(true);
        FileTypeLabel.setEnabled(true);

    }

    private void disableFile() {
        fileNameTextField.setEnabled(false);
        fileNameLabel.setEnabled(false);
        fileSplitSpinner.setEnabled(false);
        fileSplitLabel.setEnabled(false);
        fileTypeComboBox.setEnabled(false);
        FileTypeLabel.setEnabled(false);

    }

    public void checkTrClass() {
        // control access to the form
        if (transportClassComboBox.getSelectedItem().equals("Et")) {
            enableEt();

            disableEmu();
            disableUdp();
            disableFile();
            disableTcpStream();

        } else if (transportClassComboBox.getSelectedItem().equals("File")) {
            enableFile();

            disableEt();
            disableEmu();
            disableUdp();
            disableTcpStream();

        } else if (transportClassComboBox.getSelectedItem().equals("EmuSocket")) {
            enableEmu();

            disableEt();
            disableUdp();
            disableFile();
            disableTcpStream();

        } else if (transportClassComboBox.getSelectedItem().equals("EmuSocket+Et")) {
            enableEmu();
            enableEt();

            disableUdp();
            disableFile();
            disableTcpStream();

        } else if (transportClassComboBox.getSelectedItem().equals("UdpStream")) {
            enableUdp();

            disableEt();
            disableEmu();
            disableFile();
            disableTcpStream();

        } else if (transportClassComboBox.getSelectedItem().equals("TcpStream")) {
            enableTcpStream();

            disableUdp();
            disableEt();
            disableEmu();
            disableFile();

        } else if (transportClassComboBox.getSelectedItem().equals("None") ||
                transportClassComboBox.getSelectedItem().equals("Debug")) {
            disableEt();
            disableEmu();
            disableUdp();
            disableFile();
            disableTcpStream();
        }
    }

    private void checkBoxEtCreateMouseClicked(MouseEvent e) {
        // TODO add your code here
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        menuBar2 = new JMenuBar();
        etCustomizationMenue = new JMenu();
        menuItem1 = new JMenuItem();
        menuItem3 = new JMenuItem();
        etDefaultsMenuItem = new JMenuItem();
        panel1 = new JPanel();
        label2 = new JLabel();
        transportClassComboBox = new JComboBox();
        label1 = new JLabel();
        sourceComponentTextField = new JTextField();
        label3 = new JLabel();
        destinationComponentTextField = new JTextField();
        singleEventOutCheckBox = new JCheckBox();
        panel2 = new JPanel();
        etNameLabel = new JLabel();
        etNameTextField = new JTextField();
        etHostLabel = new JLabel();
        etHostTextField = new JTextField();
        etTcpPortLabel = new JLabel();
        etUdpPortLabel = new JLabel();
        etConnectionMethodLabel = new JLabel();
        connectionMethodComboBox = new JComboBox<>();
        etMAddressLabel = new JLabel();
        mAddressTextField = new JTextField();
        etTcpPortSpinner = new JSpinner();
        etUdpPortSpinner = new JSpinner();
        label4 = new JLabel();
        label5 = new JLabel();
        etNumberEvents = new JSpinner();
        label6 = new JLabel();
        etEventSize = new JSpinner();
        etChunkSize = new JSpinner();
        label9 = new JLabel();
        etWait = new JSpinner();
        etSubnetLabel = new JLabel();
        etSubnetTextField = new JTextField();
        label19 = new JLabel();
        inputEtChunkSize = new JSpinner();
        label20 = new JLabel();
        checkBoxEtCreate = new JCheckBox();
        panel3 = new JPanel();
        fileNameLabel = new JLabel();
        fileNameTextField = new JTextField();
        fileSplitLabel = new JLabel();
        fileSplitSpinner = new JSpinner();
        FileTypeLabel = new JLabel();
        fileTypeComboBox = new JComboBox<>();
        cancelButton = new JButton();
        clearButton = new JButton();
        okButton = new JButton();
        removeButton = new JButton();
        panel4 = new JPanel();
        etTcpPortLabel2 = new JLabel();
        label7 = new JLabel();
        emuMaxBufferSpinner = new JSpinner();
        label10 = new JLabel();
        emuSocketWaitSpinner = new JSpinner();
        emuPortSpinner = new JSpinner();
        label17 = new JLabel();
        emuSubnetTextField = new JTextField();
        emuFatPipeCheckBox = new JCheckBox();
        label18 = new JLabel();
        fpgaLinkIpTextField = new JTextField();
        panel5 = new JPanel();
        label11 = new JLabel();
        UdpHostTextField = new JTextField();
        label12 = new JLabel();
        label13 = new JLabel();
        UdpPortSpinner = new JSpinner();
        label14 = new JLabel();
        label15 = new JLabel();
        UdpFpgaLinkIp = new JTextField();
        UdpBufferSizeSpinner = new JSpinner();
        UdpUseLoadBalancer = new JCheckBox();
        UdpUseErsap = new JCheckBox();
        UdpStreamsSpinner = new JSpinner();
        label16 = new JLabel();
        panel6 = new JPanel();
        etTcpPortLabel3 = new JLabel();
        label8 = new JLabel();
        tcpStreamMaxBufferSpinner = new JSpinner();
        label22 = new JLabel();
        tcpStreamSocketWaitSpinner = new JSpinner();
        tcpStreamPortSpinner = new JSpinner();
        label23 = new JLabel();
        tcpStreamSubnetTextField = new JTextField();
        label24 = new JLabel();
        tcpStreamFpgaLinkIpTextField = new JTextField();
        tcpStreamsSpinner = new JSpinner();
        label25 = new JLabel();
        action1 = new CancelAction();
        action2 = new ClearAction();
        action3 = new OkAction();
        action4 = new RemoveAction();
        action5 = new TransportClassChanged();
        action8 = new DirectMcastAction();
        action6 = new EtCustomizeAction();
        action7 = new EtDefaultAction();
        action9 = new EtCustomizationDisableAction();

        //======== this ========
        setTitle("Link");
        setAutoRequestFocus(false);
        Container contentPane = getContentPane();

        //======== menuBar2 ========
        {

            //======== etCustomizationMenue ========
            {
                etCustomizationMenue.setText("ET Customization");

                //---- menuItem1 ----
                menuItem1.setText("Enable");
                menuItem1.setAction(action6);
                etCustomizationMenue.add(menuItem1);
                etCustomizationMenue.addSeparator();

                //---- menuItem3 ----
                menuItem3.setAction(action9);
                etCustomizationMenue.add(menuItem3);
                etCustomizationMenue.addSeparator();

                //---- etDefaultsMenuItem ----
                etDefaultsMenuItem.setAction(action7);
                etDefaultsMenuItem.setEnabled(false);
                etCustomizationMenue.add(etDefaultsMenuItem);
            }
            menuBar2.add(etCustomizationMenue);
        }
        setJMenuBar(menuBar2);

        //======== panel1 ========
        {
            panel1.setBorder(new TitledBorder("General"));

            //---- label2 ----
            label2.setText("Class");

            //---- transportClassComboBox ----
            transportClassComboBox.setAction(action5);
            transportClassComboBox.setModel(comboModel);

            //---- label1 ----
            label1.setText("Source");

            //---- sourceComponentTextField ----
            sourceComponentTextField.setEditable(false);

            //---- label3 ----
            label3.setText("Destination");

            //---- destinationComponentTextField ----
            destinationComponentTextField.setEditable(false);

            //---- singleEventOutCheckBox ----
            singleEventOutCheckBox.setText("SingleEventOut");
            singleEventOutCheckBox.setEnabled(false);

            GroupLayout panel1Layout = new GroupLayout(panel1);
            panel1.setLayout(panel1Layout);
            panel1Layout.setHorizontalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panel1Layout.createParallelGroup()
                            .addComponent(label3)
                            .addComponent(label1))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addComponent(sourceComponentTextField, GroupLayout.PREFERRED_SIZE, 256, GroupLayout.PREFERRED_SIZE)
                            .addComponent(destinationComponentTextField, GroupLayout.PREFERRED_SIZE, 256, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(label2)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                            .addComponent(singleEventOutCheckBox, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(transportClassComboBox))
                        .addGap(30, 30, 30))
            );
            panel1Layout.setVerticalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGroup(panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(label2))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(label1)
                                    .addComponent(sourceComponentTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(label3)
                                    .addComponent(destinationComponentTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(transportClassComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(singleEventOutCheckBox)))
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
        }

        //======== panel2 ========
        {
            panel2.setBorder(new TitledBorder("ET"));

            //---- etNameLabel ----
            etNameLabel.setText("ET Name");

            //---- etNameTextField ----
            etNameTextField.setEnabled(false);

            //---- etHostLabel ----
            etHostLabel.setText("Host");

            //---- etTcpPortLabel ----
            etTcpPortLabel.setText("TCP Port");

            //---- etUdpPortLabel ----
            etUdpPortLabel.setText("UDP Port");

            //---- etConnectionMethodLabel ----
            etConnectionMethodLabel.setText("Method");

            //---- connectionMethodComboBox ----
            connectionMethodComboBox.setModel(new DefaultComboBoxModel<>(new String[] {
                "direct",
                "mcast"
            }));
            connectionMethodComboBox.setAction(action8);

            //---- etMAddressLabel ----
            etMAddressLabel.setText("mAddress");

            //---- mAddressTextField ----
            mAddressTextField.setText("239.200.0.0");

            //---- etTcpPortSpinner ----
            etTcpPortSpinner.setModel(new SpinnerNumberModel(23911, 1, 99999, 1));

            //---- etUdpPortSpinner ----
            etUdpPortSpinner.setModel(new SpinnerNumberModel(23912, 1, 99999, 1));

            //---- label4 ----
            label4.setText("( IP address )");
            label4.setEnabled(false);

            //---- label5 ----
            label5.setText("NEvents");

            //---- etNumberEvents ----
            etNumberEvents.setModel(new SpinnerNumberModel(1, 1, 9999, 1));
            etNumberEvents.setEnabled(false);

            //---- label6 ----
            label6.setText("EventSize (KByte)");

            //---- etEventSize ----
            etEventSize.setModel(new SpinnerNumberModel(4200, 1, 100000, 1));
            etEventSize.setEnabled(false);

            //---- etChunkSize ----
            etChunkSize.setModel(new SpinnerNumberModel(2, 1, 9999, 1));
            etChunkSize.setEnabled(false);

            //---- label9 ----
            label9.setText("Wait");

            //---- etWait ----
            etWait.setModel(new SpinnerNumberModel(0, 0, 30, 1));
            etWait.setEnabled(false);

            //---- etSubnetLabel ----
            etSubnetLabel.setText("Subnet");

            //---- label19 ----
            label19.setText("InChunkSize");

            //---- inputEtChunkSize ----
            inputEtChunkSize.setEnabled(false);
            inputEtChunkSize.setModel(new SpinnerNumberModel(2, 1, 9999, 1));

            //---- label20 ----
            label20.setText("OutChunkSize");

            //---- checkBoxEtCreate ----
            checkBoxEtCreate.setText("Create");
            checkBoxEtCreate.setSelected(true);
            checkBoxEtCreate.setEnabled(false);
            checkBoxEtCreate.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    checkBoxEtCreateMouseClicked(e);
                }
            });

            GroupLayout panel2Layout = new GroupLayout(panel2);
            panel2.setLayout(panel2Layout);
            panel2Layout.setHorizontalGroup(
                panel2Layout.createParallelGroup()
                    .addGroup(panel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panel2Layout.createParallelGroup()
                            .addGroup(panel2Layout.createSequentialGroup()
                                .addGroup(panel2Layout.createParallelGroup()
                                    .addGroup(panel2Layout.createSequentialGroup()
                                        .addGroup(panel2Layout.createParallelGroup()
                                            .addComponent(etTcpPortLabel)
                                            .addComponent(etHostLabel))
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(panel2Layout.createParallelGroup()
                                            .addGroup(panel2Layout.createSequentialGroup()
                                                .addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                                    .addComponent(etHostTextField, GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                                                    .addComponent(etNameTextField, GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(panel2Layout.createParallelGroup()
                                                    .addGroup(panel2Layout.createSequentialGroup()
                                                        .addGap(0, 0, Short.MAX_VALUE)
                                                        .addComponent(etConnectionMethodLabel)
                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(connectionMethodComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                                    .addGroup(panel2Layout.createSequentialGroup()
                                                        .addComponent(label4)
                                                        .addGap(18, 18, 18)
                                                        .addComponent(etSubnetLabel)
                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(etSubnetTextField))))
                                            .addGroup(panel2Layout.createSequentialGroup()
                                                .addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                    .addGroup(panel2Layout.createSequentialGroup()
                                                        .addComponent(label6)
                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(etEventSize, GroupLayout.PREFERRED_SIZE, 93, GroupLayout.PREFERRED_SIZE))
                                                    .addGroup(GroupLayout.Alignment.LEADING, panel2Layout.createSequentialGroup()
                                                        .addComponent(etTcpPortSpinner, GroupLayout.PREFERRED_SIZE, 76, GroupLayout.PREFERRED_SIZE)
                                                        .addGap(34, 34, 34)
                                                        .addComponent(etUdpPortLabel)
                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(etUdpPortSpinner, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                        .addComponent(etMAddressLabel)))
                                                .addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                                    .addGroup(panel2Layout.createSequentialGroup()
                                                        .addGap(46, 46, 46)
                                                        .addComponent(mAddressTextField, GroupLayout.PREFERRED_SIZE, 124, GroupLayout.PREFERRED_SIZE))
                                                    .addGroup(panel2Layout.createSequentialGroup()
                                                        .addGap(18, 18, 18)
                                                        .addComponent(checkBoxEtCreate)
                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(label9)))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(etWait, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
                                    .addComponent(etNameLabel))
                                .addContainerGap())
                            .addGroup(panel2Layout.createSequentialGroup()
                                .addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                    .addGroup(panel2Layout.createSequentialGroup()
                                        .addComponent(label5)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(etNumberEvents, GroupLayout.PREFERRED_SIZE, 78, GroupLayout.PREFERRED_SIZE)
                                        .addGap(202, 202, 202))
                                    .addGroup(panel2Layout.createSequentialGroup()
                                        .addComponent(label19)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(inputEtChunkSize, GroupLayout.PREFERRED_SIZE, 71, GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(label20)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(etChunkSize, GroupLayout.PREFERRED_SIZE, 69, GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE))))
            );
            panel2Layout.setVerticalGroup(
                panel2Layout.createParallelGroup()
                    .addGroup(panel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(etNameLabel)
                            .addComponent(etNameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(connectionMethodComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(etConnectionMethodLabel))
                        .addGap(18, 18, 18)
                        .addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(etHostTextField)
                            .addComponent(etHostLabel)
                            .addComponent(label4)
                            .addComponent(etSubnetLabel)
                            .addComponent(etSubnetTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(etTcpPortLabel)
                            .addComponent(mAddressTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(etMAddressLabel)
                            .addComponent(etUdpPortLabel)
                            .addComponent(etUdpPortSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(etTcpPortSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addGap(4, 4, 4)
                        .addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(label5)
                            .addComponent(etEventSize, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(label6)
                            .addComponent(etNumberEvents, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(etWait, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(label9)
                            .addComponent(checkBoxEtCreate))
                        .addGap(8, 8, 8)
                        .addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(label19)
                            .addComponent(inputEtChunkSize, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(label20)
                            .addComponent(etChunkSize, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())
            );
        }

        //======== panel3 ========
        {
            panel3.setBorder(new TitledBorder("File"));

            //---- fileNameLabel ----
            fileNameLabel.setText("Name");

            //---- fileSplitLabel ----
            fileSplitLabel.setText("Split x 10MByte");

            //---- fileSplitSpinner ----
            fileSplitSpinner.setModel(new SpinnerNumberModel(2000, 0, 1000000, 1));

            //---- FileTypeLabel ----
            FileTypeLabel.setText("Type");

            //---- fileTypeComboBox ----
            fileTypeComboBox.setModel(new DefaultComboBoxModel<>(new String[] {
                "coda",
                "binary",
                "ascii"
            }));

            GroupLayout panel3Layout = new GroupLayout(panel3);
            panel3.setLayout(panel3Layout);
            panel3Layout.setHorizontalGroup(
                panel3Layout.createParallelGroup()
                    .addGroup(panel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addComponent(fileNameLabel)
                            .addComponent(FileTypeLabel))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel3Layout.createParallelGroup()
                            .addGroup(GroupLayout.Alignment.TRAILING, panel3Layout.createSequentialGroup()
                                .addComponent(fileTypeComboBox, GroupLayout.PREFERRED_SIZE, 91, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(fileSplitLabel)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(fileSplitSpinner, GroupLayout.PREFERRED_SIZE, 106, GroupLayout.PREFERRED_SIZE))
                            .addComponent(fileNameTextField))
                        .addContainerGap())
            );
            panel3Layout.setVerticalGroup(
                panel3Layout.createParallelGroup()
                    .addGroup(panel3Layout.createSequentialGroup()
                        .addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(fileNameLabel)
                            .addComponent(fileNameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addGroup(panel3Layout.createParallelGroup()
                            .addGroup(panel3Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(fileSplitSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(fileSplitLabel)))
                            .addGroup(panel3Layout.createSequentialGroup()
                                .addGap(8, 8, 8)
                                .addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(fileTypeComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(FileTypeLabel)))))
            );
        }

        //---- cancelButton ----
        cancelButton.setAction(action1);

        //---- clearButton ----
        clearButton.setAction(action2);

        //---- okButton ----
        okButton.setAction(action3);
        okButton.setText("Ok");

        //---- removeButton ----
        removeButton.setAction(action4);

        //======== panel4 ========
        {
            panel4.setBorder(new TitledBorder("EmuSocket"));

            //---- etTcpPortLabel2 ----
            etTcpPortLabel2.setText("Port");

            //---- label7 ----
            label7.setText("Max Buffer (KB)");

            //---- emuMaxBufferSpinner ----
            emuMaxBufferSpinner.setModel(new SpinnerNumberModel(4100, 1, 30000, 1));

            //---- label10 ----
            label10.setText("Wait");

            //---- emuSocketWaitSpinner ----
            emuSocketWaitSpinner.setModel(new SpinnerNumberModel(5, 0, 30, 1));

            //---- emuPortSpinner ----
            emuPortSpinner.setModel(new SpinnerNumberModel(46000, 1, 99999, 1));

            //---- label17 ----
            label17.setText("Subnet");

            //---- emuFatPipeCheckBox ----
            emuFatPipeCheckBox.setText("FatPipe");

            //---- label18 ----
            label18.setText("FPGA Link IP");

            GroupLayout panel4Layout = new GroupLayout(panel4);
            panel4.setLayout(panel4Layout);
            panel4Layout.setHorizontalGroup(
                panel4Layout.createParallelGroup()
                    .addGroup(panel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(etTcpPortLabel2)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                            .addGroup(panel4Layout.createSequentialGroup()
                                .addComponent(emuPortSpinner, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label7))
                            .addGroup(panel4Layout.createSequentialGroup()
                                .addComponent(emuFatPipeCheckBox)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(label18)))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                        .addGroup(panel4Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addGroup(panel4Layout.createSequentialGroup()
                                .addComponent(fpgaLinkIpTextField, GroupLayout.PREFERRED_SIZE, 134, GroupLayout.PREFERRED_SIZE)
                                .addGap(287, 287, 287))
                            .addGroup(GroupLayout.Alignment.LEADING, panel4Layout.createSequentialGroup()
                                .addComponent(emuMaxBufferSpinner, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label10)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emuSocketWaitSpinner, GroupLayout.PREFERRED_SIZE, 66, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label17)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emuSubnetTextField, GroupLayout.PREFERRED_SIZE, 175, GroupLayout.PREFERRED_SIZE))))
            );
            panel4Layout.setVerticalGroup(
                panel4Layout.createParallelGroup()
                    .addGroup(panel4Layout.createSequentialGroup()
                        .addGroup(panel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(etTcpPortLabel2)
                            .addComponent(label7)
                            .addComponent(emuMaxBufferSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(emuPortSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(label10)
                            .addComponent(emuSocketWaitSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(label17)
                            .addComponent(emuSubnetTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(emuFatPipeCheckBox)
                            .addComponent(fpgaLinkIpTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(label18))
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
        }

        //======== panel5 ========
        {
            panel5.setBorder(new TitledBorder("UdpStream"));

            //---- label11 ----
            label11.setText("Host");

            //---- label12 ----
            label12.setText("( IP address )");
            label12.setEnabled(false);

            //---- label13 ----
            label13.setText("Port");

            //---- UdpPortSpinner ----
            UdpPortSpinner.setModel(new SpinnerNumberModel(46100, 1, 99999, 1));

            //---- label14 ----
            label14.setText("BufferSize (KB)");

            //---- label15 ----
            label15.setText("FPGA Link Ip");

            //---- UdpBufferSizeSpinner ----
            UdpBufferSizeSpinner.setModel(new SpinnerNumberModel(100, 1, 3000, 1));

            //---- UdpUseLoadBalancer ----
            UdpUseLoadBalancer.setText("LoadBalancer");
            UdpUseLoadBalancer.setSelected(true);
            UdpUseLoadBalancer.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    checkBoxEtCreateMouseClicked(e);
                }
            });

            //---- UdpUseErsap ----
            UdpUseErsap.setText("ERSAP");
            UdpUseErsap.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    checkBoxEtCreateMouseClicked(e);
                }
            });

            //---- UdpStreamsSpinner ----
            UdpStreamsSpinner.setModel(new SpinnerNumberModel(1, 1, 16, 1));

            //---- label16 ----
            label16.setText("Streams");

            GroupLayout panel5Layout = new GroupLayout(panel5);
            panel5.setLayout(panel5Layout);
            panel5Layout.setHorizontalGroup(
                panel5Layout.createParallelGroup()
                    .addGroup(panel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panel5Layout.createParallelGroup()
                            .addGroup(panel5Layout.createSequentialGroup()
                                .addComponent(label15)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(UdpFpgaLinkIp, GroupLayout.PREFERRED_SIZE, 135, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                                .addComponent(label16)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(UdpStreamsSpinner, GroupLayout.PREFERRED_SIZE, 93, GroupLayout.PREFERRED_SIZE)
                                .addGap(59, 59, 59))
                            .addGroup(panel5Layout.createSequentialGroup()
                                .addComponent(label11)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(UdpHostTextField, GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label12)
                                .addGap(41, 41, 41)
                                .addComponent(label13)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(UdpPortSpinner, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)))
                        .addGroup(panel5Layout.createParallelGroup()
                            .addGroup(GroupLayout.Alignment.TRAILING, panel5Layout.createSequentialGroup()
                                .addComponent(UdpUseLoadBalancer)
                                .addGap(18, 18, 18)
                                .addComponent(UdpUseErsap))
                            .addGroup(GroupLayout.Alignment.TRAILING, panel5Layout.createSequentialGroup()
                                .addComponent(label14)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(UdpBufferSizeSpinner, GroupLayout.PREFERRED_SIZE, 93, GroupLayout.PREFERRED_SIZE)))
                        .addGap(34, 34, 34))
            );
            panel5Layout.setVerticalGroup(
                panel5Layout.createParallelGroup()
                    .addGroup(panel5Layout.createSequentialGroup()
                        .addGroup(panel5Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(label11)
                            .addComponent(UdpBufferSizeSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(label14)
                            .addComponent(UdpPortSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(label13)
                            .addComponent(label12)
                            .addComponent(UdpHostTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panel5Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(label15)
                            .addComponent(UdpUseErsap)
                            .addComponent(UdpUseLoadBalancer)
                            .addComponent(UdpStreamsSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(label16)
                            .addComponent(UdpFpgaLinkIp, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 13, Short.MAX_VALUE))
            );
        }

        //======== panel6 ========
        {
            panel6.setBorder(new TitledBorder("TcpStream"));

            //---- etTcpPortLabel3 ----
            etTcpPortLabel3.setText("Port");

            //---- label8 ----
            label8.setText("Max Buffer (KB)");

            //---- tcpStreamMaxBufferSpinner ----
            tcpStreamMaxBufferSpinner.setModel(new SpinnerNumberModel(4100, 1, 30000, 1));

            //---- label22 ----
            label22.setText("Wait");

            //---- tcpStreamSocketWaitSpinner ----
            tcpStreamSocketWaitSpinner.setModel(new SpinnerNumberModel(5, 0, 30, 1));

            //---- tcpStreamPortSpinner ----
            tcpStreamPortSpinner.setModel(new SpinnerNumberModel(46100, 1, 99999, 1));

            //---- label23 ----
            label23.setText("Subnet");

            //---- label24 ----
            label24.setText("FPGA Link IP");

            //---- tcpStreamsSpinner ----
            tcpStreamsSpinner.setModel(new SpinnerNumberModel(1, 1, 2, 1));

            //---- label25 ----
            label25.setText("Streams");

            GroupLayout panel6Layout = new GroupLayout(panel6);
            panel6.setLayout(panel6Layout);
            panel6Layout.setHorizontalGroup(
                panel6Layout.createParallelGroup()
                    .addGroup(panel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panel6Layout.createParallelGroup()
                            .addGroup(panel6Layout.createSequentialGroup()
                                .addComponent(etTcpPortLabel3)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tcpStreamPortSpinner, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                                .addComponent(label8))
                            .addGroup(panel6Layout.createSequentialGroup()
                                .addComponent(label24)
                                .addGap(18, 18, 18)
                                .addComponent(tcpStreamFpgaLinkIpTextField, GroupLayout.PREFERRED_SIZE, 134, GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel6Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addGroup(panel6Layout.createSequentialGroup()
                                .addComponent(label25)
                                .addGap(12, 12, 12)
                                .addComponent(tcpStreamsSpinner, GroupLayout.PREFERRED_SIZE, 93, GroupLayout.PREFERRED_SIZE)
                                .addGap(38, 38, 38))
                            .addGroup(GroupLayout.Alignment.LEADING, panel6Layout.createSequentialGroup()
                                .addComponent(tcpStreamMaxBufferSpinner, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label22)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tcpStreamSocketWaitSpinner, GroupLayout.PREFERRED_SIZE, 66, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label23)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tcpStreamSubnetTextField, GroupLayout.PREFERRED_SIZE, 175, GroupLayout.PREFERRED_SIZE))))
            );
            panel6Layout.setVerticalGroup(
                panel6Layout.createParallelGroup()
                    .addGroup(panel6Layout.createSequentialGroup()
                        .addGroup(panel6Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(etTcpPortLabel3)
                            .addComponent(tcpStreamMaxBufferSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(tcpStreamPortSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(label22)
                            .addComponent(tcpStreamSocketWaitSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(label23)
                            .addComponent(tcpStreamSubnetTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(label8))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel6Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(tcpStreamsSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(label25)
                            .addComponent(label24)
                            .addComponent(tcpStreamFpgaLinkIpTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addComponent(panel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                            .addGap(0, 0, Short.MAX_VALUE)
                            .addComponent(okButton)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(removeButton)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(clearButton)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cancelButton))
                        .addComponent(panel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(panel4, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(panel6, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(panel5, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(panel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(panel1, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(panel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(panel4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(panel6, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(panel5, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(panel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(cancelButton)
                        .addComponent(clearButton)
                        .addComponent(removeButton)
                        .addComponent(okButton))
                    .addContainerGap())
        );
        pack();
        setLocationRelativeTo(getOwner());
// JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    private void showWarning(String s) {
        JOptionPane.showMessageDialog(this, s);
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JMenuBar menuBar2;
    private JMenu etCustomizationMenue;
    private JMenuItem menuItem1;
    private JMenuItem menuItem3;
    private JMenuItem etDefaultsMenuItem;
    private JPanel panel1;
    private JLabel label2;
    private JComboBox transportClassComboBox;
    private JLabel label1;
    private JTextField sourceComponentTextField;
    private JLabel label3;
    private JTextField destinationComponentTextField;
    private JCheckBox singleEventOutCheckBox;
    private JPanel panel2;
    private JLabel etNameLabel;
    private JTextField etNameTextField;
    private JLabel etHostLabel;
    private JTextField etHostTextField;
    private JLabel etTcpPortLabel;
    private JLabel etUdpPortLabel;
    private JLabel etConnectionMethodLabel;
    private JComboBox<String> connectionMethodComboBox;
    private JLabel etMAddressLabel;
    private JTextField mAddressTextField;
    private JSpinner etTcpPortSpinner;
    private JSpinner etUdpPortSpinner;
    private JLabel label4;
    private JLabel label5;
    private JSpinner etNumberEvents;
    private JLabel label6;
    private JSpinner etEventSize;
    private JSpinner etChunkSize;
    private JLabel label9;
    private JSpinner etWait;
    private JLabel etSubnetLabel;
    private JTextField etSubnetTextField;
    private JLabel label19;
    private JSpinner inputEtChunkSize;
    private JLabel label20;
    private JCheckBox checkBoxEtCreate;
    private JPanel panel3;
    private JLabel fileNameLabel;
    private JTextField fileNameTextField;
    private JLabel fileSplitLabel;
    private JSpinner fileSplitSpinner;
    private JLabel FileTypeLabel;
    private JComboBox<String> fileTypeComboBox;
    private JButton cancelButton;
    private JButton clearButton;
    private JButton okButton;
    private JButton removeButton;
    private JPanel panel4;
    private JLabel etTcpPortLabel2;
    private JLabel label7;
    private JSpinner emuMaxBufferSpinner;
    private JLabel label10;
    private JSpinner emuSocketWaitSpinner;
    private JSpinner emuPortSpinner;
    private JLabel label17;
    private JTextField emuSubnetTextField;
    private JCheckBox emuFatPipeCheckBox;
    private JLabel label18;
    private JTextField fpgaLinkIpTextField;
    private JPanel panel5;
    private JLabel label11;
    private JTextField UdpHostTextField;
    private JLabel label12;
    private JLabel label13;
    private JSpinner UdpPortSpinner;
    private JLabel label14;
    private JLabel label15;
    private JTextField UdpFpgaLinkIp;
    private JSpinner UdpBufferSizeSpinner;
    private JCheckBox UdpUseLoadBalancer;
    private JCheckBox UdpUseErsap;
    private JSpinner UdpStreamsSpinner;
    private JLabel label16;
    private JPanel panel6;
    private JLabel etTcpPortLabel3;
    private JLabel label8;
    private JSpinner tcpStreamMaxBufferSpinner;
    private JLabel label22;
    private JSpinner tcpStreamSocketWaitSpinner;
    private JSpinner tcpStreamPortSpinner;
    private JLabel label23;
    private JTextField tcpStreamSubnetTextField;
    private JLabel label24;
    private JTextField tcpStreamFpgaLinkIpTextField;
    private JSpinner tcpStreamsSpinner;
    private JLabel label25;
    private CancelAction action1;
    private ClearAction action2;
    private OkAction action3;
    private RemoveAction action4;
    private TransportClassChanged action5;
    private DirectMcastAction action8;
    private EtCustomizeAction action6;
    private EtDefaultAction action7;
    private EtCustomizationDisableAction action9;
// JFormDesigner - End of variables declaration  //GEN-END:variables

    private class DirectMcastAction extends AbstractAction {
        private DirectMcastAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            putValue(NAME, "dm");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            if (connectionMethodComboBox.getSelectedItem().equals("direct")) {
                etMAddressLabel.setEnabled(false);
                mAddressTextField.setEnabled(false);
                etUdpPortLabel.setEnabled(false);
                etUdpPortSpinner.setEnabled(false);
                etTcpPortLabel.setEnabled(true);
                etTcpPortSpinner.setEnabled(true);
                etHostLabel.setEnabled(true);
                etHostTextField.setEnabled(true);
                etSubnetLabel.setEnabled(true);
                etSubnetTextField.setEnabled(true);
            } else {
                etTcpPortLabel.setEnabled(false);
                etTcpPortSpinner.setEnabled(false);
                etUdpPortLabel.setEnabled(true);
                etUdpPortSpinner.setEnabled(true);
                etMAddressLabel.setEnabled(true);
                mAddressTextField.setEnabled(true);
                etHostLabel.setEnabled(false);
                etHostTextField.setEnabled(false);
                etHostTextField.setText("anywhere");
                etSubnetLabel.setEnabled(true);
                etSubnetTextField.setEnabled(true);
            }
        }
    }


    private class TransportClassChanged extends AbstractAction {
        private TransportClassChanged() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            putValue(NAME, "transport");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            checkTrClass();
        }
    }

    private class RemoveAction extends AbstractAction {
        private RemoveAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            putValue(NAME, "Remove");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            JCGComponent s = canvas.getGCMPs().get(link.getSourceComponentName());
            JCGComponent d = canvas.getGCMPs().get(link.getDestinationComponentName());
            for (JCGLink l : s.getLnks()) {
                if (l.getName().equals(link.getName())) {
                    s.removeLnk(l);
                    break;
                }
            }
            for (JCGLink ll : d.getLnks()) {
                if (ll.getName().equals(link.getName())) {
                    d.removeLnk(ll);
                    break;
                }
            }
            canvas.getGCMPs().put(s.getName(), s);
            canvas.getGCMPs().put(d.getName(), d);
            canvas.repaint();
            dispose();
        }
    }

    private class OkAction extends AbstractAction {
        private OkAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            putValue(NAME, "Ok");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {

            if (etHostTextField.isEnabled()) {
                if (!JCUtil.IP_validate(etHostTextField.getText())) {
                    showWarning("Host name must be a valid IP address (e.g. 129.57.29.62)");
                    return;
                }
            }

            // fill and add/update the transport
            destinationTransport.setTransClass(transportClassComboBox.getSelectedItem().toString());
            destinationTransport.setEtName(etNameTextField.getText().trim());

            destinationTransport.setEtHostName(etHostTextField.getText().trim());
            destinationTransport.setEtSubNet(etSubnetTextField.getText().trim());
            destinationTransport.setEtTcpPort((Integer) etTcpPortSpinner.getValue());
            destinationTransport.setEtUdpPort((Integer) etUdpPortSpinner.getValue());
            destinationTransport.setmAddress(mAddressTextField.getText().trim());
            destinationTransport.setEtMethodCon(connectionMethodComboBox.getSelectedItem().toString());
            destinationTransport.setEtEventNum((Integer) etNumberEvents.getValue());
            destinationTransport.setEtEventSize((Integer) etEventSize.getValue() * 1000);
            destinationTransport.setEtChunkSize((Integer) etChunkSize.getValue());
            destinationTransport.setInputEtChunkSize((Integer) inputEtChunkSize.getValue());
            destinationTransport.setEtWait((Integer) etWait.getValue());

            if (checkBoxEtCreate.isSelected()) {
                destinationTransport.setDestinationEtCreate("true");
            } else {
                destinationTransport.setDestinationEtCreate("false");
            }

            if (singleEventOutCheckBox.isSelected()) {
                destinationTransport.setSingle("true");
            } else {
                destinationTransport.setSingle("false");
            }

            destinationTransport.setFileName(fileNameTextField.getText().trim());

            int d = (Integer) fileSplitSpinner.getValue();
            destinationTransport.setFileSplit(d * 10000000L);
            destinationTransport.setFileType((String) fileTypeComboBox.getSelectedItem());

            //emu
            destinationTransport.setEmuDirectPort((int) emuPortSpinner.getValue());
            destinationTransport.setEmuMaxBuffer((int) emuMaxBufferSpinner.getValue() * 1000);
            destinationTransport.setEmuWait((int) emuSocketWaitSpinner.getValue());
            destinationTransport.setEmuSubNet(emuSubnetTextField.getText());
            destinationTransport.setFpgaLinkIp(fpgaLinkIpTextField.getText());

            sourceTransport.setEmuFatPipe(emuFatPipeCheckBox.isSelected());

            //TcpStream
            destinationTransport.setTcpStreamDirectPort((int) tcpStreamPortSpinner.getValue());
            destinationTransport.setTcpStreamMaxBuffer((int) tcpStreamMaxBufferSpinner.getValue() * 1000);
            destinationTransport.setTcpStreamWait((int) tcpStreamSocketWaitSpinner.getValue());
            destinationTransport.setTcpStreamSubNet(tcpStreamSubnetTextField.getText());
            destinationTransport.setTcpStreamFpgaLinkIp(tcpStreamFpgaLinkIpTextField.getText());
            destinationTransport.setEmuTcpStreams((int)tcpStreamsSpinner.getValue());
            destinationTransport.setTcpStreamFpgaLinkIp(tcpStreamFpgaLinkIpTextField.getText());

            //Udp
            destinationTransport.setUdpHost(UdpHostTextField.getText());
            destinationTransport.setUdpPort((int) UdpPortSpinner.getValue());
            destinationTransport.setUdpBufferSize((int)UdpBufferSizeSpinner.getValue() * 1000);
            destinationTransport.setUdpStreams((int)UdpStreamsSpinner.getValue());
            destinationTransport.setLB(UdpUseLoadBalancer.isSelected());
            destinationTransport.setErsap(UdpUseErsap.isSelected());
            destinationTransport.setUdpFpgaLinkIp(UdpFpgaLinkIp.getText());

            // update subtype of the destination component if it is of type File
            JCGComponent tc = canvas.getGCMPs().get(link.getDestinationComponentName());
            if (tc.getType().equals(ACodaType.FILE.name())) {
                tc.setSubType(destinationTransport.getTransClass());
            }

            // add transports to the destination component
            canvas.getGCMPs().get(link.getDestinationComponentName()).addTrnsport(destinationTransport);

            // add links
            canvas.getGCMPs().get(link.getSourceComponentName()).addLnk(link);
            canvas.getGCMPs().get(link.getDestinationComponentName()).addLnk(link);

            System.out.println("DDDDD ================> "+transportClassComboBox.getSelectedItem());
                if (transportClassComboBox.getSelectedItem().equals("UdpStream") ||
                        transportClassComboBox.getSelectedItem().equals("TcpStream")) {
                    canvas.getGCMPs().get(link.getSourceComponentName()).setStreaming(true);
                    canvas.getGCMPs().get(link.getDestinationComponentName()).setStreaming(true);
                }
//                else {
//                    canvas.getGCMPs().get(link.getSourceComponentName()).setStreaming(false);
//                    canvas.getGCMPs().get(link.getDestinationComponentName()).setStreaming(false);
//                }
                if(transportClassComboBox.getSelectedItem().equals("File") ||
                    transportClassComboBox.getSelectedItem().equals("ET")) {
                canvas.getGCMPs().get(link.getDestinationComponentName()).setStreaming(false);
            }


            canvas.repaint();
            dispose();

        }
    }

    private class ClearAction extends AbstractAction {
        private ClearAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            putValue(NAME, "Clear");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            if (DrawingCanvas.getComp(link.getDestinationComponentName()).getType().equals(ACodaType.FILE.name())) {
                transportClassComboBox.setSelectedItem("File");
            } else {
                transportClassComboBox.setSelectedItem("EmuSocket");
            }
            etHostTextField.setText("anywhere");
            etSubnetTextField.setText("undefined");
            etTcpPortSpinner.setValue(23911);
            etUdpPortSpinner.setValue(23912);
            etNumberEvents.setValue(1);
            etEventSize.setValue(2100);
            etChunkSize.setValue(2);
            inputEtChunkSize.setValue(2);
            etWait.setValue(0);
            singleEventOutCheckBox.setSelected(false);

            mAddressTextField.setText("239.200.0.0");
            connectionMethodComboBox.setSelectedItem("mcast");

            fileNameTextField.setText("");
            fileSplitSpinner.setValue(2000);
            fileTypeComboBox.setSelectedItem("coda");
            enableEtCustomization(false);

            // emuSocket
            emuPortSpinner.setValue(46000);
            emuSocketWaitSpinner.setValue(0);
            emuMaxBufferSpinner.setValue(2100);
            emuSubnetTextField.setText("");
            fpgaLinkIpTextField.setText("");
            emuFatPipeCheckBox.setSelected(false);

            // tcpStream
            tcpStreamPortSpinner.setValue(46100);
            tcpStreamSocketWaitSpinner.setValue(0);
            tcpStreamMaxBufferSpinner.setValue(100);
            tcpStreamSubnetTextField.setText("");
            tcpStreamFpgaLinkIpTextField.setText("");
            tcpStreamsSpinner.setValue(1);

            // udpStream
            UdpHostTextField.setText("");
            UdpUseLoadBalancer.setSelected(false);
            UdpUseErsap.setSelected(false);
            UdpPortSpinner.setValue(1);
            UdpBufferSizeSpinner.setValue(100);
            fpgaLinkIpTextField.setText("");

        }
    }

    private class CancelAction extends AbstractAction {
        private CancelAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            putValue(NAME, "Cancel");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            dispose();
        }
    }

    private class EtCustomizeAction extends AbstractAction {
        private EtCustomizeAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            putValue(NAME, "Enable");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            enableEtCustomization(true);
        }
    }

    private class EtDefaultAction extends AbstractAction {
        private EtDefaultAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            putValue(NAME, "Set Defaults");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            etNumberEvents.setValue(1);
            etEventSize.setValue(2100);
            etChunkSize.setValue(2);
            inputEtChunkSize.setValue(2);
            etWait.setValue(0);
            singleEventOutCheckBox.setSelected(false);
        }
    }

    private class EtCustomizationDisableAction extends AbstractAction {
        private EtCustomizationDisableAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            putValue(NAME, "Disable");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            enableEtCustomization(false);
        }
    }
}
