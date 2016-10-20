package db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Lenovo on 2015/12/20.
 */
public class RepiareRecordOpenHelper extends SQLiteOpenHelper {
    //建立三张表 ，province  city country
    public static final String CREATE_REPAIRERECORD = "create table RepaireRecord (" +
            "id integer primary key autoincrement," +
            "uid text," +
            "rid text," +
            "repaire_name text," +
            "repaire_des text," +
            "repaire_type text," +
            "repaire_date text," +
            "repaire_status integer)";
    public RepiareRecordOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_REPAIRERECORD);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
