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
package com.lyndir.lhunath.opal.security;

import static com.google.common.base.Preconditions.*;

import com.lyndir.lhunath.opal.system.util.MetaObject;
import javax.annotation.Nonnull;


/**
 * <h2>{@link AbstractSecureObject}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Mar 14, 2010</i> </p>
 *
 * @param <P> The type of the parent object.
 *
 * @author lhunath
 */
public abstract class AbstractSecureObject<S extends Subject, P extends SecureObject<S, ?>> extends MetaObject
        implements SecureObject<S, P> {

    private final S owner;
    private final ACL acl = new ACL();

    protected AbstractSecureObject() {

        owner = null;
    }

    protected AbstractSecureObject(@Nonnull final S owner) {

        this.owner = owner;
    }

    @Nonnull
    @Override
    public S getOwner() {

        if (owner == null) {
            P parent = getParent();
            if (parent != null)
                return parent.getOwner();
        }

        return checkNotNull( owner );
    }

    @Override
    public ACL getACL() {

        return acl;
    }
}
