/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.reference.browser

import android.content.Context
import mozilla.components.browser.errorpages.ErrorPages
import mozilla.components.browser.errorpages.ErrorType
import mozilla.components.concept.engine.EngineSession
import mozilla.components.concept.engine.request.RequestInterceptor
import org.mozilla.reference.browser.ext.components
import org.mozilla.reference.browser.tabs.PrivatePage

/**
 * NB, and FIXME: this class is consumed by a 'Core' component group, but itself relies on 'firefoxAccountsFeature'
 * component; this creates a circular dependency, since firefoxAccountsFeature relies on tabsUseCases
 * which in turn needs 'core' itself.
 */
class AppRequestInterceptor(private val context: Context) : RequestInterceptor {
    override fun onLoadRequest(session: EngineSession, uri: String): RequestInterceptor.InterceptionResponse? {
        return when (uri) {
            "about:privatebrowsing" -> {
                val page = PrivatePage.createPrivateBrowsingPage(context, uri)
                return RequestInterceptor.InterceptionResponse.Content(page, encoding = "base64")
            }

            else -> context.components.services.accountsAuthFeature.interceptor.onLoadRequest(session, uri)
        }
    }

    override fun onErrorRequest(
        session: EngineSession,
        errorType: ErrorType,
        uri: String?
    ): RequestInterceptor.ErrorResponse? {
        return RequestInterceptor.ErrorResponse(ErrorPages.createErrorPage(context, errorType))
    }
}
