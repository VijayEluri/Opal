/*
 *   Copyright 2005-2007 Maarten Billemont
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
package com.lyndir.lhunath.opal.math;

import static com.lyndir.lhunath.opal.system.util.StringUtils.*;

import com.lyndir.lhunath.opal.system.util.ObjectMeta;
import java.io.Serializable;
import java.util.Objects;
import javax.annotation.Nullable;


/**
 * <i>Vec2 - A two dimensional vector.</i><br> <br> <p> TODO: Optimize by caching calculations (much like Angle)? </p> <br> The Vec2 object
 * represents a two dimensional vector in a plane.<br> <br>
 *
 * @author lhunath
 */
public class Vec2 implements Serializable {

    private static final long serialVersionUID = 0;

    /**
     * X-Axis coordinate.
     */
    private final int x;

    /**
     * Y-Axis coordinate.
     */
    private final int y;

    @Nullable
    @ObjectMeta(ignoreFor = ObjectMeta.For.toString)
    private final Size wrapSize;

    /**
     * Create a new two dimensional vector at the origin.
     */
    public Vec2() {

        this( 0, 0, null );
    }

    /**
     * Create a new two dimensional vector.
     *
     * @param x The x-coordinate of the new vector.
     * @param y The y-coordinate of the new vector.
     */
    public Vec2(final int x, final int y) {

        this( x, y, null );
    }

    /**
     * Create a new two dimensional vector in a wrapping space.
     *
     * @param x        The x-coordinate of the new vector.
     * @param y        The y-coordinate of the new vector.
     * @param wrapSize The size of the wrapping space.
     */
    public Vec2(final int x, final int y, @Nullable final Size wrapSize) {

        this.x = x;
        this.y = y;
        this.wrapSize = wrapSize;
    }

    /**
     * @return The horizontal destination of this vector.
     */
    public int getX() {

        return x;
    }

    /**
     * @return The vertical destination of this vector.
     */
    public int getY() {

        return y;
    }

    public int getDX(final Vec2 other) {
        int dx = other.getX() - getX();

        // Take wrapping into account.
        if (wrapSize != null) {
            int width = wrapSize.getWidth();
            if (dx > width / 2)
                dx -= width;
            else if (dx < -width / 2)
                dx += width;
        }

        return dx;
    }

    public int getDY(final Vec2 other) {
        int dy = other.getY() - getY();

        // Take wrapping into account.
        if (wrapSize != null) {
            int height = wrapSize.getHeight();
            if (dy > height / 2)
                dy -= height;
            else if (dy < -height / 2)
                dy += height;
        }

        return dy;
    }

    public int distanceTo(final Vec2 other) {
        int dx = getDX( other ), dy = getDY( other );

        return (Math.abs( dx ) + Math.abs( dy ) + Math.abs( dx + dy )) / 2;
    }

    /**
     * @return The size at which operations on this vector wrap.
     */
    @Nullable
    public Size getWrapSize() {
        return wrapSize;
    }

    /**
     * Calculate the length of the vector.
     *
     * @return The length of this vector.
     */
    public double length() {

        return Math.sqrt( lengthSq() );
    }

    /**
     * Calculate the squared length of the vector.<br> <br> It is advised to use this function in favor of {@link #length()} due to
     * performance.
     *
     * @return The squared length of this vector.
     */
    public int lengthSq() {

        return getX() * getX() + getY() * getY();
    }

    /**
     * Normalize this vector.
     *
     * @return A new, normalized version of this vector; pointing in the same direction with length 1.
     */
    public Vec2 normalize() {

        double length = length();
        return multiply( 1 / length );
    }

    /**
     * Rotate this vector over an angle.
     *
     * @param a The angle over which to rotate.
     *
     * @return A new vector with the resulting coordinates.
     */
    public Vec2 rotate(final Angle a) {

        if (a == null)
            return this;

        return copyWith( (int) (getX() * a.sin() + getY() * a.cos()), //
                         (int) (getX() * a.cos() - getY() * a.sin()) );
    }

    /**
     * Add another vector to this one.
     *
     * @param vector The vector which will be added to this.
     *
     * @return A new vector with the resulting coordinates.
     */
    public Vec2 translate(final Vec2 vector) {

        if (vector == null)
            return this;

        return translate( vector.getX(), vector.getY() );
    }

