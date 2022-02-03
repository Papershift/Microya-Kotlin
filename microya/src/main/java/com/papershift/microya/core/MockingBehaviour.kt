package com.papershift.microya.core

import kotlin.time.Duration

/**
 *  The behavior when mocking is turned on.
 *  @property [delay] Mocked data should be returned after the given delay.
 *  @property [mockResponseProvider] Defines how the mocked response is retrieved from an endpoint.
 *  Defaults to just returning the endpoints `mockedResponse`.
 */
class MockingBehaviour<endpoint : Endpoint> constructor(
    val delay: Duration,
    val mockResponseProvider: (endpoint) -> MockedResponse? = { it.mockedResponse }
)
