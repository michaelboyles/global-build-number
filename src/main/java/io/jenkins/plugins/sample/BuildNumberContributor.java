package io.jenkins.plugins.sample;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.EnvVars;
import hudson.Extension;
import hudson.model.EnvironmentContributor;
import hudson.model.Job;
import hudson.model.Run;
import hudson.model.TaskListener;
import jnr.ffi.annotations.In;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Sets the display name, build ID and build number before a run starts.
 */
@Extension
@SuppressWarnings("unused")
public class BuildNumberContributor extends EnvironmentContributor
{
    private static final Logger LOGGER = Logger.getLogger(BuildNumberContributor.class.getName());

    @Override
    public void buildEnvironmentFor(@NonNull Run run, @NonNull EnvVars envs, @NonNull TaskListener listener)
        throws IOException, InterruptedException
    {
        final Job<?, ?> job = run.getParent();
        if (job.getProperty(BuildNumberProperty.class) != null)
        {
            listener.getLogger().println("Global build number is enabled");
            final String id = Integer.toString(BuildNumberManager.INSTANCE.assignId());
            run.setDisplayName("#" + id);
            envs.put("BUILD_ID", id);
            envs.put("BUILD_NUMBER", id);
        }
    }
}
