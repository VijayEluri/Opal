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
package com.lyndir.lhunath.lib.system;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.lyndir.lhunath.lib.system.logging.Logger;

/**
 * <i>BaseConfig - A configuration backend system with built-in persistence.</i><br>
 * <br>
 * You should extend this class and create configurable entries as demonstrated by the implementation of
 * {@link #configFile}.<br>
 * <br>
 * NOTE: Any subclass needs to follow the directions outlined in {@link #initClass(Class)}!<br>
 * <br>
 * 
 * @author lhunath
 * @param <T>
 *        The type of this entry's value.
 */
public class BaseConfig<T extends Serializable> implements Serializable {

    /**
     * Version of the class. Augment this whenever the class type of a config entry field changes, or the context of a
     * field becomes incompatible.
     */
    public static final long                    serialVersionUID = 210L;

    /**
     * The size of the file/web read buffer.
     */
    public static final int                     BUFFER_SIZE      = 1024;

    /**
     * The file in which to save the config settings.
     */
    public static BaseConfig<File>              configFile       = create( File.class );

    protected static Set<Runnable>              shutdownHooks    = new HashSet<Runnable>();
    protected static Map<BaseConfig<?>, String> names            = new HashMap<BaseConfig<?>, String>();
    protected static Map<String, String>        types            = new HashMap<String, String>();

    static {
        /* CALL THIS METHOD IN EVERY SUBCLASS! */
        initClass( BaseConfig.class );

        /* Make a shutdown hook to save the config on exit. */
        Runtime.getRuntime().addShutdownHook( new Thread( "Config ShutdownHook" ) {

            @Override
            public void run() {

                Logger.finest( "stat.saveConfig", configFile.get() );
                try {
                    for (Runnable hook : shutdownHooks)
                        hook.run();

                    if (configFile.isEmpty()) {
                        Logger.warn( "Config file unset, can't save!" );
                        return;
                    }

                    if (configFile.get().exists())
                        configFile.get().delete();
                    configFile.get().createNewFile();

                    FileOutputStream out = new FileOutputStream( configFile.get() );
                    ObjectOutputStream objects = new ObjectOutputStream( out );
                    objects.writeObject( names );
                    objects.writeObject( types );
                    objects.close();
                } catch (UnsupportedEncodingException e) {
                    Logger.error( e, "Charset %s is unsupported!", Utils.getCharset().name() );
                } catch (FileNotFoundException e) {
                    Logger.error( e, "Could not find the config file '%s'!", configFile.get() );
                } catch (IOException e) {
                    Logger.error( e, "Could not create/write to the config file '%s'!", configFile.get() );
                } finally {
                    Logger.finest( null );
                }
            }
        } );
    }

    /**
     * Create a new {@link BaseConfig} that defaults to being unset.
     * 
     * @param type
     *        The type of value for this {@link BaseConfig}.
     * @param <T>
     *        See type.
     * @return The {@link BaseConfig} object for this entry.
     */
    public static <T extends Serializable> BaseConfig<T> create(Class<T> type) {

        return new BaseConfig<T>( null );
    }

    /**
     * Create a new {@link BaseConfig} with the given default value.
     * 
     * @param defaultValue
     *        The value that will be used if no other value is defined.
     * @param <T>
     *        The type of value for this {@link BaseConfig}.
     * @return The {@link BaseConfig} object for this entry.
     */
    public static <T extends Serializable> BaseConfig<T> create(T defaultValue) {

        return new BaseConfig<T>( defaultValue );
    }

    /**
     * Create a new {@link BaseConfig} with the given default value that will be parsed into a URL.
     * 
     * @param defaultValue
     *        The value that will be used if no other value is defined.
     * @return The {@link BaseConfig} object for this entry.
     */
    public static BaseConfig<URL> createUrl(String defaultValue) {

        return create( Utils.url( defaultValue ) );
    }

    /**
     * Dump out all known settings for debug purposes to standard output for the given config class.
     */
    public static void dump() {

        for (BaseConfig<?> entry : names.keySet())
            System.out.println( entry.toString() );
    }

    /**
     * Create a new {@link BaseConfig} that defaults to being unset.
     * 
     * @param <T>
     *        The type of value for this {@link BaseConfig}.
     * @return The {@link BaseConfig} object for this entry.
     */
    public static <T extends Serializable> BaseConfig<T> empty() {

        return create( null );
    }

