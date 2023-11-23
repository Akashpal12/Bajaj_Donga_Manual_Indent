package in.co.vibrant.bajajdongamanual.model;

public class UserDetailsModel {


    public static final String TABLE_NAME = "user_details";
    public static final String COLUMN_ID = "id";
    public static final String factory_code = "factory_code";
    public static final String u_code = "u_code";
    public static final String u_name = "u_name";


    protected String id;
    protected String factoryCode;
    protected String userCode;
    protected String userName;



    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + factory_code + " INTEGER,"
                    + u_code + " TEXT,"
                    + u_name + " TEXT"
                    + ")";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFactoryCode() {
        return factoryCode;
    }

    public void setFactoryCode(String factoryCode) {
        this.factoryCode = factoryCode;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
