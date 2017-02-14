package it.nicolabrogelli.imedici.interfaces;

/**
 *
 * @param <Post>
 */
public interface AsyncTaskCompleteListener<Post> {
    /**
     * Invoked when the AsyncTask has completed its execution.
     * @param result The resulting object from the AsyncTask.
     */
    public void onTaskComplete(Post result);
}
