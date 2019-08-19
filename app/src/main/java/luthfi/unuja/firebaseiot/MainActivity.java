package luthfi.unuja.firebaseiot;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import me.itangqi.waveloadingview.WaveLoadingView;

public class MainActivity extends AppCompatActivity {
    //pengenalan variable-variable dalam tampilan mainactivity
    CardView cvGantiParameter;
    TextView tvNilai;
    EditText etNilai;
    Button btnNilai, btnView, btnUp;
    WaveLoadingView wlvAir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        //Variable dan fungsi koneksi ke Real Time Database Firebase online
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        //Variable pnentuan child yang ada dalam Real Time Database Firebase
        final DatabaseReference parameter = myRef.child("Parameter").child("Nilai");
        final DatabaseReference ultrasonik = myRef.child("Ultrasonik").child("Sensor");

        //pengenalan identitas varialble pada tampilan
        cvGantiParameter = findViewById(R.id.view_ganti_parameter);
        cvGantiParameter.setVisibility(View.GONE);
        tvNilai = findViewById(R.id.tv_nilai);
        etNilai = findViewById(R.id.et_nilai);

        //implementasi class InputFilterMinMax pada variable etNilai (EditText)
        etNilai.setFilters(new InputFilter[]{new InputFilterMinMax("1", "100")});

        btnNilai = findViewById(R.id.btn_nilai);
        btnView = findViewById(R.id.btn_tampil_view);
        btnUp = findViewById(R.id.btn_up);
        wlvAir = findViewById(R.id.waveLoadingView);

        //menampilkan data dari Real Time Database Firebase (Parameter)
        parameter.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                tvNilai.setText(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //menampilkan data dari Real Time Database Firebase (Persentase Sensor)
        ultrasonik.addValueEventListener(new ValueEventListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                //Log.d(TAG, "Value is: " + value);
                wlvAir.setProgressValue(Integer.parseInt(String.valueOf(value)));
                wlvAir.setCenterTitle(String.format("%d%%", Integer.parseInt(String.valueOf(value))));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        //aksi tombol ganti
        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cvGantiParameter.setVisibility(View.VISIBLE);
            }
        });

        //aksi tombol arah atas
        btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cvGantiParameter.setVisibility(View.INVISIBLE);
            }
        });

        //eksekusi tombol simpan
        btnNilai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etNilai.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, "Nilai Tidak Boleh Kosong ...", Toast.LENGTH_SHORT).show();
                } else {
                    String changedNilai = etNilai.getText().toString();
                    parameter.setValue(changedNilai);
                    etNilai.setText("");
                }

            }
        });

    }

    //fungsi menu profile
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        clickedMenu(item.getItemId());
        return super.onOptionsItemSelected(item);
    }

    private void clickedMenu(int itemId) {
        switch (itemId) {
            case R.id.menu_profile:
                Intent intentProfile = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intentProfile);
                break;
            case R.id.menu_logout:
                Preferences.clearLoggedInUser(getBaseContext());
                startActivity(new Intent(getBaseContext(),AuthActivity.class));
                finish();
                break;
        }
    }
}
