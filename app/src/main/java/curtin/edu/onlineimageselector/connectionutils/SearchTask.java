package curtin.edu.onlineimageselector.connectionutils;

import android.app.Activity;
import android.net.Uri;


import java.net.HttpURLConnection;
import java.util.concurrent.Callable;

import curtin.edu.onlineimageselector.connectionutils.RemoteUtilities;


// Code is taken from provided demonstration code in MAD by Sajib Mistry
public class SearchTask implements Callable<String> {
    private String searchkey;
    private String baseUrl;
    private RemoteUtilities remoteUtilities;
    private Activity uiActivity;

    public SearchTask(Activity uiActivity) {
        this.searchkey = null;
        this.baseUrl = "https://pixabay.com/api/";
        this.uiActivity = uiActivity;
        this.remoteUtilities = RemoteUtilities.getInstance(this.uiActivity);
    }

    @Override
    public String call() {
        String response = null;
        String endpoint = getSearchEndpoint();
        HttpURLConnection connection = remoteUtilities.openConnection(endpoint);
        if (connection != null) {
            if (remoteUtilities.isConnectionOkay(connection)) {
                response = remoteUtilities.getResponseString(connection);
                connection.disconnect();
            }
        }
        return response;
    }

    private String getSearchEndpoint() {
        String data = null;
        Uri.Builder url = Uri.parse(this.baseUrl).buildUpon();
        url.appendQueryParameter("key", "23319229-94b52a4727158e1dc3fd5f2db");
        url.appendQueryParameter("q", this.searchkey);
        data = url.build().toString();

        return data;
    }

    public void setSearchkey(String searchkey) {
        this.searchkey = searchkey;
    }
}
