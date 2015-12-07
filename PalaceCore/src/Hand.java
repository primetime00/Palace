import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Ryan on 12/5/2015.
 */
public class Hand {

    enum HandType {
        PLAYER,
        CPU,
        MAX
    }
    private Deck mDeck; //the deck we are playing from
    private int mID;
    private HandType mType;

    private List<Card> mHiddenCards;
    private List<Card> mEndCards;
    private List<Card> mActiveCards;

    private EndCardsListener mEndCardListener;
    private BlockingQueue mQueue;

    public interface EndCardsListener {
        void onEndCardDone(Hand hand);
    }


    public Hand(int id, HandType type, Deck deck, BlockingQueue queue) {
        mID = id;
        mType = type;
        mDeck = deck;
        mQueue = queue;
        mHiddenCards = new ArrayList<>();
        mEndCards = new ArrayList<>();
        mActiveCards = new ArrayList<>();
    }

    public void SetEndCardListener(EndCardsListener listener) {
        mEndCardListener = listener;
    }

    public void AddHiddenCard(Card card) {
        mHiddenCards.add(card);
    }

    public void AddActiveCard(Card card) {
        mActiveCards.add(card);
    }

    public void SelectEndCards() {
        System.out.print("Player " + mID + " Select your 3 end cards.");
        long seed = System.nanoTime();
        Random r = new Random(seed);
        if (mType == HandType.PLAYER) {
            for (int i=0; i<3; ++i) {
                Card c = mActiveCards.get(r.nextInt(mActiveCards.size()));
                mEndCards.add(c);
                mActiveCards.remove(c);
                System.out.print("HUMAN SELECTS " + c);
/*                while (true) {
                    System.out.print(this);
                    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                    try {
                        String value = br.readLine();
                        int index = Integer.parseInt(value);
                        Card c = mActiveCards.get(index);
                        mEndCards.add(c);
                        mActiveCards.remove(c);
                    } catch (Exception e) {
                        continue;
                    }
                    break;
                }*/
            }
        } else {
            //cpu ai
            for (int i=0; i<3; ++i) {
                Card c = mActiveCards.get(r.nextInt(mActiveCards.size()));
                mEndCards.add(c);
                mActiveCards.remove(c);
            }
        }
        if (mEndCardListener != null) {
            mQueue.add(new Runnable() {
                @Override
                public void run() {
                    mEndCardListener.onEndCardDone(Hand.this);
                }
            });
        }
    }

    @Override
    public String toString() {
        String s = "Active Cards: ";
        for (Card c : mActiveCards) {
            s+= c.toString() + "\n";
        }
        return s;
    }
}
