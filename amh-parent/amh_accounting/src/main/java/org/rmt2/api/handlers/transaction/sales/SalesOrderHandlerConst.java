package org.rmt2.api.handlers.transaction.sales;

/**
 * Sales order API message handler constants.
 * 
 * @author roy.terrell
 *
 */
public class SalesOrderHandlerConst {
    public static final String MSG_MISSING_SALESORDER_STRUCTURE = "Sales order structure is required for create sales order operation";
    public static final String MSG_SALESORDER_LIST_EMPTY = "Sales order list cannot be empty for create sales order operation";
    public static final String MSG_SALESORDER_LIST_CONTAINS_TOO_MANY = "Sales order list must contain only 1 entry for create sales order operation";
    public static final String MSG_REVERSE_SUCCESS = "Existing Sales order transaction, %s1, was reversed: %s2";
    public static final String MSG_CREATE_SUCCESS = "New sales order transaction was created: %s";
    public static final String MSG_CLOSE_SUCCESS = "%s sales order(s) were closed successfully";
    public static final String MSG_CREATE_FAILURE = "Failure to create sales order";
    public static final String MSG_SALESORDER_CLOSE_TOO_MANY_TRANSACTIONS = "Too many transactions for close sales order operation";
}
