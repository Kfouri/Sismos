package com.kfouri.sismos;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Principal extends AppCompatActivity
{

    private ProgressDialog pDialog;

    static final int DATE_DIALOG_ID = 0;
    private int mYear;
    private int mMonth;
    private int mDay;

    boolean blBuscar = false;
    String strOrden;
    String strMagnitudMinima;
    String strMagnitudMaxima;
    SismosAdapter listAdapter;

    EditText EditFechaDesde;

    JSONArray sismos = null;

    public static ArrayList<Sismo> ListaSismos = new ArrayList<Sismo>();

    ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lv = (ListView)findViewById(android.R.id.list);

        new LeerWS().execute();

        mYear = Calendar.getInstance().get(Calendar.YEAR);
        mMonth = Calendar.getInstance().get(Calendar.MONTH);
        mDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        blBuscar = false;
        strMagnitudMinima = "";
        strMagnitudMaxima = "";

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_app)
        {
            Intent intent1 = new Intent("android.intent.action.VIEW", Uri.parse("market://search?q=pub:MAKP+Team"));
            startActivity(intent1);
        }
        else if (id == R.id.action_buscar)
        {
            displayAlertDialog();
        }
        else if (id == R.id.action_compartir)
        {
            String texto;
            texto = getString(R.string.app_name)+"\n";
            texto = texto +getString(R.string.compartir)+"\n";
            texto = texto +"https://play.google.com/store/apps/details?id=com.kfouri.sismos";

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, texto);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }


        return super.onOptionsItemSelected(item);
    }

    public void displayAlertDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.dialog_layout, null);
        final EditText magnitudMinima = (EditText) alertLayout.findViewById(R.id.magnitudMinima);
        final EditText magnitudMaxima = (EditText) alertLayout.findViewById(R.id.magnitudMaxima);

        EditFechaDesde = (EditText) alertLayout.findViewById(R.id.EditFechaDesde);

        final Spinner spinner = (Spinner) alertLayout.findViewById(R.id.spnOrden);

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.orden, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Button button = (Button) alertLayout.findViewById(R.id.btn_fechaDesde);

        updateDisplay();

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0)
            {
                showDialog(DATE_DIALOG_ID);
            }

        });

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Filtro");
        alert.setView(alertLayout);
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Toast.makeText(getBaseContext(), "Cancel clicked", Toast.LENGTH_SHORT).show();
            }
        });

        alert.setPositiveButton("Buscar", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // code for matching password
                blBuscar = true;
                strMagnitudMinima = magnitudMinima.getText().toString();
                strMagnitudMaxima = magnitudMaxima.getText().toString();
                strOrden = spinner.getSelectedItem().toString();

                listAdapter.clear();
                listAdapter.notifyDataSetChanged();

                new LeerWS().execute();
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    class LeerWS extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Principal.this);
            pDialog.setMessage(getString(R.string.buscando_sismos));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            String result = "";
            try{
                //httpclient = new DefaultHttpClient();
                String fecha="";
                String orden="";
                String otros="";

                if (blBuscar)
                {

                    if (!strMagnitudMinima.equals(""))
                    {
                        otros = "&minmag="+strMagnitudMinima;
                    }

                    if (!strMagnitudMaxima.equals(""))
                    {
                        otros = otros + "&maxmag="+strMagnitudMaxima;
                    }

                    int xMonth = mMonth+1;
                    fecha = "&start="+mYear+"-"+xMonth+"-"+mDay+"T00:00:00.000000Z";

                    String chOrden = ""+strOrden.charAt(1);

                    if ((""+strOrden.charAt(0)).equals("M"))
                    {
                        orden = "&orderby=magnitude";
                    }
                    else
                    {
                        orden = "&orderby=time";
                    }

                }
                else
                {
                    Date date = Calendar.getInstance().getTime();
                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    String today = formatter.format(date);

                    fecha = "&start=" + today + "T00:00:00.000000Z";
                    orden = "&orderby=time";
                }

                URL url;
                HttpURLConnection urlConnection = null;
                result = "";
                try {
                    url = new URL("http://www.seismicportal.eu/fdsnws/event/1/query?limit=50"+fecha+"&format=json"+orden+otros);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = urlConnection.getInputStream();
                    InputStreamReader isw = new InputStreamReader(in);

                    int data = isw.read();
                    while (data != -1) {
                        char current = (char) data;
                        data = isw.read();
                        System.out.print(current);
                        result = result + current;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }

            } catch (Exception e){
                result = "error1";
            }

            try
            {
                JSONObject jsonObj = new JSONObject(result);

                sismos = jsonObj.getJSONArray("features");

                for (int i = 0; i < sismos.length(); i++) {
                    JSONObject c = sismos.getJSONObject(i);

                    // Phone node is JSON Object
                    JSONObject properties = c.getJSONObject("properties");
                    String lat = "Lat: "+properties.getString("lat");
                    String lon = "Lon: "+properties.getString("lon");
                    String mag = properties.getString("mag");
                    String fecha = getString(R.string.fecha) + properties.getString("time");
                    String ciudad = properties.getString("flynn_region");

                    ListaSismos.add(new Sismo(mag,fecha,ciudad,lat,lon));

                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            return result;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {

                    listAdapter = new SismosAdapter(Principal.this , R.layout.list_item , ListaSismos);
                    //lv.setItemsCanFocus(false);
                    lv.setAdapter(listAdapter);
                    listAdapter.notifyDataSetChanged();
                }
            });

        }

    }

    private void updateDisplay()
    {
        EditFechaDesde.setText(new StringBuilder().append(mDay).append("/").append(mMonth + 1).append("/").append(mYear).append(" "));
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = new
            DatePickerDialog.OnDateSetListener()
            {

                public void onDateSet(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {

                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                    updateDisplay();
                }
            };

    @Override
    protected Dialog onCreateDialog(int id)
    {
        switch(id)
        {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,mDay);
        }
        return null;
    }
}
