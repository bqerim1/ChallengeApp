package com.challengeapp.data.remote

import com.challengeapp.domain.model.RefreshError
import retrofit2.HttpException
import java.io.IOException

// A bad connection or a server error may pass on the next try; a 4xx or a malformed body won't.
internal fun Exception.isRetryable(): Boolean =
    this is IOException || (this is HttpException && code() >= 500)

internal fun Exception.toRefreshError(): RefreshError =
    if (this is IOException) RefreshError.Offline else RefreshError.Unavailable
