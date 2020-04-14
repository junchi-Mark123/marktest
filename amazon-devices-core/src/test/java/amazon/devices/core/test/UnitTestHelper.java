/*

(C) Copyright 2020 Amazon Devices.
Contributors:
Frank Zheng fzheng@ext.nuxeo.com
*/
package amazon.devices.core.test;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.nuxeo.common.utils.FileUtils;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.Blobs;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.platform.usermanager.NuxeoPrincipalImpl;
import org.nuxeo.ecm.platform.usermanager.UserManager;

public class UnitTestHelper {
    public static Blob getFileFromFilename(String filename, String format) throws IOException {
        File file = FileUtils.getResourceFileFromContext(filename);
        assertNotNull("Failed to load resource: " + filename, file);
        Blob blob = Blobs.createBlob(file, format);
        blob.setFilename(FilenameUtils.getName(filename));
        blob.setDigest(computeDigest(blob, "MD5"));

        return blob;
    }

    private static String computeDigest(Blob blob, String digestAlgo) throws IOException {

        MessageDigest md;
        try {
            md = MessageDigest.getInstance(digestAlgo);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] b = DigestUtils.updateDigest(md, blob.getStream()).digest();
        return Base64.encodeBase64String(b);
    }
    
    /**
     * Create user based on User name and groups
     * @param userName
     * @param groupNames
     * @param session
     * @return 
     * @return
     */
    public static NuxeoPrincipal createUser(String userName, List<String> groupNames, UserManager userManager) {
        NuxeoPrincipal user = new NuxeoPrincipalImpl(userName);
        user.setGroups(groupNames);
        DocumentModel userModel = userManager.createUser(user.getModel());
        user = new NuxeoPrincipalImpl(userName);
        user.setModel(userModel);
        return user;
    }
}
