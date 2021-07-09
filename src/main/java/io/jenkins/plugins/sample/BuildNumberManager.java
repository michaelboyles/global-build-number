package io.jenkins.plugins.sample;

import com.thoughtworks.xstream.XStream;
import hudson.XmlFile;
import hudson.model.Saveable;
import hudson.model.listeners.SaveableListener;
import hudson.util.XStream2;
import jenkins.model.Jenkins;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Holds the state of the next available build number and allows updates. Persists to XML.
 */
public enum BuildNumberManager implements Saveable {
    INSTANCE;

    private static class Xml {
        private final int id;

        private Xml(int id) {
            this.id = id;
        }
    }

    private static final Logger LOGGER = Logger.getLogger(BuildNumberManager.class.getName());

    private transient final XStream xstream = new XStream2();
    private transient final ReentrantLock lock = new ReentrantLock();

    private volatile AtomicInteger nextBuildId;

    public int nextAvailableId() {
        read();
        return nextBuildId.get();
    }

    public void setNextAvailableId(int id) {
        nextBuildId = new AtomicInteger(id);
        save();
    }

    public int assignId() {
        read();
        final int id = nextBuildId.getAndIncrement();
        save();
        return id;
    }

    @Override
    public void save() {
        if (Jenkins.getInstanceOrNull() != null) {
            this.lock.lock();
            try {
                final XmlFile xmlFile = new XmlFile(xstream, this.getXMLFile());
                try {
                    xmlFile.write(new Xml(this.nextBuildId.get()));
                }
                catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Failed to write to " + getXMLFile().getAbsolutePath(), e);
                }
                SaveableListener.fireOnChange(this, xmlFile);
            }
            finally {
                this.lock.unlock();
            }
        }
    }

    private void read() {
        if (nextBuildId == null) {
            this.lock.lock();
            try {
                if (nextBuildId == null) {
                    final XmlFile xmlFile = new XmlFile(xstream, this.getXMLFile());
                    if (xmlFile.exists()) {
                        final Xml xml = (Xml) xmlFile.read();
                        this.nextBuildId = new AtomicInteger(xml.id);
                    }
                    else {
                        this.nextBuildId = new AtomicInteger(1);
                    }
                }
            }
            catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Failed to read " + getXMLFile().getAbsolutePath(), e);
            }
            finally {
                this.lock.unlock();
            }
        }
    }

    private File getXMLFile() {
        return new File(Jenkins.get().getRootDir(), "plugins/global-build-number-numbers.xml");
    }
}
