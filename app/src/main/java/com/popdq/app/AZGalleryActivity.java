package com.popdq.app;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.popdq.libs.AZCommon;
import com.popdq.libs.AZDirectory;
import com.popdq.libs.AZDirectoryAdapter;
import com.popdq.libs.AZPhoto;
import com.popdq.libs.AzDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class AZGalleryActivity extends AppCompatActivity {
    private ArrayList<AZDirectory> mDirectories;
    private GridView gvGalery;
    private AZDirectoryAdapter mAdapter;
//	private TextView tvNumSelected;
//	private ImageButton btnOk;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.azstack_gallery);

        initActionbar();

        // init common
        AZCommon.mDirectorySelected = null;
        AZCommon.numSelected = 0;
        AZCommon.isSendClicked = false;

        // initStorageDirectory();

        setComponentView();
        setViewListener();
        mDirectories = getListDirectory();

        mAdapter = new AZDirectoryAdapter(getBaseContext(), mDirectories);
        gvGalery.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (AZCommon.isSendClicked) {
            onButtonSendClick();
        }
    }

    @Override
    public void onBackPressed() {
        if (AZCommon.numSelected > 0) {
            AzDialog dialog = new AzDialog(AZGalleryActivity.this);
            dialog.setContent(getString(R.string.azstack_discard_image_select, AZCommon.numSelected));
            dialog.setNegative(getString(R.string.cancel), null);
            dialog.setPositive(getString(R.string.ok), new AzDialog.BtnDialogListener() {

                @Override
                public void onClick() {
                    AZCommon.numSelected = 0;
                    onBackPressed();
                }
            });
            dialog.show();
        } else {
            finish();
            super.onBackPressed();
        }
    }

    @Override
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
            onButtonSendClick();
        }

        return true;
    }

    private void initActionbar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.choose_photo);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    private void setComponentView() {
        gvGalery = (GridView) findViewById(R.id.gv_galery);
    }

    private void setViewListener() {
        gvGalery.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                Intent intent = new Intent(getBaseContext(), AZDirectoryActivity.class);
                AZCommon.mDirectorySelected = (AZDirectory) mAdapter.getItem(pos);
                startActivity(intent);
            }
        });
    }

    private ArrayList<AZDirectory> getListDirectory() {
        ArrayList<AZDirectory> directoryList = new ArrayList<AZDirectory>();
        HashMap<String, AZDirectory> hashMap = new HashMap<String, AZDirectory>();

        try {
            final String[] projection = {MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media._ID, MediaStore.Images.Media.DATE_ADDED};
            final String orderBy = MediaStore.Images.Media.DATE_ADDED + " DESC";

            Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
                    null, orderBy);

            if (cursor == null)
                return null;

            AZDirectory dir = null;
            while (cursor.moveToNext()) {
                String imageName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                String imageDate = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED));
                String parentPath = getParentPath(imagePath);
                String parentName = getParentName(imagePath);

                if (parentPath != null) {
                    dir = hashMap.get(parentPath);
                    if (dir == null) {
                        dir = new AZDirectory();
                        dir.setPath(parentPath);
                        dir.setName(parentName);
                        dir.setDate(imageDate);

                        hashMap.put(parentPath, dir);
                    }

                    AZPhoto image = new AZPhoto();
                    image.setPath(imagePath);
                    image.setName(imageName);
                    image.setSelected(false);
                    dir.addImage(image);
                }
            }

            directoryList.addAll(hashMap.values());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // soft
        directoryList = softDirectory(directoryList);

        return directoryList;
    }

    public static ArrayList<AZDirectory> softDirectory(ArrayList<AZDirectory> dirs) {
        Collections.sort(dirs, new Comparator<AZDirectory>() {

            @Override
            public int compare(AZDirectory dir1, AZDirectory dir2) {
                return dir2.getDate().compareTo(dir1.getDate());
            }
        });
        return dirs;
    }

    private String getParentPath(String filePath) {
        if (filePath == null)
            return "Other";

        File file = new File(filePath);
        if (file.exists()) {
            return file.getParent();
        } else {
            // reget with error file
            // String temp = filePath.substring(0, filePath.lastIndexOf("/"));
            return null;
        }
    }

    private String getParentName(String filePath) {
        if (filePath == null)
            return "Other";
        File file = new File(filePath);
        if (file.exists()) {
            return file.getParentFile().getName();
        } else {
            String temp = filePath.substring(0, filePath.lastIndexOf("/"));
            if (temp.indexOf("/") > 0) {
                temp = temp.substring(filePath.lastIndexOf("/"));
                return temp;
            }
        }
        return "Other";
    }

    public void onButtonSendClick() {
        AZCommon.isSendClicked = false;
        Intent intent = new Intent();
        if (AZCommon.numSelected > 0) {
            ArrayList<AZPhoto> listImageSelected = new ArrayList<AZPhoto>();
            for (AZDirectory dir : mDirectories) {
                for (AZPhoto image : dir.getListImage()) {
                    if (image.isSelected()) {
                        listImageSelected.add(image);
                    }
                }
            }
            intent.putExtra("listImageSelected", listImageSelected);
            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }
}
