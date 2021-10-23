package com.ashabanebert.mukemergency;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class PoliceListActivity extends AppCompatActivity {
    ListView listView;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_police_list);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Police Directory");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        listView = (ListView) findViewById(R.id.listofpolice);

        String[] values = new String[]{

                "EMERGENCY TOLL FREE LINES\n" + "\n" + "1\tJoint Security Command Centre\t0800199399\n" + "2\tNational Operations Information Room (information that requires quick reaction e.g theft of mv)\t0800199699\n" + "3\tProfessional Standards Unit – PSU (addresses complaints against police officers)\t0800200010/0800199199\n" + "4\tLand Protection Unit (Fraud on land issues)\t0800100999\n" + "5\tCriminal Investigation department, Anti-human sacrifice.\t0800199499\n" + "6\tEmergency Response Unit, Central Police Station, Kampala\t0800122291\n" + "7\tTraffic Operations Room, Central Police Station, Kampala\t0800199099\n" + "8\tFire Rescue & Service Unit, Kampala\t0800121222\n" + "9\tMulago Hospital, Causality Police Post\t0800199188\n" + "10\tDog Section, Canine Unit\t0800300900\n" + "11\tIncident Room, Central Police Station, Kampala\t0800199088\n" + "12\tSpecial Investigations Unit (SIU)\t0800299911\n" + "13\tChild & Family Protection Unit\t0800199033\n" + "14\tCounter Terrorism Unit (CT) – Acacia Avenue\t0800199139\n" + " \n" + "\n" + " \n" + "\n" + "Relevant Specialized Units\n" + "\n" + " \n" + "\n" + "Aviation Police\t0714667719\n" + "Homicide Unit\t0711778265\n" + "Marine / Water Unit\t0714667756\n" + "Police Band Master\t0718357416\n" + "Railway Police\t0715989985\n" + "Environmental Police\t0711042325\n" + "Tourism Police\t0717179563\n" + "Emergency\t0711042210\n" + " \n" + "\n" + " \n" + "\n" + "Official Contacts of Top Management Police Officers\n" + "\n" + " \n" + "\n" + "Inspector General of Police (IGP)\t0712755999\n" + "Deputy Inspector General of Police (D/IGP)\t0712745013\n" + "Assistant Inspector General of Police (AIGP) / Operations\t0716337799\n" + "AIGP Logistics & Engineering\t0712745022\n" + "AIGP Interpol\t0714667710\n" + "AIGP Chief Political Commissar\t0714667758\n" + "AIGP Criminal Investigation Department\t0718300753\n" + "AIGP Research, Planning & Development\t0711667704\n" + "AIGP Administration\t0714667712\n" + "AIGP Special Duties\t0711042128\n" + "AIGP Counter-terrorism\t0712892740\n" + " \n" + "\n" + "AIGP Oil & Gas\t0712745024\n" + "AIGP Kampala Metropolitan Police\t0714629987\n" + "AIGP Traffic & Road Safety\t0712767710\n" + "AIGP Human Resource, Development and Administration\t0714667712\n" + "AIGP Fire & Rescue Services\t0712144799\n" + "AIGP Legal\t0712925011\n" + " \n" + "\n" + " \n" + "\n" + "Official Contacts of Police Stations within the country\n" + "\n" + " \n" + "\n" + " \n" + "\n" + " \n" + "\n" + "Kawempe\t0714667780\n" + "Old Kampala\t0714667784\n" + "Wandegeya\t0714667776\n" + "Wakiso\t0714667816\n" + "Kasangati\t0717179570\n" + "Kakiri\t0718851397\n" + "Katwe\t0714667793\n" + "Nsangi\t0718851395\n" + "Kajjansi\t0715490368\n" + "Kabalagala\t0715989997\n" + "Entebbe\t0714667806\n" + "Central Police Station (CPS)\t0714667772\n" + "Jinja raod\t0714667790\n" + "Kira road\t0714667787\n" + "Kiira Division (along Kyaliwajjala – Najera rd)\t0714668027\n" + "Mukono\t0718300750\n" + "Nagalama\t0714667820\n" + "Mpigi\t0714667810\n" + "Gombe / Butambala\t0718731749\n" + "Gomba\t0718731753\n" + "Mityana\t0714667834\n" + "Mubende\t0714667821\n" + "Kiboga\t0714667827\n" + "Kyankwanzi\t0718731756\n" + "Luwero\t0714667813\n" + "Nakaseke\t0714667819\n" + "Nakasongola\t0714667835\n" + "Buikwe\t0714667824\n" + "Buvuma\t0718731757\n" + "Kayunga\t0714667830\n" + "Njeru Division\t0711042346\n" + "Kiira Central Division, Jinja\t0714668018\n" + "Kiira East Division, Kakira\t0717125145\n" + "Kiira North Division, Buwenge\t0713534923\n" + "Buyende\t0718452643\n" + " \n" + "\n" + "Kamuli\t0714668012\n" + "Kaliro\t0714668015\n" + "Luuka\t0718731794\n" + "Iganga\t0714668009\n" + "Namayingo\t0718642492\n" + "Mayuge\t0714668006\n" + "Bugiri\t0714668003\n" + "Namutumba\t0714668052\n" + "Mbale\t0714667960\n" + "Sironko\t0714668032\n" + "Manafwaa\t0714667964\n" + "Bulambuli\t0718790612\n" + "Bududa\t0715411679\n" + "Kapchorwa\t0714668064\n" + "Kween\t0718731747\n" + "Bukwo\t0718946095\n" + "Tororo\t0714667966\n" + "Butaleja\t0714668067\n" + "Kibuku\t0718790608\n" + "Busia\t0714667973\n" + "Budaka\t0714668036\n" + "Pallisa\t0714667970\n" + "Soroti\t0714667982\n" + "Bukedia\t0715490357\n" + "Katakwi\t0714667988\n" + "Kaberemaido\t0714667985\n" + "Serere\t0718792428\n" + "Ngora\t0718731793\n" + "Amuria\t0711374163\n" + "Kumi\t0714667989\n" + "Moroto\t0714668078\n" + "Napak\t0718731148\n" + "Nakapiripiriti\t0772415440\n" + "Amudat\t0782350809\n" + "Kotido\t0714667994\n" + "Abim\t0712278519\n" + "Kabong\t0758579575\n" + "Lira\t0714667902\n" + "Amolatar\t0715989973\n" + "Dokolo\t0715120462\n" + "Kole\t0718731780\n" + "Alebtong\t0718731776\n" + "Oyam\t0718357459\n" + "Otuke\t0718731799\n" + "Apac\t0714667905\n" + "Gulu\t0714667893\n" + " \n" + "\n" + "Kitgum\t0714667896\n" + "Agago\t0718792472\n" + "Lamwo\t0718642454\n" + "Nwoya\t0718792403\n" + "Amuru\t0718731709\n" + "Pader\t0714667899\n" + "Moyo\t0714667918\n" + "Adjumani\t0714667916\n" + "Yumbe\t0714667922\n" + "Arua\t0714667911\n" + "Koboko\t0714667913\n" + "Zombo\t0718642432\n" + "Maracha\t0711374175\n" + "Nebbi\t0714667919\n" + "Hoima\t0714667947\n" + "Buliisa\t0714667953\n" + "Masindi\t0714667950\n" + "Kiryandongo\t0718731761\n" + "Kibaale\t0714667944\n" + "Kabarole\t0714667938\n" + "Kasese\t0714667926\n" + "Kamwenge\t0714667932\n" + "Ntoroko\t0718731795\n" + "Bundibugyo\t0714667929\n" + "Kyenjonjo\t0714667935\n" + "Kyegegwa\t0718731772\n" + "Mbarara\t0714667841\n" + "Ibanda\t0714667845\n" + "Ntungamo\t0714667848\n" + "Kiruhura\t0714667863\n" + "Isingiro\t0714667866\n" + "Bushenyi\t0714667851\n" + "Rubirizi\t0718792436\n" + "Mitooma\t0718792468\n" + "Sheema / Kibingo\t0718731758\n" + "Buhweju / Nsiika\t0718792448\n" + "Kabale\t0714667857\n" + "Kanungu\t0714667860\n" + "Rukungiri\t0714667869\n" + "Kisoro\t0714667854\n" + "Masaka\t0714667877\n" + "Lwengo\t0718731751\n" + "Kalungu\t0718792421\n" + "Bukomansimbi\t0718731796\n" + "Lyantonde\t0714667876\n" + "Kalangala\t0714667886\n" + " \n" + "\n" + "Rakai\t0714667880\n" + "Sembabule\t0714667883"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.activity_list_item, android.R.id.text1, values){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view.findViewById(android.R.id.text1);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,14);
                return view;
            }
        };

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                int itemPosition = position;
                String itemValue = (String) listView.getItemAtPosition(position);

                /**Toast.makeText(getApplicationContext(),
                        "Position :" + itemPosition + "  ListItem : " + itemValue, Toast.LENGTH_LONG)
                        .show();**/

            }
        });
    }
}
