/*
 *   Copyright 2007, Maarten Billemont
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.lyndir.lhunath.opal.gui;

import com.lyndir.lhunath.opal.math.Vec2D;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import javax.swing.*;


/**
 * <h2>{@link GLabel}<br> <sub>A rounded replacement for the default {@link JLabel}.</sub></h2>
 *
 * <p> <i>Apr 9, 2008</i> </p>
 *
 * @author mbillemo
 */
public class GLabel extends JLabel {

    private static final long serialVersionUID = 1L;

    protected boolean hasMouse, isPressed;

    protected int           padding;
    private   GradientPaint gradientBase;
    private int arc = 5;

    /**
     * Create a new {@link GLabel} instance.
     *
     * @param text                The text to render in the label.
     * @param icon                The icon to display next to the text on the label.
     * @param horizontalAlignment The alignment of the text in the label.
     * @param gradient            The gradient base color for the label.
     */
    public GLabel(final String text, final Icon icon, final int horizontalAlignment, final Color gradient) {

        super( text, icon, horizontalAlignment );
        init( gradient );
    }

    /**
     * Create a new {@link GLabel} instance.
     *
     * @param text                The text to render in the label.
     * @param horizontalAlignment The alignment of the text in the label.
     * @param gradient            The gradient base color for the label.
     */
    public GLabel(final String text, final int horizontalAlignment, final Color gradient) {

        super( text, horizontalAlignment );
        init( gradient );
    }

    /**
     * Create a new {@link GLabel} instance.
     *
     * @param text     The text to render in the label.
     * @param gradient The gradient base color for the label.
     */
    public GLabel(final String text, final Color gradient) {

        super( text );
        init( gradient );
    }

    /**
     * Create a new {@link GLabel} instance.
     *
     * @param icon                The icon to display next to the text on the label.
     * @param horizontalAlignment The alignment of the text in the label.
     * @param gradient            The gradient base color for the label.
     */
    public GLabel(final Icon icon, final int horizontalAlignment, final Color gradient) {

        super( icon, horizontalAlignment );
        init( gradient );
    }

    /**
     * Create a new {@link GLabel} instance.
     *
     * @param icon     The icon to display next to the text on the label.
     * @param gradient The gradient base color for the label.
     */
    public GLabel(final Icon icon, final Color gradient) {

        super( icon );
        init( gradient );
    }

    /**
     * Create a new {@link GLabel} instance.
     *
     * @param gradient The gradient base color for the label.
     */
    public GLabel(final Color gradient) {

        init( gradient );
    }

    private void init(final Color gradient) {

        addMouseListener( new GLabelMouseAdapter() );
        setHorizontalAlignment( HORIZONTAL );
        setGradient( gradient );
        setPadding( 1 );
    }

    /**
     * @return The padding of this {@link GLabel}.
     */
    public int getPadding() {

        return padding;
    }

    /**
     * @param padding The padding of this {@link GLabel}.
     */
    public void setPadding(final int padding) {

        this.padding = padding;
        repaint();
    }

    /**
     * @param base The base color of the gradient for this {@link GLabel}'s background.
     */
    private void setGradient(final Color base) {

        Color src = new Color( base.getRed() / 255f, base.getGreen() / 255f, base.getBlue() / 255f, 50 / 255f );
        gradientBase = new GradientPaint( new Point2D.Double( 0, 0 ), src, new Point2D.Double( 0, 1 ), base );
    }

    /**
     * @return The arc of this {@link GLabel}.
     */
    public int getArc() {

        return arc;
    }

    /**
     * @param arc The arc of this {@link GLabel}.
     */
    public void setArc(final int arc) {

        this.arc = arc;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void paintComponent(final Graphics g) {

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

        Vec2D size = new Vec2D( getSize().getWidth(), getSize().getHeight() );
        Vec2D src = new Vec2D( gradientBase.getPoint1().getX(), gradientBase.getPoint1().getY() ).multiply( size );
        Vec2D dst = new Vec2D( gradientBase.getPoint2().getX(), gradientBase.getPoint2().getY() ).multiply( size );
        Color color1 = gradientBase.getColor1();
        Color color2 = gradientBase.getColor2();

        if (hasMouse) {
            color1 = new Color( color1.getAlpha() << 24 | color1.brighter().getRGB() - 0xff000000, true );
            color2 = new Color( color2.getAlpha() << 24 | color2.brighter().getRGB() - 0xff000000, true );
        }

        GradientPaint gradient = new GradientPaint( new Point2D.Double( src.getX(), src.getY() ), color1, new Point2D.Double( dst.getX(), dst.getY() ), color2 );

        g2.setPaint( gradient );
        g2.fillRoundRect( padding, padding, getWidth() - 1 - padding * 2, getHeight() - 1 - padding * 2, arc, arc );

        g2.setPaint( gradientBase.getColor2().brighter() );
        g2.drawRoundRect( padding, padding, getWidth() - 1 - padding * 2, getHeight() - 1 - padding * 2, arc, arc );

        super.paintComponent( g );
    }

    private class GLabelMouseAdapter extends MouseAdapter {

        private static final int CLICK_PADDING = 2;
        private int originalPadding;

        GLabelMouseAdapter() {

        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void mouseEntered(final MouseEvent e) {

            hasMouse = true;
            repaint();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void mouseExited(final MouseEvent e) {

            hasMouse = false;
            repaint();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void mousePressed(final MouseEvent e) {

            isPressed = true;
            originalPadding = padding;
            setPadding( originalPadding + CLICK_PADDING );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void mouseReleased(final MouseEvent e) {

            isPressed = false;
            setPadding( originalPadding );
        }
    }
}
