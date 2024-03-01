package org.kappeh.illegalcrimevelocity.chat;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ChatHistory implements Iterable<Component> {
    @NotNull private final ArrayList<Component> buffer;
    private final int maxSize;
    private int size;
    private int head;

    public ChatHistory(final int maxSize) {
        this.buffer = new ArrayList<>(maxSize);
        this.maxSize = maxSize;
        this.size = 0;
        this.head = 0;
    }

    public void push(@NotNull final Component message) {
        if (this.size < this.maxSize) {
            this.buffer.add(message);
            this.size += 1;
        } else {
            this.buffer.set(this.head, message);
            this.head = (this.head + 1) % this.maxSize;
        }
    }

    @NotNull @Override public final Iterator<Component> iterator() {
        return new ChatHistoryIterator(this.buffer, this.size, this.head);
    }

    private static class ChatHistoryIterator implements Iterator<Component> {
        @NotNull private final ArrayList<Component> buffer;
        private final int size;
        private final int head;
        private int index;

        public ChatHistoryIterator(@NotNull final ArrayList<Component> buffer, final int size, final int head) {
            this.buffer = buffer;
            this.size = size;
            this.head = head;
            this.index = 0;
        }

        @Override public boolean hasNext() {
            return this.index < this.size;
        }

        @Override public Component next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            final Component message = buffer.get((this.index + this.head) % this.size);
            this.index += 1;
            return message;
        }
    }
}
