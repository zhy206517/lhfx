/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.youa.mobile.common.http;

import android.webkit.SslErrorHandler;
import android.webkit.WebView;

public class LhWebViewClient extends android.webkit.WebViewClient{

    /**
     * Notify the host application to handle a ssl certificate error request
     * (display the error to the user and ask whether to proceed or not). The
     * host application has to call either handler.cancel() or handler.proceed()
     * as the connection is suspended and waiting for the response. The default
     * behavior is to cancel the load.
     * 
     * @param view The WebView that is initiating the callback.
     * @param handler An SslErrorHandler object that will handle the user's
     *            response.
     * @param error The SSL error object.
     */
    public void onReceivedSslError(WebView view, SslErrorHandler handler,
            SslError error) {
//        handler.cancel();
        handler.proceed();// 接受证书
    }

}
