package com.example.cometimplementation.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;


import com.example.cometimplementation.utilities.ApiCalls;
import com.example.cometimplementation.R;
import com.example.cometimplementation.utilities.Utilities;
import com.example.cometimplementation.utilities.SharedPrefData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout input;
    private TextInputEditText uid_input;
    private FloatingActionButton login;
    private TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        if (SharedPrefData.getUserId(LoginActivity.this).isEmpty() && SharedPrefData.getSaveContacts(this) == null && SharedPrefData.getCommonUser(this) == null) {
            setContentView(R.layout.activity_login);
            initViews();
        }else if(SharedPrefData.getSaveContacts(this) == null || SharedPrefData.getCommonUser(this) == null){
            Intent i = new Intent(LoginActivity.this, ContactImportingAndProcessingActivity.class);
            startActivity(i);
            finish();
        }
        else {

            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }

    }

    private void initViews() {

        input = findViewById(R.id.input);
        login = findViewById(R.id.login);
        uid_input = findViewById(R.id.uid_input);
        message = findViewById(R.id.message);
        uid_input.addTextChangedListener(textWatcher);
        getSupportActionBar().setTitle("Login");
        login.setEnabled(false);
        message.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce_anim));


        login.setOnClickListener(view -> {
            ApiCalls.cometChatLogin(LoginActivity.this, input.getEditText().getText().toString().trim());
        });

    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            if (Utilities.isNumberValid(charSequence.toString())) {
                login.setEnabled(true);
                input.setError(null);
                hideKeyBoard();
            } else {
                login.setEnabled(false);
                input.setError("Invalid number");
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    public void hideKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}