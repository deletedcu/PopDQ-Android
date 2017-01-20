package com.popdq.app.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.popdq.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dang Luu on 22/07/2016.
 */
public class RateView extends LinearLayout {
    public interface OnRateListiner {
        public void onClickStar(int numberStar);
    }

    private OnRateListiner onRateListiner;
    List<ImageModel> stars;


    public class ImageModel {
        public int index;
        public ImageView imageView;
    }

    public void setOnRateListiner(OnRateListiner onRateListiner) {
        this.onRateListiner = onRateListiner;
    }

    public void setRate(int star) {
        for (int i = 0; i < 5; i++) {
            if (i < star)
                stars.get(i).imageView.setImageResource(R.drawable.add_to_favorite_favlist_added_icon);
            else
                stars.get(i).imageView.setImageResource(R.drawable.add_to_favorite_favlist_not_added_icon);

        }
    }

    public void initView(Context context) {
        stars = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ImageModel imageModel = new ImageModel();
            imageModel.index = i;
            imageModel.imageView = new ImageView(context);
            stars.add(imageModel);
        }
        for (final ImageModel imageModel : stars) {
            imageModel.imageView.setImageResource(R.drawable.add_to_favorite_favlist_not_added_icon);
            imageModel.imageView.setTag(imageModel);
            imageModel.imageView.setPadding(2, 2, 2, 2);
            imageModel.imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    int intdex = imageModel.index;
                    if (onRateListiner != null) {
                        onRateListiner.onClickStar(intdex + 1);
                    }
                    for (ImageModel imageModel : stars) {
                        ImageModel imageModel1 = (ImageModel) imageModel.imageView.getTag();
                        if (imageModel1.index <= intdex) {
                            imageModel1.imageView.setImageResource(R.drawable.add_to_favorite_favlist_added_icon);
                        } else {
                            imageModel1.imageView.setImageResource(R.drawable.add_to_favorite_favlist_not_added_icon);
                        }
                    }
                }
            });
            addView(imageModel.imageView);
        }

    }

    public RateView(Context context) {
        super(context);
        initView(context);
    }

    public RateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);

    }

    public RateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RateView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);

    }
}
