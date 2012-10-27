package at.bitcoinaustria.bliver;

import android.os.AsyncTask;

/**
 * @author apetersson
 */
public abstract class BasicTask extends AsyncTask<Void,Void,Void> {
    @Override
    final protected Void doInBackground(Void... params) {
        run();
        return null;
    }

    abstract void run() ;

    public void start(){
        execute();
    }
}
