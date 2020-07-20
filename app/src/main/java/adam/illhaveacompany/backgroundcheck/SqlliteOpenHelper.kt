package adam.illhaveacompany.backgroundcheck

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SqliteOpenHelper(context: Context, factory: SQLiteDatabase.CursorFactory?)
    : SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "SevenMinutesWorkout.db"
        private const val TABLE_HISTORY = "history" // Table Name
        private const val COLUMN_ID = "_id" // Column Id
        private const val COLUMN_COMPLETED_DATE = "completed_date" // Column for Completed Date
    }

    override fun onCreate(db: SQLiteDatabase?) {
        //CREATE TABLE history (_id INTEGER PRIMARY KEY, completed_date TEXT
        val CREATE_EXERCISE_TABLE = ("CREATE TABLE " + TABLE_HISTORY + "(" + COLUMN_ID+
                " INTEGER PRIMARY KEY" + COLUMN_COMPLETED_DATE + " TEXT)")
        db?.execSQL(CREATE_EXERCISE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY)
        onCreate(db)
    }


    //this only adds the date itself
    fun addDate(date: String){
        val values = ContentValues()
        values.put(COLUMN_COMPLETED_DATE, date)
        //allows us to writ to the database
        val db = this.writableDatabase
        db.insert(TABLE_HISTORY, null, values)
        db.close()
    }

    fun getAllCompletedDatesList() : ArrayList<String>{
        val list = ArrayList<String>()
        val db = this.readableDatabase
        //table history is the name of the database
        val cursor = db.rawQuery("SELECT * FROM $TABLE_HISTORY", null)

        while(cursor.moveToNext()){
            val dateValue = (cursor.getString(cursor.getColumnIndex(COLUMN_COMPLETED_DATE)))
            //adds the date value to the arraylist
            list.add(dateValue)
        }
        cursor.close()
        return list
    }//163

}//162
