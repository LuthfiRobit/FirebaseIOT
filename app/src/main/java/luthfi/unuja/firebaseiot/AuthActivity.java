package luthfi.unuja.firebaseiot;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AuthActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    //pengenalan variable-variable
    private EditText etUsername, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        //pengenalan identitas variable dalam tampilan
        etUsername = (EditText) findViewById(R.id.et_username);
        etPassword = (EditText) findViewById(R.id.et_password);
        /* Menjalankan Method razia() Jika tombol SignIn di keyboard di sentuh */
        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {
                    razia();
                    return true;
                }
                return false;
            }
        });

        CheckBox cbShow = (CheckBox) findViewById(R.id.cb_show);
        cbShow.setOnCheckedChangeListener(this);

        //fungsi tombol signUp
        Button btnSignUp = (Button) findViewById(R.id.btn_signup_move);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), SignUpActivity.class));
            }
        });

        //fungsi tombol login
        Button btnSignIn = (Button) findViewById(R.id.btn_signin);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                razia();
            }
        });
    }

    /**
     * ke MainActivity jika data Status Login dari Data Preferences bernilai true
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (Preferences.getLoggedInStatus(getBaseContext())) {
            startActivity(new Intent(getBaseContext(), MainActivity.class));
            finish();
        }
    }

    /**
     * Men-check inputan User dan Password dan Memberikan akses ke MainActivity
     */
    private void razia() {
        /* Mereset semua Error dan fokus menjadi default */
        etUsername.setError(null);
        etPassword.setError(null);
        View fokus = null;
        boolean cancel = false;

        /* Mengambil text dari form User dan form Password dengan variable baru bertipe String*/
        String user = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        /* Jika form user kosong atau TIDAK memenuhi kriteria di Method cekUser() maka, Set error
         *  di Form User dengan menset variable fokus dan error di Viewnya juga cancel menjadi true*/
        if (TextUtils.isEmpty(user)) {
            etUsername.setError("This field is required");
            fokus = etUsername;
            cancel = true;
        } else if (!cekUser(user)) {
            etUsername.setError("This Username is not found");
            fokus = etUsername;
            cancel = true;
        }

        /* Sama syarat percabangannya dengan User seperti di atas. Bedanya ini untuk Form Password*/
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("This field is required");
            fokus = etPassword;
            cancel = true;
        } else if (!cekPassword(password)) {
            etPassword.setError("This password is incorrect");
            fokus = etPassword;
            cancel = true;
        }

        /* Jika cancel true, variable fokus mendapatkan fokus */
        if (cancel) fokus.requestFocus();
        else masuk();
    }

    /**
     * Menuju ke MainActivity dan Set User dan Status sedang login, di Preferences
     */
    private void masuk() {
        Preferences.setLoggedInUser(getBaseContext(), Preferences.getRegisteredUser(getBaseContext()));
        Preferences.setLoggedInStatus(getBaseContext(), true);
        startActivity(new Intent(getBaseContext(), MainActivity.class));
        finish();
    }

    /**
     * True jika parameter password sama dengan data password yang terdaftar dari Preferences
     */
    private boolean cekPassword(String password) {
        return password.equals(Preferences.getRegisteredPass(getBaseContext()));
    }

    /**
     * True jika parameter user sama dengan data user yang terdaftar dari Preferences
     */
    private boolean cekUser(String user) {
        return user.equals(Preferences.getRegisteredUser(getBaseContext()));
    }

    //metode lihat password
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!isChecked) {
            // show password
            etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        } else {
            // hide password
            etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }
    }
}
