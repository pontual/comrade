package com.pontual_telemetria.pontual_monitor_api.infrastructure.util;

public class Constants {

    private Constants() {
        // private constructor
    }

    //SCHEMAS
    public static final String SCHEMA_PERSON = "sch_person";
    public static final String SCHEMA_USER = "sch_user";
    public static final String SCHEMA_CUSTOMER = "sch_customer";
    public static final String SCHEMA_REGULATORY = "sch_regulatory";
    public static final String SCHEMA_MONITORING = "sch_monitoring";
    public static final String SCHEMA_CONFIGURATION = "sch_configuration";

    //TABLES
    public static final String TABLE_PERSON = "person";
    public static final String TABLE_ACCOUNT_USER = "account_user";
    public static final String TABLE_ACCOUNT_USER_REQUESTER = "account_user_requester";
    public static final String TABLE_REQUESTER = "requester";
    public static final String TABLE_LOCATION = "location";
    public static final String TABLE_USAGE_GRANT = "usage_grant";
    public static final String TABLE_USAGE_GRANT_MONTHLY = "usage_grant_monthly";
    public static final String TABLE_CONTROL = "control";
    public static final String TABLE_CONTROL_READING = "control_reading";
    public static final String TABLE_INSTANTANEOUS_FLOW_RATE = "instantaneous_flow_rate";
    public static final String TABLE_DEVICE = "device";
    public static final String TABLE_DAILY_OPERATION_HOURS_OVERRIDE = "daily_operation_hours_override";
    public static final String TABLE_FUNCTION = "function";

    //ERRORS
    public static final String SGMAN_ERROR_MESSAGE = "Erro ao realizar consulta SGMAN: ";

    //FUNCTIONS
    public static final String CAP_TO_24H = "operation-daily-limit-to-24h";
}
