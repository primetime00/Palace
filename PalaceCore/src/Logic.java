import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Ryan on 12/5/2015.
 */
public class Logic implements Hand.EndCardsListener{

    enum GameState {
        DEAL,
        SELECT_END_CARDS,
        PLAY,
        MAX
    }

    private BlockingQueue<Runnable> mQueue;

    private GameState mState;
    private int mNumberOfPlayers;

    private List<Hand> mHands, mEndHands;
    private Deck mDeck;

    public Logic() {
        mState = GameState.DEAL;
        mNumberOfPlayers = 4;
        mDeck = new Deck();
        mHands = new ArrayList<>();
        mEndHands = new ArrayList<>();
        mQueue = new LinkedBlockingQueue<Runnable>();
        for (int i=0; i<mNumberOfPlayers; ++i) {
            Hand h = new Hand(i, i == 0 ? Hand.HandType.PLAYER : Hand.HandType.CPU, mDeck, mQueue);
            mHands.add(h);
            h.SetEndCardListener(this);
        }
    }

    public void Run() {
        while (true) {
            switch (mState) {
                case DEAL:
                    mDeck.Shuffle();
                    DealCards();
                    mState = GameState.SELECT_END_CARDS;
                    break;
                case SELECT_END_CARDS:
                    SelectEndCards();
                    if (mQueue.size() > 0) {
                        try {
                            mQueue.take().run();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    }

    private void SelectEndCards() {
        mEndHands.addAll(mHands);
        for (final Hand h : mHands) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    h.SelectEndCards();
                }
            }).start();
        }
    }

    @Override
    public void onEndCardDone(Hand hand) {
        mEndHands.remove(hand);
        if (mEndHands.size() == 0) {
            mState = GameState.PLAY;
        }
    }


    public void DealCards() {
        System.out.print("Dealing cards...");
        //first 3 are for hidden
        int i;
        for (i=0; i<3; ++i) {
            for (Hand h : mHands) {
                h.AddHiddenCard(mDeck.Draw());
            }
        }
        //next 7 are active!
        for (i=0; i<7; ++i) {
            for (Hand h : mHands) {
                h.AddActiveCard(mDeck.Draw());
            }
        }
    }
}
