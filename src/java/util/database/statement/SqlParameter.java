package util.database.statement;

public class SqlParameter {

    private String name;
    private Object value;
    private String type;
    private ParameterDirection direction = ParameterDirection.INPUT;

    public SqlParameter() {
    }

    public SqlParameter(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public SqlParameter(String name, Object value, String type) {
        this(name, value);
        this.type = type;
    }

    public SqlParameter(String name, Object value, String type, ParameterDirection direction) {
        this(name, value, type);
        this.direction = direction;
    }

    @Override
    public SqlParameter clone() throws CloneNotSupportedException {
        return new SqlParameter(name, value, type, direction);
    }

    /**
     * @return the parameterName
     */
    public String getName() {
        return name;
    }

    /**
     * @param parameterName the parameterName to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the value
     */
    public Object getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * @return the parameterType
     */
    public String getType() {
        return type;
    }

    /**
     * @param parameterType the parameterType to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the direction
     */
    public ParameterDirection getDirection() {
        return direction;
    }

    /**
     * @param direction the direction to set
     */
    public void setDirection(ParameterDirection direction) {
        this.direction = direction;
    }
}