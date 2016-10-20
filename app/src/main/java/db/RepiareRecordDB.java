package db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;

import bean.RepaireTable;

/**
 * Created by Lenovo on 2015/12/20.
 */
public class RepiareRecordDB {
    /**
     * 数据库名字
     */
    public static final String DB_NAME = "repaire_record";

    /**
     * 数据库版本
     */
    public static final int VERSION = 1;

    private static RepiareRecordDB repaireRecordDB;

    private SQLiteDatabase db;


    private RepiareRecordDB(Context context){
        RepiareRecordOpenHelper dbHelper = new RepiareRecordOpenHelper(context,DB_NAME,null,VERSION);
        db = dbHelper.getWritableDatabase();
    }


    public synchronized static RepiareRecordDB getInstance(Context context){
        if(repaireRecordDB == null){
            repaireRecordDB = new RepiareRecordDB(context);
        }
        return repaireRecordDB;
    }
    //将获得的RepaireRecorde实例存储到数据库
    public void saveRepaireRecorde(ArrayList<RepaireTable> tables,String uid){
        if(tables!=null){
            for(RepaireTable table : tables){
                if(checkColumnExists2(db,table.getRid())){
                    ContentValues values = new ContentValues();
                    values.put("repaire_name",table.getRepaireName());
                    values.put("repaire_des",table.getRepaireDes());
                    values.put("repaire_type",table.getType());
                    values.put("repaire_date",table.getRepaire_time().toString());
                    values.put("repaire_status", table.getRepaireStatus());
                    db.update("RepaireRecord",values,"rid=?",new String[]{table.getRid()});
                }else{
                    ContentValues values = new ContentValues();
                    values.put("uid",uid);
                    values.put("rid",table.getRid());
                    values.put("repaire_name",table.getRepaireName());
                    values.put("repaire_des",table.getRepaireDes());
                    values.put("repaire_type",table.getType());
                    values.put("repaire_date",table.getRepaire_time().toString());
                    values.put("repaire_status", table.getRepaireStatus());
                    db.insert("RepaireRecord",null,values);
                }
            }
        }
    }
    //将RepaireRecorde信息读出
    public ArrayList<RepaireTable> loadRepaireRecorde(String uid){
        ArrayList<RepaireTable> list = new ArrayList<RepaireTable>();
        Cursor cursor = db.query("RepaireRecord",null,"uid=?",new String[]{uid},null,null,null);
        if(cursor.moveToFirst()){
            do{
                RepaireTable table = new RepaireTable();
                table.setRid(cursor.getString(cursor.getColumnIndex("rid")));
                table.setRepaireName(cursor.getString(cursor.getColumnIndex("repaire_name")));
                table.setRepaireDes(cursor.getString(cursor.getColumnIndex("repaire_des")));
                table.setType(cursor.getString(cursor.getColumnIndex("repaire_type")));
                table.setRepaire_time(new Date(cursor.getString(cursor.getColumnIndex("repaire_date"))));
                table.setRepaireStatus(cursor.getInt(cursor.getColumnIndex("repaire_status")));
                list.add(table);
            }while (cursor.moveToNext());
        }
        return list;
    }
    private boolean checkColumnExists2(SQLiteDatabase db, String rid) {
        boolean result = false ;
        Cursor cursor = null ;
        cursor = db.query("RepaireRecord",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                if (rid.equals(cursor.getString(cursor.getColumnIndex("rid")))){
                    result = true;
                    return result;
                }
            }while (cursor.moveToNext());
        }

        return result ;
    }

}
