package gms.helper;

import com.genesyslab.platform.commons.collections.KeyValueCollection;

import static com.genesyslab.functional.tests.gms.helper.PropertiesInitialization.getPropertiesFile;
import static com.genesyslab.functional.tests.gms.helper.TimeOperations.getLocalTimeZone;
import static com.genesyslab.functional.tests.gms.helper.URLsHelper.getORSBaseURL;

/**
 * Created by bvolovyk on 01.12.2016.
 */
public class ServiceOptions {
    //you should consider this set of options as default set and shouldn't change it since related TC's will be failed
    public static KeyValueCollection getUserTermSchCallbackServiceOptions() {
        //as example was taken User Terminated Immediate Callback template from GMS 8.5.110.01
        KeyValueCollection callbackServiceOptionList = new KeyValueCollection();
        callbackServiceOptionList.addString("_agent_availability_notification_delay", "30");
        callbackServiceOptionList.addString("_agent_disposition_timeout", "45");
        callbackServiceOptionList.addString("_agent_first_via_rp", "false");
        callbackServiceOptionList.addString("_agent_preview", "false");
        callbackServiceOptionList.addString("_agent_preview_allow_reject", "0");
        callbackServiceOptionList.addString("_agent_preview_data", "Value 1,Value 2,Value 3,Value 4,Value 5");
        callbackServiceOptionList.addString("_agent_preview_timeout", "30");
        callbackServiceOptionList.addString("_agent_preview_via_rp", "false");
        callbackServiceOptionList.addString("_agent_reject_retry_timeout", "0");
        callbackServiceOptionList.addString("_agent_reserve_timeout", "30");
        callbackServiceOptionList.addString("_agent_transfer_confirm_timeout", "0");
        callbackServiceOptionList.addString("_attach_udata", "single_json");
        callbackServiceOptionList.addString("_booking_expiration_timeout", "30");
        callbackServiceOptionList.addString("_business_hours_service", "bh_24x7"); //default ""
        callbackServiceOptionList.addString("_call_direction", "USERTERMINATED");
        callbackServiceOptionList.addString("_call_timeguard_timeout", "15000");
        callbackServiceOptionList.addString("_callback_events_list", "");
        callbackServiceOptionList.addString("_calling_party_display_name", "");
        callbackServiceOptionList.addString("_calling_party_number", "");
        callbackServiceOptionList.addString("_capacity_service", "cap_1000x24x7");
        callbackServiceOptionList.addString("_chat_endpoint", "");
        callbackServiceOptionList.addString("_cpd_enable", "false"); //default "true"
        callbackServiceOptionList.addString("_customer_lookup_keys", "_customer_number");
        callbackServiceOptionList.addString("_default_country", "US");
        callbackServiceOptionList.addString("_desired_connect_time_threshold", "180");
        callbackServiceOptionList.addString("_dial_retry_timeout", "300");
        callbackServiceOptionList.addString("_disallow_impossible_phone_numbers", "false"); //default "true"
        callbackServiceOptionList.addString("_disallow_premium_phone_numbers", "false"); //default "true"
        callbackServiceOptionList.addString("_disposition_userevent_mediatype", "0");
        callbackServiceOptionList.addString("_enable_disposition_dialog", "false");
        callbackServiceOptionList.addString("_enable_in_queue_checking", "false"); //default "true"
        callbackServiceOptionList.addString("_enable_status_notification", "false");
        callbackServiceOptionList.addString("_eta_pos_threshold", "0:10,10:5,20:2,30:1,40:0");
        callbackServiceOptionList.addString("_exceptions", "");
        callbackServiceOptionList.addString("_ixn_createcall_hints", "");
        callbackServiceOptionList.addString("_ixn_createcall_timeout", "32");
        callbackServiceOptionList.addString("_ixn_redirect_confirm", "true");
        callbackServiceOptionList.addString("_ixn_redirect_hints", "");
        callbackServiceOptionList.addString("_mandatory_customer_lookup_keys", "_customer_number");
        callbackServiceOptionList.addString("_max_dial_attempts", "3");
        callbackServiceOptionList.addString("_max_notify_delivery_attempts", "3");
        callbackServiceOptionList.addString("_max_number_of_user_availability_confirmation_attempts", "3");
        callbackServiceOptionList.addString("_max_ors_submit_attempts", "3");
        callbackServiceOptionList.addString("_max_queued_callbacks_per_service", "1000");
        callbackServiceOptionList.addString("_max_request_by_time_bucket", "100");
        callbackServiceOptionList.addString("_max_time_to_reach_eta_pos_threshold", "14160");
        callbackServiceOptionList.addString("_max_time_to_wait_for_agent_on_the_call", "3600");
        callbackServiceOptionList.addString("_max_time_to_wait_for_ixn_delete", "3600");
        callbackServiceOptionList.addString("_max_transfer_to_agent_attempts", "5");
        callbackServiceOptionList.addString("_max_urs_ewt_pos_polling_interval", "30");
        callbackServiceOptionList.addString("_media_type", "voice");
        callbackServiceOptionList.addString("_min_queue_wait", "0");
        callbackServiceOptionList.addString("_min_urs_ewt_pos_polling_interval", "2");
        callbackServiceOptionList.addString("_notification_message_file", "");
        callbackServiceOptionList.addString("_offer_callback", "false");
        callbackServiceOptionList.addString("_offer_callback_vxml_app_url", "");
        callbackServiceOptionList.addString("_on_route_done_delay", "0");
        callbackServiceOptionList.addString("_on_user_confirm_timeout", "CONNECT-ANYWAY");
        callbackServiceOptionList.addString("_paused_services_id", "");
        callbackServiceOptionList.addString("_paused_services_list", "GMS_Paused_Services");
        callbackServiceOptionList.addString("_plugin_on_dial_associate_ixn", "false"); //default "true"
        callbackServiceOptionList.addString("_plugin_on_dial_invoke_on_call_failed", "false"); //default "true"
        callbackServiceOptionList.addString("_plugin_on_dial_timeout", "120");
        callbackServiceOptionList.addString("_plugin_on_dial_url", "");
        callbackServiceOptionList.addString("_prefix_dial_out", ""); //default "91"
        callbackServiceOptionList.addString("_preview_userevent_mediatype", "0");
        callbackServiceOptionList.addString("_provide_code", "false");
        callbackServiceOptionList.addString("_provider_name", "");
        callbackServiceOptionList.addString("_queue_ping_ors_period", "-120,-2");
        callbackServiceOptionList.addString("_queue_poll_period", "-120,120");
        callbackServiceOptionList.addString("_queue_poll_period_recovery", "-1440,-120");
        callbackServiceOptionList.addString("_reject_future_desired_time", "1M");
        callbackServiceOptionList.addString("_rep_userevent_dn", "");
        callbackServiceOptionList.addString("_rep_userevent_enable", "false");
        callbackServiceOptionList.addString("_rep_userevent_mediatype", "0");
        callbackServiceOptionList.addString("_rep_userevent_switch", "");
        callbackServiceOptionList.addString("_reporting_aggregator_url", "");
        callbackServiceOptionList.addString("_request_ewt_service", "");
        callbackServiceOptionList.addString("_request_execution_time_buffer", "120");
        callbackServiceOptionList.addString("_request_queue_time_stat", "");
        callbackServiceOptionList.addString("_request_time_bucket", "5");
        callbackServiceOptionList.addString("_resource_group", "DNIS"); //default "{name of the resource pool to be used - configured under Transactions/GMS_Resources/Annex}"
        callbackServiceOptionList.addString("_retain_session_until_ixn_deleted", "true"); //default "false" (changed to "true" to provide PROCESSING state)
        callbackServiceOptionList.addString("_route_point", "8999@SIP_Switch"); //default "{Route Point}@{Telephony Switch}"
        callbackServiceOptionList.addString("_service", "callback");
        callbackServiceOptionList.addString("_snooze_duration", "300");
        callbackServiceOptionList.addString("_src_route_point", "");
        callbackServiceOptionList.addString("_status_notification_provider", "");
        callbackServiceOptionList.addString("_status_notification_target", "http://135.17.38.71:1664/test"); //default ""
        callbackServiceOptionList.addString("_status_notification_type", "httpcb");
        callbackServiceOptionList.addString("_target", "Customer_Service@Stat_Server.GA"); //default "{<Target String>@<StatServer name>.<Target Type> Example: Billing@Stat_Server.GA - Agent group 'Billing'}"
        callbackServiceOptionList.addString("_throttle_callbacks_per_service_1", "500");
        callbackServiceOptionList.addString("_throttle_callbacks_per_service_2", "1000");
//        callbackServiceOptionList.addString("_throttle_customer_number_limit", "600"); //default "6", for GMS up to 8.5.110.00 use this option instead "_throttle_request_parameters_limit"
        callbackServiceOptionList.addString("_throttle_request_parameters_limit", "600"); //default "6", since GMS 8.5.110.00
        callbackServiceOptionList.addString("_throttle_request_parameters", "_customer_number");
        callbackServiceOptionList.addString("_throttle_ttl_1", "300");
        callbackServiceOptionList.addString("_throttle_ttl_2", "3600");
//        callbackServiceOptionList.addString("_transfer_to_agent_retry_timeout", ""); //removed
        callbackServiceOptionList.addString("_treatment_call_failure_answering_machine", "");
        callbackServiceOptionList.addString("_treatment_customer_connect", "");
        callbackServiceOptionList.addString("_treatment_find_agent_fail", "");
        callbackServiceOptionList.addString("_treatment_waiting_for_agent", "");
        callbackServiceOptionList.addString("_ttl", "86400");
        callbackServiceOptionList.addString("_type", "ors");
        callbackServiceOptionList.addString("_urs_call_interaction_age", "");
        callbackServiceOptionList.addString("_urs_ewt_estimation_method", "ursdial");
        callbackServiceOptionList.addString("_urs_extension_data", "");
        callbackServiceOptionList.addString("_urs_prioritization_strategy", "WaitForTarget");
        callbackServiceOptionList.addString("_urs_queued_ttl", "14400");
        callbackServiceOptionList.addString("_urs_request_timeout", "100");
//        callbackServiceOptionList.addString("_urs_sec_server_url", ""); //removed
//        callbackServiceOptionList.addString("_urs_server_url", "http://localhost:7311"); //removed
        callbackServiceOptionList.addString("_urs_strategy_update_sub_routine", "SetRouteDelay");
        callbackServiceOptionList.addString("_urs_udata_xfer_keys", "");
        callbackServiceOptionList.addString("_urs_virtual_queue", "SIP_VQ_SIP_Switch"); //default "{Specify virtual queue to be used by strategy}"
        callbackServiceOptionList.addString("_urs_vq_priority", "");
        callbackServiceOptionList.addString("_urs_vq_priority_increment", "");
        callbackServiceOptionList.addString("_urs_vq_priority_increment_interval", "");
        callbackServiceOptionList.addString("_use_debug_push_certificate", "false");
        callbackServiceOptionList.addString("_use_reporting_aggregator", "");
        callbackServiceOptionList.addString("_user_confirm_timeout", "30");
        callbackServiceOptionList.addString("_userorig_connect_limit", "3");
        callbackServiceOptionList.addString("_userterminated_first_connect_party", "CUSTOMER");
        callbackServiceOptionList.addString("_vq_for_outbound_calls", "");
        callbackServiceOptionList.addString("_wait_for_agent", "true"); //default "false"
        callbackServiceOptionList.addString("_wait_for_user_confirm", "true"); //default "false"
        return callbackServiceOptionList;
    }

