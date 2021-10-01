package com.epam.esm.util;

import java.util.Map;

public class SqlUtils {

    private static final String AND = " AND ";
    private static final String ORDER_BY = " ORDER BY ";
    private static final String WHERE = " WHERE ";
    private static final String LIKE = " LIKE ";
    private static final String ASCENDING = " ASC";
    private static final String DESCENDING = " DESC";
    private static final String WILDCARD_ANY = "%";
    private static final String SINGLE_QUOTE = "'";
    private static final String COMMA = ",";
    private static final String EQUAL = " = ";
    private static final String ID = " ID = ";

    private SqlUtils() {
    }

    public static String constructQueryForGettingWithPartialSearchAndSorting(String query, Map<String, String> fieldAndValueForPartialSearch, String fieldForSorting, Boolean isAscending) {
        StringBuilder stringBuilder = new StringBuilder(query);

        if (fieldAndValueForPartialSearch != null && !fieldAndValueForPartialSearch.isEmpty()) {
            String partialSearchQuery = applyPartialSearch(fieldAndValueForPartialSearch);
            stringBuilder.append(partialSearchQuery);
        }

        if (fieldForSorting != null && isAscending != null) {
            String sortingQuery = applySorting(fieldForSorting, isAscending);
            stringBuilder.append(sortingQuery);
        }

        return stringBuilder.toString();
    }

    public static String constructQueryForUpdating(String query, long id, Map<String, String> fieldNameValueForUpdate) {
        StringBuilder stringBuilder = new StringBuilder(query);

        if (fieldNameValueForUpdate != null && !fieldNameValueForUpdate.isEmpty()) {

            for (Map.Entry<String, String> entry : fieldNameValueForUpdate.entrySet()) {
                String fieldName = entry.getKey();
                String fieldValue = entry.getValue();

                if (fieldName != null) {
                    stringBuilder
                            .append(fieldName)
                            .append(EQUAL)
                            .append(SINGLE_QUOTE)
                            .append(fieldValue)
                            .append(SINGLE_QUOTE)
                            .append(COMMA);
                }
            }
        }

        int lastInsertedCommaIndex = stringBuilder.length() - 1;
        stringBuilder.deleteCharAt(lastInsertedCommaIndex);

        stringBuilder.append(WHERE).append(ID).append(id);

        return stringBuilder.toString();
    }

    private static String applyPartialSearch(Map<String, String> fieldAndValueForPartialSearch) {
        StringBuilder stringBuilder = new StringBuilder(WHERE);

        for (Map.Entry<String, String> entry : fieldAndValueForPartialSearch.entrySet()) {
            String fieldName = entry.getKey();
            String partOfField = entry.getValue();

            if (partOfField != null) {
                stringBuilder
                        .append(fieldName)
                        .append(LIKE)
                        .append(SINGLE_QUOTE)
                        .append(WILDCARD_ANY)
                        .append(partOfField)
                        .append(WILDCARD_ANY)
                        .append(SINGLE_QUOTE)
                        .append(AND);
            }
        }

        int lengthWithoutLastAnd = stringBuilder.length() - AND.length();
        stringBuilder.setLength(lengthWithoutLastAnd);

        return stringBuilder.toString();
    }

    private static String applySorting(String fieldForSorting, Boolean isAscending) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder
                .append(ORDER_BY)
                .append(fieldForSorting);

        if (isAscending) {
            stringBuilder.append(ASCENDING);
        } else {
            stringBuilder.append(DESCENDING);
        }

        return stringBuilder.toString();
    }
}
