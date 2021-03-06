package com.lyndir.lhunath.opal.wayward.navigation;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import java.util.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.wicket.markup.html.panel.Panel;


/**
 * <h2>{@link AbstractTabState}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>07 10, 2010</i> </p>
 *
 * @author lhunath
 */
public abstract class AbstractTabState<P extends Panel> implements TabState<P> {

    private final List<String> fragments;

    /**
     * You probably don't need to implement this constructor.
     *
     * @param fragments The fragment elements of this state.
     */
    protected AbstractTabState(final Collection<String> fragments) {

        this.fragments = new LinkedList<>( fragments );
        assertFragments();
    }

    protected AbstractTabState(final CharSequence fragment) {

        this( Lists.newLinkedList( Splitter.on( '/' ).split( fragment ) ) );
    }

    protected AbstractTabState(final P panel) {

        fragments = loadFragments( panel );
        assertFragments();
    }

    protected abstract List<String> loadFragments(P panel);

    @Override
    public void apply(@Nonnull final P panel)
            throws IncompatibleStateException {

        applyFragments( panel, new LinkedList<>( fragments ) );
    }

    @SuppressWarnings("RedundantThrows")
    protected abstract void applyFragments(P panel, Deque<String> fragments)
            throws IncompatibleStateException;

    @Nullable
    protected String findFragment(final int index) {

        if (index < fragments.size())
            return fragments.get( index );

        return null;
    }

    protected void appendFragment(final String fragment) {

        fragments.add( fragment );
        assertFragments();
    }

    /**
     * Override this method if you need to do custom checks on the validity of String fragments.
     */
    protected void assertFragments() {

    }

    @Override
    public String toFragment() {

        return Joiner.on( '/' ).useForNull( "" ).join( getFragments() );
    }

    protected final Iterable<String> getFragments() {

        return fragments;
    }

    @Override
    public String toString() {

        return String.format( "{%s: %s}", getClass().getSimpleName(), toFragment() );
    }
}