    public static KeyValueCollection getCallbackOrigServiceOptions() {
        KeyValueCollection callbackServiceOptionList = new KeyValueCollection();
        callbackServiceOptionList.addString("_agent_availability_notification_delay", "30");
        callbackServiceOptionList.addString("_agent_disposition_timeout", "45");
        callbackServiceOptionList.addString("_agent_preview", "false");
        callbackServiceOptionList.addString("_agent_preview_allow_reject", "0");
        callbackServiceOptionList.addString("_agent_preview_data", "Value 1,Value 2,Value 3,Value 4,Value 5");
        callbackServiceOptionList.addString("_agent_preview_timeout", "30");
        callbackServiceOptionList.addString("_agent_preview_via_rp", "false");
        callbackServiceOptionList.addString("_agent_reject_retry_timeout", "0");
        callbackServiceOptionList.addString("_agent_reserve_timeout", "30");
        callbackServiceOptionList.addString("_attach_udata", "single_json");
        callbackServiceOptionList.addString("_booking_expiration_timeout", "30");
        callbackServiceOptionList.addString("_business_hours_service", "business_hours");
        callbackServiceOptionList.addString("_call_direction", "USERORIGINATED");
        callbackServiceOptionList.addString("_call_timeguard_timeout", "15000");
        callbackServiceOptionList.addString("_callback_events_list", "");
        callbackServiceOptionList.addString("_calling_party_display_name", "");
        callbackServiceOptionList.addString("_calling_party_number", "");
        callbackServiceOptionList.addString("_capacity_service", "cap1");
        callbackServiceOptionList.addString("_chat_endpoint", "");
        callbackServiceOptionList.addString("_cpd_enable", "false");
        callbackServiceOptionList.addString("_customer_lookup_keys", "_customer_number");
        callbackServiceOptionList.addString("_desired_connect_time_threshold", "180");
        callbackServiceOptionList.addString("_default_country", "US");
        callbackServiceOptionList.addString("_disallow_impossible_phone_numbers", "false");
        callbackServiceOptionList.addString("_disallow_premium_phone_numbers", "false");
        callbackServiceOptionList.addString("_dial_retry_timeout", "300");
        callbackServiceOptionList.addString("_disposition_userevent_mediatype", "0");
        callbackServiceOptionList.addString("_enable_disposition_dialog", "false");
        callbackServiceOptionList.addString("_enable_in_queue_checking", "false");
        callbackServiceOptionList.addString("_enable_status_notification", "subscribe_notify");
        callbackServiceOptionList.addString("_eta_pos_threshold", "0:10,10:5,20:2,30:1,40:0");
        callbackServiceOptionList.addString("_exceptions", "");
        callbackServiceOptionList.addString("_ixn_createcall_hints", "");
        callbackServiceOptionList.addString("_ixn_createcall_timeout", "15");
        callbackServiceOptionList.addString("_ixn_redirect_confirm", "true");
        callbackServiceOptionList.addString("_ixn_redirect_hints", "");
        callbackServiceOptionList.addString("_mandatory_customer_lookup_keys", "_customer_number");
        callbackServiceOptionList.addString("_max_dial_attempts", "3");
        callbackServiceOptionList.addString("_max_notify_delivery_attempts", "3");
        callbackServiceOptionList.addString("_max_number_of_user_availability_confirmation_attempts", "3");
        callbackServiceOptionList.addString("_max_ors_submit_attempts", "3");
        callbackServiceOptionList.addString("_max_request_by_time_bucket", "100");
        callbackServiceOptionList.addString("_max_time_to_reach_eta_pos_threshold", "3600");
        callbackServiceOptionList.addString("_max_time_to_wait_for_agent_on_the_call", "3600");
        callbackServiceOptionList.addString("_max_time_to_wait_for_ixn_delete", "3600");
        callbackServiceOptionList.addString("_max_transfer_to_agent_attempts", "5");
        callbackServiceOptionList.addString("_max_urs_ewt_pos_polling_interval", "30");
        callbackServiceOptionList.addString("_media_type", "voice");
        callbackServiceOptionList.addString("_min_queue_wait", "0");
        callbackServiceOptionList.addString("_min_urs_ewt_pos_polling_interval", "2");
        callbackServiceOptionList.addString("_notification_message_file", "");
        callbackServiceOptionList.addString("_offer_callback", "false");
        callbackServiceOptionList.addString("_offer_callback_vxml_app_url", "");
        callbackServiceOptionList.addString("_on_user_confirm_timeout", "CONNECT-ANYWAY");
        callbackServiceOptionList.addString("_plugin_on_dial_associate_ixn", "false");
        callbackServiceOptionList.addString("_plugin_on_dial_invoke_on_call_failed", "false");
        callbackServiceOptionList.addString("_plugin_on_dial_timeout", "20");
        callbackServiceOptionList.addString("_plugin_on_dial_url", "");
        callbackServiceOptionList.addString("_prefix_dial_out", "");
        callbackServiceOptionList.addString("_preview_userevent_mediatype", "0");
        callbackServiceOptionList.addString("_provide_code", "false");
        callbackServiceOptionList.addString("_provider_name", "");
        callbackServiceOptionList.addString("_queue_ping_ors_period", "-120,-2");
        callbackServiceOptionList.addString("_queue_poll_period", "-120,120");
        callbackServiceOptionList.addString("_queue_poll_period_recovery", "-1440,-120");
        callbackServiceOptionList.addString("_reject_future_desired_time", "1M");
        callbackServiceOptionList.addString("_rep_userevent_dn", "");
        callbackServiceOptionList.addString("_rep_userevent_enable", "false");
        callbackServiceOptionList.addString("_rep_userevent_mediatype", "0");
        callbackServiceOptionList.addString("_rep_userevent_switch", "");
        callbackServiceOptionList.addString("_reporting_aggregator_url", "");
        callbackServiceOptionList.addString("_request_execution_time_buffer", "120");
        callbackServiceOptionList.addString("_request_queue_time_stat", "");
        callbackServiceOptionList.addString("_request_time_bucket", "5");
        callbackServiceOptionList.addString("_resource_group", "DNIS");
        callbackServiceOptionList.addString("_route_point", "8999@SIP_Switch");
        callbackServiceOptionList.addString("_service", "callback");
        callbackServiceOptionList.addString("_snooze_duration", "300");
        callbackServiceOptionList.addString("_src_route_point", "");
        callbackServiceOptionList.addString("_status_notification_provider", "");
        callbackServiceOptionList.addString("_status_notification_target", "http://135.17.38.71:1664/test");
        callbackServiceOptionList.addString("_status_notification_type", "httpcb");
        callbackServiceOptionList.addString("_target", "Customer_Service@Stat_Server.GA");
        callbackServiceOptionList.addString("_throttle_customer_number_limit", "600");
        callbackServiceOptionList.addString("_transfer_to_agent_retry_timeout", "");
        callbackServiceOptionList.addString("_treatment_call_failure_answering_machine", "");
        callbackServiceOptionList.addString("_treatment_customer_connect", "");
        callbackServiceOptionList.addString("_treatment_find_agent_fail", "");
        callbackServiceOptionList.addString("_treatment_waiting_for_agent", "");
        callbackServiceOptionList.addString("_ttl", "86400");
        callbackServiceOptionList.addString("_type", "ors");
        callbackServiceOptionList.addString("_urs_call_interaction_age", "");
        callbackServiceOptionList.addString("_urs_ewt_estimation_method", "ursdial");
        callbackServiceOptionList.addString("_urs_extension_data", "");
        callbackServiceOptionList.addString("_urs_prioritization_strategy", "WaitForTarget");
        callbackServiceOptionList.addString("_urs_queued_ttl", "660");
        callbackServiceOptionList.addString("_urs_request_timeout", "100");
        callbackServiceOptionList.addString("_urs_sec_server_url", "");
        callbackServiceOptionList.addString("_urs_server_url", "http://localhost:7311");
        callbackServiceOptionList.addString("_urs_strategy_update_sub_routine", "SetRouteDelay");
        callbackServiceOptionList.addString("_urs_udata_xfer_keys", "");
        callbackServiceOptionList.addString("_urs_virtual_queue", "SIP_VQ_SIP_Switch");
        callbackServiceOptionList.addString("_urs_vq_priority", "");
        callbackServiceOptionList.addString("_urs_vq_priority_increment", "");
        callbackServiceOptionList.addString("_urs_vq_priority_increment_interval", "");
        callbackServiceOptionList.addString("_use_debug_push_certificate", "false");
        callbackServiceOptionList.addString("_use_reporting_aggregator", "");
        callbackServiceOptionList.addString("_user_confirm_timeout", "30");
        callbackServiceOptionList.addString("_userorig_connect_limit", "3");
        callbackServiceOptionList.addString("_userterminated_first_connect_party", "CUSTOMER");
        callbackServiceOptionList.addString("_vq_for_outbound_calls", "");
        callbackServiceOptionList.addString("_wait_for_agent", "false");
        callbackServiceOptionList.addString("_wait_for_user_confirm", "false");
        return callbackServiceOptionList;
    }

