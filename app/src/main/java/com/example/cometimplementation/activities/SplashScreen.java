package com.example.cometimplementation.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cometimplementation.R;
import com.example.cometimplementation.utilities.ApiCalls;
import com.example.cometimplementation.utilities.SharedPrefData;
import com.example.cometimplementation.utilities.Utilities;

public class SplashScreen extends AppCompatActivity {
    LinearLayout splash_screen;
    RelativeLayout bottom_sheet;
    EditText input_number;
    Handler handler;
    TextView login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_splash_screen);
        initViews();
        handler.postDelayed(() -> {
            if (SharedPrefData.getUserId(SplashScreen.this).isEmpty()) {
                bottom_sheet.setVisibility(View.VISIBLE);
            } else {
                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, 3000);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utilities.isNumberValid(input_number.getText().toString().trim()))
                    ApiCalls.cometChatLogin(SplashScreen.this, input_number.getText().toString().trim());
                else
                    Utilities.showSnackBarMessage(view,"Invalid Number");
            }
        });
    }

    private void initViews() {

        input_number = findViewById(R.id.input_number);
        bottom_sheet = findViewById(R.id.bottom_sheet);
        splash_screen = findViewById(R.id.splash_screen);
        login = findViewById(R.id.login);
        handler = new Handler();
        input_number.addTextChangedListener(textWatcher);

    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

//            if (Utilities.isNumberValid(charSequence.toString())) {
//                login.setClickable(true);
//                input_number.setError(null);
//                hideKeyBoard();
//            } else {
//                login.setClickable(false);
//                input_number.setError("Invalid number");
//            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

//    public void login(View view) {
//        if (Utilities.isNumberValid(input_number.getText().toString().trim()))
//            ApiCalls.cometChatLogin(SplashScreen.this, input_number.getText().toString().trim());
//        else
//            Utilities.showSnackBarMessage(view,"Invalid Number");
//    }

    public void hideKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}