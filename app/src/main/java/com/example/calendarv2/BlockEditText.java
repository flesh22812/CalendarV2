package com.example.calendarv2;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

/**This class blocks  incorrect inputs*/
public class BlockEditText implements TextWatcher {

    private View v;
    private EditText[] edList;

    public BlockEditText(@NonNull EditText[] edList, @NonNull Button v) {
        this.v = v;
        this.edList = edList;
    }

    @Override
    public void beforeTextChanged(@NonNull CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(@NonNull CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(@NonNull Editable s) {
        for (EditText editText : edList) {
            if (editText.getText().toString().trim().length() <= 0) {
                v.setEnabled(false);
                break;
            } else {
                if (editText.getText().toString().trim().length() >= 18) {
                    v.setEnabled(false);
                    break;
                } else {
                    v.setEnabled(true);
                }
            }
        }
    }

}