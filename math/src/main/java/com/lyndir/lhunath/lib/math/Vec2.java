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
package com.lyndir.lhunath.lib.math;

import java.awt.Dimension;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;


/**
 * <i>Vec2 - A two dimensional vector.</i><br>
 * <br>
 * <p>
 * TODO: Optimize by caching calculations (much like Angle)?
 * </p>
 * <br>
 * The Vec2 object represents a two dimensional vector in a plane.<br>
 * <br>
 * 
 * @author lhunath
 */
public class Vec2 implements Cloneable {

    /**
     * X-Axis coordinate.
     */
    public double x;

    /**
     * Y-Axis coordinate.
     */
    public double y;


    /**
     * Create a new two dimensional vector in the origin.
     */
    public Vec2() {

        this( 0, 0 );
    }

    /**
     * Create a new two dimensional vector.
     * 
     * @param x
     *            The x-coordinate of the new vector.
     * @param y
     *            The y-coordinate of the new vector.
     */
    public Vec2(double x, double y) {

        this.x = x;
        this.y = y;
    }

    /**
     * Create a new two dimensional vector.
     * 
     * @param p
     *            A 2D point that describes the endpoint of the vector.
     */
    public Vec2(Point2D p) {

        x = p.getX();
        y = p.getY();
    }

    /**
     * Create a new two dimensional vector.
     * 
     * @param d
     *            A 2D dimension that describes the endpoint of the vector.
     */
    public Vec2(Dimension2D d) {

        x = d.getWidth();
        y = d.getHeight();
    }

    /**
     * Create a point that represents this vector.
     * 
     * @return A new point at the target of the vector.
     */
    public Point2D toPoint() {

        return new Point2D.Double( x, y );
    }

    /**
     * Create a point that represents this vector.
     * 
     * @return A new point at the target of the vector.
     */
    public Dimension toDimension() {

        Dimension dim = new Dimension();
        dim.setSize( x, y );

        return dim;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Vec2 clone() {

        return new Vec2( x, y );
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
     * Calculate the squared length of the vector.<br>
     * <br>
     * It is advised to use this function in favor of {@link #length()} due to performance.
     * 
     * @return The squared length of this vector.
     */
    public double lengthSq() {

        return x * x + y * y;
    }

    /**
     * Normalize this vector; returning it's original length.
     * 
     * @return The length of the vector; before it had been normalized.
     */
    public double normalize() {

        double l = length();
        multiply( l );

        return l;
    }

    /**
     * Rotate this vector over an angle.
     * 
     * @param a
     *            The angle over which to rotate.
     * @return A reference to the this vector, after it has been updated.
     */
    public Vec2 rotate(Angle a) {

        if (a == null)
            return this;

        double newX = x * a.cos() - y * a.sin();
        y = x * a.sin() + y * a.cos();
        x = newX;

        return this;
    }

    /**
     * Add another vector to this one.
     * 
     * @param vector
     *            The vector which will be added to this.
     * @return A reference to the this vector, after it has been updated.
     */
    public Vec2 add(Vec2 vector) {

        if (vector == null)
            return this;

        x += vector.x;
        y += vector.y;

        return this;
    }

    /**
     * Subtract another vector from this one.
     * 
     * @param vector
     *            The vector which will be subtracted from this.
     * @return A reference to the this vector, after it has been updated.
     */
    public Vec2 substract(Vec2 vector) {

        if (vector == null)
            return this;

        x -= vector.x;
        y -= vector.y;

        return this;
    }

    /**
     * Multiply this vector with the coefficients of another.
     * 
     * @param vector
     *            The vector whose coefficients will be used for the multiplication.
     * @return A reference to the this vector, after it has been updated.
     */
    public Vec2 multiply(Vec2 vector) {

        if (vector == null)
            return this;

        x *= vector.x;
        y *= vector.y;

        return this;
    }

    /**
     * Multiply this vector with a scalar number.
     * 
     * @param c
     *            The scalar value with which to multiply this vector.
     * @return A reference to the this vector, after it has been updated.
     */
    public Vec2 multiply(double c) {

        x *= c;
        y *= c;

        return this;
    }

    /**
     * Inverse the direction of this vector.<br>
     * This is basically the same as {@link #multiply(double)} with -1.
     * 
     * @return A reference to the this vector, after it has been updated.
     */
    public Vec2 inverse() {

        return multiply( -1 );
    }

    /**
     * Multiply this vector with another vector using the cross product.<br>
     * <br>
     * <i>The length of the cross product of this vector with a given one is the area of the parallelogram having this
     * and the given vector as sides.</i>
     * 
     * <pre>
     *    _______
     *    \      \  - Consider this paralellogram's horizontal side (either of the two) as a vector,
     *     \______\     and the vertical side as another. Their cross product returns the area.
     * </pre>
     * 
     * @param vector
     *            The vector with which this vector will be multiplied.
     * @return The result of the cross product of this vector with the given one.
     */
    public double crossMultiply(Vec2 vector) {

        if (vector == null)
            return 0;

        return x * vector.y - y * vector.x;
    }

    /**
     * Multiply this vector with another vector using the dot product. <i>The dot product returns the length of the
     * projection of this vector on the given one.<br>
     * As a result of this; the dot product of two perpendicular vectors is 0.</i>
     * 
     * @param vector
     *            The vector with which this vector will be multiplied.
     * @return The result of the dot product of this vector with the given one.
     */
    public double dotMultiply(Vec2 vector) {

        if (vector == null)
            return 0;

        return x * vector.x + y * vector.y;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {

        return "vec(" + x + ", " + y + ")";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {

        return toString().hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {

        if (o == this)
            return true;
        if (!(o instanceof Vec2))
            return false;

        return (x == ((Vec2) o).x && y == ((Vec2) o).y);
    }
}
