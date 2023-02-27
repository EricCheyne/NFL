package com.example.nfl_from_json

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset

class MainActivity : AppCompatActivity() {

    lateinit var myJSON_array: JSONArray
    lateinit var spTeam: Spinner
    lateinit var spConference: Spinner
    lateinit var spDivision: Spinner
    lateinit var tvCoach: TextView
    lateinit var ivTeam: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spConference = findViewById<Spinner>(R.id.spConference)
        spDivision = findViewById<Spinner>(R.id.spDivision)
        spTeam = findViewById<Spinner>(R.id.spTeam)
        tvCoach = findViewById<TextView>(R.id.tvCoach)
        ivTeam = findViewById<ImageView>(R.id.ivTeam)

        // read file -- the main JSON object is/includes an array called NFL  -- extract that array
        val input_stream = resources.openRawResource(R.raw.nfl)
        var myText = input_stream.readBytes().toString(Charset.defaultCharset())

        val myJSON_object = JSONObject(myText)
        myJSON_array = myJSON_object.getJSONArray("NFL")

        //arrays for conferences and divisions
        val conferences = ArrayList<String>()
        val divisions = ArrayList<String>()

        var userConference: String
        var userDivision: String

        //create two String arrays of conference & division -- used to populate spinner
        for (i in 0 until myJSON_array.length()) {
            //get an individual element of the JSON array
            val aJSON_element = myJSON_array.getJSONObject(i)
            //get the individual properties of the JSON element
            val aConference = aJSON_element.getString("Conference")
            val aDivision = aJSON_element.getString("Division")

            //if don't already have conference add it
            if (conferences.indexOf(aConference) == -1) {
                conferences.add(aConference)
            }

            //if don't already have division add it
            if (divisions.indexOf(aDivision) == -1) {
                divisions.add(aDivision)
            }
        }//end loop over myJSON_array

        //populate the division and conference spinners
        val aaConference = ArrayAdapter(this, android.R.layout.simple_spinner_item, conferences)
        aaConference.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // The drop down view
        spConference.setAdapter(aaConference)

        val aaDivision = ArrayAdapter(this, android.R.layout.simple_spinner_item, divisions)
        aaDivision.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // The drop down view
        spDivision.setAdapter(aaDivision)

        //************************************************************************************************
        // conference spinner handler
        spConference.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                loadTeams()
            }//end onItemSelected -- CONFERENCE

            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        })//end onItemSelectedListener -- CONFERENCE

        //************************************************************************************************
        // division spinner handler
        spDivision.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                loadTeams()
            }//end onItemSelected -- DIVISION

            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        })//end onItemSelectedListener -- DIVISION

    }//end onCreate


    fun loadTeams() {
        spTeam.adapter = null  //clear out any old teams
        val teams = ArrayList<String>()

        var userConference = spConference.getSelectedItem().toString()
        var userDivision = spDivision.getSelectedItem().toString()

        for (j in 0 until myJSON_array.length()) {
            //get an individual element of the JSON array
            val aJSON_element = myJSON_array.getJSONObject(j)
            //get the individual properties of the JSON element
            val aConference = aJSON_element.getString("Conference")
            val aDivision = aJSON_element.getString("Division")
            if (aConference == userConference && aDivision == userDivision) {
                val aCity = aJSON_element.getString("City")
                val aTeam = aJSON_element.getString("TeamName")
                teams.add("$aCity-$aTeam")
            }
        }//end loop over array

        //add teams matching conference and division
        val aaTeam = ArrayAdapter(baseContext, android.R.layout.simple_spinner_item, teams)
        aaTeam.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // The drop down view
        spTeam.adapter = aaTeam

        spTeam.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            val tvCoach = findViewById<TextView>(R.id.tvCoach)
            val ivTeam = findViewById<ImageView>(R.id.ivTeam)
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View,
                i: Int,
                l: Long
            ) {
                val mytv = view as TextView
                var myTeam = mytv.text.toString()
                //spinner has city and team -- get just team
                val tokens = myTeam.split("-")
                myTeam = tokens[tokens.size - 1]

                for (j in 0 until myJSON_array.length()) {
                    if (myJSON_array.getJSONObject(j).getString("TeamName")
                            .equals(myTeam)
                    ) {
                        tvCoach.text = myJSON_array.getJSONObject(j).getString("Coach")
                        // string off file extension -- then file corresponding id
                        var imagefile = myJSON_array.getJSONObject(j).getString("Image")
                        imagefile = imagefile.substring(0, imagefile.lastIndexOf("."))
                        val draw_res_id =
                            resources.getIdentifier(imagefile, "drawable", packageName)
                        ivTeam.setImageResource(draw_res_id)
                    }
                }//end loop over array
            } //on Team selected

            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        })//end o

    }

}//end MainActivity


