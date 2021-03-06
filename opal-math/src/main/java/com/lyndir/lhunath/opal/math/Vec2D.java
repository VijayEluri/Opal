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
 * <i>Vec2D - A two dimensional vector.</i><br> <br> <p> TODO: Optimize by caching calculations (much like Angle)? </p> <br> The Vec2 object
 * represents a two dimensional vector in a plane.<br> <br>
 *
 * @author lhunath
 */
public class Vec2D implements Serializable {

    private static final long serialVersionUID = 0;

    /**
     * X-Axis coordinate.
     */
    private final double x;

    /**
     * Y-Axis coordinate.
     */
    private final double y;

    /**
     * Create a new two dimensional vector at the origin.
     */
    public Vec2D() {

        this( 0, 0 );
    }

    /**
    * Create a new two dimensional vector.
    *
    * @param x The x-coordinate of the new vector.
    * @param y The y-coordinate of the new vector.
    */
    public Vec2D(final double x, final double y) {

        this.x = x;
        this.y = y;
    }

    /**
     * @return The horizontal destination of this vector.
     */
    public double getX() {

        return x;
    }

    /**
     * @return The vertical destination of this vector.
     */
    public double getY() {

        return y;
    }

    public double getDX(final Vec2D other) {
        return other.getX() - getX();
    }

    public double getDY(final Vec2D other) {
        return other.getY() - getY();
    }

    public double distanceTo(final Vec2D other) {
        double dx = getDX( other ), dy = getDY( other );

        return (Math.abs( dx ) + Math.abs( dy ) + Math.abs( dx + dy )) / 2;
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
    public double lengthSq() {

        return getX() * getX() + getY() * getY();
    }

    /**
     * Normalize this vector.
     *
     * @return The normalized version of this vector; pointing in the same direction with length 1.
     */
    public Vec2D normalize() {

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
    public Vec2D rotate(final Angle a) {

        if (a == null)
            return this;

        return copyWith( x * a.sin() + y * a.cos(), getX() * a.cos() - getY() * a.sin() );
    }

    /**
     * Add another vector to this one.
     *
     * @param vector The vector which will be added to this.
     *
     * @return A new vector with the resulting coordinates.
     */
    public Vec2D translate(final Vec2D vector) {

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
    public Vec2D translate(final double dx, final double dy) {

        return copyWith( getX() + dx, getY() + dy );
    }

    /**
     * Multiply this vector with the coefficients of another.
     *
     * @param vector The vector whose coefficients will be used for the multiplication.
     *
     * @return A new vector with the resulting coordinates.
     */
    public Vec2D multiply(final Vec2D vector) {

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
    public Vec2D multiply(final double multiplier) {

        return copyWith( getX() * multiplier, getY() * multiplier );
    }

    /**
     * Inverse the direction of this vector.<br> This is basically the same as {@link #multiply(double)} with -1.
     *
     * @return A new vector with the resulting coordinates.
     */
    public Vec2D inverse() {

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
    public double crossMultiply(final Vec2D vector) {

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
    public double dotMultiply(final Vec2D vector) {

        if (vector == null)
            return 0;

        return getX() * vector.getX() + getY() * vector.getY();
    }

    @Override
    public String toString() {

        return strf( "vec(%.2f, %.2f)", getX(), getY() );
    }

    @Override
    public int hashCode() {

        return Objects.hash( getX(), getY() );
    }

    @Override
    public boolean equals(final Object obj) {

        if (obj == this)
            return true;
        if (!(obj instanceof Vec2D))
            return false;

        Vec2D o = (Vec2D) obj;
        return getX() == o.getX() && getY() == o.getY();
    }

    /**
     * @param newX The x coordinate to use for the new vector.
     *
     * @return A new vector with the resulting coordinates.
     */
    public Vec2D copyWithX(final double newX) {

        return copyWith( newX, getY() );
    }

    /**
     * @param newY The y coordinate to use for the new vector.
     *
     * @return A new vector with the resulting coordinates.
     */
    public Vec2D copyWithY(final double newY) {

        return copyWith( getX(), newY );
    }

    /**
     * @param newX The x coordinate to use for the new vector.
     * @param newY The y coordinate to use for the new vector.
     *
     * @return A new vector with the resulting coordinates.
     */
    public Vec2D copyWith(final double newX, final double newY) {

        return new Vec2D( newX, newY );
    }
}
