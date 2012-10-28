package at.bitcoinaustria.votebot;

import android.app.Application;
import android.util.Log;
import com.google.bitcoin.core.*;
import com.google.bitcoin.discovery.DnsDiscovery;
import com.google.bitcoin.store.BlockStoreException;
import com.google.bitcoin.store.MemoryBlockStore;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author apetersson
 */
public class Net extends Application {

    static {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                Log.e(TAG, "unhandled", ex);
            }
        });
    }

    public static final NetworkParameters NETWORK = NetworkParameters.prodNet();


    public static final String OBAMA = "15sGYjXGSdnnJPdjhidfBj9SBMuripSWdg";      //http://btc.to/7v4
    public static final String ROMNEY = "1PShwtrh3mQKhAi7wSrb8NdmQGk5w5QdCT";     //http://btc.to/7v5
    public static final Address OBAMA_ADDR;

    static {
        try {
            OBAMA_ADDR = new Address(null, OBAMA);
        } catch (AddressFormatException e) {
            throw new RuntimeException(e);
        }
    }

    public static final Address ROMNEY_ADDR;

    static {
        try {
            ROMNEY_ADDR = new Address(null, ROMNEY);
        } catch (AddressFormatException e) {
            throw new RuntimeException(e);
        }
    }

    public static final int MAX_CONNECTIONS = 8;
    public static final String TAG = "SUPERPAC";

    final Set<Sha256Hash> obamaHashes = Sets.newHashSet();
    final Set<Sha256Hash> romneyHashes = Sets.newHashSet();

    final AtomicLong obamaAmount = new AtomicLong();
    final AtomicLong romneyAmount = new AtomicLong();

    final List<VoteNotifier> listeners = Lists.newArrayList();

    public static Net global;
    private final PeerGroup peerGroup;

    public Net() {
        global = this;

        try {
            peerGroup = new PeerGroup(NETWORK, new BlockChain(NETWORK, new MemoryBlockStore(NETWORK)));
            long secondsSince1970 = new Date().getTime() / 1000;
            peerGroup.setFastCatchupTimeSecs(secondsSince1970);
            peerGroup.addEventListener(new AbstractPeerEventListener() {
                @Override
                public void onPeerDisconnected(Peer peer, int peerCount) {
                    Log.i(TAG, "-connected peers: " + peerCount);
                }

                @Override
                public void onPeerConnected(Peer peer, int peerCount) {
                    Log.i(TAG, "+connected peers: " + peerCount);
                }

                @Override
                public void onTransaction(Peer peer, Transaction t) {
                    super.onTransaction(peer, t);
                    Log.d(TAG, "tx " + t.getHash());
                    for (TransactionOutput output : t.getOutputs()) {
                        try {
                            Sha256Hash hash = t.getHash();
                            addValue(output, hash, OBAMA_ADDR, obamaAmount, obamaHashes);
                            addValue(output, hash, ROMNEY_ADDR, romneyAmount, romneyHashes);

                        } catch (ScriptException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
            peerGroup.addPeerDiscovery(new DnsDiscovery(NETWORK));
            peerGroup.setMaxConnections(MAX_CONNECTIONS);
            peerGroup.start();
            Log.i(TAG, "waiting for peers");
            peerGroup.waitForPeers(3);
            Log.i(TAG, "got 3 peers!");
        } catch (BlockStoreException e) {
            throw new RuntimeException(e);
        }
    }

    public void addListener(VoteNotifier notifier) {
        listeners.add(notifier);
        Log.i(TAG, "attached listeners: " + listeners.size());
        if (isOriginal()) {
            //spice it up a little
            Random random = new Random();
            int rnd1 = (int) (random.nextFloat() * 100);
            int rnd2 = (int) (random.nextFloat() * 100);
            obamaAmount.addAndGet(rnd1*1000);
            romneyAmount.addAndGet(rnd2*1000);
        }
        notifier.onVoteStatus(obamaAmount.longValue(), romneyAmount.longValue());
    }

    private boolean isOriginal() {
        return obamaAmount.longValue() == 0 && romneyAmount.longValue() == 0;
    }
    //todo remove listener as well

    private void addValue(TransactionOutput output, Sha256Hash hash, Address addr, AtomicLong amount, Set<Sha256Hash> hashes) throws ScriptException {
        if (addr.equals(output.getScriptPubKey().getToAddress())) {
            if (!hashes.contains(hash)) {
                amount.addAndGet(output.getValue().longValue());
                for (VoteNotifier voteNotifier : listeners) {
                    voteNotifier.onVoteStatus(obamaAmount.longValue(), romneyAmount.longValue());
                }
                hashes.add(hash);
            }
        }
    }

    public void removeAllListeners() {
        Log.i(TAG, "removed listeners");
        listeners.clear();
    }
}
