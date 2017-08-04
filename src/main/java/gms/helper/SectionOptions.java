package gms.helper;

import com.genesyslab.platform.commons.collections.KeyValueCollection;

/**
 * Created by bvolovyk on 03.12.2016.
 */
public class SectionOptions {
    //you should consider this set of options as default set and shouldn't change it since related TC's will be failed
    public static KeyValueCollection getGMSCallbackSectionOptions() {
        KeyValueCollection callbackSectionOptionList = new KeyValueCollection();
        callbackSectionOptionList.addString("log-background-activity", "false");
        callbackSectionOptionList.addString("queue-polling-rate", "60");
        callbackSectionOptionList.addString("queue-polling-rate-recover", "3600");
        callbackSectionOptionList.addString("_enable_in_queue_checking", "true");
        callbackSectionOptionList.addString("_throttle_callbacks_per_service_1", "500");
        callbackSectionOptionList.addString("_throttle_ttl_1", "300");
        callbackSectionOptionList.addString("_throttle_callbacks_per_service_2", "1000");
        callbackSectionOptionList.addString("_throttle_ttl_2", "3600");
//        callbackSectionOptionList.addString("_throttle_customer_number_limit", "600"); //default "6", for GMS up to 8.5.110.00 use this option instead "_throttle_request_parameters_limit"
        callbackSectionOptionList.addString("_throttle_request_parameters_limit", "600"); //default "6", since GMS 8.5.110.00
        callbackSectionOptionList.addString("_throttle_request_parameters", "_customer_number");
        return callbackSectionOptionList;
    }

    public static KeyValueCollection getGMSCviewSectionOptions() {
        KeyValueCollection cviewSectionOptionList = new KeyValueCollection();
        cviewSectionOptionList.addString("allow-custom-ids", "false"); //default "false"
        cviewSectionOptionList.addString("data-validation", "false"); //default "false"
        cviewSectionOptionList.addString("enabled", "false"); //default "false"
        cviewSectionOptionList.addString("expiration", "5y");
        cviewSectionOptionList.addString("use-role", "false"); //default "false"
        return cviewSectionOptionList;
    }

    public static KeyValueCollection getPersonGMSSection() {
        KeyValueCollection gmsSectionOptionList = new KeyValueCollection();
        gmsSectionOptionList.addString("roles", "Administrator");
        KeyValueCollection personGMSSection = new KeyValueCollection();
        personGMSSection.addList("gms", gmsSectionOptionList);
        return personGMSSection;
    }
}
