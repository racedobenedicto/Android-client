package com.example.blueberry;


import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.app.PendingIntent.getActivity;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity {

    User user = new User();
    ProgressDialog pd;
    EditText editText;
    EditText editText2;
    EditText editText3;
    TextView textView;
    TextView welcome;
    Button btnLogin, btnLogout, btnSend;
    JSONObject json = null;
    ResultUser ru;
    ImageView logogrande;
    TextView saludo;
    EditText query;
    String estado;
    String boton = "login";
    Table table;
    TableLayout tabla;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.user);
        editText2 = (EditText) findViewById(R.id.pass);
        btnLogin = (Button) findViewById(R.id.login);
        btnLogout = (Button) findViewById(R.id.logout);
        btnSend = (Button) findViewById(R.id.send);
        logogrande = (ImageView) findViewById(R.id.welcome);
        welcome = (TextView) findViewById(R.id.welcometext);
        editText3 = (EditText) findViewById(R.id.host);
        saludo = (TextView) findViewById(R.id.textView);
        query = (EditText) findViewById(R.id.query);
        textView = (TextView) findViewById(R.id.error);
        tabla=(TableLayout) findViewById(R.id.tabla);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boton = "login";
                user.userName = editText.getText().toString();
                user.pass = editText2.getText().toString();
                String link = "http://www.institutelpalau.net/gestin2/raquel/sql_client.php?login2?user=" + user.userName + "&pass=" + user.pass;
                new JsonTask().execute(link);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boton = "logout";
                //Cambio de pantalla
                logogrande.setVisibility(VISIBLE);
                welcome.setVisibility(VISIBLE);
                editText3.setVisibility(VISIBLE);
                editText.setVisibility(VISIBLE);
                editText2.setVisibility(VISIBLE);
                btnLogin.setVisibility(VISIBLE);
                query.setVisibility(INVISIBLE);
                saludo.setVisibility(INVISIBLE);
                btnSend.setVisibility(INVISIBLE);
                btnLogout.setVisibility(INVISIBLE);
                tabla.setVisibility(INVISIBLE);
                textView.setVisibility(INVISIBLE);
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boton = "send";
                String link = "http://www.institutelpalau.net/gestin2/raquel/sql_client.php?" + query.getText().toString();
                if(Integer.parseInt(ru.student_id)!=0){
                    link=link+"&idStudent="+ru.student_id;
                }
                new JsonTask().execute(link);

            }
        });
    }

    static class User {
        String userName = "";
        String pass = "";
        String name;
    }

    static class Table {
        String[] fields;
        String[][] data;
        String name = "";
    }

    static class ResultUser {
        String name;
        String student_id;

        ResultUser(String name, String student_id) {
            this.name = name;
            this.student_id = student_id;
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void crearFilas(TableLayout tableLayout, String[] tabla, String tipo, int index){
        TableRow fila = new TableRow(MainActivity.this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        lp.setMargins(10, 0, 10, 0);
        fila.setLayoutParams(lp);
        //El elemento de la izquierda
        for(int i=0; i<tabla.length; i++) {
            TextView tv= new TextView(this);
            tv.setText("    "+tabla[i]);
            switch(tipo) {
                case "cabecera":
                    fila.setBackgroundColor(Color.parseColor("#6200EE"));
                    tv.setTextColor(Color.parseColor("#FFFFFF"));
                    break;
                case "par":
                    fila.setBackgroundColor(Color.parseColor("#A569C2"));
                    break;
                case "impar":
                    fila.setBackgroundColor(Color.parseColor("#E5D4ED"));
                    break;
                default:
                    break;
            }
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
            fila.addView(tv);
            System.out.println(tv.getText());
        }
        tableLayout.addView(fila,index);
    }

    private class JsonTask extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)
                }

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (pd.isShowing()) {
                pd.dismiss();
            }

            String tipo;
            if (result.contains("state")) {
                try {
                    json = new JSONObject(result);
                    if (json != null) {
                        estado = json.getString("state");

                        if (estado.compareTo("success") == 0) {
                            if (boton == "login") {
                                ru = new ResultUser(json.getString("name"), json.getString("student_id"));
                                user.name = json.getString("name");
                                saludo.setText("Welcome: " + user.name);

                                //Cambio de pantalla
                                logogrande.setVisibility(INVISIBLE);
                                welcome.setVisibility(INVISIBLE);
                                editText3.setVisibility(INVISIBLE);
                                editText.setVisibility(INVISIBLE);
                                editText2.setVisibility(INVISIBLE);
                                btnLogin.setVisibility(INVISIBLE);
                                textView.setVisibility(INVISIBLE);
                                query.setVisibility(VISIBLE);
                                saludo.setVisibility(VISIBLE);
                                btnSend.setVisibility(VISIBLE);
                                btnLogout.setVisibility(VISIBLE);

                            } else if (boton == "send") {
                                textView.setVisibility(INVISIBLE);
                                if (table != null) {
                                    table = null;
                                    tabla.removeAllViews();
                                }
                                table = new Table();
                                table.name = json.getString("nameTable");
                                System.out.println("name: " + table.name);

                                JSONArray jsData = json.getJSONArray("data");
                                JSONArray jsFields = json.getJSONArray("fields");
                                System.out.println("longitud Fields: " + jsFields.length());
                                table.fields = new String[jsFields.length()];
                                for (int i = 0; i < jsFields.length(); i++) {
                                    table.fields[i] = jsFields.optString(i);
                                    System.out.println("fields: " + "[" + i + "] " + table.fields[i]);
                                    //tabla.rellenarTabla();
                                }
                                crearFilas(tabla, table.fields, "cabecera", 0);

                                System.out.println("longitud Data: " + jsData.length());
                                table.data = new String[jsData.length()][jsFields.length()];

                                for (int i = 0; i < jsData.length(); i++) {
                                    for (int j = 0; j < jsFields.length(); j++) {
                                        table.data[i][j] = jsData.optJSONArray(i).optString(j);
                                        System.out.print(table.data[i][j] + " ");
                                    }
                                    System.out.println();
                                    if ((i + 1) % 2 == 0) {
                                        tipo = "par";
                                    } else {
                                        tipo = "impar";
                                    }
                                    crearFilas(tabla, table.data[i], tipo, i + 1);
                                }
                                tabla.setVisibility(VISIBLE);
                            }
                        }
                        else {
                            if (boton == "login") {
                                textView.setGravity(Gravity.CENTER_HORIZONTAL);
                                textView.setText("User or Password incorrect. \n Try Again!");
                                textView.setVisibility(VISIBLE);
                            } else if (boton == "send") {
                                textView.setText("Incorrect Query. Try Again!");
                                textView.setGravity(Gravity.CENTER_HORIZONTAL);
                                textView.setVisibility(VISIBLE);
                                tabla.setVisibility(INVISIBLE);
                            }
                    }
                }else {
                        textView.setVisibility(VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{
                if (boton == "login") {
                    textView.setGravity(Gravity.CENTER_HORIZONTAL);
                    textView.setText("User or Password incorrect. \n Try Again!");
                    textView.setVisibility(VISIBLE);
                } else if (boton == "send") {
                    textView.setText("Incorrect Query. Try Again!");
                    textView.setGravity(Gravity.CENTER_HORIZONTAL);
                    textView.setVisibility(VISIBLE);
                    tabla.setVisibility(INVISIBLE);
                }
            }
        }
    }

}