    /**
     * @deprecated use {@link ServiceOptions#getUserTermImmCallbackServiceOptions()} instead.
     */
    public static KeyValueCollection getCallbackTermServiceOptions() {
        KeyValueCollection callbackServiceOptionList = new KeyValueCollection();
        callbackServiceOptionList.addString("_agent_availability_notification_delay", "30");
        callbackServiceOptionList.addString("_agent_disposition_timeout", "45");
        callbackServiceOptionList.addString("_agent_preview", "false");
        callbackServiceOptionList.addString("_agent_preview_allow_reject", "0");
        callbackServiceOptionList.addString("_agent_preview_data", "Value 1,Value 2,Value 3,Value 4,Value 5");
        callbackServiceOptionList.addString("_agent_preview_timeout", "30");
        callbackServiceOptionList.addString("_agent_preview_via_rp", "false");
        callbackServiceOptionList.addString("_agent_reject_retry_timeout", "0");
        callbackServiceOptionList.addString("_agent_reserve_timeout", "30");
        callbackServiceOptionList.addString("_attach_udata", "single_json");
        callbackServiceOptionList.addString("_booking_expiration_timeout", "30");
        callbackServiceOptionList.addString("_business_hours_service", "business_hours");
        callbackServiceOptionList.addString("_call_direction", "USERTERMINATED");
        callbackServiceOptionList.addString("_call_timeguard_timeout", "15000");
        callbackServiceOptionList.addString("_callback_events_list", "");
        callbackServiceOptionList.addString("_calling_party_display_name", "");
        callbackServiceOptionList.addString("_calling_party_number", "");
        callbackServiceOptionList.addString("_capacity_service", "cap1");
        callbackServiceOptionList.addString("_chat_endpoint", "");
        callbackServiceOptionList.addString("_cpd_enable", "true");
        callbackServiceOptionList.addString("_customer_lookup_keys", "_customer_number");
        callbackServiceOptionList.addString("_desired_connect_time_threshold", "180");
        callbackServiceOptionList.addString("_default_country", "US");
        callbackServiceOptionList.addString("_disallow_impossible_phone_numbers", "false");
        callbackServiceOptionList.addString("_disallow_premium_phone_numbers", "false");
        callbackServiceOptionList.addString("_dial_retry_timeout", "300");
        callbackServiceOptionList.addString("_disposition_userevent_mediatype", "0");
        callbackServiceOptionList.addString("_enable_disposition_dialog", "false");
        callbackServiceOptionList.addString("_enable_in_queue_checking", "false");
        callbackServiceOptionList.addString("_enable_status_notification", "false");
        callbackServiceOptionList.addString("_eta_pos_threshold", "0:10,10:5,20:2,30:1,40:0");
        callbackServiceOptionList.addString("_exceptions", "");
        callbackServiceOptionList.addString("_ixn_createcall_hints", "");
        callbackServiceOptionList.addString("_ixn_createcall_timeout", "32");
        callbackServiceOptionList.addString("_ixn_redirect_confirm", "true");
        callbackServiceOptionList.addString("_ixn_redirect_hints", "");
        callbackServiceOptionList.addString("_mandatory_customer_lookup_keys", "_customer_number");
        callbackServiceOptionList.addString("_max_dial_attempts", "3");
        callbackServiceOptionList.addString("_max_notify_delivery_attempts", "3");
        callbackServiceOptionList.addString("_max_number_of_user_availability_confirmation_attempts", "3");
        callbackServiceOptionList.addString("_max_ors_submit_attempts", "3");
        callbackServiceOptionList.addString("_max_request_by_time_bucket", "100");
        callbackServiceOptionList.addString("_max_time_to_reach_eta_pos_threshold", "3600");
        callbackServiceOptionList.addString("_max_time_to_wait_for_agent_on_the_call", "3600");
        callbackServiceOptionList.addString("_max_time_to_wait_for_ixn_delete", "3600");
        callbackServiceOptionList.addString("_max_transfer_to_agent_attempts", "5");
        callbackServiceOptionList.addString("_max_urs_ewt_pos_polling_interval", "30");
        callbackServiceOptionList.addString("_media_type", "voice");
        callbackServiceOptionList.addString("_min_queue_wait", "0");
        callbackServiceOptionList.addString("_min_urs_ewt_pos_polling_interval", "2");
        callbackServiceOptionList.addString("_notification_message_file", "");
        callbackServiceOptionList.addString("_offer_callback", "false");
        callbackServiceOptionList.addString("_offer_callback_vxml_app_url", "");
        callbackServiceOptionList.addString("_on_user_confirm_timeout", "CONNECT-ANYWAY");
        callbackServiceOptionList.addString("_plugin_on_dial_associate_ixn", "true");
        callbackServiceOptionList.addString("_plugin_on_dial_invoke_on_call_failed", "true");
        callbackServiceOptionList.addString("_plugin_on_dial_timeout", "120");
        callbackServiceOptionList.addString("_plugin_on_dial_url", "");
        callbackServiceOptionList.addString("_prefix_dial_out", "91");
        callbackServiceOptionList.addString("_preview_userevent_mediatype", "0");
        callbackServiceOptionList.addString("_provide_code", "false");
        callbackServiceOptionList.addString("_provider_name", "");
        callbackServiceOptionList.addString("_queue_ping_ors_period", "-120,-2");
        callbackServiceOptionList.addString("_queue_poll_period", "-120,120");
        callbackServiceOptionList.addString("_queue_poll_period_recovery", "-1440,-120");
        callbackServiceOptionList.addString("_reject_future_desired_time", "1M");
        callbackServiceOptionList.addString("_rep_userevent_dn", "");
        callbackServiceOptionList.addString("_rep_userevent_enable", "false");
        callbackServiceOptionList.addString("_rep_userevent_mediatype", "0");
        callbackServiceOptionList.addString("_rep_userevent_switch", "");
        callbackServiceOptionList.addString("_reporting_aggregator_url", "");
        callbackServiceOptionList.addString("_request_execution_time_buffer", "120");
        callbackServiceOptionList.addString("_request_queue_time_stat", "");
        callbackServiceOptionList.addString("_request_time_bucket", "5");
        callbackServiceOptionList.addString("_resource_group", "DNIS");
        callbackServiceOptionList.addString("_route_point", "8999@SIP_Switch");
        callbackServiceOptionList.addString("_service", "callback");
        callbackServiceOptionList.addString("_snooze_duration", "300");
        callbackServiceOptionList.addString("_src_route_point", "");
        callbackServiceOptionList.addString("_status_notification_provider", "");
        callbackServiceOptionList.addString("_status_notification_target", "");
        callbackServiceOptionList.addString("_status_notification_type", "");
        callbackServiceOptionList.addString("_target", "[{'target':'Customer_Service@Stat_Server.GA','timeout':'15','clear':true}]");
        callbackServiceOptionList.addString("_throttle_customer_number_limit", "600");
        callbackServiceOptionList.addString("_transfer_to_agent_retry_timeout", "");
        callbackServiceOptionList.addString("_treatment_call_failure_answering_machine", "");
        callbackServiceOptionList.addString("_treatment_customer_connect", "");
        callbackServiceOptionList.addString("_treatment_find_agent_fail", "");
        callbackServiceOptionList.addString("_treatment_waiting_for_agent", "");
        callbackServiceOptionList.addString("_ttl", "86400");
        callbackServiceOptionList.addString("_type", "ors");
        callbackServiceOptionList.addString("_urs_call_interaction_age", "");
        callbackServiceOptionList.addString("_urs_ewt_estimation_method", "ursdial");
        callbackServiceOptionList.addString("_urs_extension_data", "");
        callbackServiceOptionList.addString("_urs_prioritization_strategy", "WaitForTarget");
        callbackServiceOptionList.addString("_urs_queued_ttl", "660");
        callbackServiceOptionList.addString("_urs_request_timeout", "100");
        callbackServiceOptionList.addString("_urs_sec_server_url", "");
        callbackServiceOptionList.addString("_urs_server_url", "");
        callbackServiceOptionList.addString("_urs_strategy_update_sub_routine", "SetRouteDelay");
        callbackServiceOptionList.addString("_urs_udata_xfer_keys", "");
        callbackServiceOptionList.addString("_urs_virtual_queue", "SIP_VQ_SIP_Switch");
        callbackServiceOptionList.addString("_urs_vq_priority", "");
        callbackServiceOptionList.addString("_urs_vq_priority_increment", "");
        callbackServiceOptionList.addString("_urs_vq_priority_increment_interval", "");
        callbackServiceOptionList.addString("_use_debug_push_certificate", "false");
        callbackServiceOptionList.addString("_use_reporting_aggregator", "");
        callbackServiceOptionList.addString("_user_confirm_timeout", "30");
        callbackServiceOptionList.addString("_userorig_connect_limit", "3");
        callbackServiceOptionList.addString("_userterminated_first_connect_party", "CUSTOMER");
        callbackServiceOptionList.addString("_vq_for_outbound_calls", "");
        callbackServiceOptionList.addString("_wait_for_agent", "false");
        callbackServiceOptionList.addString("_wait_for_user_confirm", "false");
        return callbackServiceOptionList;
    }

