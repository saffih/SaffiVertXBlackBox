package saffi;

import io.vertx.core.Future;
import org.junit.Test;

public class TestFuture {
    @Test
    public void testLearnFuture() {
        final int[] cnt = {0};
        Future<Void> start = Future.future();
        Future<Void> fut1 = Future.future();
        Future<Void> fut2 = Future.future();
        Future<Void> fut3 = Future.future();

        fut1.setHandler(v -> {
            cnt[0] += 1;
            fut2.complete();
        });
        fut2.setHandler(v -> {
            cnt[0] *= 2;
            fut3.complete();
        });
        fut3.setHandler(v -> {
            cnt[0] *= cnt[0];
            start.complete();
        });
        fut1.complete();
        if (cnt[0] != 4) {
            throw new RuntimeException("" + cnt[0]);
        }
    }
}
