package util.database.statement;

public enum SqlStatementType {

    QUERY,
    CALL,
    DELETE,
    INSERT,
    UPDATE,
    XQUERY,
    OTHERSQL,
    MERGE,
    CREATE,
    DROP,
    ALTER,
    GRANT,
    REVOKE,
    COMMENT,
    LABEL,
    RENAME,
    DECLAREGLOBALTEMPTABLE,
    SET,
    VALUES,
    COMPOUND,
    SET_METHOD,
    SINGLE_ROW_QUERY;

    public static SqlStatementType getSqlStatementType(String type) {
        for (SqlStatementType t : values()) {
            if (t.name().equals(type)) {
                return t;
            }
        }
        return null;
    }

    @Deprecated
    public boolean isQuery() {
        return (QUERY == this) || (XQUERY == this);
    }

    public boolean isSqlSetOrSetMethod() {
        return (SET == this) || (SET_METHOD == this);
    }

    public static boolean isDDL(SqlStatementType type) {
        if (null == type) {
            return false;
        }

        switch (type.ordinal()) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                return true;
        }
        return false;
    }

    public static boolean isSELECTorXQUERY(SqlStatementType type) {
        if (null == type) {
            return false;
        }

        switch (type.ordinal()) {
            case 9:
            case 10:
                return true;
        }
        return false;
    }

    public static boolean isSELECTorVALUES(SqlStatementType type) {
        if (null == type) {
            return false;
        }

        switch (type.ordinal()) {
            case 9:
            case 11:
                return true;
        }
        return false;
    }

    public static boolean isSELECTorVALUESorXQUERY(SqlStatementType type) {
        if (null == type) {
            return false;
        }

        switch (type.ordinal()) {
            case 9:
            case 10:
            case 11:
                return true;
        }
        return false;
    }
}