    /**
     * Prepare this config (sub)class for use. This will cause all config fields to be exported into the settings object
     * that will be used for serialization of the settings.<br>
     * <br>
     * <b>YOU MUST CALL THIS FUNCTION IN A STATIC BLOCK WHENEVER YOU SUBCLASS THIS CLASS!</b>
     * 
     * @param configClass
     *        The class that is being initialized. This is the Class object of the {@link BaseConfig} subclass.
     */
    @SuppressWarnings("unchecked")
    public static void initClass(Class<? extends BaseConfig> configClass) {

        flushConfig( configClass );
        loadConfig();
    }

    /**
     * Change the location of the config file and reload the config from the new location.
     * 
     * @param config
     *        The new config file.
     */
    public static void setConfig(File config) {

        configFile.set( config );
        loadConfig();
    }

    /**
     * Read in all static fields of the given class and if they're {@link BaseConfig} fields, add them to the settings
     * list so that they will be serialized.
     * 
     * @param configClass
     *        The name of the class whose static {@link BaseConfig} fields should be flushed into the settings list.
     */
    @SuppressWarnings("unchecked")
    private static void flushConfig(Class<? extends BaseConfig> configClass) {

        for (Field field : configClass.getFields())
            try {
                if (field.get( null ) instanceof BaseConfig) {
                    BaseConfig<?> config = (BaseConfig<?>) field.get( null );
                    ParameterizedType type = (ParameterizedType) field.getGenericType();

                    config.hashCode = config.getName( configClass ).hashCode();
                    names.put( config, config.getName( configClass ) );
                    types.put( config.getName( configClass ), type.getActualTypeArguments()[0].toString() );
                }
            } catch (IllegalArgumentException e) {
                continue;
            } catch (IllegalAccessException e) {
                continue;
            }
    }

    /**
     * Load config settings from the config file and change every existing setting with the same field name to reflect
     * its value from the config file.
     */
    @SuppressWarnings("unchecked")
    private static void loadConfig() {

        try {
            /* Check if the config file exists before trying to read it. */
            if (configFile.isEmpty() || !configFile.get().isFile())
                return;

            /* Read in the config file to a new settings object. */
            InputStream stream = new BufferedInputStream( new FileInputStream( configFile.get() ) );
            ObjectInputStream objects = new ObjectInputStream( stream );
            Map<BaseConfig, String> configNames;
            Map<String, String> configTypes;

            configNames = names.getClass().cast( objects.readObject() );
            configTypes = types.getClass().cast( objects.readObject() );
            objects.close();

            /* Apply config file and check its settings for any keys not defined by the application. */
            for (BaseConfig fileEntry : configNames.keySet())
                for (BaseConfig currEntry : names.keySet()) {

                    /* Don't load the config file entry.. */
                    if (currEntry.equals( configFile ))
                        continue;

                    /* Persistent entry info. */
                    String configName = configNames.get( fileEntry );
                    String configType = configTypes.get( configName );

                    /* If field names match .. */
                    if (currEntry.getName().equals( configName )) {
                        try {
                            /* Abort if types don't match. */
                            if (!currEntry.getType().equals( configType ))
                                throw new ClassCastException( "Generic types don't match." );

                            currEntry.set( fileEntry.get() );
                        }

                        /* Value type does not match. */
                        catch (ClassCastException e) {
                            Logger.warn( "Couldn't load value for %s, " + "its config value is longer compatible.",
                                    currEntry.getName() );
                        }
                        break;
                    }
                }
        }

        /* Names and/or Types Map has become incompatible. */
        catch (InvalidClassException e) {
            Logger.warn( "Config file is incompatible, reverting to defaults." );
            revert();
        } catch (ClassCastException e) {
            Logger.warn( "Config file is incompatible, reverting to defaults." );
            revert();
        } catch (ClassNotFoundException e) {
            Logger.error( e, "Object in config file not supported, reverting to defaults." );
            revert();
        }

        /* I/O errors. */
        catch (FileNotFoundException e) {
            Logger.error( e, "Config file '%s' not found!", configFile.get() );
        } catch (IOException e) {
            Logger.error( e, "Couldn't read the config file '%s'!", configFile.get() );
        }
    }

