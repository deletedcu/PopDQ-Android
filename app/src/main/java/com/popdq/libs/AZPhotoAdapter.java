package com.popdq.libs;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.popdq.app.R;
import com.popdq.app.util.Utils;

import java.io.File;
import java.util.ArrayList;

public class AZPhotoAdapter extends BaseAdapter {
    private ArrayList<AZPhoto> mImages;
    private LayoutInflater mInflater;
    private Context mContext;
    private AZPhotoAdapterListener AZPhotoAdapterListener;

    public AZPhotoAdapter(Context context, ArrayList<AZPhoto> images, AZPhotoAdapterListener listener) {
        mContext = context;
        mImages = images;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        AZPhotoAdapterListener = listener;
    }

    @Override
    public int getCount() {
        return mImages.size();
    }

    @Override
    public Object getItem(int pos) {
        return mImages.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    public void selectImage(int pos) {
        boolean select = !mImages.get(pos).isSelected();
        if (select)
            AZCommon.numSelected++;
        else if (AZCommon.numSelected > 0)
            AZCommon.numSelected--;
        mImages.get(pos).setSelected(select);
        notifyDataSetChanged();
    }

    public boolean isSelected(int pos) {
        return mImages.get(pos).isSelected();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup arg2) {
        ViewHolder holder;
        AZPhoto image = mImages.get(position);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.azstack_gung_item_photo, null);
            holder = new ViewHolder();
            holder.imvPhoto = (ImageView) convertView.findViewById(R.id.imv_photo);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        displayPhoto(image, holder.imvPhoto);

        holder.imvPhoto.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AZPhotoAdapterListener.onContentClick(position);
            }
        });

        return convertView;
    }

    private void displayPhoto(AZPhoto image, ImageView imvPhoto) {
        try {
            String fileName = image.getName();
            // gen cache file path
            // String cacheFilePath = GunGConfig.GALARY_FOLDER + "/" + "IMG_" +
            // fileName + ".jpg";
            File file = new File(Utils.getAppSubDirectory(mContext, Constant.GALLERY_CACHE), "IMG_" + fileName + ".jpg");
            String cacheFilePath = file.getPath();

            Bitmap bm = AZBitmapMemCacheManager.getInstance(mContext).getBitmapFromMemCache(cacheFilePath);

            if (bm == null) { // neu anh tuong ung filePath da duoc cache
                GunGBitmapWorkerTask task = new GunGBitmapWorkerTask(mContext, imvPhoto);
                task.setGenCacheFile(true);
                task.execute(image.getPath());
            } else {
                imvPhoto.setImageBitmap(bm);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ViewHolder {
        ImageView imvPhoto;
    }

    public interface AZPhotoAdapterListener {
        public void onContentClick(int position);

        public void onCheckboxClick(int position);
    }
}
