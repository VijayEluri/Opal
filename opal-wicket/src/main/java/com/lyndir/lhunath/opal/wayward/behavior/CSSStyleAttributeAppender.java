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
package com.lyndir.lhunath.opal.wayward.behavior;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.model.*;


/**
 * <h2>{@link CSSStyleAttributeAppender}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Mar 12, 2010</i> </p>
 *
 * @author lhunath
 */
public class CSSStyleAttributeAppender extends AttributeAppender {

    private static final String STYLE_ATTRIBUTE = "style";
    private static final String STYLE_SEPARATOR = "; ";

    /**
     * @param property The CSS property that should be modified.
     * @param value    The CSS property value that should be applied.
     */
    public CSSStyleAttributeAppender(final String property, final String value) {

        // noinspection RedundantCast
        this( property, new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {

                return value;
            }
        } );
    }

    /**
     * @param property The CSS property that should be modified.
     * @param value    The model that provides the CSS property value that should be applied.
     */
    public CSSStyleAttributeAppender(final String property, final IModel<String> value) {

        super( STYLE_ATTRIBUTE, true, new LoadableDetachableModel<String>() {
            @Override
            protected String load() {

                return String.format( "%s: %s", property, value.getObject() );
            }
        }, STYLE_SEPARATOR );
    }
}
