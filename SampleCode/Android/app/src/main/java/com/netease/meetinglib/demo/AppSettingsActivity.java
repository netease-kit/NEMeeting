/*
 * Copyright (c) 2014-2020 NetEase, Inc.
 * All right reserved.
 */

package com.netease.meetinglib.demo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.netease.meetinglib.demo.utils.SPUtils;

import org.jetbrains.annotations.NotNull;

public class AppSettingsActivity extends AppCompatActivity {

    public static void start(Context context) {
        Intent starter = new Intent(context, AppSettingsActivity.class);
        context.startActivity(starter);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setId(View.generateViewId());
        setContentView(frameLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        getSupportFragmentManager()
                .beginTransaction()
                .replace(frameLayout.getId(), new AppSettingsFragment())
                .commit();
    }

    public static class AppSettingsFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            getPreferenceManager().setSharedPreferencesName(SPUtils.SP_FILE);
            setPreferencesFromResource(R.xml.app_settings, rootKey);
            EditTextPreference numberPreference = findPreference("meeting-logger-level-config");
            if (numberPreference != null) {
                numberPreference.setOnBindEditTextListener(
                        editText -> editText.setInputType(InputType.TYPE_CLASS_NUMBER));
            }
        }
    }
}
