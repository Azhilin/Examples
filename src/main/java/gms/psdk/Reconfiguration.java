package gms.psdk;

import com.genesyslab.ats.cfg.actor.CfgManager;
import com.genesyslab.ats.ex.AtsCfgComponentException;
import com.genesyslab.ats.ex.AtsOperationFailed;
import com.genesyslab.functional.tests.gms.helper.PropertiesInitialization;
import com.genesyslab.platform.applicationblocks.com.ConfService;
import com.genesyslab.platform.applicationblocks.com.ConfServiceFactory;
import com.genesyslab.platform.applicationblocks.com.ConfigException;
import com.genesyslab.platform.applicationblocks.com.objects.CfgApplication;
import com.genesyslab.platform.applicationblocks.com.objects.CfgEnumerator;
import com.genesyslab.platform.applicationblocks.com.objects.CfgEnumeratorValue;
import com.genesyslab.platform.applicationblocks.com.objects.CfgTenant;
import com.genesyslab.platform.applicationblocks.com.queries.CfgApplicationQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgEnumeratorQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgEnumeratorValueQuery;
import com.genesyslab.platform.applicationblocks.com.queries.CfgTenantQuery;
import com.genesyslab.platform.commons.collections.KeyValueCollection;
import com.genesyslab.platform.commons.collections.KeyValuePair;
import com.genesyslab.platform.commons.protocol.ProtocolException;
import com.genesyslab.platform.configuration.protocol.types.CfgEnumeratorType;
import com.genesyslab.platform.configuration.protocol.types.CfgFlag;
import com.genesyslab.platform.configuration.protocol.types.CfgObjectState;
import com.genesyslab.scsmanager.SCSManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static com.genesyslab.functional.tests.gms.files.WorkWithResources.getAllMatchesInString;
import static com.genesyslab.functional.tests.gms.helper.PropertiesInitialization.getPropertyConfiguration;
import static com.genesyslab.functional.tests.gms.helper.ServiceOptions.*;

public class Reconfiguration {

    private String configHost = getPropertyConfiguration("config.server.host");
    private int port = Integer.parseInt(getPropertyConfiguration("config.server.port"));
    private String gmsHost = getPropertyConfiguration("gms.host");
    private String gmsPort = getPropertyConfiguration("gms.port");
    private String GMSClusterObjName = getPropertyConfiguration("gms.cluster.app.name");
    private String GMSObjName = getPropertyConfiguration("gms.app.name");
    private String username = getPropertyConfiguration("config.user.name");
    private String password = getPropertyConfiguration("config.user.password");
    private CfgEnumerator businessAttribute;
    private CfgEnumeratorValue businessAttributeValue;
    private ConfigurationServer confServerProtocol;
    private ConfService service;
    private CfgTenant tenant;

    private Properties properties;
    private CfgManager cfgManager = new CfgManager();
    private SCSManager scsManager = new SCSManager(cfgManager);

    public Reconfiguration() {
    }

