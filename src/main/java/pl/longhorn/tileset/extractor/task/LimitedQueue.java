package pl.longhorn.tileset.extractor.task;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Stream;

public class LimitedQueue<CONTENT> {

    private Queue<CONTENT> contents = new ConcurrentLinkedDeque<CONTENT>();
    private int maxSize;

    public LimitedQueue(int maxSize) {
        this.maxSize = maxSize;
    }

    public void add(CONTENT content) {
        contents.add(content);
        if (contents.size() > maxSize) {
            contents.remove();
        }
    }

    public Stream<CONTENT> stream() {
        return contents.stream();
    }

    public void remove(CONTENT content) {
        contents.remove(content);
    }
}
