package io.jenkins.plugins.sample;

import hudson.Extension;
import hudson.model.Action;
import hudson.model.Descriptor;
import hudson.model.DescriptorVisibilityFilter;
import hudson.model.Items;
import hudson.model.Job;
import hudson.model.JobProperty;
import hudson.model.ProminentProjectAction;
import hudson.model.RootAction;
import hudson.security.Permission;
import jenkins.model.BuildDiscarder;
import jenkins.model.BuildDiscarderProperty;
import jenkins.model.Messages;
import jenkins.model.OptionalJobProperty;
import net.sf.json.JSONObject;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.verb.POST;

import javax.servlet.ServletException;
import java.io.IOException;

import static hudson.Functions.checkPermission;

/**
 * Adds a checkbox property to each job.
 */
@Extension
@SuppressWarnings("unused")
public class BuildNumberProperty extends OptionalJobProperty<Job<?, ?>> {
    @DataBoundConstructor
    public BuildNumberProperty() {
    }

    @Extension
    @Symbol({"useGlobalBuildNumber"})
    public static class DescriptorImpl extends OptionalJobPropertyDescriptor {
        public DescriptorImpl() {
        }

        public String getDisplayName() {
            return "Use global build number";
        }
    }
}
