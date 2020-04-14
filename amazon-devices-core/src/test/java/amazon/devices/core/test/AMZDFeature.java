/*

(C) Copyright 2020 Amazon Devices.
Contributors:
Frank Zheng fzheng@ext.nuxeo.com
*/
package amazon.devices.core.test;

import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.platform.audit.AuditFeature;
import org.nuxeo.ecm.platform.routing.test.DocumentRoutingFeature;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.ecm.restapi.test.RestServerFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.PartialDeploy;
import org.nuxeo.runtime.test.runner.RunnerFeature;
import org.nuxeo.runtime.test.runner.TargetExtensions;

@Features({ CoreFeature.class, AuditFeature.class, PlatformFeature.class, AutomationFeature.class,
        RestServerFeature.class, DocumentRoutingFeature.class })
@Deploy({ "amazon.devices.core.amazon-devices-core", 
        "org.nuxeo.ecm.default.config","org.nuxeo.ecm.platform.types.core",
        "org.nuxeo.ecm.platform.tag"})
@PartialDeploy(bundle = "studio.extensions.amazon-devices-global-dam", extensions = {
        TargetExtensions.ContentModel.class, DirectoryExtensions.ContentTemplate.class,
        TargetExtensions.Automation.class, WorkflowExtensions.class })
@RepositoryConfig(cleanup = Granularity.METHOD)

public class AMZDFeature implements RunnerFeature {

    @Override
    public void beforeRun(FeaturesRunner runner) throws Exception {
        RunnerFeature.super.beforeRun(runner);
    }
}