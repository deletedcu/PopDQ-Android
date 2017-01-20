package com.popdq.app.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.popdq.app.R;
import com.popdq.app.values.Values;
import com.bumptech.glide.Glide;

/**
 * Created by Dang Luu on 22/07/2016.
 */
public class FragmentImage extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_fragment_image_view, container, false);
        ImageView image = (ImageView) view.findViewById(R.id.image);
        String uri = getArguments().getString(Values.image);
        Glide.with(getContext()).load(Values.BASE_URL_AVATAR + uri).into(image);
        return view;
    }
}
