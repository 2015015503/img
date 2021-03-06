package com.mengxin.img.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.mengxin.img.R;
import com.mengxin.img.ui.fragment.ContributeBeginFragment;
import com.mengxin.img.ui.fragment.ContributeFragment;
import com.mengxin.img.utils.NetworkUtils;
import com.r0adkll.slidr.Slidr;

public class ContributeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Long id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contribute);
        Slidr.attach(this);
        initView();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());

        ContributeBeginFragment contributeBeginFragment = new ContributeBeginFragment();

        id = NetworkUtils.isLogin(this);
        Bundle bundle = new Bundle();
        bundle.putLong("authorId",id);
        contributeBeginFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().replace(R.id.contribute_main, contributeBeginFragment).commit();

    }


}