    public static KeyValueCollection getChatServiceOptions() {
        KeyValueCollection chatServiceOptionList = new KeyValueCollection();
        chatServiceOptionList.addString("_agent_availability_notification_delay", "30");
        chatServiceOptionList.addString("_agent_disposition_timeout", "45");
        chatServiceOptionList.addString("_agent_preview", "false");
        chatServiceOptionList.addString("_agent_preview_allow_reject", "0");
        chatServiceOptionList.addString("_agent_preview_data", "Value 1,Value 2,Value 3,Value 4,Value 5");
        chatServiceOptionList.addString("_agent_preview_timeout", "30");
        chatServiceOptionList.addString("_agent_preview_via_rp", "false");
        chatServiceOptionList.addString("_agent_reject_retry_timeout", "0");
        chatServiceOptionList.addString("_agent_reserve_timeout", "30");
        chatServiceOptionList.addString("_attach_udata", "single_json");
        chatServiceOptionList.addString("_booking_expiration_timeout", "30");
        chatServiceOptionList.addString("_business_hours_service", "");
        chatServiceOptionList.addString("_call_direction", "USERTERMINATED");
        chatServiceOptionList.addString("_call_timeguard_timeout", "15000");
        chatServiceOptionList.addString("_callback_events_list", "");
        chatServiceOptionList.addString("_calling_party_display_name", "");
        chatServiceOptionList.addString("_calling_party_number", "");
        chatServiceOptionList.addString("_capacity_service", "");
        chatServiceOptionList.addString("_chat_400_response_on_disconnected", "false");
        chatServiceOptionList.addString("_chat_endpoint", "Environment:chat_ors");
        chatServiceOptionList.addString("_client_timeout", "120");
        chatServiceOptionList.addString("_cpd_enable", "true");
        chatServiceOptionList.addString("_customer_lookup_keys", "_customer_number");
        chatServiceOptionList.addString("_desired_connect_time_threshold", "180");
        chatServiceOptionList.addString("_dial_retry_timeout", "300");
        chatServiceOptionList.addString("_disposition_userevent_mediatype", "0");
        chatServiceOptionList.addString("_enable_disposition_dialog", "false");
        chatServiceOptionList.addString("_enable_status_notification", "");
        chatServiceOptionList.addString("_eta_pos_threshold", "0:10,10:5,20:2,30:1,40:0");
        chatServiceOptionList.addString("_exceptions", "");
        chatServiceOptionList.addString("_ixn_createcall_hints", "");
        chatServiceOptionList.addString("_ixn_createcall_timeout", "15");
        chatServiceOptionList.addString("_ixn_redirect_confirm", "true");
        chatServiceOptionList.addString("_ixn_redirect_hints", "");
        chatServiceOptionList.addString("_mandatory_customer_lookup_keys", "_customer_number");
        chatServiceOptionList.addString("_max_dial_attempts", "3");
        chatServiceOptionList.addString("_max_notify_delivery_attempts", "3");
        chatServiceOptionList.addString("_max_number_of_user_availability_confirmation_attempts", "3");
        chatServiceOptionList.addString("_max_ors_submit_attempts", "3");
        chatServiceOptionList.addString("_max_request_by_time_bucket", "100");
        chatServiceOptionList.addString("_max_time_to_reach_eta_pos_threshold", "3600");
        chatServiceOptionList.addString("_max_time_to_wait_for_agent_on_the_call", "3600");
        chatServiceOptionList.addString("_max_time_to_wait_for_ixn_delete", "3600");
        chatServiceOptionList.addString("_max_transfer_to_agent_attempts", "5");
        chatServiceOptionList.addString("_max_urs_ewt_pos_polling_interval", "30");
        chatServiceOptionList.addString("_media_type", "chat");
        chatServiceOptionList.addString("_min_queue_wait", "0");
        chatServiceOptionList.addString("_min_urs_ewt_pos_polling_interval", "2");
        chatServiceOptionList.addString("_notification_message_file", "");
        chatServiceOptionList.addString("_offer_callback", "false");
        chatServiceOptionList.addString("_offer_callback_vxml_app_url", "");
        chatServiceOptionList.addString("_on_user_confirm_timeout", "CONNECT-ANYWAY");
        chatServiceOptionList.addString("_ors", "http://10.10.27.4:7210");
        chatServiceOptionList.addString("_ors_lb_strategy", "circular");
        chatServiceOptionList.addString("_plugin_on_dial_associate_ixn", "true");
        chatServiceOptionList.addString("_plugin_on_dial_invoke_on_call_failed", "true");
        chatServiceOptionList.addString("_plugin_on_dial_timeout", "20");
        chatServiceOptionList.addString("_plugin_on_dial_url", "");
        chatServiceOptionList.addString("_prefix_dial_out", "9");
        chatServiceOptionList.addString("_preview_userevent_mediatype", "0");
        chatServiceOptionList.addString("_provide_code", "false");
        chatServiceOptionList.addString("_provider_name", "");
        chatServiceOptionList.addString("_queue_ping_ors_period", "-120,-2");
        chatServiceOptionList.addString("_queue_poll_period", "-120,120");
        chatServiceOptionList.addString("_queue_poll_period_recovery", "-1440,-120");
        chatServiceOptionList.addString("_reject_future_desired_time", "1M");
        chatServiceOptionList.addString("_rep_userevent_dn", "");
        chatServiceOptionList.addString("_rep_userevent_enable", "false");
        chatServiceOptionList.addString("_rep_userevent_mediatype", "0");
        chatServiceOptionList.addString("_rep_userevent_switch", "");
        chatServiceOptionList.addString("_reporting_aggregator_url", "");
        chatServiceOptionList.addString("_request_execution_time_buffer", "120");
        chatServiceOptionList.addString("_request_queue_time_stat", "");
        chatServiceOptionList.addString("_request_time_bucket", "5");
        chatServiceOptionList.addString("_resource_group", "DNIS");
        chatServiceOptionList.addString("_route_point", "8999@SIP_Switch");
        chatServiceOptionList.addString("_service", "callback");
        chatServiceOptionList.addString("_snooze_duration", "300");
        chatServiceOptionList.addString("_src_route_point", "");
        chatServiceOptionList.addString("_status_notification_provider", "");
        chatServiceOptionList.addString("_status_notification_target", "http://135.17.38.71:1664/test");
        chatServiceOptionList.addString("_status_notification_type", "httpcb");
        chatServiceOptionList.addString("_target", "Customer_Service@Stat_Server.GA");
        chatServiceOptionList.addString("_transfer_to_agent_retry_timeout", "");
        chatServiceOptionList.addString("_treatment_call_failure_answering_machine", "");
        chatServiceOptionList.addString("_treatment_customer_connect", "");
        chatServiceOptionList.addString("_treatment_find_agent_fail", "");
        chatServiceOptionList.addString("_treatment_waiting_for_agent", "");
        chatServiceOptionList.addString("_ttl", "86400");
        chatServiceOptionList.addString("_type", "ors");
        chatServiceOptionList.addString("_urs_call_interaction_age", "");
        chatServiceOptionList.addString("_urs_ewt_estimation_method", "ursdial");
        chatServiceOptionList.addString("_urs_extension_data", "");
        chatServiceOptionList.addString("_urs_prioritization_strategy", "WaitForTarget");
        chatServiceOptionList.addString("_urs_queued_ttl", "660");
        chatServiceOptionList.addString("_urs_request_timeout", "100");
        chatServiceOptionList.addString("_urs_sec_server_url", "");
        chatServiceOptionList.addString("_urs_server_url", "http://localhost:7311");
        chatServiceOptionList.addString("_urs_strategy_update_sub_routine", "SetRouteDelay");
        chatServiceOptionList.addString("_urs_udata_xfer_keys", "");
        chatServiceOptionList.addString("_urs_virtual_queue", "GMS_VQ_SIP_Switch");
        chatServiceOptionList.addString("_urs_vq_priority", "");
        chatServiceOptionList.addString("_urs_vq_priority_increment", "");
        chatServiceOptionList.addString("_urs_vq_priority_increment_interval", "");
        chatServiceOptionList.addString("_use_debug_push_certificate", "false");
        chatServiceOptionList.addString("_use_reporting_aggregator", "");
        chatServiceOptionList.addString("_user_confirm_timeout", "30");
        chatServiceOptionList.addString("_userorig_connect_limit", "3");
        chatServiceOptionList.addString("_userterminated_first_connect_party", "CUSTOMER");
        chatServiceOptionList.addString("_vq_for_outbound_calls", "");
        chatServiceOptionList.addString("_wait_for_agent", "false");
        chatServiceOptionList.addString("_wait_for_user_confirm", "false");
        return chatServiceOptionList;
    }

