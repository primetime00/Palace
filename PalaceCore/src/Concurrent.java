import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by keg45397 on 12/7/2015.
 */
public class Concurrent {
    private BlockingQueue<Runnable> mQueue;
    private DoneSelectingTopCards mDoneSelectingTopCardsListener;

    public interface DoneSelectingTopCards {
        void onDoneSelectingTopCards(Hand hand);
    }

    public Concurrent() {
        mQueue = new LinkedBlockingQueue<Runnable>();
    }

    public void runConcurrent(Runnable start, Runnable end) {
        new Thread(start);
    }

    public void setOnDoneSelectingTopCardsListener(DoneSelectingTopCards listener) {
        mDoneSelectingTopCardsListener = listener;
    }




}
