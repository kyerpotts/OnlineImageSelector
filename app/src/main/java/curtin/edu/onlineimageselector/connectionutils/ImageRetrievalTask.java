package curtin.edu.onlineimageselector.connectionutils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import curtin.edu.onlineimageselector.connectionutils.RemoteUtilities;


// Code is taken from provided demonstration code in MAD by Sajib Mistry
public class ImageRetrievalTask implements Callable<List<Bitmap>> {
    private Activity uiActivity;
    private String data;
    private RemoteUtilities remoteUtilities;

    public ImageRetrievalTask(Activity uiActivity) {
        this.uiActivity = uiActivity;
        remoteUtilities = RemoteUtilities.getInstance(this.uiActivity);
        this.data = null;
    }

    @Override
    public List<Bitmap> call() throws Exception {
        List<Bitmap> images = null;
        Bitmap image;
        List<String> endpoints = getEndpoints(this.data);
        if (endpoints == null) {
            uiActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(uiActivity, "No image found", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            images = new ArrayList<>();
            for (String endpoint : endpoints) {
                image = getImageFromUrl(endpoint);
                images.add(image);
            }
        }
        return images;
    }

    private List<String> getEndpoints(String data) {
        List<String> imageUrls = null;
        try {
            JSONObject jBase = new JSONObject(data);
            JSONArray jHits = jBase.getJSONArray("hits");
            if (jHits.length() > 0) {
                imageUrls = new ArrayList<>();
                int i = 0;
                while (i < jHits.length() && i < 15) {
                    JSONObject jHitsItem = jHits.getJSONObject(i);
                    imageUrls.add(jHitsItem.getString("previewURL"));
                    i++;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return imageUrls;
    }

    private Bitmap getImageFromUrl(String endpoint) {
        Bitmap image = null;
        Uri.Builder url = Uri.parse(endpoint).buildUpon();
        String urlString = url.build().toString();
        HttpURLConnection connection = remoteUtilities.openConnection(urlString);
        if (connection != null) {
            if (remoteUtilities.isConnectionOkay(connection)) {
                image = getBitmapFromConnection(connection);
                connection.disconnect();
            }
        }
        return image;
    }

    private Bitmap getBitmapFromConnection(HttpURLConnection connection) {
        Bitmap data = null;
        try {
            InputStream inputStream = connection.getInputStream();
            byte[] byteData = getByteArrayFromInputStream(inputStream);
            data = BitmapFactory.decodeByteArray(byteData, 0, byteData.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    private byte[] getByteArrayFromInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[4096];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }

    public void setData(String data) {
        this.data = data;
    }

}