    public static KeyValueCollection getUserTermImmCallbackServiceOptions() {
        KeyValueCollection callbackServiceOptionList = getUserTermSchCallbackServiceOptions();
        callbackServiceOptionList.getPair("_business_hours_service").setStringValue("");
        callbackServiceOptionList.getPair("_capacity_service").setStringValue("");
        callbackServiceOptionList.getPair("_wait_for_agent").setStringValue("false");
        callbackServiceOptionList.getPair("_wait_for_user_confirm").setStringValue("false");
        return callbackServiceOptionList;
    }

    public static KeyValueCollection getUserTermPrevCallbackServiceOptions() {
        KeyValueCollection callbackServiceOptionList = getUserTermSchCallbackServiceOptions();
        callbackServiceOptionList.getPair("_agent_preview").setStringValue("true");
        callbackServiceOptionList.getPair("_business_hours_service").setStringValue("");
        callbackServiceOptionList.getPair("_capacity_service").setStringValue("");
        callbackServiceOptionList.getPair("_cpd_enable").setStringValue("false");
        callbackServiceOptionList.getPair("_plugin_on_dial_associate_ixn").setStringValue("true");
        callbackServiceOptionList.getPair("_plugin_on_dial_invoke_on_call_failed").setStringValue("true");
        callbackServiceOptionList.getPair("_userterminated_first_connect_party").setStringValue("AGENT");
        callbackServiceOptionList.getPair("_wait_for_user_confirm").setStringValue("false");
        return callbackServiceOptionList;
    }

