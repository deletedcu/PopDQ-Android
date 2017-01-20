package com.popdq.libs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.view.Display;
import android.view.WindowManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class BitmapUtils {

    public static Bitmap decodeFile(File f) {
        try {
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream stream1 = new FileInputStream(f);
            BitmapFactory.decodeStream(stream1, null, o);
            stream1.close();

            // Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 100;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            FileInputStream stream2 = new FileInputStream(f);
            Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
            stream2.close();
            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap decodeFile(File f, int size) {
        try {
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream stream1 = new FileInputStream(f);
            BitmapFactory.decodeStream(stream1, null, o);
            stream1.close();

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < size || height_tmp / 2 < size)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            FileInputStream stream2 = new FileInputStream(f);
            Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
            stream2.close();
            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap decodeFile(Context context, String path) {
        try {
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            File f = new File(path);
            FileInputStream stream1 = new FileInputStream(f);
            BitmapFactory.decodeStream(stream1, null, o);
            stream1.close();

            // Find the correct scale value. It should be the power of 2.
            Display dm = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            @SuppressWarnings("deprecation")
            int wDisplay = dm.getWidth();
            if (wDisplay < 240) {
                wDisplay = 240;
            }
            final int REQUIRED_SIZE = wDisplay / 2;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 <= REQUIRED_SIZE || height_tmp / 2 <= REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            FileInputStream stream2 = new FileInputStream(f);
            Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
            stream2.close();
            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap decodeFile(String path, int size) {
        try {
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            File f = new File(path);
            FileInputStream stream1 = new FileInputStream(f);
            BitmapFactory.decodeStream(stream1, null, o);
            stream1.close();

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < size || height_tmp / 2 < size)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            FileInputStream stream2 = new FileInputStream(f);
            Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
            stream2.close();
            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // public static void galleryAddPic(Context context, String filePath) {
    // Intent mediaScanIntent = new Intent(
    // Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
    // File f = new File(filePath);
    // Uri contentUri = Uri.fromFile(f);
    // mediaScanIntent.setData(contentUri);
    // context.sendBroadcast(mediaScanIntent);
    // }

    /**
     * Resize a bitmap
     *
     * @param input
     * @param destWidth
     * @param destHeight
     * @return
     * @throws OutOfMemoryError
     */
    public static Bitmap resizeBitmap(final Bitmap input, int destWidth, int destHeight) throws OutOfMemoryError {
        return resizeBitmap(input, destWidth, destHeight, 0);
    }

    /**
     * Resize a bitmap object to fit the passed width and height
     *
     * @param input      The bitmap to be resized
     * @param destWidth  Desired maximum width of the result bitmap
     * @param destHeight Desired maximum height of the result bitmap
     * @return A new resized bitmap
     * @throws OutOfMemoryError if the operation exceeds the available vm memory
     */
    public static Bitmap resizeBitmap(final Bitmap input, int destWidth, int destHeight, int rotation)
            throws OutOfMemoryError {

        int dstWidth = destWidth;
        int dstHeight = destHeight;
        final int srcWidth = input.getWidth();
        final int srcHeight = input.getHeight();

        if (rotation == 90 || rotation == 270) {
            dstWidth = destHeight;
            dstHeight = destWidth;
        }

        boolean needsResize = false;
        float p;
        if ((srcWidth > dstWidth) || (srcHeight > dstHeight)) {
            needsResize = true;
            if ((srcWidth > srcHeight) && (srcWidth > dstWidth)) {
                p = (float) dstWidth / (float) srcWidth;
                dstHeight = (int) (srcHeight * p);
            } else {
                p = (float) dstHeight / (float) srcHeight;
                dstWidth = (int) (srcWidth * p);
            }
        } else {
            dstWidth = srcWidth;
            dstHeight = srcHeight;
        }

        if (needsResize || rotation != 0) {
            Bitmap output;

            if (rotation == 0) {
                output = Bitmap.createScaledBitmap(input, dstWidth, dstHeight, true);
            } else {
                Matrix matrix = new Matrix();
                matrix.postScale((float) dstWidth / srcWidth, (float) dstHeight / srcHeight);
                matrix.postRotate(rotation);
                output = Bitmap.createBitmap(input, 0, 0, srcWidth, srcHeight, matrix, true);
            }
            return output;
        } else
            return input;
    }

    public static Bitmap rotate(Context context, String path) {
        Bitmap bitmap = decodeFile(context, path);
        try {
            // Find if the picture is rotated
            ExifInterface exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_NORMAL:
                    return bitmap;
                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                    matrix.setScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.setRotate(180);
                    break;
                case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                    matrix.setRotate(180);
                    matrix.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_TRANSPOSE:
                    matrix.setRotate(90);
                    matrix.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.setRotate(90);
                    break;
                case ExifInterface.ORIENTATION_TRANSVERSE:
                    matrix.setRotate(-90);
                    matrix.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.setRotate(-90);
                    break;
                default:
                    return bitmap;
            }

            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            Bitmap bitmapRotated = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
            bitmap.recycle();
            return bitmapRotated;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public static Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }
}
