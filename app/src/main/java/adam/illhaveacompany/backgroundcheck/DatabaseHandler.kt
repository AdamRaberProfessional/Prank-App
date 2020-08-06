package adam.illhaveacompany.saveimagesinsqlite

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper


class DatabaseHandler (context: Context):
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){

    companion object {
        //changed when I want to add a column
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "PictureDatabase"

        private const val TABLE_PICTURE = "PictureTABLE"

        private const val KEY_ID = "_id"
        private const val KEY_PICTURE = "pictureBitmap"
    }//9

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_PICTURE_TABLE = ("CREATE TABLE " + TABLE_PICTURE + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_PICTURE + " TEXT" + ")")
        //pushes the code through
        db?.execSQL(CREATE_PICTURE_TABLE)
    }//10

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_PICTURE)
        onCreate(db)
    }//11

    fun addPicture(picture: Picture) : Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()

        contentValues.put(KEY_PICTURE, picture.image)

        val success = db.insert(TABLE_PICTURE, null, contentValues)
        db.close()
        return success
    }//14

    fun getPictureList() : ArrayList<Picture> {
        val pictureList: ArrayList<Picture> = ArrayList<Picture>()
        val selectQuery = "SELECT * FROM $TABLE_PICTURE"

        val db = this.readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: SQLiteException){
            db.execSQL(selectQuery)
        }//5

        var id : Int
        var picture: ByteArray

        if (cursor != null) {
            if(cursor.moveToFirst()) {
                do{
                    id = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                    //*POSSIBLE PROBLEM*
                    picture = cursor.getBlob(cursor.getColumnIndex(KEY_PICTURE))

                    val pic = Picture(id, picture)

                    pictureList.add(pic)
                } while(cursor.moveToNext())
            }
        }

            return pictureList

    }//17



    fun areTherePictures() : Boolean {


        val database = this.readableDatabase
        val NoOfRows = DatabaseUtils.queryNumEntries(database, TABLE_PICTURE).toInt()

        return if (NoOfRows == 0) {
            false
        } else return true


    }//21



}