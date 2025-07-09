package com.pontual_telemetria.pontual_monitor_api.infrastructure.util;

public class Constants {

    private Constants() {
        // private constructor
    }

    //SCHEMAS
    public static final String SCHEMA_PERSON = "sch_person";
    public static final String SCHEMA_USER = "sch_user";
    public static final String SCHEMA_CUSTOMER = "sch_customer";

    //TABLES
    public static final String TABLE_PERSON = "person";
    public static final String TABLE_ACCOUNT_USER = "account_user";
    public static final String TABLE_REQUESTER = "requester";
    public static final String TABLE_LOCATION = "location";

    //ERRORS
    public static final String SGMAN_ERROR_MESSAGE = "Erro ao realizar consulta SGMAN: ";
}
