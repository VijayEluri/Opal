/*
 *   Copyright 2008, Maarten Billemont
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
package com.lyndir.lhunath.opal.xml;

import java.lang.annotation.*;


/**
 * <h2>{@link XInject}<br> <sub>Mark a field that accepts XML data from an XML resource.</sub></h2>
 *
 * <p> A field with this annotation will be used during XML data injection. </p>
 *
 * <p> <i>Dec 15, 2008</i> </p>
 *
 * @author lhunath
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface XInject {

    /**
     * @return The XPath expression that evaluates to the XML data to inject into this field.
     */
    String value();

    /**
     * @return The name of the XML node's attribute to serialize this field's data into.
     */
    String attribute() default "";
}
