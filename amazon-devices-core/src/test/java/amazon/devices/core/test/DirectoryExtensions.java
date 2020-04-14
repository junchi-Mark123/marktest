/*

(C) Copyright 2020 Amazon Devices.
Contributors:
Frank Zheng fzheng@ext.nuxeo.com
*/
package amazon.devices.core.test;

import org.nuxeo.runtime.test.runner.TargetExtensions.ContentTemplate;

public class DirectoryExtensions extends ContentTemplate {
    @Override
    protected void initialize() {
        super.initialize();
        addTargetExtension("org.nuxeo.ecm.directory.GenericDirectory", "directories");
        addTargetExtension("org.nuxeo.ecm.core.cache.CacheService", "caches");
    }
}
