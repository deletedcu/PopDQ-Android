package com.popdq.libs;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.popdq.app.R;
import com.popdq.app.util.Utils;

import java.io.File;
import java.util.ArrayList;

public class AZDirectoryAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<AZDirectory> mDirectories;
    private LayoutInflater mInflater;

    public AZDirectoryAdapter(Context context, ArrayList<AZDirectory> directories) {
        mContext = context;
        mDirectories = directories;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mDirectories.size();
    }

    @Override
    public Object getItem(int pos) {
        return mDirectories.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup arg2) {
        ViewHolder holder;
        AZDirectory dir = mDirectories.get(pos);


        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.azstack_item_album, null);
            holder = new ViewHolder();
            holder.imvPhoto = (ImageView) convertView.findViewById(R.id.imv_photo);
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tvNumImage = (TextView) convertView.findViewById(R.id.tv_num_image);
            holder.tvNumSelected = (TextView) convertView.findViewById(R.id.tv_num_selected);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvName.setText(dir.getName());
        holder.tvNumImage.setText(dir.getListImage().size() + "");
        int numSelected = dir.getNumImageSelected();
        if (numSelected == 0) {
            holder.tvNumSelected.setVisibility(View.GONE);
        } else {
            holder.tvNumSelected.setVisibility(View.VISIBLE);
            holder.tvNumSelected.setText("+" + numSelected);
        }

        displayPhoto(dir.getFirstImage(), holder.imvPhoto);

        return convertView;
    }

    private void displayPhoto(AZPhoto image, ImageView imvPhoto) {
        try {
            String fileName = image.getName();
            // gen cache file path
            // String cacheFilePath = GunGConfig.GALARY_FOLDER + "/" + "IMG_" +
            // fileName + ".jpg";
            File file = new File(Utils.getAppSubDirectory(mContext, Constant.GALLERY_CACHE), fileName + ".jpg");
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
        TextView tvNumSelected, tvName, tvNumImage;
    }
}
