package at.bitcoinaustria.votebot;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Menu;
import android.widget.ProgressBar;
import android.widget.TextView;

public class VoteDisplay extends Activity {


    public static final int SCALE = 1000;
    private ProgressBar obama;
    private ProgressBar romney;
    private TextView obamaText;
    private TextView romneyText;
    private PowerManager.WakeLock wakeLock;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_display);
        obama = (ProgressBar) findViewById(R.id.obama);
        romney = (ProgressBar) findViewById(R.id.romney);
        obamaText = (TextView) findViewById(R.id.obamaText);
        romneyText = (TextView) findViewById(R.id.romneyText);
        scaleDrawable(obama);
        scaleDrawable(romney);
        Net.global.addListener(new VoteNotifier() {
            @Override
            public void onVoteStatus(final long obamaAmount, final long romneyAmout) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateProgress(obamaAmount, romneyAmout);
                    }
                });
            }
        });
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, Net.TAG);
        wakeLock.acquire();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wakeLock.release();
        Net.global.removeAllListeners();
    }

    private void updateProgress(long obamaAmount, long romneyAmount) {
        obama.setProgress(0);
        romney.setProgress(0);
        final int total;
        total = Math.max(1, (int) ((obamaAmount + romneyAmount) / SCALE));
        obama.setMax(total);
        romney.setMax(total);
        obama.setProgress((int) (obamaAmount / SCALE));
        romney.setProgress((int) (romneyAmount / SCALE));
        obamaText.setText(Bitcoins.valueOf(obamaAmount).toCurrencyString() + "          btc.to/7v4");
        romneyText.setText(Bitcoins.valueOf(romneyAmount).toCurrencyString() + "          btc.to/7v5");
        Log.i(Net.TAG, "new voting powers: obama " + obamaAmount + " romney " + romneyAmount);
    }

    private void scaleDrawable(ProgressBar person) {
        person.setScaleY(6.0f);
    /*    Rect bounds = person.getProgressDrawable().getBounds();


        Drawable origDraw = person.getProgressDrawable();
        person.setProgressDrawable(new ColorDrawable(android.R.color.black));
        person.setMax(100);
        person.setProgress(40);
        person.getProgressDrawable().setBounds(bounds);
    */    // person.setProgressDrawable(new ScaleDrawable(origDraw, 0, 1.0f, 2.0f));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_vote_display, menu);
        return true;
    }
}
