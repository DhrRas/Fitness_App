package com.example.myapplication.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment {
    ProgressDialog mProgress;

    
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProgress = new ProgressDialog(getActivity());
    }

    public void showDialog(String msg) {
        try {
            mProgress.setMessage(msg);
            mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgress.setIndeterminate(false);
            mProgress.setCancelable(false);
            mProgress.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void CloseDialog() {
        if (mProgress != null) {
            mProgress.dismiss();
        }
    }
}
