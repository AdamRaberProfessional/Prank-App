package adam.illhaveacompany.saveimagesinsqlite

import adam.illhaveacompany.backgroundcheck.Picture
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
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "PictureDatabase"

        private const val TABLE_PICTURE = "PictureTABLE"

        private const val KEY_ID = "_id"
        private const val KEY_PICTURE = "pictureBitmap"
    }//9

    override fun onCreate(db: SQLiteDatabase?) {

        db?.execSQL("CREATE TABLE " + TABLE_PICTURE + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_PICTURE + " TEXT" + ")")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_PICTURE")
        onCreate(db)
    }//11

    fun addPicture(picture: Picture) : Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()

        contentValues.put(KEY_PICTURE, picture.image)
        contentValues.put(KEY_ID, 0)

        val success = db.insert(TABLE_PICTURE, null, contentValues)
        db.close()
        return success
    }//14

    fun getPicture() : ByteArray? {
        var picture: ByteArray? = null
        val selectQuery = "SELECT $KEY_PICTURE FROM $TABLE_PICTURE WHERE $KEY_ID = 0"

        val db = this.readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: SQLiteException){
            db.execSQL(selectQuery)
        }//5


        if (cursor != null) {
            if(cursor.moveToFirst()) {
                do{
                    picture = cursor.getBlob(cursor.getColumnIndex(KEY_PICTURE))
                } while(cursor.moveToNext())
            }
        }

        return picture

    }//17



    fun areTherePictures() : Boolean {
        val database = this.readableDatabase
        val numberOfRows = DatabaseUtils.queryNumEntries(database, TABLE_PICTURE).toInt()

        return if (numberOfRows == 0) {
            false
        } else return true
    }//21




}