    public Reconfiguration(String propFile) {
        properties = PropertiesInitialization.getProperties(propFile);
        try {
            scsManager.init(properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openConnectionToConfig() {
        confServerProtocol = new ConfigurationServer("Test", port, configHost,
                username, password);
        // how to initialize service
        service = (ConfService) ConfServiceFactory
                .createConfService(confServerProtocol.protocol);
        confServerProtocol.connect();
    }

    public void closeConnectionToConfig() {
        ConfServiceFactory.releaseConfService(service);
        confServerProtocol.disconnect();
    }

    public void addNewAnnexSection(String minute, String action) throws ConfigException {
        CfgApplicationQuery query = new CfgApplicationQuery(service);
        query.setName(GMSObjName);

        CfgApplication obj = query.executeSingleResult();
        System.out.println(obj);

        KeyValueCollection kv = new KeyValueCollection();
        // Creating properties for the agent login(annex tab in CME)
        kv.addPair(new KeyValuePair("action", action));//"purge.service.all"
        kv.addPair(new KeyValuePair("cron-expression", minute + " * * * *"));
        kv.addPair(new KeyValuePair("enabled", "true"));
        kv.addPair(new KeyValuePair("period", "1"));
        kv.addPair(new KeyValuePair("period-type", "hours"));

        KeyValueCollection annexProp = new KeyValueCollection();
        annexProp.addList("scheduled-job-01", kv);
        obj.setUserProperties(annexProp);
        obj.save();
    }

    public void addNewAnnexSection(String appName, String minute, String action) throws ConfigException {
        CfgApplicationQuery query = new CfgApplicationQuery(service);
        query.setName(appName);

        CfgApplication obj = query.executeSingleResult();
        System.out.println(obj);

        KeyValueCollection kv = new KeyValueCollection();
        // Creating properties for the agent login(annex tab in CME)
        kv.addPair(new KeyValuePair("action", action));//"purge.service.all"
        kv.addPair(new KeyValuePair("cron-expression", minute + " * * * *"));
        kv.addPair(new KeyValuePair("enabled", "true"));
        kv.addPair(new KeyValuePair("period", "1"));
        kv.addPair(new KeyValuePair("period-type", "hours"));

        KeyValueCollection annexProp = new KeyValueCollection();
        annexProp.addList("scheduled-job-01", kv);
        obj.setUserProperties(annexProp);
        obj.save();
    }

    public void addServerSection() throws ConfigException {

        CfgApplicationQuery query = new CfgApplicationQuery(service);
        query.setName(GMSObjName);

        CfgApplication obj = query.executeSingleResult();
        System.out.println(obj);

        KeyValueCollection sectionData = new KeyValueCollection();
        sectionData.addString("external_url_base", "http://" + gmsHost + ":"
                + gmsPort + "/");
        sectionData.addString("node_id", "1");
        sectionData.addString("web_port", "8080");

        KeyValuePair kp = new KeyValuePair("server", sectionData);

        obj.getOptions().add(kp);

        obj.save();

    }

    public void addServerSection(boolean relativeURL) throws ConfigException {

        CfgApplicationQuery query = new CfgApplicationQuery(service);
        query.setName(GMSObjName);

        CfgApplication obj = query.executeSingleResult();
        System.out.println(obj);

        KeyValueCollection sectionData = new KeyValueCollection();
        sectionData.addString("external_url_base", "http://" + gmsHost + ":"
                + gmsPort + "/");
        sectionData.addString("node_id", "1");
        sectionData.addString("relative_url", String.valueOf(relativeURL));
        sectionData.addString("web_port", "8080");

        KeyValuePair kp = new KeyValuePair("server", sectionData);

        obj.getOptions().add(kp);

        obj.save();
    }

    public void addPortControlSection() throws ConfigException {

        // how to add option

        CfgApplicationQuery query = new CfgApplicationQuery(service);
        query.setName(GMSObjName);

        CfgApplication obj = query.executeSingleResult();
        System.out.println(obj);

        KeyValueCollection sectionData = new KeyValueCollection();
        sectionData.addString("/genesys/1/admin/*", "8100-8102");
        sectionData.addString("/genesys/1/admin/node/*", "8100-8102");
        sectionData.addString("/genesys/1/resource*", "81,90,8043");
        sectionData.addString("/genesys/1/service/*", "8080-8090");
        sectionData.addString("/genesys/1/storage/*", "81");

        KeyValuePair kp = new KeyValuePair("port_restrictions", sectionData);

        obj.getOptions().add(kp);

        obj.save();

    }

    public void addBusinessAttributesSection() throws ConfigException {

        // how to add option

        CfgApplicationQuery query = new CfgApplicationQuery(service);
        query.setName(GMSObjName);

        CfgApplication obj = query.executeSingleResult();
        System.out.println(obj);

        KeyValueCollection sectionData = new KeyValueCollection();
        sectionData.addString("map-names", "true");
        sectionData.addString("Service.application_type", "ApplicationType");
        sectionData.addString("Service.disposition", "DispositionCode");
        sectionData.addString("Service.media_type", "MediaType");
        sectionData.addString("Service.resource_type", "ResourceType");
        sectionData.addString("Service.type", "ServiceType");
        sectionData.addString("State.application_type", "ApplicationType");
        sectionData.addString("State.disposition", "DispositionCode");
        sectionData.addString("State.media_type", "MediaType");
        sectionData.addString("State.resource_type", "ResourceType");
        sectionData.addString("State.type", "StateType");
        sectionData.addString("store-names", "false");
        sectionData.addString("Task.application_type", "ApplicationType");
        sectionData.addString("Task.disposition", "DispositionCode");
        sectionData.addString("Task.media_type", "MediaType");
        sectionData.addString("Task.resource_type", "ResourceType");
        sectionData.addString("Task.type", "TaskType");

        KeyValuePair kp = new KeyValuePair("business-attributes", sectionData);

        obj.getOptions().add(kp);

        obj.save();

    }

    public void addBusinessAttributesSection(String tenantName) throws ConfigException {

        // how to add option

        CfgApplicationQuery query = new CfgApplicationQuery(service);
        query.setName(GMSObjName);

        CfgApplication obj = query.executeSingleResult();
        System.out.println(obj);

        KeyValueCollection sectionData = new KeyValueCollection();
        sectionData.addString("map-names", "true");
        sectionData.addString("Service.application_type", "ApplicationType");
        sectionData.addString("Service.disposition", "DispositionCode");
        sectionData.addString("Service.media_type", "MediaType");
        sectionData.addString("Service.resource_type", "ResourceType");
        sectionData.addString("Service.type", "ServiceType");
        sectionData.addString("State.application_type", "ApplicationType");
        sectionData.addString("State.disposition", "DispositionCode");
        sectionData.addString("State.media_type", "MediaType");
        sectionData.addString("State.resource_type", "ResourceType");
        sectionData.addString("State.type", "StateType");
        sectionData.addString("store-names", "false");
        sectionData.addString("Task.application_type", "ApplicationType");
        sectionData.addString("Task.disposition", "DispositionCode");
        sectionData.addString("Task.media_type", "MediaType");
        sectionData.addString("Task.resource_type", "ResourceType");
        sectionData.addString("Task.type", "TaskType");

        KeyValuePair kp = new KeyValuePair("business-attributes." + tenantName, sectionData);

        obj.getOptions().add(kp);

        obj.save();
    }

    public void addCviewSection() throws ConfigException {

        // how to add option

        CfgApplicationQuery query = new CfgApplicationQuery(service);
        query.setName(GMSObjName);

        CfgApplication obj = query.executeSingleResult();
        System.out.println(obj);

        KeyValueCollection sectionData = new KeyValueCollection();
        sectionData.addString("allow-custom-ids", "false");
        sectionData.addString("enabled", "true");
        sectionData.addString("data-validation", "false");
        sectionData.addString("use-role", "false");
        sectionData.addString("#log-background-activity", "true");

        KeyValuePair kp = new KeyValuePair("cview12", sectionData);

        obj.getOptions().add(kp);

        obj.save();
    }

    /**
     * @param expiration e.g 7d -7 days
     * @throws ConfigException
     */
    public void addCviewSection(String expiration) throws ConfigException {

        // how to add option

        CfgApplicationQuery query = new CfgApplicationQuery(service);
        query.setName(GMSObjName);

        CfgApplication obj = query.executeSingleResult();
        System.out.println(obj);

        KeyValueCollection sectionData = new KeyValueCollection();
        sectionData.addString("allow-custom-ids", "true");
        sectionData.addString("enabled", "true");
        sectionData.addString("data-validation", "true");
        sectionData.addString("use-role", "false");
        sectionData.addString("expiration", expiration);
        sectionData.addString("#log-background-activity", "true");

        KeyValuePair kp = new KeyValuePair("cview", sectionData);

        obj.getOptions().add(kp);

        obj.save();

    }

    /**
     * @param expiration e.g 7d -7 days
     * @throws ConfigException
     */
    public void addCviewSectionInAppCluster(String expiration) throws ConfigException {

        // how to add option

        CfgApplicationQuery query = new CfgApplicationQuery(service);
        query.setName(GMSClusterObjName);

        CfgApplication obj = query.executeSingleResult();
        System.out.println(obj);

        KeyValueCollection sectionData = new KeyValueCollection();
        sectionData.addString("allow-custom-ids", "true");
        sectionData.addString("enabled", "true");
        sectionData.addString("data-validation", "true");
        sectionData.addString("use-role", "false");
        sectionData.addString("expiration", expiration);
        sectionData.addString("#log-background-activity", "true");

        KeyValuePair kp = new KeyValuePair("cview", sectionData);

        obj.getOptions().add(kp);

        obj.save();

    }

    public void addLogSection() throws ConfigException {

        // how to add option

        CfgApplicationQuery query = new CfgApplicationQuery(service);
        query.setName(GMSObjName);

        CfgApplication obj = query.executeSingleResult();
        System.out.println(obj);

        KeyValueCollection sectionData = new KeyValueCollection();
        sectionData.addString("all", "stdout, C:\\Logs\\GMS\\gms_");
        sectionData.addString("buffering", "true");
        sectionData.addString("expire", "false");
        sectionData.addString("segment", "3000");
        sectionData.addString("verbose", "all");

        KeyValuePair kp = new KeyValuePair("log", sectionData);

        obj.getOptions().add(kp);

        obj.save();

    }

    /**
     * @param level : fatal, error, info, warn, debug
     * @throws ConfigException
     */
    public void addLogSection(String level) throws ConfigException {

        // how to add option

        CfgApplicationQuery query = new CfgApplicationQuery(service);
        query.setName(GMSObjName);

        CfgApplication obj = query.executeSingleResult();
        System.out.println(obj);

        KeyValueCollection sectionData = new KeyValueCollection();
        sectionData.addString("all", "stdout, C:\\Logs\\GMS\\gms_");
        sectionData.addString("buffering", "true");
        sectionData.addString("expire", "false");
        sectionData.addString("segment", "false");
        sectionData.addString("verbose", "all");
        sectionData.addString("internal ", level);

        KeyValuePair kp = new KeyValuePair("log", sectionData);

        obj.getOptions().add(kp);

        obj.save();

    }

    public void deleteSection(String sectionName) throws ConfigException {

        // how to add option

        CfgApplicationQuery query = new CfgApplicationQuery(service);
        query.setName(GMSObjName);

        CfgApplication obj = query.executeSingleResult();
        System.out.println(obj);


        obj.getOptions().remove(sectionName);

        obj.save();

    }

    public void deleteOptionFromSection(String sectionName, String option) throws ConfigException {


        CfgApplicationQuery query = new CfgApplicationQuery(service);
        query.setName(GMSObjName);

        CfgApplication obj = query.executeSingleResult();
        //System.out.println(obj);


        obj.getOptions().getList(sectionName).remove(option);

        obj.save();

    }

    public void addOptionInSection(String sectionName, String option, String value) throws ConfigException {


        CfgApplicationQuery query = new CfgApplicationQuery(service);
        query.setName(GMSObjName);

        CfgApplication obj = query.executeSingleResult();

        if (obj.getOptions().getList(sectionName).contains(option)) {
            obj.getOptions().getList(sectionName).remove(option);
        }

        obj.getOptions().getList(sectionName).addString(option, value);

        obj.save();

    }


    public void deleteSectionInAppCluster(String sectionName) throws ConfigException {

        // how to add option

        CfgApplicationQuery query = new CfgApplicationQuery(service);
        query.setName(GMSClusterObjName);

        CfgApplication obj = query.executeSingleResult();
        System.out.println(obj);


        obj.getOptions().remove(sectionName);

        obj.save();

    }

    public void addLogFilterOptions(String sectionName, String key, String value) throws ConfigException {

        // how to add option

        CfgApplicationQuery query = new CfgApplicationQuery(service);
        query.setName(GMSObjName);

        CfgApplication obj = query.executeSingleResult();
        System.out.println(obj);

        KeyValueCollection sectionData = new KeyValueCollection();
        sectionData.addString(key, value);


        KeyValuePair kp = new KeyValuePair(sectionName, sectionData);

        obj.getOptions().add(kp);

        obj.save();

    }

    //todo ???????
    public void addTenantsToAppObject() throws ConfigException {

        CfgApplicationQuery query = new CfgApplicationQuery(service);
        query.setName(GMSObjName);

        CfgApplication obj = query.executeSingleResult();
        System.out.println(obj);


        CfgTenant acme = new CfgTenant(service);
        acme.setName("ACME");
        CfgTenant environment = new CfgTenant(service);
        acme.setName("Environment");

        List<CfgTenant> collection = new ArrayList<>();
        collection.add(acme);
        collection.add(environment);

        obj.setTenants(collection);

        //	Collection<CfgTenant> collection = obj.getTenants();

        obj.save();

    }

    public void createEnumerator(String name, String displayName, String description) {// e.g.
        // service_type
        System.out.println("Creating Enumerator");
        // tenant = CreateTenant(CfgTenant.class.getSimpleName());// ??

        try {
            CfgEnumeratorQuery query = new CfgEnumeratorQuery();
            query.setName(name);
            query.setTenantDbid(1);
            businessAttribute = service.retrieveObject(CfgEnumerator.class,
                    query);

            System.out.println("");

            if (businessAttribute == null) {
                businessAttribute = new CfgEnumerator(service);
                System.out.println("The enumerator '" + query.getName()
                        + " does not exist + + + + + +");
                businessAttribute.setName(query.getName());
                businessAttribute.setTenantDBID(1);
                businessAttribute.setDescription(description);
                businessAttribute
                        .setType(CfgEnumeratorType.CFGENTInteractionOperationalAttribute);// type
                // InteractionOperationalAttribute
                businessAttribute.setDisplayName(displayName);
                businessAttribute.setState(CfgObjectState.CFGEnabled);// state
                // enabled

                service.saveObject(businessAttribute);
                System.out.println("The enumerator '"
                        + businessAttribute.getName() + "' was created.");
            } else {
                System.out.println("The enumerator '"
                        + businessAttribute.getName() + "' already exists.");
                service.refreshObject(businessAttribute);
            }

        } catch (Exception e) {
            System.out.println("Exception while creating enumerator: " + e);
        }
    }

    //todo
    private void CreateEnumeratorValue2(String BAname, String name, String displayName, String description) {// e.g. service1
        System.out.println("Creating EnumeratorValue");

        try {
            CfgEnumeratorValueQuery query = new CfgEnumeratorValueQuery();
            // query.setName(GetName(CfgEnumeratorValue.class.getSimpleName()));
            // query.setDisplayName(value);
            //	query.setName(BAname);
            query.setTenantDbid(1);
            businessAttributeValue = service.retrieveObject(
                    CfgEnumeratorValue.class, query);

            System.out.println("");

            if (businessAttributeValue == null) {
                businessAttributeValue = new CfgEnumeratorValue(service);
                System.out.println("The enumerator '" + query.getName()
                        + " does not exist + + + + + +");
                businessAttributeValue.setName(query.getName());
                businessAttributeValue.setEnumerator(businessAttribute);
                // businessAttributeValue.setTenant();
                businessAttribute.setTenantDBID(1);
                businessAttributeValue.setIsDefault(CfgFlag.CFGFalse);
                businessAttributeValue.setDisplayName(displayName);
                businessAttributeValue.setDescription(description);
                businessAttribute.setName(name);

                service.saveObject(businessAttributeValue);
                System.out.println("The enumerator value '"
                        + businessAttributeValue.getName() + "' was created.");
            } else {
                System.out.println("The enumerator value '"
                        + businessAttributeValue.getName()
                        + "' already exists.");
                service.refreshObject(businessAttributeValue);
            }
            System.out.println(businessAttributeValue);
        } catch (Exception e) {
            System.out.println("Exception while creating enumerator value: "
                    + e);
        }
    }
//	@Test
//	public void test(){
//		Reconfiguration r = new Reconfiguration();
//		r.openConnectionToConfig();
//	//	r.createEnumerator("NewEnum", "New Enum", "My description");
//		r.createEnumeratorValue("MyEnumerator2","MyServiceaaa2", "My Serviceaaa2", "");
//		//r.CreateEnumeratorValue2();
//		r.closeConnectionToConfig();
//	}

    /**
     * @param BAname      name (not display name of BA) e.g ServiceType, StateType
     * @param name        BA value name e.g MyService1, MyService2
     * @param displayName BA value for display name e.g. "My Service1"
     * @param description
     */
    public void createEnumeratorValue(String BAname, String name, String displayName, String description) {// e.g. service1
        System.out.println("Creating EnumeratorValue");

        try {

            // find enumerator
            CfgEnumeratorQuery q = new CfgEnumeratorQuery(service);
            q.setName(BAname);//enumerator
            CfgEnumerator en = q.executeSingleResult();

            // create enumerator value
            CfgEnumeratorValue value = new CfgEnumeratorValue(service);
            value.setName(name);
            value.setDisplayName(displayName);
            value.setIsDefault(CfgFlag.CFGFalse);
            value.setDescription(description);
            value.setEnumeratorDBID(en.getDBID());
            value.save();

            System.out.println("");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param BAname      name (not display name of BA) e.g ServiceType, StateType
     * @param name        BA value name e.g MyService1, MyService2
     * @param displayName BA value for display name e.g. "My Service1"
     * @param description
     * @param isDefault   true for default value
     */
    public void createEnumeratorValue(String BAname, String name, String displayName, String description, boolean isDefault) {// e.g. service1
        System.out.println("Creating EnumeratorValue");

        try {

            // find enumerator
            CfgEnumeratorQuery q = new CfgEnumeratorQuery(service);
            q.setName(BAname);//enumerator
            CfgEnumerator en = q.executeSingleResult();

            // create enumerator value
            CfgEnumeratorValue value = new CfgEnumeratorValue(service);
            value.setName(name);
            value.setDisplayName(displayName);
            if (isDefault) {
                value.setIsDefault(CfgFlag.CFGTrue);
            } else {
                value.setIsDefault(CfgFlag.CFGFalse);
            }
            value.setDescription(description);
            value.setEnumeratorDBID(en.getDBID());
            value.save();

            System.out.println("");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CfgTenant CreateTenant(String name) {
        System.out.println("Creating Tenant: " + name);

        CfgTenant tempTenant = null;
        try {

            CfgTenantQuery query = new CfgTenantQuery();
            query.setName(name);

            tempTenant = service.retrieveObject(CfgTenant.class, query);// ??

            if (tempTenant == null) {
                tempTenant = new CfgTenant(service);

                tempTenant.setName(query.getName());
                tempTenant.setState(CfgObjectState.CFGEnabled);
                System.out.println("The tenant '" + query.getName()
                        + "' does not exit!");

                service.saveObject(tempTenant);

                System.out.println("The tenant '" + tempTenant.getName()
                        + "' has been created.");
            } else {
                System.out.println("The tenant '" + tempTenant.getName()
                        + "' already exists.");
                service.refreshObject(tempTenant);
            }

        } catch (Exception e) {
            System.out.println("Exception while creating tenant:"
                    + e.toString());
        }
        return tempTenant;
    }

    @Deprecated
    public void addServiceHello() throws ConfigException {
        // how to add option
        CfgApplicationQuery query = new CfgApplicationQuery(service);
        query.setName(GMSObjName);

        CfgApplication obj = query.executeSingleResult();
        System.out.println(obj);

        KeyValueCollection sectionData = new KeyValueCollection();
        sectionData.addString("_name", "The Hello World Service");
        sectionData.addString("_ors", "http://135.17.36.157:7022");
        sectionData.addString("_ors_lb_strategy", "linear");
        sectionData.addString("_service", "hello");
        sectionData.addString("_ttl", "3600");
        sectionData.addString("_type", "ors");

        KeyValuePair kp = new KeyValuePair("service.hello", sectionData);

        obj.getOptions().add(kp);
        obj.save();
    }

    @Deprecated
    public void addORSSection(String maxORSRequestAttempts) throws ConfigException {
        // how to add option
        CfgApplicationQuery query = new CfgApplicationQuery(service);
        query.setName(GMSObjName);

        CfgApplication obj = query.executeSingleResult();
        System.out.println(obj);

        KeyValueCollection sectionData = new KeyValueCollection();
        sectionData.addString("max_ors_request_attempts", maxORSRequestAttempts);

        KeyValuePair kp = new KeyValuePair("ors", sectionData);

        obj.getOptions().add(kp);
        obj.save();
    }

    public void addSection(String appName, String sectionName, KeyValueCollection sectOptions) throws AtsCfgComponentException {
        cfgManager.getAppApi().setApp(appName).getOptionsApi().addSection(sectionName, sectOptions);
    }

    public void createService(String appName, String serviceName, KeyValueCollection serviceOptions) throws AtsCfgComponentException {
        addSection(appName, "service." + serviceName, serviceOptions);
    }

    public void deactivate() {
        cfgManager.deactivate();
    }

    public void deleteSection(String appName, String sectionName) throws AtsCfgComponentException {
        cfgManager.getAppApi().setApp(appName).getOptionsApi().setSection(sectionName).deleteSection();
    }

    public void deleteService(String appName, String serviceName) throws AtsCfgComponentException {
        deleteSection(appName, "service." + serviceName);
    }

    public void deleteAllSections(String appName) throws Exception {
        cfgManager.getAppApi().setApp(appName).getOptionsApi().deleteAllSections();
    }

    public void deleteAllServices(String appName) throws AtsCfgComponentException {
        KeyValueCollection sections = cfgManager.getAppApi().setApp(appName).getOptionsApi().getAppOptions();
        String string = sections.toString();
        List<String> services = getAllMatchesInString(string, "(?<=')(service\\..*)(?=')");
        if (!services.isEmpty()) {
            for (String service : services) {
                deleteSection(appName, service);
            }
        } else {
            System.out.printf("There are no services in %s!!!%n", appName);
        }
    }

    public void deleteOptionFromSection(String appName, String sectionName, String key) throws AtsCfgComponentException {
        cfgManager.getAppApi().setApp(appName).getOptionsApi().setSection(sectionName).deleteOption(key);
    }

    public void deleteOptionFromService(String appName, String serviceName, String key) throws AtsCfgComponentException {
        deleteOptionFromSection(appName, "service." + serviceName, key);
    }

    public void changeOptionValueInSection(String appName, String sectionName, String key, String val) throws Exception {
        cfgManager.getAppApi().setApp(appName).getOptionsApi().setSection(sectionName).changeOptionValue(key, val);
    }

    public void changeOptionValueInService(String appName, String serviceName, String key, String val) throws Exception {
        changeOptionValueInSection(appName, "service." + serviceName, key, val);
    }

    public void startApplication(String appName) throws AtsOperationFailed {
        scsManager.startApplication(appName);
    }

    public void stopApplication(String appName) throws AtsOperationFailed, ProtocolException {
        scsManager.stopApplication(appName);
    }

    public void restartApplication(String appName) throws AtsOperationFailed, ProtocolException {
        scsManager.restartApplication(appName);
    }

    public void restartApplicationWithDelay(String appName, long delay) throws AtsOperationFailed, ProtocolException, InterruptedException {
        System.out.printf("Stopping %s application...%n", appName);
        scsManager.stopApplication(appName);
        System.out.printf("Waiting %s seconds until time coming...%n", (delay / 1000));
        Thread.sleep(delay);
        System.out.printf("Starting %s application...%n", appName);
        scsManager.startApplication(appName);
    }

    public void addOptionToSection(String appName, String sectionName, String key, String val) throws Exception {
        cfgManager.getAppApi().setApp(appName).getOptionsApi().setSection(sectionName).addOption(key, val);
    }

    public void addOptionToService(String appName, String serviceName, String key, String val) throws Exception {
        addOptionToSection(appName, "service." + serviceName, key, val);
    }

    public void createBaseGMSServices(String appName) throws AtsCfgComponentException {
        createService(appName, "bh_24x7", getRegularOfficeHours());
        createService(appName, "cap_1000x24x7", getCapacityOptions());
        createService(appName, "gms-status", getGETServiceOptions());
        createService(appName, "match-interaction", getMatchInteractionServiceOptions());
        createService(appName, "request-access", getRequestAccessServiceOptions());
        createService(appName, "request-chat", getRequestChatServiceOptions());
        createService(appName, "request-interaction", getRequestInteractionServiceOptions());
    }

    public void createTransaction(String transactionName, String transactionType) throws Exception {
        cfgManager.getTransactionApi().setTransactionName(transactionName).setTransactionType(transactionType)
                .createNewTransaction();
  }
    
    public void createTransaction(String transactionName, String transactionType, String sectionName, String optionName, String optionValue) throws Exception {
  	  cfgManager.getTransactionApi().setTransactionName(transactionName).setTransactionType(transactionType).addUserPropertyValue(sectionName, optionName, optionValue)
        .createNewTransaction();

  }
    
    public void addSectionToTransaction(String transactionName, String sectionName) throws Exception {
   	  cfgManager.getTransactionApi().setTransactionName(transactionName).getAnnexApi().addTransactionSection(sectionName); 

   }
    
    //transaction object with corresponding section's name must be created before adding option
    public void addOptionToTransaction(String transactionName, String sectionName, String optionName, String optionValue) throws Exception {
      cfgManager.getTransactionApi().setTransactionName(transactionName).addUserPropertyValue(sectionName, optionName, optionValue);
    	  
    }

    public void deleteTransaction(String transactionName) throws Exception {
        cfgManager.getTransactionApi().deleteTransaction(transactionName);
    }
}