    private static void revert() {

        names = new HashMap<BaseConfig<?>, String>();
        types = new HashMap<String, String>();
    }

    public static void addShutdownHook(Runnable hook) {

        shutdownHooks.add( hook );
    }

    private T                                       value;
    private int                                     hashCode;
    private transient Set<ConfigChangedListener<T>> listeners;

    /**
     * Create a new {@link BaseConfig} instance.
     * 
     * @param defaultValue
     *        The default value for this entry.
     */
    protected BaseConfig(T defaultValue) {

        value = defaultValue;
        listeners = new HashSet<ConfigChangedListener<T>>();
    }

    /**
     * Get the configured value for this setting.
     * 
     * @return Guess.
     */
    public T get() {

        return value;
    }

    /**
     * Get the name of this {@link BaseConfig}.
     * 
     * @return The field name of this {@link BaseConfig}.
     */
    public String getName() {

        if (names.containsKey( this ))
            return names.get( this );

        // Logger.warn( "This config entry has not (yet) been flushed, trying a fall back to find
        // its name." );
        return getName( getClass() );
    }

    /**
     * Get the name of this {@link BaseConfig}.
     * 
     * @return The field name of this {@link BaseConfig}.
     */
    public String getType() {

        if (types.containsKey( getName() ))
            return types.get( getName() );

        Logger.warn( "This config entry has not (yet) been flushed, returning its superclass." );

        if (value == null)
            return "Object";
        return value.getClass().toString();
    }

    /**
     * Check whether this {@link BaseConfig} is unset (<code>null</code> or empty {@link #toString()} or
     * {@link Collection#isEmpty()}).
     * 
     * @return Guess.
     */
    public boolean isEmpty() {

        if (value == null)
            return true;

        if (value instanceof Collection)
            return ((Collection<?>) value).isEmpty();

        return value.toString().length() == 0;
    }

    /**
     * Check whether there is a value defined for this {@link BaseConfig}.
     * 
     * @return Guess.
     */
    public boolean isSet() {

        return !isEmpty();
    }

    /**
     * Change the configured value for this setting.
     * 
     * @param newValue
     *        The setting's new value.
     * @return <code>true</code> in case the original value was not the same as the new value.
     * @throws ClassCastException
     *         If the object is not null and is not assignable to the setting's type.
     */
    public boolean set(T newValue) throws ClassCastException {

        if (value == null && newValue == null)
            return false;
        if (value != null && newValue != null && value.equals( newValue ))
            return false;

        value = newValue;

        for (ConfigChangedListener<T> listener : listeners)
            listener.configValueChanged( this, value, newValue );

        return true;
    }

    /**
     * Forcefully change the configured value for this setting, even if the current setting is already equal to the new
     * value.<br>
     * You should use this if you need the object in the config file to be a reference to the new value.
     * 
     * @param newValue
     *        The setting's new value.
     * @return true if the value was changed.
     */
    public boolean force(T newValue) {

        value = null;
        return set( newValue );
    }

    /**
     * Register an event handler that will be called when this value changes.
     */
    public void register(ConfigChangedListener<T> listener) {

        listeners.add( listener );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {

        return String.format( "  %-20s %15s  =  %s", getType().replaceAll( "[^ <]* |\\w+\\.", "" ), getName(), value );
    }

    /**
     * Unset this {@link BaseConfig}, setting its value to <code>null</code>.
     */
    public void unset() {

        value = null;
    }

    @SuppressWarnings("unchecked")
    private String getName(Class<? extends BaseConfig> configClass) {

        for (Field field : configClass.getFields())
            try {
                if (field.get( null ) != null && field.get( null ) == this)
                    return field.getName();
            } catch (IllegalArgumentException e) {
                continue;
            } catch (IllegalAccessException e) {
                continue;
            }

        Logger.warn( "Could not find the name of config entry with value: %s", get() );
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {

        if (hashCode == 0) {
            Logger.warn( "Could not find the hash code of config entry with value: %s", get() );
            return super.hashCode();
        }

        return hashCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {

        if (obj == this)
            return true;
        if (!(obj instanceof BaseConfig))
            return false;

        return obj.hashCode() == hashCode();
    }
}
