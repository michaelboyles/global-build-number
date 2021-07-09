package io.jenkins.plugins.sample;

import hudson.Extension;
import hudson.model.RootAction;
import hudson.security.Permission;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.verb.POST;

import javax.servlet.ServletException;
import java.io.IOException;

import static hudson.Functions.checkPermission;

/**
 * Adds a page to the homepage of Jenkins to see the next build number and to update it.
 */
@Extension
@SuppressWarnings("unused")
public class BuildNumberRootAction implements RootAction
{
    @Override
    public String getIconFileName()
    {
        return "attribute.png";
    }

    @Override
    public String getDisplayName()
    {
        return "Global Build Number";
    }

    @Override
    public String getUrlName()
    {
        return "global-build-number";
    }

    @POST
    public void doSubmit(StaplerRequest req, StaplerResponse rsp) throws IOException, ServletException
    {
        checkPermission(Permission.UPDATE);

        JSONObject formData = req.getSubmittedForm();
        if (formData.has("next-build-number"))
        {
            BuildNumberManager.INSTANCE.setNextAvailableId(formData.getInt("next-build-number"));
        }
        rsp.sendRedirect("../");
    }

    // Used by jelly
    public int getNextBuildNumber()
    {
        return BuildNumberManager.INSTANCE.nextAvailableId();
    }
}
