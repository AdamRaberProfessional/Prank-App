package adam.illhaveacompany.backgroundcheck

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_saved_image_list.*

class SavedImageList : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_image_list)


        setSupportActionBar(toolbar_saved_image_list_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.title = "HISTORY"

            toolbar_saved_image_list_activity.setNavigationOnClickListener {
                onBackPressed()
            }//157
        }

        getAllCompletedDates()
    }

    private fun getAllCompletedDates(){
        val dbHandler = SqliteOpenHelper(this, null)
        val allCompletedDatesList = dbHandler.getAllCompletedDatesList()

        if(allCompletedDatesList.size > 0){
            tvHistory.visibility = View.VISIBLE
            rvHistory.visibility = View.VISIBLE
            tvNoDataAvailable.visibility = View.GONE


            rvHistory.layoutManager = LinearLayoutManager(this)
            val historyAdapter = HistoryAdapter(this, allCompletedDatesList)
            rvHistory.adapter = historyAdapter
        }else{
            tvHistory.visibility = View.GONE
            rvHistory.visibility = View.GONE
            tvNoDataAvailable.visibility = View.VISIBLE
        }
    }//163
}