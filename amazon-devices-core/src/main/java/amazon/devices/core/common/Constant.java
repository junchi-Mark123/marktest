/*

(C) Copyright 2020 Amazon Devices.
Contributors:
Frank Zheng fzheng@ext.nuxeo.com
*/
package amazon.devices.core.common;

public class Constant {
    public static class PATH {
        public static final String ROOT = "/";

        public static final String DOMAIN = "/default-domain/";

        public static final String EXTERNAL_USER_MANAGEMENT = "/default-domain/External User Management/";

        public static final String DEVICE_MANAGEMENT = "/default-domain/Device Management/";

        public static final String DEVICES = "/default-domain/Device Management/Devices";

        public static final String CODENAMES = "/default-domain/Device Management/Codenames";

        public static final String DEVICES_NORMAL = "/default-domain/Device Management/Devices/Normal";

        public static final String DEVICES_ACCESS_CONTROLLED = "/default-domain/Device Management/Devices/Access Controlled";

        public static final String CODENAME_NORMAL = "/default-domain/Device Management/Codenames/Normal";

        public static final String CODENAME_ACCESS_CONTROLLED = "/default-domain/Device Management/Codenames/Access Controlled";

        public static final String DEVICE_HUB_STAGING = "/default-domain/Approved Content/Amazon Device Hub Staging";
        
        public static final String PRODUCTION_APPROVED = "/default-domain/Approved Content/Production Approved";
        
        public static final String TEMPLATE_LIBRARY = "/default-domain/Content Creation/Creative Brand/TemplateLibrary";
        
        public static final String CREATIVE_BRAND = "/default-domain/Content Creation/Creative Brand";

        public static final String RETAIL_USERS = EXTERNAL_USER_MANAGEMENT.concat("Retail Users");

        public static final String VENDOR_USERS = EXTERNAL_USER_MANAGEMENT.concat("Vendor Users");

        public static final String AGENCY_USERS = EXTERNAL_USER_MANAGEMENT.concat("Agency Users");

    }

    public static class XPATH {
        public static final String FILE_CONTENT = "file:content";

        public static final String TITLE = "dc:title";
        
        public static final String DESCRIPTION = "dc:description";

        public static final String RELATED_USER = "userAccess:related_User";

        public static final String PASSWORD = "userAccess:password";

        public static final String GTM = "product:production_date";

        public static final String T_WEEK = "product:tminus_week";

        public static final String CODENAME_DEVICE = "codename:device";

        public static final String MARKET_NAME = "codename:market_name";
        
        public static final String SPECIFIC_NAMES = "codename:specific_names";

        public static final String PRODUCT_CREATE_ASSET_ACCESS = "product:device_asset_access_create";

        public static final String PRODUCT_READ_ASSET_ACCESS = "product:device_asset_access_read";
        
        public static final String RELATIONS_WORKSPACE_LOCATION = "relations:workspaceLocation";
        
        public static final String PROJECT_PRIORITY = "project:priority";
    }

    public static class DOCTYPE {
        public static final String DOMAIN = "Domain";

        public static final String EXT_USERS = "ExtUsers";

        public static final String EXT_USERS_TYPE = "ExtUsers_Type";

        public static final String COMPANY = "Company";

        public static final String USER_ACCESS = "UserAccess";

        public static final String DEVICE_MANAGEMENT = "DeviceManagement";

        public static final String DEVICE_TYPE = "DeviceType";

        public static final String AMZFOLDER = "AMZFolder";

        public static final String DEVICE = "Product";

        public static final String SUBFOLDER = "SubFolder";

        public static final String YEAR = "Year";

        public static final String PRODUCT = "Product";

        public static final String CODENAME = "Codename";

        public static final String DEVICE_ASSET = "DeviceAsset";
        
        public static final String APPROVED_CONTENT = "ApprovedContent";
        
        public static final String CREATIVE_CONTENT = "CreativeContent";
        
        public static final String ThreeD_CREATIVE_CONTENT = "ThreeDCreativeContent";

        public static final String CONTENT_CREATION = "Content";
        
        public static final String CREATIVE_PROJECT_TEMPLATE = "CreativeProjectTemplate";
        
        public static final String TEMPLATE_LIBRARY = "TemplateLibrary";
        
        public static final String CREATIVE_ASSET = "CreativeAsset";
        
        public static final String PRODUCTION_CREATIVE_CONTENT = "ProductionCreativeContent";
        
        public static final String BRAND_CREATIVE_CONTENT = "BrandStrategyCreativeContent";
        
        public static final String IP_CREATIVE_CONTENT = "IPContentCreation";
        
        public static final String RETAILS_CREATIVE_CONTENT = "RetailExperienceContentCreation";

        public static final String AMAZON_DEVICE_HUB = "AMZDeviceHub";
        
        public static final String CREATIVE_PROJECT = "CreativeProject";

    }

    public static class PERMISSION {
        public static final String VIEW_NODOWNLOAD = "view_noDownload";

        public static final String DOWNLOAD_WITH_REQUEST = "DownloadWithRequest";

        public static final String DOWNLOAD = "Download";

        public static final String EDIT = "Edit";

        public static final String CREATE_CONTENTS = "CreateContents";

        public static final String EVERYTHING = "Everything";
    }

    public static class GROUP {
        public static final String ADMINISTRATORS = "administrators";

        public static final String LIBRARIAN = "Librarian";

        public static final String ADH_MANAGER = "ADH_Manager";

        public static final String DEVICE_MANAGER = "Device_Manager";

        public static final String CXLABS_CONSUMER = "CXLabs_Consumer";

        public static final String RETAIL = "retail";

        public static final String VENDOR = "vendor";

        public static final String AGENCY = "agency";

        public static final String CXPM = "CXPM";
    }

    public static class LIFECYCLE {
        public static final String FOLDER = "folder";

        public static final String USER_ACCESS = "UserAccess";
        
        public static final String GENERAL_OBJECT = "generalObject";
    }

    public static class EVENT {
        public static final String DOCUMENT_CREATED = "documentCreated";

        public static final String DOCUMENT_MODIFIED = "documentModified";

        public static final String UPDATE_PRODUCT_PERMISSION = "updateProductAssetPermission";
        
    }
}
