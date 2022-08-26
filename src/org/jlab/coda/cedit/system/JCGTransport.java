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

package org.jlab.coda.cedit.system;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

@XmlRootElement(name = "transport")
public class JCGTransport {
    private String  name           = "undefined";
    private String  transClass     = "EmuSocket";
    private String  etName         = "undefined";
    private boolean etCreate       = false;
    private int     etTcpPort      = 23911;
    private int     etUdpPort      = 23912;
    private int     etWait         = 0;
    private String  mAddress       = "239.200.0.0";
    private int     etEventNum     = 1;
    private int     etEventSize    = 4200000; // in Bytes
    private int     inputEtChunkSize    = 2;
    private int     etChunkSize    = 2;
    private String  single         = "false";
    private int     etGroups       = 1;
    private int     etRecvBuf      = 0;
    private int     etSendBuf      = 0;
    private String  etMethodCon    = "mcast";
    private String  etHostName     = "anywhere";
    private String  etSubNet       = "undefined";
    private String  destinationEtCreate = "true";

    private int     emuDirectPort  = 46100;
    private int     emuMaxBuffer   = 1000000; // in Bytes
    private int     emuWait        = 5;
    private String  emuSubNet      = "undefined";
    private String  fpgaLinkIp     = "undefined";
    private boolean emuFatPipe     = false;


    private int     tcpStreamDirectPort  = 46100;
    private int     tcpStreamMaxBuffer   = 1000000; // in Bytes
    private int     tcpStreamWait        = 5;
    private String  tcpStreamSubNet      = "undefined";
    private String  tcpStreamFpgaLinkIp  = "undefined";
    private int     emuTcpStreams        = 1;

    private String  udpHost        = "undefined";
    private int     udpPort        = 45000;
    private int     udpBufferSize  = 9000;
    private String  UdpFpgaLinkIp  = "undefined";
    private int udpStreams         = 1;
    private boolean isLB           = false;
    private boolean isErsap        = false;

    private String  fileName    = "undefined";
    private String  fileType    = "coda";
    private long    fileSplit   = 20000000000l;
    private int fileInternalBuffer = 100;

    private boolean noLink = false;

    // this can break backwards compatibility
    private int compression = 1; //lz4 0-no compression, 1-lz4, 2-lz4_best, 3-gzip
    private int compressionThreads = 2;

    public JCGTransport() {
    }

