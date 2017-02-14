package com.popdq.app.connection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.popdq.app.model.FileModel;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by Dang Luu on 14/07/2016.
 */
public class FileUtil {
    public static String getPathBitmapCompressed(Bitmap bitmap) {

        try {
        bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), false);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
        String fileName = System.currentTimeMillis() + "";
        File folder = new File(Environment.getExternalStorageDirectory()
                + "/azpop/temp/");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File f = new File(Environment.getExternalStorageDirectory()
                + "/azpop/temp/" + fileName + ".jpg");

            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            fo.close();
            return f.getPath();

        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static Bitmap scaleBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float dst = 1;
        if (width > 500) {
            dst = 500f / (float) width;
            bitmap = Bitmap.createScaledBitmap(bitmap, (int) (dst * bitmap.getWidth()), (int) (dst * bitmap.getHeight()), true);
        }
        return bitmap;
    }

    public static Bitmap compressBitmap(Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
        bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
        return bitmap;
    }


    public static String sendFile(String urlServer, String token, List<FileModel> fileModels) throws IOException {
        String url = urlServer;
        HttpPost httpost = new HttpPost(url);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        for (FileModel fileModel : fileModels) {
            stringBuilder.append("{\"fileSize\":" + fileModel.getFileSize() + ",\"duration\":" + fileModel.getDuration() + "},");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1).append("]");

        String a = stringBuilder.toString();

//        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
//                "ota.log");
//        File file2 = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
//                "hehe.log");
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        try {
            builder.addPart("token", new StringBody(token, Charset.forName(HTTP.UTF_8)));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        for (FileModel fileModel : fileModels) {
            builder.addPart("attachments[]", new FileBody(new File(fileModel.getPath())));
            Log.e("send file", fileModel.getPath());
        }
        builder.addPart("attachments_info", new StringBody(a, Charset.forName(HTTP.UTF_8)));
//        builder.addPart("attachments[]", new FileBody(file));
//        builder.addPart("attachments[]", new FileBody(file2));
        final HttpEntity yourEntity = builder.build();
        httpost.setEntity(yourEntity);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response = client.execute(httpost);
        String message = getContent(response);
        Log.e("send file", message);
        int s = 0;
        s++;
        String link = "";
        try {
            JSONObject jsonObject = new JSONObject(message);
            link = jsonObject.getString("attachments");
            return link;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return link;
    }

    public static String getContent(HttpResponse response) throws IOException {
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String body = "";
        String content = "";

        while ((body = rd.readLine()) != null) {
            content += body + "\n";
        }
        return content.trim();
    }


}
