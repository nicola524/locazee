package it.nicolabrogelli.imedici.task;

import android.content.Context;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import it.nicolabrogelli.imedici.interfaces.AsyncTaskCompleteListener;
import it.nicolabrogelli.imedici.models.Post;
import it.nicolabrogelli.imedici.utils.DoneHandlerInputStream;

/**
 * Created by Nicola on 27/09/2015.
 */
public class DownloadPostAsyncTask extends AsyncTask<String, Post, Post> {

    //private ArrayList<Post> postArrayList;

    // URL to get contacts JSON
    private  String site_url = "http://www.aviscerretoguidi.it/api/get_post/?id=7774";

    // JSON Node names
    private static final String TAG_POSTS = "posts";
    private static final String TAG_ID = "id";
    private static final String TAG_POST_URL = "url";
    private static final String TAG_TYPE = "type";
    private static final String TYPE_SULG = "slug";
    private static final String TAG_STATUS = "status";
    private static final String TAG_TITLE = "title";
    private static final String TAG_CONTENT = "content";
    private static final String TAG_ATTACHMENTS = "attachments";
    private static final String TAG_URL_IMAGE = "url";
    private static final String TAG_POST_EXCERPT = "excerpt";

    private Context context;
    private AsyncTaskCompleteListener<Post> listener;


    public DownloadPostAsyncTask(Context ctx, AsyncTaskCompleteListener<Post> listener) {
        this.context = ctx;
        this.listener = listener;
    }

    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     * <p/>
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     *
     * @param params The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    @Override
    protected Post doInBackground(String... params) {
        Post t = null;
        try {

            JSONObject obj = getJSONObject(params[0]);

            if (obj.getString("status").equalsIgnoreCase("ok")) {
                t = new Post();
                t.set_ID((Integer) obj.getJSONObject("post").get(TAG_ID));
                t.set_TITLE(Html.fromHtml(obj.getJSONObject("post").get(TAG_TITLE).toString()).toString());
                t.set_CONTENT(obj.getJSONObject("post").get(TAG_CONTENT).toString());
                t.set_URL(obj.getJSONObject("post").get(TAG_POST_URL).toString());
                t.set_EXCERPT(obj.getJSONObject("post").get(TAG_POST_EXCERPT).toString());

                //postArrayList.add(t);
                //t = null;

            }
        } catch (JSONException ignored) {
            Log.e("ERRORE", ignored.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return t;
    }

    @Override
    protected void onProgressUpdate(Post... values) {

    }

    @Override
    protected void onPostExecute(Post post) {
        super.onPostExecute(post);
        listener.onTaskComplete(post);
    }



    private JSONObject getJSONObject(String url) throws IOException, MalformedURLException, JSONException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();

        InputStream in = conn.getInputStream();

        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader r = new BufferedReader(new InputStreamReader(new DoneHandlerInputStream(in)));
            for (String line = r.readLine(); line != null; line = r.readLine()) {
                sb.append(line);
            }
            return new JSONObject(sb.toString());
        } finally {
            in.close();
        }
    }

}
