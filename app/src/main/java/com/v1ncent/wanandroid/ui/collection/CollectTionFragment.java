package com.v1ncent.wanandroid.ui.collection;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.v1ncent.wanandroid.R;
import com.v1ncent.wanandroid.base.AppFragment;

/**
 * Created by v1ncent on 2018/3/15.
 */

public class CollectTionFragment extends AppFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_index, null);
        return view;
    }

    @Override
    public void onClickListener(View v) {

    }
}
