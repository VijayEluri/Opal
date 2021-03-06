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
package com.lyndir.lhunath.opal.gui.zui;

import com.lyndir.lhunath.opal.gui.zui.PDialog;


/**
 * TODO: PDialogClosedListener<br>
 *
 * @author lhunath
 */
public interface PDialogClosedListener {

    /**
     * Notify the listener that the given dialog was closed.
     *
     * @param dialog The dialog that was closed.
     */
    void dialogClosed(PDialog dialog);
}
