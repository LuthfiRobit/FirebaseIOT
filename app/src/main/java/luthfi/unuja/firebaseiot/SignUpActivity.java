package luthfi.unuja.firebaseiot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class SignUpActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private EditText etUsernameDft, etPasswordDft, etPasswordConfirmDft;
    private Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etUsernameDft = (EditText) findViewById(R.id.et_username);
        etPasswordDft = (EditText) findViewById(R.id.et_password);
        etPasswordConfirmDft = (EditText) findViewById(R.id.et_confirm_password);
        etPasswordConfirmDft.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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

        btnSignUp = (Button) findViewById(R.id.btn_signup);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                razia();
            }
        });

    }

    /**
     * Men-check inputan User dan Password dan Memberikan akses ke MainActivity
     */
    private void razia() {
        /* Mereset semua Error dan fokus menjadi default */
        etUsernameDft.setError(null);
        etPasswordDft.setError(null);
        etPasswordConfirmDft.setError(null);
        View fokus = null;
        boolean cancel = false;

        /* Mengambil text dari Form User, Password, Repassword dengan variable baru bertipe String*/
        String repassword = etPasswordConfirmDft.getText().toString();
        String user = etUsernameDft.getText().toString();
        String password = etPasswordDft.getText().toString();

        /* Jika form user kosong atau MEMENUHI kriteria di Method cekUser() maka, Set error di Form-
         * User dengan menset variable fokus dan error di Viewnya juga cancel menjadi true*/
        if (TextUtils.isEmpty(user)) {
            etUsernameDft.setError("This field is required");
            fokus = etUsernameDft;
            cancel = true;
        } else if (cekUser(user)) {
            etUsernameDft.setError("This Username is already exist");
            fokus = etUsernameDft;
            cancel = true;
        }

        /* Jika form password kosong dan MEMENUHI kriteria di Method cekPassword maka,
         * Reaksinya sama dengan percabangan User di atas. Bedanya untuk Password dan Repassword*/
        if (TextUtils.isEmpty(password)) {
            etPasswordDft.setError("This field is required");
            fokus = etPasswordDft;
            cancel = true;
        } else if (!cekPassword(password, repassword)) {
            etPasswordConfirmDft.setError("This password is incorrect");
            fokus = etPasswordConfirmDft;
            cancel = true;
        }

        /** Jika cancel true, variable fokus mendapatkan fokus. Jika false, maka
         *  Kembali ke LoginActivity dan Set User dan Password untuk data yang terdaftar */
        if (cancel) {
            fokus.requestFocus();
        } else {
            Preferences.setRegisteredUser(getBaseContext(), user);
            Preferences.setRegisteredPass(getBaseContext(), password);
            finish();
        }
    }

    /**
     * True jika parameter password sama dengan parameter repassword
     */
    private boolean cekPassword(String password, String repassword) {
        return password.equals(repassword);
    }

    /**
     * True jika parameter user sama dengan data user yang terdaftar dari Preferences
     */
    private boolean cekUser(String user) {
        return user.equals(Preferences.getRegisteredUser(getBaseContext()));
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!isChecked) {
            // show password
            etPasswordDft.setTransformationMethod(PasswordTransformationMethod.getInstance());
            etPasswordConfirmDft.setTransformationMethod(PasswordTransformationMethod.getInstance());
        } else {
            // hide password
            etPasswordDft.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            etPasswordConfirmDft.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }
    }
}
