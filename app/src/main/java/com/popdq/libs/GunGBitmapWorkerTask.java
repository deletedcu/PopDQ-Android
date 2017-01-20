package com.popdq.libs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.popdq.app.util.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

public class GunGBitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

    private final WeakReference<ImageView> imageViewReference;
    public static final String TYPE_ABS_PATH = "absolute_path";
    public static final String TYPE_CONTENT_URI = "content_uri";
    private String inputType;
    private Context context;
    private static final String TAG = "BitmapWorkerTask";
    private static final int WIDTH = 240;
    private boolean genCacheFile = false;

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public void setGenCacheFile(boolean genCacheFile) {
        this.genCacheFile = genCacheFile;
    }

    public GunGBitmapWorkerTask(Context ctx, ImageView imageView) {
        // Use a WeakReference to ensure the ImageView can be garbage
        // collected
        context = ctx;
        imageViewReference = new WeakReference<ImageView>(imageView);
        inputType = TYPE_ABS_PATH;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        Bitmap bitmap = null;
        String filePath = params[0];
        //
        String fileName = getFileName(filePath);
        String originalPath = filePath;

        if (genCacheFile) {
            filePath = Utils.getAppSubDirectory(context, Constant.GALLERY_CACHE) + "/" + "IMG_" + fileName + ".jpg";
        }

        if (inputType.equals(TYPE_ABS_PATH)) {
            // params[0]: path of image
            // kiem tra xem da ton tai trong cache cua anh nay chua
            bitmap = AZBitmapMemCacheManager.getInstance(context).getBitmapFromMemCache(filePath);
            if (bitmap == null) {
                Log.d(TAG, "-----get disk");
                // check disk file
                File f = new File(filePath);
                if (genCacheFile && !f.exists()) {
                    Log.d(TAG, "-----gen file");
                    bitmap = genCacheFile(context, originalPath, WIDTH, WIDTH, fileName);
                }
                //
                if (bitmap == null)
                    bitmap = decodeSampledBitmapFromFile(filePath, WIDTH, WIDTH);
            }

            if (bitmap != null) {
                AZBitmapMemCacheManager.getInstance(context).addBitmapToMemoryCache(String.valueOf(filePath), bitmap);
            }
        } else {
            try {
                InputStream in = context.getContentResolver().openInputStream(Uri.parse(filePath));
                bitmap = decodeSampledBitmapFromInputStream(in, WIDTH, WIDTH);
                AZBitmapMemCacheManager.getInstance(context).addBitmapToMemoryCache(String.valueOf(filePath), bitmap);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                return null;
            }

        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = (ImageView) imageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    private String getFileName(String filePath) {
        File f = new File(filePath);
        String fileName = f.getName();
        if (fileName.indexOf(".") >= 0)
            fileName = fileName.substring(0, fileName.indexOf("."));
        return fileName;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee a final image
            // with both dimensions larger than or equal to the requested height
            // and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger
            // inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down
            // further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    /**
     * @param reqWidth  The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @return A bitmap sampled down from the original with the same aspect
     * ratio and dimensions that are equal to or greater than the
     * requested width and height
     * @author cngp_thaodv Decode and sample down a bitmap from a file to the
     * requested width and height.
     */
    public static Bitmap decodeSampledBitmapFromInputStream(InputStream in, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(in, null, options);
        options.inPreferredConfig = Config.RGB_565;
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // If we're running on Honeycomb or newer, try to use inBitmap
        /*
         * if (Utils.hasHoneycomb()) { addInBitmapOptions(options, cache); }
		 */
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap decodedBitmap = BitmapFactory.decodeStream(in, null, options);
        return decodedBitmap;
    }

    /**
     * @param filename  The full path of the file to decode
     * @param reqWidth  The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @return A bitmap sampled down from the original with the same aspect
     * ratio and dimensions that are equal to or greater than the
     * requested width and height
     * @author cngp_thaodv Decode and sample down a bitmap from a file to the
     * requested width and height.
     */
    public static Bitmap decodeSampledBitmapFromFile(String filename, int reqWidth, int reqHeight) {
        try {
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filename, options);
            options.inPreferredConfig = Config.RGB_565;
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // If we're running on Honeycomb or newer, try to use inBitmap
            /*
             * if (Utils.hasHoneycomb()) { addInBitmapOptions(options, cache); }
			 */

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            Bitmap decodedBitmap = BitmapFactory.decodeFile(filename, options);
            return decodedBitmap;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "fileName = " + filename);
            return null;
        }
    }

    public static Bitmap genCacheFile(Context context, String filePath, int width, int height, String cacheName) {

        Bitmap scaledBitmap = null;
        try {
            scaledBitmap = decodeSampledBitmapFromFile(filePath, width, height);
            if (scaledBitmap == null) {
                return null;
            }
            // rotate Bitmap
            scaledBitmap = rotateBitmap(scaledBitmap, filePath);

            // save Bitmap
            // ByteArrayOutputStream out = null;
            String newFilePath = Utils.getAppSubDirectory(context, Constant.GALLERY_CACHE) + "/" + "IMG_"
                    + cacheName + ".jpg";
            FileOutputStream fos;

            // out = new ByteArrayOutputStream();
            fos = new FileOutputStream(newFilePath);
            boolean scaled = scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            Log.d(TAG,
                    "scaled = " + scaled + " height = " + scaledBitmap.getHeight() + " width = "
                            + scaledBitmap.getWidth());
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "ExceptionEnd: " + e.toString());
        }
        return scaledBitmap;
    }

    public static Bitmap rotateBitmap(Bitmap scaledBitmap, String filePath) {
        try {
            ExifInterface exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            // Returns an immutable bitmap from subset of the source bitmap,
            // transformed by the optional matrix. The new bitmap may be the
            // same object as source, or a copy may have been made. It is
            // initialized with the same density as the original bitmap. If
            // the source bitmap is immutable and the requested subset is
            // the same as the source bitmap itself, then the source bitmap
            // is returned and no new bitmap is created.
            return Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            Log.e(TAG, "rotateBitmap: " + e.toString());
            return null;
        }
    }
}