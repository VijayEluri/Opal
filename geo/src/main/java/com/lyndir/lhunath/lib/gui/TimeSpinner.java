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
package com.lyndir.lhunath.lib.gui;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.swing.AbstractSpinnerModel;
import javax.swing.JSpinner;

import com.lyndir.lhunath.lib.system.Utils;
import com.lyndir.lhunath.lib.system.logging.Logger;


/**
 * <i>{@link TimeSpinner} - [in short] (TODO).</i><br>
 * <br>
 * [description / usage].<br>
 * <br>
 * 
 * @author lhunath
 */
public class TimeSpinner extends JSpinner {

    static final Logger      logger = Logger.get( TimeSpinner.class );

    private TimeSpinnerModel model;
    private int              jumpField;


    /**
     * Create a new {@link TimeSpinner} instance.
     */
    public TimeSpinner() {

        this( null, null, 0 );
    }

    /**
     * Create a new {@link TimeSpinner} instance.
     * 
     * @param stepField
     *            The calendar field that designates the size of the spinner's arrows' steps.
     * @param jumpField
     *            The calendar field that defines the format to use (all fields up to but not including the jumpField
     *            will be included in the spinner's format).
     */
    public TimeSpinner(int stepField, int jumpField) {

        Calendar cal = Calendar.getInstance( TimeZone.getTimeZone( "GMT" ) );
        cal.setTimeInMillis( 0 );
        cal.set( stepField, cal.getMaximum( stepField ) );
        long step = cal.getTimeInMillis();

        setModel( model = new TimeSpinnerModel( null, null, step ) );
        setEditor( new TimeSpinnerEditor( this ) );

        setJumpField( jumpField );
    }

    /**
     * Create a new {@link TimeSpinner} instance.
     * 
     * @param format
     *            The format that will be used to display the time. See {@link DateFormat}. You may use
     *            <code>null</code> for a default format (HH:mm).
     * @param initial
     *            The initial value of the spinner in the specified format. You may use <code>null</code> to designate
     *            the epoch timestamp 0.
     * @param step
     *            The jump in milliseconds that the spinner's next and previous actions will perform on the current
     *            value. You may use 0 for a default jump value of one minute.
     */
    public TimeSpinner(String format, String initial, long step) {

        setModel( model = new TimeSpinnerModel( format, initial, step ) );
        getEditor().setEnabled( true );
    }

    /**
     * Specify the calendar field that defines the format to use (all fields up to but not including the jump field will
     * be included in the spinner's format).
     * 
     * @param jumpField
     *            A {@link Calendar} field.
     */
    public void setJumpField(int jumpField) {

        this.jumpField = jumpField;
        StringBuffer format = new StringBuffer();
        for (int field : Utils.calendarFields) {
            if (field == jumpField)
                break;

            format.insert( 0, Utils.calendarFormat.get( field ) );
        }

        model.setFormat( format.toString() );
    }

    /**
     * @return The jumpField of this {@link TimeSpinner}.
     */
    public int getJumpField() {

        return jumpField;
    }

    /**
     * Return the amount of milliseconds represented in this spinner's current value.
     * 
     * @return Amount of milliseconds represented by the spinner.
     */
    public long getTime() {

        try {
            String value = getValue().toString();
            return model.getFormat().parse( value ).getTime();
        } catch (ParseException e) {
            logger.err( e, "You entered an invalid time specification!" );
        }

        return 0;
    }

    /**
     * Return the amount of seconds represented in this spinner's current value.
     * 
     * @return Amount of seconds represented by the spinner.
     */
    public long getSeconds() {

        return getTime() / 1000;
    }

    /**
     * Return the amount of minutes represented in this spinner's current value.
     * 
     * @return Amount of minutes represented by the spinner.
     */
    public long getMinutes() {

        return getSeconds() / 60;
    }

    /**
     * Return the amount of hours represented in this spinner's current value.
     * 
     * @return Amount of hours represented by the spinner.
     */
    public long getHours() {

        return getMinutes() / 60;
    }

    /**
     * Return the amount of days represented in this spinner's current value.
     * 
     * @return Amount of days represented by the spinner.
     */
    public long getDays() {

        return getHours() / 24;
    }


    class TimeSpinnerModel extends AbstractSpinnerModel {

        private Date             value;
        private SimpleDateFormat format;
        private long             step;


        /**
         * Create a new {@link TimeSpinnerModel} instance.
         */
        public TimeSpinnerModel() {

            this( null, null, 0 );
        }

        /**
         * Create a new {@link TimeSpinnerModel} instance.
         * 
         * @param timeFormat
         *            The format that will be used to display the time. See {@link DateFormat}. You may use
         *            <code>null</code> for a default format (HH:mm).
         * @param initialValue
         *            The initial value of the spinner in the specified format. You may use <code>null</code> to
         *            designate the epoch timestamp 0.
         * @param step
         *            The jump in milliseconds that the spinner's next and previous actions will perform on the current
         *            value. You may use 0 for a default jump value of one minute.
         */
        public TimeSpinnerModel(String timeFormat, String initialValue, long step) {

            if (step == 0)
                step = 60000;

            setFormat( timeFormat );
            setValue( initialValue );
            this.step = step;
        }

        /**
         * Retrieve the format of this {@link TimeSpinner.TimeSpinnerModel}.
         * 
         * @return Guess.
         */
        public SimpleDateFormat getFormat() {

            return format;
        }

        /**
         * Set the format of this {@link TimeSpinner.TimeSpinnerModel}.
         * 
         * @param formatPattern
         *            The pattern of the new format to use for this spinner.
         */
        public void setFormat(String formatPattern) {

            if (formatPattern == null)
                formatPattern = "HH:mm";
            if (formatPattern.length() == 0)
                formatPattern = "'0'";

            if (format != null)
                format.applyPattern( formatPattern );
            else {
                format = new SimpleDateFormat( formatPattern );
                format.setTimeZone( TimeZone.getTimeZone( "GMT" ) );
            }

            if (value != null)
                setValue( getValue() );
        }

        /**
         * {@inheritDoc}
         */
        public Object getNextValue() {

            return format.format( new Date( value.getTime() + step ) );
        }

        /**
         * {@inheritDoc}
         */
        public Object getPreviousValue() {

            return format.format( new Date( value.getTime() - step ) );
        }

        /**
         * {@inheritDoc}
         */
        public Object getValue() {

            return format.format( value );
        }

        /**
         * {@inheritDoc}
         */
        public void setValue(Object value) {

            if (value == null)
                this.value = new Date( 0 );

            else
                try {
                    Integer time = Utils.parseInt( value.toString() );
                    if (time != null)
                        this.value = new Date( time );
                    else
                        this.value = format.parse( value.toString() );
                } catch (ParseException e) {
                    logger.err( e, "%s is not a valid time specification!", value );
                }

            fireStateChanged();
        }
    }


    private class TimeSpinnerEditor extends JSpinner.DefaultEditor {

        /**
         * Create a new {@link TimeSpinnerEditor} instance.
         * 
         * @param spinner
         *            The spinner that contains this editor.
         */
        public TimeSpinnerEditor(JSpinner spinner) {

            super( spinner );
            getTextField().setEditable( true );
        }
    }
}
