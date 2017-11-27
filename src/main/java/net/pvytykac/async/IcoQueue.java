package net.pvytykac.async;

import net.pvytykac.async.Dao.BatchItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Paly
 * @since 2017-11-26
 */
public class IcoQueue {

    private final Thread thread;
    private final BlockingQueue<BatchItem> queue = new LinkedBlockingQueue<>();
    private final FillTask filler = new FillTask(queue);

    public IcoQueue() {
        thread = new Thread(filler);
    }

    public void start() {
        thread.start();
    }

    public void stop() {
        thread.interrupt();
    }

    public boolean hasMore() {
        return filler.offsetId < 10000000;
    }

    public BatchItem next() {
        BatchItem item = null;
        do {
            try {
                item = queue.take();
            } catch (InterruptedException ignored) {}
        } while (item == null);

        return item;
    }

    private static class FillTask implements Runnable {

        private static final Logger LOG = LoggerFactory.getLogger(FillTask.class);

        private final Queue<BatchItem> queue;
        private int offsetId = 0;
        private boolean running = true;

        public FillTask(Queue<BatchItem> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            while(running) {
                try {
                    int size = queue.size();
                    if (size < 20) {
                        Set<BatchItem> batch = DaoImpl.INSTANCE.getNextBatch(offsetId, 100);
                        queue.addAll(batch);
                        LOG.debug("Added a batch of {}", batch.size());

                        batch.stream()
                            .max(Comparator.comparingInt(BatchItem::getId))
                            .map(it -> this.offsetId = it.getId());
                    } else {
                        LOG.trace("There are still {} items in the queue", size);
                    }

                    Thread.sleep(1000L);
                } catch (InterruptedException ex) {
                    this.running = false;
                } catch (SQLException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }
        }
    }

}
