/*

(C) Copyright 2020 Amazon Devices.
Contributors:
Frank Zheng fzheng@ext.nuxeo.com
*/
package amazon.devices.core.test;

import org.nuxeo.runtime.test.runner.TargetExtensions;

/**
 * @author frank.zheng
 *
 */
public class WorkflowExtensions extends TargetExtensions{

    /* (non-Javadoc)
     * @see org.nuxeo.runtime.test.runner.TargetExtensions#initialize()
     */
    @Override
    protected void initialize() {
        addTargetExtension("org.nuxeo.ecm.platform.routing.service", "routeModelImporter");
    }

}