    public JCGTransport(String name, String transClass, String etName, boolean etCreate, int etTcpPort,
                        int etUdpPort, int etWait, String mAddress, int etEventNum, int etEventSize,
                        int etChunkSize, int inputEtChunkSize, String single, int etGroups, int etRecvBuf, int etSendBuf,
                        String etMethodCon, String etHostName, String etSubNet, String destinationEtCreate, int emuDirectPort,
                        int emuMaxBuffer, int emuWait, String emuSubNet, String fpgaLinkIp, boolean emuFatPipe,
                        int tcpStreamMaxBuffer, int tcpStreamWait, String tcpStreamSubNet, String tcpStreamFpgaLinkIp, int emuTcpStreams,
                        String udpHost, int udpPort, int udpBufferSize,  String UdpFpgaLinkIp, int udpStreams, boolean isLB, boolean isErsap,
                        String fileName, String fileType, long fileSplit, int fileInternalBuffer, boolean noLink,
                        int compression, int compressionThreads) {
        this.name = name;
        this.transClass = transClass;
        this.etName = etName;
        this.etCreate = etCreate;
        this.etTcpPort = etTcpPort;
        this.etUdpPort = etUdpPort;
        this.etWait = etWait;
        this.mAddress = mAddress;
        this.etEventNum = etEventNum;
        this.etEventSize = etEventSize;
        this.etChunkSize = etChunkSize;
        this.inputEtChunkSize = inputEtChunkSize;
        this.single = single;
        this.etGroups = etGroups;
        this.etRecvBuf = etRecvBuf;
        this.etSendBuf = etSendBuf;
        this.etMethodCon = etMethodCon;
        this.etHostName = etHostName;
        this.etSubNet = etSubNet;
        this.destinationEtCreate = destinationEtCreate;

        this.emuDirectPort = emuDirectPort;
        this.emuMaxBuffer = emuMaxBuffer;
        this.emuWait = emuWait;
        this.emuSubNet = emuSubNet;
        this.fpgaLinkIp = fpgaLinkIp;
        this.UdpFpgaLinkIp = UdpFpgaLinkIp;
        this.emuFatPipe = emuFatPipe;

        this.tcpStreamMaxBuffer = tcpStreamMaxBuffer;
        this.tcpStreamWait = tcpStreamWait;
        this.tcpStreamSubNet = tcpStreamSubNet;
        this.tcpStreamFpgaLinkIp = tcpStreamFpgaLinkIp;
        this.emuTcpStreams = emuTcpStreams;

        this.udpHost = udpHost;
        this.udpPort = udpPort;
        this.udpBufferSize = udpBufferSize;
        this.udpStreams = udpStreams;
        this.isLB = isLB;
        this.isErsap = isErsap;

        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSplit = fileSplit;
        this.fileInternalBuffer = fileInternalBuffer;
        this.noLink = noLink;
        this.compression = compression;
        this.compressionThreads = compressionThreads;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTransClass() {
        return transClass;
    }

    public void setTransClass(String transClass) {
        this.transClass = transClass;
    }

    public String getEtName() {
        return etName;
    }

    public void setEtName(String etName) {
        this.etName = etName;
    }

    public boolean isEtCreate() {
        return etCreate;
    }

    public void setEtCreate(boolean etCreate) {
        this.etCreate = etCreate;
    }

    public int getEtWait() {
        return etWait;
    }

    public void setEtWait(int etWait) {
        this.etWait = etWait;
    }

    public int getEtEventNum() {
        return etEventNum;
    }

    public void setEtEventNum(int etEventNum) {
        this.etEventNum = etEventNum;
    }

    public int getEtEventSize() {
        return etEventSize;
    }

    public void setEtEventSize(int etEventSize) {
        this.etEventSize = etEventSize;
    }

    public int getEtSendBuf() {
        return etSendBuf;
    }

    public void setEtSendBuf(int etSendBuf) {
        this.etSendBuf = etSendBuf;
    }

    public int getEtRecvBuf() {
        return etRecvBuf;
    }

    public void setEtRecvBuf(int etRecvBuf) {
        this.etRecvBuf = etRecvBuf;
    }

    public int getEtTcpPort() {
        return etTcpPort;
    }

    public void setEtTcpPort(int etTcpPort) {
        this.etTcpPort = etTcpPort;
    }

    public int getEtUdpPort() {
        return etUdpPort;
    }

    public void setEtUdpPort(int etUdpPort) {
        this.etUdpPort = etUdpPort;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSplit() {
        return fileSplit;
    }

    public void setFileSplit(long fileSplit) {
        this.fileSplit = fileSplit;
    }

    public int getFileInternalBuffer() {
        return fileInternalBuffer;
    }

    public void setFileInternalBuffer(int fileInternalBuffer) {
        this.fileInternalBuffer = fileInternalBuffer;
    }

    public String getEtHostName() {
        return etHostName;
    }

    public void setEtHostName(String etHostName) {
        this.etHostName = etHostName;
    }

    public String getEtSubNet() {
        return etSubNet;
    }

    public void setEtSubNet(String etSubNet) {
        this.etSubNet = etSubNet;
    }

    public String getDestinationEtCreate() {
        return destinationEtCreate;
    }

    public void setDestinationEtCreate(String destinationEtCreate) {
        this.destinationEtCreate = destinationEtCreate;
    }

    public String getEmuSubNet() {
        return emuSubNet;
    }

    public void setEmuSubNet(String emuSubNet) {
        this.emuSubNet = emuSubNet;
    }

    public String getFpgaLinkIp() {
        return fpgaLinkIp;
    }

    public void setFpgaLinkIp(String fpgaLinkIp) {
        this.fpgaLinkIp = fpgaLinkIp;
    }

    public String getUdpFpgaLinkIp() {
        return UdpFpgaLinkIp;
    }

    public void setUdpFpgaLinkIp(String udpFpgaLinkIp) {
        UdpFpgaLinkIp = udpFpgaLinkIp;
    }

    public int getEmuTcpStreams() {
        return emuTcpStreams;
    }

    public void setEmuTcpStreams(int emuTcpStreams) {
        this.emuTcpStreams = emuTcpStreams;
    }

    public int getUdpStreams() {
        return udpStreams;
    }

    public void setUdpStreams(int udpStreams) {
        this.udpStreams = udpStreams;
    }

    public boolean isEmuFatPipe() {
        return emuFatPipe;
    }

    public void setEmuFatPipe(boolean emuFatPipe) {
        this.emuFatPipe = emuFatPipe;
    }

    public String getmAddress() {
        return mAddress;
    }

    public void setmAddress(String mAddress) {
        this.mAddress = mAddress;
    }

    public int getEtGroups() {
        return etGroups;
    }

    public void setEtGroups(int etGroups) {
        this.etGroups = etGroups;
    }

    public String getEtMethodCon() {
        return etMethodCon;
    }

    public void setEtMethodCon(String etMethodCon) {
        this.etMethodCon = etMethodCon;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public boolean isNoLink() {
        return noLink;
    }

    public void setNoLink(boolean noLink) {
        this.noLink = noLink;
    }

    public int getEtChunkSize() {
        return etChunkSize;
    }

    public void setEtChunkSize(int etChunkSize) {
        this.etChunkSize = etChunkSize;
    }

    public int getInputEtChunkSize() {
        return inputEtChunkSize;
    }

    public void setInputEtChunkSize(int inputEtChunkSize) {
        this.inputEtChunkSize = inputEtChunkSize;
    }

    public int getEmuDirectPort() {
        return emuDirectPort;
    }

    public void setEmuDirectPort(int emuDirectPort) {
        this.emuDirectPort = emuDirectPort;
    }

    public int getEmuMaxBuffer() {
        return emuMaxBuffer;
    }

    public void setEmuMaxBuffer(int emuMaxBuffer) {
        this.emuMaxBuffer = emuMaxBuffer;
    }

    public int getEmuWait() {
        return emuWait;
    }

    public void setEmuWait(int emuWait) {
        this.emuWait = emuWait;
    }

    public String getUdpHost() {
        return udpHost;
    }

    public void setUdpHost(String udpHost) {
        this.udpHost = udpHost;
    }

    public int getUdpPort() {
        return udpPort;
    }

    public void setUdpPort(int udpPort) {
        this.udpPort = udpPort;
    }

    public int getUdpBufferSize() {
        return udpBufferSize;
    }

    public void setUdpBufferSize(int udpBufferSize) {
        this.udpBufferSize = udpBufferSize;
    }

    public boolean isLB() {
        return isLB;
    }

    public void setLB(boolean LB) {
        isLB = LB;
    }

    public boolean isErsap() {
        return isErsap;
    }

    public void setErsap(boolean ersap) {
        isErsap = ersap;
    }

    public String getSingle() {
        return single;
    }

    public void setSingle(String single) {
        this.single = single;
    }

    public int getCompression() {
        return compression;
    }

    public void setCompression(int compression) {
        this.compression = compression;
    }

    public int getCompressionThreads() {
        return compressionThreads;
    }

    public void setCompressionThreads(int compressionThreads) {
        this.compressionThreads = compressionThreads;
    }

    public int getTcpStreamDirectPort() {
        return tcpStreamDirectPort;
    }

    public void setTcpStreamDirectPort(int tcpStreamDirectPort) {
        this.tcpStreamDirectPort = tcpStreamDirectPort;
    }

    public int getTcpStreamMaxBuffer() {
        return tcpStreamMaxBuffer;
    }

    public void setTcpStreamMaxBuffer(int tcpStreamMaxBuffer) {
        this.tcpStreamMaxBuffer = tcpStreamMaxBuffer;
    }

    public int getTcpStreamWait() {
        return tcpStreamWait;
    }

    public void setTcpStreamWait(int tcpStreamWait) {
        this.tcpStreamWait = tcpStreamWait;
    }

    public String getTcpStreamSubNet() {
        return tcpStreamSubNet;
    }

    public void setTcpStreamSubNet(String tcpStreamSubNet) {
        this.tcpStreamSubNet = tcpStreamSubNet;
    }

    public String getTcpStreamFpgaLinkIp() {
        return tcpStreamFpgaLinkIp;
    }

    public void setTcpStreamFpgaLinkIp(String tcpStreamFpgaLinkIp) {
        this.tcpStreamFpgaLinkIp = tcpStreamFpgaLinkIp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JCGTransport)) return false;
        JCGTransport that = (JCGTransport) o;
        return getEtTcpPort() == that.getEtTcpPort() && getEtUdpPort() == that.getEtUdpPort() &&
                getEtWait() == that.getEtWait() && getEtEventNum() == that.getEtEventNum() &&
                getEtEventSize() == that.getEtEventSize() && getInputEtChunkSize() == that.getInputEtChunkSize() &&
                getEtChunkSize() == that.getEtChunkSize() && getEtGroups() == that.getEtGroups() &&
                getEtRecvBuf() == that.getEtRecvBuf() && getEtSendBuf() == that.getEtSendBuf() &&
                getEmuDirectPort() == that.getEmuDirectPort() && getEmuMaxBuffer() == that.getEmuMaxBuffer() &&
                getEmuWait() == that.getEmuWait() && isEmuFatPipe() == that.isEmuFatPipe() &&
                getTcpStreamDirectPort() == that.getTcpStreamDirectPort() &&
                getTcpStreamMaxBuffer() == that.getTcpStreamMaxBuffer() &&
                getTcpStreamWait() == that.getTcpStreamWait() && getEmuTcpStreams() == that.getEmuTcpStreams() &&
                getUdpPort() == that.getUdpPort() && getUdpBufferSize() == that.getUdpBufferSize() &&
                getUdpStreams() == that.getUdpStreams() && isLB() == that.isLB() && isErsap() == that.isErsap() &&
                getFileSplit() == that.getFileSplit() && getFileInternalBuffer() == that.getFileInternalBuffer() &&
                isNoLink() == that.isNoLink() && getCompression() == that.getCompression() &&
                getCompressionThreads() == that.getCompressionThreads() &&
                Objects.equals(getName(), that.getName()) && Objects.equals(getTransClass(), that.getTransClass()) &&
                Objects.equals(getEtName(), that.getEtName()) && Objects.equals(getmAddress(), that.getmAddress()) &&
                Objects.equals(getSingle(), that.getSingle()) &&
                Objects.equals(getEtMethodCon(), that.getEtMethodCon()) &&
                Objects.equals(getEtHostName(), that.getEtHostName()) &&
                Objects.equals(getEtSubNet(), that.getEtSubNet()) &&
                Objects.equals(getDestinationEtCreate(), that.getDestinationEtCreate()) &&
                Objects.equals(getEmuSubNet(), that.getEmuSubNet()) &&
                Objects.equals(getFpgaLinkIp(), that.getFpgaLinkIp()) &&
                Objects.equals(getTcpStreamSubNet(), that.getTcpStreamSubNet()) &&
                Objects.equals(getTcpStreamFpgaLinkIp(), that.getTcpStreamFpgaLinkIp()) &&
                Objects.equals(getUdpHost(), that.getUdpHost()) &&
                Objects.equals(getUdpFpgaLinkIp(), that.getUdpFpgaLinkIp()) &&
                Objects.equals(getFileName(), that.getFileName()) && Objects.equals(getFileType(), that.getFileType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getTransClass(), getEtName(), isEtCreate(), getEtTcpPort(),
                getEtUdpPort(), getEtWait(), getmAddress(), getEtEventNum(), getEtEventSize(),
                getInputEtChunkSize(), getEtChunkSize(), getSingle(), getEtGroups(), getEtRecvBuf(),
                getEtSendBuf(), getEtMethodCon(), getEtHostName(), getEtSubNet(), getDestinationEtCreate(),
                getEmuDirectPort(), getEmuMaxBuffer(), getEmuWait(), getEmuSubNet(), getFpgaLinkIp(),
                isEmuFatPipe(), getTcpStreamDirectPort(), getTcpStreamMaxBuffer(), getTcpStreamWait(),
                getTcpStreamSubNet(), getTcpStreamFpgaLinkIp(), getEmuTcpStreams(), getUdpHost(),
                getUdpPort(), getUdpBufferSize(), getUdpFpgaLinkIp(), getUdpStreams(), isLB(),
                isErsap(), getFileName(), getFileType(), getFileSplit(), getFileInternalBuffer(),
                isNoLink(), getCompression(), getCompressionThreads());
    }

    @Override
    public String toString() {
        return "JCGTransport{" +
                "name='" + name + '\'' +
                ", transClass='" + transClass + '\'' +
                ", etName='" + etName + '\'' +
                ", etCreate=" + etCreate +
                ", etTcpPort=" + etTcpPort +
                ", etUdpPort=" + etUdpPort +
                ", etWait=" + etWait +
                ", mAddress='" + mAddress + '\'' +
                ", etEventNum=" + etEventNum +
                ", etEventSize=" + etEventSize +
                ", inputEtChunkSize=" + inputEtChunkSize +
                ", etChunkSize=" + etChunkSize +
                ", single='" + single + '\'' +
                ", etGroups=" + etGroups +
                ", etRecvBuf=" + etRecvBuf +
                ", etSendBuf=" + etSendBuf +
                ", etMethodCon='" + etMethodCon + '\'' +
                ", etHostName='" + etHostName + '\'' +
                ", etSubNet='" + etSubNet + '\'' +
                ", destinationEtCreate='" + destinationEtCreate + '\'' +
                ", emuDirectPort=" + emuDirectPort +
                ", emuMaxBuffer=" + emuMaxBuffer +
                ", emuWait=" + emuWait +
                ", emuSubNet='" + emuSubNet + '\'' +
                ", fpgaLinkIp='" + fpgaLinkIp + '\'' +
                ", emuFatPipe=" + emuFatPipe +
                ", tcpStreamDirectPort=" + tcpStreamDirectPort +
                ", tcpStreamMaxBuffer=" + tcpStreamMaxBuffer +
                ", tcpStreamWait=" + tcpStreamWait +
                ", tcpStreamSubNet='" + tcpStreamSubNet + '\'' +
                ", tcpStreamFpgaLinkIp='" + tcpStreamFpgaLinkIp + '\'' +
                ", emuTcpStreams=" + emuTcpStreams +
                ", udpHost='" + udpHost + '\'' +
                ", udpPort=" + udpPort +
                ", udpBufferSize=" + udpBufferSize +
                ", UdpFpgaLinkIp='" + UdpFpgaLinkIp + '\'' +
                ", udpStreams=" + udpStreams +
                ", isLB=" + isLB +
                ", isErsap=" + isErsap +
                ", fileName='" + fileName + '\'' +
                ", fileType='" + fileType + '\'' +
                ", fileSplit=" + fileSplit +
                ", fileInternalBuffer=" + fileInternalBuffer +
                ", noLink=" + noLink +
                ", compression=" + compression +
                ", compressionThreads=" + compressionThreads +
                '}';
    }
}
