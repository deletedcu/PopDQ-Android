package com.popdq.app.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import com.popdq.app.R;
import com.popdq.app.fragment.FragmentImage;
import com.popdq.app.values.Values;

import java.util.List;

public class ViewImageActivity extends BaseActivity {
    private ViewPager pager;
    private List<String> listImage;
    private int currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        pager = (ViewPager) findViewById(R.id.pager);
        listImage = getIntent().getExtras().getStringArrayList("listImages");
        currentPosition = getIntent().getExtras().getInt(Values.index);
        pager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()));
        pager.setCurrentItem(currentPosition,false);

    }

    public class FragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {

        public FragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return listImage.size();
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new FragmentImage();
            Bundle bundle = new Bundle();
            bundle.putString(Values.image, listImage.get(position));
            fragment.setArguments(bundle);
            return fragment;
        }


    }
}