    public static KeyValueCollection getOfficeHoursOptions() {
        KeyValueCollection officeHoursServiceOptionList = new KeyValueCollection();
        officeHoursServiceOptionList.addString("_service", "office-hours");
        officeHoursServiceOptionList.addString("_timezone", getLocalTimeZone());
        officeHoursServiceOptionList.addString("_ttl", "30");
        officeHoursServiceOptionList.addString("_type", "builtin");
        return officeHoursServiceOptionList;
    }

    public static KeyValueCollection getRegularOfficeHours() {
        KeyValueCollection officeHoursServiceOptionList = getOfficeHoursOptions();
        officeHoursServiceOptionList.addString("_bh_regular1", "Mon-Sun 00:00-24:00");
        return officeHoursServiceOptionList;
    }

    public static KeyValueCollection getCapacityOptions() {
        KeyValueCollection capacityServiceOptionList = new KeyValueCollection();
        capacityServiceOptionList.addString("_capacity", "[Mon, Tue, Wed, Thu, Fri, Sat, Sun]");
        capacityServiceOptionList.addString("_capacity_1", "{\"1\":{\"1000\":1000,\"1100\":1000,\"1200\":1000,\"1300\":1000,\"1400\":1000,\"1500\":1000,\"1600\":1000,\"1700\":1000,\"1800\":1000,\"1900\":1000,\"2000\":1000,\"2100\":1000,\"2200\":1000,\"2300\":1000,\"0000\":1000,\"0100\":1000,\"0200\":1000,\"0300\":1000,\"0400\":1000,\"0500\":1000,\"0600\":1000,\"0700\":1000,\"0800\":1000,\"0900\":1000}}");
        capacityServiceOptionList.addString("_capacity_2", "{\"2\":{\"1000\":1000,\"1100\":1000,\"1200\":1000,\"1300\":1000,\"1400\":1000,\"1500\":1000,\"1600\":1000,\"1700\":1000,\"1800\":1000,\"1900\":1000,\"2000\":1000,\"2100\":1000,\"2200\":1000,\"2300\":1000,\"0000\":1000,\"0100\":1000,\"0200\":1000,\"0300\":1000,\"0400\":1000,\"0500\":1000,\"0600\":1000,\"0700\":1000,\"0800\":1000,\"0900\":1000}}");
        capacityServiceOptionList.addString("_capacity_3", "{\"3\":{\"1000\":1000,\"1100\":1000,\"1200\":1000,\"1300\":1000,\"1400\":1000,\"1500\":1000,\"1600\":1000,\"1700\":1000,\"1800\":1000,\"1900\":1000,\"2000\":1000,\"2100\":1000,\"2200\":1000,\"2300\":1000,\"0000\":1000,\"0100\":1000,\"0200\":1000,\"0300\":1000,\"0400\":1000,\"0500\":1000,\"0600\":1000,\"0700\":1000,\"0800\":1000,\"0900\":1000}}");
        capacityServiceOptionList.addString("_capacity_4", "{\"4\":{\"1000\":1000,\"1100\":1000,\"1200\":1000,\"1300\":1000,\"1400\":1000,\"1500\":1000,\"1600\":1000,\"1700\":1000,\"1800\":1000,\"1900\":1000,\"2000\":1000,\"2100\":1000,\"2200\":1000,\"2300\":1000,\"0000\":1000,\"0100\":1000,\"0200\":1000,\"0300\":1000,\"0400\":1000,\"0500\":1000,\"0600\":1000,\"0700\":1000,\"0800\":1000,\"0900\":1000}}");
        capacityServiceOptionList.addString("_capacity_5", "{\"5\":{\"1000\":1000,\"1100\":1000,\"1200\":1000,\"1300\":1000,\"1400\":1000,\"1500\":1000,\"1600\":1000,\"1700\":1000,\"1800\":1000,\"1900\":1000,\"2000\":1000,\"2100\":1000,\"2200\":1000,\"2300\":1000,\"0000\":1000,\"0100\":1000,\"0200\":1000,\"0300\":1000,\"0400\":1000,\"0500\":1000,\"0600\":1000,\"0700\":1000,\"0800\":1000,\"0900\":1000}}");
        capacityServiceOptionList.addString("_capacity_6", "{\"6\":{\"1000\":1000,\"1100\":1000,\"1200\":1000,\"1300\":1000,\"1400\":1000,\"1500\":1000,\"1600\":1000,\"1700\":1000,\"1800\":1000,\"1900\":1000,\"2000\":1000,\"2100\":1000,\"2200\":1000,\"2300\":1000,\"0000\":1000,\"0100\":1000,\"0200\":1000,\"0300\":1000,\"0400\":1000,\"0500\":1000,\"0600\":1000,\"0700\":1000,\"0800\":1000,\"0900\":1000}}");
        capacityServiceOptionList.addString("_capacity_7", "{\"7\":{\"1000\":1000,\"1100\":1000,\"1200\":1000,\"1300\":1000,\"1400\":1000,\"1500\":1000,\"1600\":1000,\"1700\":1000,\"1800\":1000,\"1900\":1000,\"2000\":1000,\"2100\":1000,\"2200\":1000,\"2300\":1000,\"0000\":1000,\"0100\":1000,\"0200\":1000,\"0300\":1000,\"0400\":1000,\"0500\":1000,\"0600\":1000,\"0700\":1000,\"0800\":1000,\"0900\":1000}}");
        capacityServiceOptionList.addString("_capacity_add", "[]");
        capacityServiceOptionList.addString("_service", "capacity");
        capacityServiceOptionList.addString("_timezone", getLocalTimeZone());
        capacityServiceOptionList.addString("_type", "builtin");
        return capacityServiceOptionList;
    }

