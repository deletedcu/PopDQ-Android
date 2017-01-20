package com.popdq.app;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.GridView;

import com.popdq.libs.AZCommon;
import com.popdq.libs.AZDirectory;
import com.popdq.libs.AZPhotoAdapter;

public class AZDirectoryActivity extends AppCompatActivity implements AZPhotoAdapter.AZPhotoAdapterListener {
    private GridView gvGalery;
    private AZPhotoAdapter mAdapter;
    private AZDirectory mDirectory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.azstack_directory);

        mDirectory = AZCommon.mDirectorySelected;
        if (mDirectory == null) {
            finish();
        }

        initActionbar();
        setComponentView();
        setViewListener();

        mAdapter = new AZPhotoAdapter(getBaseContext(), mDirectory.getListImage(), this);
        gvGalery.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void initActionbar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        if (mDirectory.getName() != null) {
            actionBar.setTitle(mDirectory.getName());
        }
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem sendItem = menu.findItem(R.id.azstack_menu_send);
        sendItem.setVisible(false);
        if (AZCommon.numSelected > 0) {
            sendItem.setVisible(true);
        } else {
            sendItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.azstack_menu_send, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemId = item.getItemId();
        if (menuItemId == android.R.id.home) {
            onBackPressed();
        } else if (menuItemId == R.id.azstack_menu_send) {
            AZCommon.isSendClicked = true;
            finish();
        }

        return true;
    }


    private void setComponentView() {
        gvGalery = (GridView) findViewById(R.id.gv_galery);
    }

    private void setViewListener() {
//        gvGalery.setOnScrollListener(new PauseOnScrollListener(LocalImageLoader.getInstance(), false, false, null));
    }

    @Override
    public void onContentClick(int position) {
        // send luon
        onCheckboxClick(position);
        AZCommon.isSendClicked = true;
        finish();
    }

    @Override
    public void onCheckboxClick(int position) {
        if (!mAdapter.isSelected(position)) {
            mDirectory.setNumImageSelected(mDirectory.getNumImageSelected() + 1);

            // trong che do select one thi send luon
            AZCommon.isSendClicked = true;
            finish();
        } else {
            mDirectory.setNumImageSelected(mDirectory.getNumImageSelected() - 1);
        }
        mAdapter.selectImage(position);
    }
}
