package com.lyndir.lhunath.opal.system.collection;

import com.google.common.base.Preconditions;
import com.google.common.collect.ForwardingListIterator;
import com.google.common.collect.Iterators;
import java.io.Serializable;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import javax.annotation.Nullable;


/**
 * <h2>{@link ListIteratorView}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>05 21, 2010</i> </p>
 *
 * @author lhunath
 */
public abstract class ListIteratorView<T extends Serializable> extends ForwardingListIterator<T> implements Serializable {

    private static final long serialVersionUID = 0;
    @Nullable
    transient ListIterator<T> delegate;
    @Nullable
    T current;
    int currentIndex = -1;

    /**
     * @return {@code true} if this iterator is not empty.
     */
    public boolean hasCurrent() {

        return currentIndex >= 0;
    }

    /**
     * @return The element at the cursor.  That is, the element that was returned from the previous call to #next or #previous.
     *
     * @throws NoSuchElementException When the iterator hasn't been navigated yet..
     */
    public T current() {

        if (!hasCurrent())
            throw new NoSuchElementException( "Cursor does not point to an element.  Use next() or previous() first." );

        return Preconditions.checkNotNull( current );
    }

    /**
     * @return The index that the cursor currently points at.  That is, the index of the element that was returned from the previous call
     *         to
     *         #next or #previous
     */
    public int currentIndex() {

        return currentIndex;
    }

    @Override
    public T next() {

        int nextIndex = nextIndex();
        current = super.next();
        currentIndex = nextIndex;

        return current;
    }

    @Override
    public T previous() {

        int previousIndex = previousIndex();
        current = super.previous();
        currentIndex = previousIndex;

        return current;
    }

    /**
     * Reset the iterator such that the next call to #next returns the first object.
     *
     * @return {@code this} for chaining.
     */
    public ListIteratorView<T> reset() {

        delegate = null;
        currentIndex = -1;

        return this;
    }

    protected abstract ListIterator<T> load();

    @Override
    protected ListIterator<T> delegate() {

        if (delegate == null) {
            delegate = load();

            if (hasCurrent())
                // Move the delegate back to the current index.
                Iterators.advance( delegate, currentIndex );
        }

        return delegate;
    }
}
