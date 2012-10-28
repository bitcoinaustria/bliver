package at.bitcoinaustria.votebot;

/**
* @author apetersson
*/
public abstract class VoteNotifier {
   public abstract void onVoteStatus(long obama, long romney);
}