    public static KeyValueCollection getRequestInteractionServiceOptions() {
        KeyValueCollection requestInteractionServiceOptionList = new KeyValueCollection();
        requestInteractionServiceOptionList.addString("_provide_code", "false");
        requestInteractionServiceOptionList.addString("_resource_group", "DNIS");
        requestInteractionServiceOptionList.addString("_service", "request-interaction");
        requestInteractionServiceOptionList.addString("_ttl", "30");
        requestInteractionServiceOptionList.addString("_type", "builtin");
        return requestInteractionServiceOptionList;
    }

    public static KeyValueCollection getHelloServiceOptions(String orsBaseURL) {
        KeyValueCollection helloServiceOptionList = new KeyValueCollection();
        helloServiceOptionList.addString("_name", "The Hello World Service");
        helloServiceOptionList.addString("_ors", orsBaseURL);
        helloServiceOptionList.addString("_ors_lb_strategy", "linear");
        helloServiceOptionList.addString("_service", "hello");
        helloServiceOptionList.addString("_ttl", "3600");
        helloServiceOptionList.addString("_type", "ors");
        return helloServiceOptionList;
    }

    public static KeyValueCollection getHelloServiceOptions() {
        return getHelloServiceOptions(getORSBaseURL(getPropertiesFile()));
    }

