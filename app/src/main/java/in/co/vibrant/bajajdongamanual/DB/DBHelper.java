package in.co.vibrant.bajajdongamanual.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import in.co.vibrant.bajajdongamanual.model.UserDetailsModel;


public class DBHelper extends SQLiteOpenHelper {

    /* DB Info*/
    private static final String TAG = "DatabaseHelper";
    /* DB Info*/
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "bajaj_donga_manual";
    SQLiteDatabase database;

    public DBHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

	@Override
	public void onCreate(SQLiteDatabase database){
        database.execSQL(UserDetailsModel.CREATE_TABLE);


        //database.close();
	}
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion){
        database.execSQL("DROP TABLE IF EXISTS " + UserDetailsModel.TABLE_NAME);
        // Create tables again
        onCreate(database);
        //database.close();
	}



    public long insertUserDetailsModel(UserDetailsModel model) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(UserDetailsModel.factory_code, model.getFactoryCode());
        values.put(UserDetailsModel.u_code, model.getUserCode());
        values.put(UserDetailsModel.u_name, model.getUserName());
        // insert row
        long id = db.insert(UserDetailsModel.TABLE_NAME, null, values);
        // close db connection
        db.close();
        // return newly inserted row id
        return id;
    }

    public List<UserDetailsModel> getUserDetailsModel() {
        List<UserDetailsModel> allData = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + UserDetailsModel.TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                UserDetailsModel singleData = new UserDetailsModel();
                singleData.setId(cursor.getString(cursor.getColumnIndex(UserDetailsModel.COLUMN_ID)));
                singleData.setFactoryCode(cursor.getString(cursor.getColumnIndex(UserDetailsModel.factory_code)));
                singleData.setUserCode(cursor.getString(cursor.getColumnIndex(UserDetailsModel.u_code)));
                singleData.setUserName(cursor.getString(cursor.getColumnIndex(UserDetailsModel.u_name)));
                allData.add(singleData);
            } while (cursor.moveToNext());
        }
        db.close();
        return allData;
    }

    public void deleteUserDetailsModel()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DELETE FROM "+UserDetailsModel.TABLE_NAME);
        db.execSQL("VACUUM");
        db.close();
    }


    public void open() throws SQLException {
        close();
        this.getWritableDatabase();
    }

    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }


}
