package adam.illhaveacompany.backgroundcheck

import adam.illhaveacompany.saveimagesinsqlite.DatabaseHandler
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_prank.*
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream


class PrankActivity : AppCompatActivity() {

    private val STORAGE_PERMISSION_CODE = 1
    private val GALLERY = 2
    //4

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prank)

        val databaseHandler = DatabaseHandler(this)
        if(databaseHandler.areTherePictures()) {
            val imageInBitmapForm = BitmapFactory.decodeByteArray(databaseHandler.getPicture(), 0, databaseHandler.getPicture()!!.size)
            val imageInInputStream: InputStream = ByteArrayInputStream(databaseHandler.getPicture())
            val rotatedImage = rotateBitmap(imageInBitmapForm, getCameraPhotoOrientation(imageInInputStream))
            iv_image.setImageBitmap(rotatedImage)
        }


        //iv_image.background = the result of the bitmap translated back to an image

        change_background_button.setOnClickListener { view ->
            if (isPermissionAllowed(Manifest.permission.READ_EXTERNAL_STORAGE)) {


                val pickPhotoIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

                startActivityForResult(pickPhotoIntent, GALLERY)
            } else {
                requestPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    STORAGE_PERMISSION_CODE
                )
            }
        }

    }


    private fun requestPermission(sPermissionName: String, iRequestCode: Int) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, sPermissionName)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }

        ActivityCompat.requestPermissions(this, arrayOf(sPermissionName), iRequestCode)
    }//5


    private fun isPermissionAllowed(sPermission: String): Boolean {
        val result = ContextCompat.checkSelfPermission(this, sPermission)

        if(result == PackageManager.PERMISSION_GRANTED){
            return true
        }else{
            return false
        }

    }//6

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY) {
                try {
                    if (data!!.data != null) {
                        //this line is not necessarily needed
                        iv_image.visibility = View.VISIBLE

                        val imageUri = data.data
                        iv_image.setImageURI(data.data)

                        if (imageUri != null) {
                            val imageInByteArray = contentResolver.openInputStream(imageUri)?.readBytes()
                            if (imageInByteArray != null) {
                                addImageToDatabase(imageInByteArray)
                            }
                        }



                    } else {
                        Toast.makeText(this, "image not transferable", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }//16
    private fun addImageToDatabase(image: ByteArray) {
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)

        val status = databaseHandler.addPicture(Picture(0, image))

        if(status > -1) {
            Toast.makeText(applicationContext, "image saved successfully", Toast.LENGTH_SHORT).show()
        }
    }//15


    fun rotateBitmap(source: Bitmap, angle: Int): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle.toFloat())
        return Bitmap.createBitmap(
            source,
            0,
            0,
            source.width,
            source.height,
            matrix,
            true
        )
    }//19

    @RequiresApi(Build.VERSION_CODES.N)
    fun getCameraPhotoOrientation(imageInBitmapForm: InputStream?): Int {
        var rotate = 0
        try {
            var exif: ExifInterface? = null
            try {
                exif = ExifInterface(imageInBitmapForm!!)
            } catch (e1: IOException) {
                e1.printStackTrace()
            }
            val orientation = exif!!.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, 0
            )
            rotate = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_270 -> 90
                else -> 0
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return rotate
    }
}