    public static KeyValueCollection getGETServiceOptions() {
        KeyValueCollection getServiceOptionList = new KeyValueCollection();
        getServiceOptionList.addString("_offline_code", "503");
        getServiceOptionList.addString("_online_code", "200");
        getServiceOptionList.addString("_service", "get");
        getServiceOptionList.addString("_type", "builtin");
        return getServiceOptionList;
    }

    public static KeyValueCollection getMatchInteractionServiceOptions() {
        KeyValueCollection matchInteractionServiceOptionList = new KeyValueCollection();
        matchInteractionServiceOptionList.addString("_service", "match-interaction");
        matchInteractionServiceOptionList.addString("_type", "builtin");
        return matchInteractionServiceOptionList;
    }

    public static KeyValueCollection getRequestAccessServiceOptions() {
        KeyValueCollection requestAccessServiceOptionList = new KeyValueCollection();
        requestAccessServiceOptionList.addString("_access_code_length", "");
        requestAccessServiceOptionList.addString("_phone_number", "undefined");
        requestAccessServiceOptionList.addString("_service", "request-access");
        requestAccessServiceOptionList.addString("_type", "builtin");
        return requestAccessServiceOptionList;
    }

    public static KeyValueCollection getRequestChatServiceOptions() {
        KeyValueCollection requestChatServiceOptionList = new KeyValueCollection();
        requestChatServiceOptionList.addString("_chat_endpoint", "Environment:default");
        requestChatServiceOptionList.addString("_service", "request-chat");
        requestChatServiceOptionList.addString("_ttl", "36000");
        requestChatServiceOptionList.addString("_type", "builtin");
        return requestChatServiceOptionList;
    }
}
