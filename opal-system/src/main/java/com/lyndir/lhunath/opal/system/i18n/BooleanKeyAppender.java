/*
 *   Copyright 2010, Maarten Billemont
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
package com.lyndir.lhunath.opal.system.i18n;

import java.lang.annotation.*;


/**
 * <h2>{@link BooleanKeyAppender}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Mar 31, 2010</i> </p>
 *
 * @author lhunath
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface BooleanKeyAppender {

    /**
     * @return The key chunk to append to the localization key when the value of the annotated parameter is {@code true} or don't
     *         append anything if unspecified or the empty string.
     */
    String y() default "";

    /**
     * @return The key chunk to append to the localization key when the value of the annotated parameter is {@code false} or don't
     *         append anything if unspecified or the empty string.
     */
    String n() default "";
}