    /**
     * Add another vector to this one.
     *
     * @param dx The amount by which to translate the x coordinate.
     * @param dy The amount by which to translate the y coordinate.
     *
     * @return A new vector with the resulting coordinates.
     */
    public Vec2 translate(final int dx, final int dy) {

        return copyWith( getX() + dx, getY() + dy );
    }

    /**
     * Multiply this vector with the coefficients of another.
     *
     * @param vector The vector whose coefficients will be used for the multiplication.
     *
     * @return A new vector with the resulting coordinates.
     */
    public Vec2 multiply(final Vec2 vector) {

        if (vector == null)
            return this;

        return copyWith( getX() * vector.getX(), getY() * vector.y );
    }

    /**
     * Multiply this vector with a scalar number.
     *
     * @param multiplier The scalar value with which to multiply this vector.
     *
     * @return A new vector with the resulting coordinates.
     */
    public Vec2 multiply(final double multiplier) {

        return copyWith( (int) (getX() * multiplier), (int) (getY() * multiplier) );
    }

    /**
     * Inverse the direction of this vector.<br> This is basically the same as {@link #multiply(double)} with -1.
     *
     * @return A new vector with the resulting coordinates.
     */
    public Vec2 inverse() {

        return multiply( -1 );
    }

    /**
     * Multiply this vector with another vector using the cross product.<br> <br> <i>The length of the cross product of this vector with a
     * given one is the area of the parallelogram having this and the given vector as sides.</i>
     *
     * <pre>
     *    _______
     *    \      \  - Consider this parallelogram's horizontal side (either of the two) as a vector,
     *     \______\     and the vertical side as another. Their cross product returns the area.
     * </pre>
     *
     * @param vector The vector with which this vector will be multiplied.
     *
     * @return The result of the cross product of this vector with the given one.
     */
    public int crossMultiply(final Vec2 vector) {

        if (vector == null)
            return 0;

        return getX() * vector.getY() - getY() * vector.getX();
    }

    /**
     * Multiply this vector with another vector using the dot product. <i>The dot product returns the length of the projection of this
     * vector on the given one.<br> As a result of this; the dot product of two perpendicular vectors is 0.</i>
     *
     * @param vector The vector with which this vector will be multiplied.
     *
     * @return The result of the dot product of this vector with the given one.
     */
    public int dotMultiply(final Vec2 vector) {

        if (vector == null)
            return 0;

        return getX() * vector.getX() + getY() * vector.getY();
    }

    @Override
    public String toString() {

        if (wrapSize == null)
            return strf( "vec(%d, %d)", getX(), getY() );

        return strf( "vec(%d, %d / %s)", getX(), getY(), getWrapSize() );
    }

    @Override
    public int hashCode() {

        return Objects.hash( getX(), getY() );
    }

    @Override
    public boolean equals(final Object obj) {

        if (obj == this)
            return true;
        if (!(obj instanceof Vec2))
            return false;

        Vec2 o = (Vec2) obj;
        return getX() == o.getX() && getY() == o.getY();
    }

    /**
     * @param newX The x coordinate to use for the new vector.
     *
     * @return A new vector with the resulting coordinates.
     */
    public Vec2 copyWithX(final int newX) {

        return copyWith( newX, getY() );
    }

    /**
     * @param newY The y coordinate to use for the new vector.
     *
     * @return A new vector with the resulting coordinates.
     */
    public Vec2 copyWithY(final int newY) {

        return copyWith( getX(), newY );
    }

    /**
     * @param newX The x coordinate to use for the new vector.
     * @param newY The y coordinate to use for the new vector.
     *
     * @return A new vector with the resulting coordinates.
     */
    public Vec2 copyWith(int newX, int newY) {

        Size size = getWrapSize();
        if (size == null)
            return new Vec2( newX, newY, null );

        // Wrap X & Y.
        // X's wrap is offset by height / 2 to compensate for hex tiling.
        // TODO: This Vec2 could should not assume hex logic.
        int width = size.getWidth();
        int height = size.getHeight();
        while (newX < 0 || newX > width)
            newX += width - height / 2;
        while (newY < 0 || newY > height)
            newY += height;

        return new Vec2( (newX + width) % width, (newY + height) % height, size );
    }
}
