package com.papershift.microya

import com.papershift.microya.core.JsonApiException
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import com.papershift.microya.core.EmptyBodyResponse
import com.papershift.microya.supporting.FooBar
import com.papershift.microya.supporting.PostmanEchoEndpoint
import com.papershift.microya.supporting.PostmanEchoError
import com.papershift.microya.supporting.PostmanEchoResponse
import com.papershift.microya.supporting.TestDataStore
import com.papershift.microya.supporting.mockedImmediateApiProvider
import com.papershift.microya.supporting.mockedWithCustomResponseApiProvider
import com.papershift.microya.supporting.sampleApiProvider
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.IllegalArgumentException

const val fooBarID: String = "aBcDeF012-gHiJkLMnOpQ3456-RsTuVwXyZ789"

@RunWith(AndroidJUnit4ClassRunner::class)
class MicroyaIntegrationTests {

    @Before
    fun setup() {
        TestDataStore.reset()
    }

    @Test
    fun testIndexRequest() {
        runBlocking {
            val result = sampleApiProvider.performRequest<PostmanEchoResponse, PostmanEchoError>(PostmanEchoEndpoint.Index(sortedBy = "updatedAt")).get()
            Assert.assertEquals("application/json", TestDataStore.request?.header("Content-Type"))
            Assert.assertEquals("application/json", TestDataStore.request?.header("Accept"))
            Assert.assertEquals("en", TestDataStore.request?.header("Accept-Language"))
            Assert.assertEquals("Basic abc123", TestDataStore.request?.header("Authorization"))
            Assert.assertEquals("GET", TestDataStore.request?.method)
            Assert.assertEquals("/get", TestDataStore.request?.url?.encodedPath)
            Assert.assertEquals("sortedBy=updatedAt", TestDataStore.request?.url?.query)
            Assert.assertEquals(1, TestDataStore.request?.url?.querySize)
            Assert.assertEquals("https://postman-echo.com/get?sortedBy=updatedAt", TestDataStore.request?.url.toString())
            Assert.assertNotNull(TestDataStore.response)
            Assert.assertTrue(TestDataStore.response?.isSuccessful!!)
            Assert.assertEquals("application/json", result?.headers?.get("content-type"))
            Assert.assertEquals("application/json", result?.headers?.get("accept"))
            Assert.assertEquals("en", result?.headers?.get("accept-language"))
            Assert.assertEquals(mapOf("sortedBy" to "updatedAt"), result?.args)
            Assert.assertEquals("https://postman-echo.com/get?sortedBy=updatedAt", result?.url)
        }
    }

    @Test
    fun testPostRequest() {
        runBlocking {
            val result = sampleApiProvider.performRequest<PostmanEchoResponse, PostmanEchoError>(PostmanEchoEndpoint.Post(FooBar("Lorem", "Ipsum"))).get()
            Assert.assertEquals("application/json", TestDataStore.request?.header("Content-Type"))
            Assert.assertEquals("application/json", TestDataStore.request?.header("Accept"))
            Assert.assertEquals("en", TestDataStore.request?.header("Accept-Language"))
            Assert.assertEquals("Basic abc123", TestDataStore.request?.header("Authorization"))
            Assert.assertEquals("POST", TestDataStore.request?.method)
            Assert.assertEquals("/post", TestDataStore.request?.url?.encodedPath)
            Assert.assertNull(TestDataStore.request?.url?.query)
            Assert.assertNotNull(TestDataStore.response)
            Assert.assertTrue(TestDataStore.response?.isSuccessful!!)
            Assert.assertEquals("application/json", result?.headers?.get("content-type"))
            Assert.assertEquals("application/json", result?.headers?.get("accept"))
            Assert.assertEquals("en", result?.headers?.get("accept-language"))
            Assert.assertEquals(emptyMap<String, String>(), result?.args)
            Assert.assertEquals("https://postman-echo.com/post", result?.url)
        }
    }

    @Test
    fun testGetRequest() {
        runBlocking {
            val result = sampleApiProvider.performRequest<PostmanEchoResponse, PostmanEchoError>(PostmanEchoEndpoint.Get(fooBarID)).getError()
            Assert.assertEquals("application/json", TestDataStore.request?.header("Content-Type"))
            Assert.assertEquals("application/json", TestDataStore.request?.header("Accept"))
            Assert.assertEquals("en", TestDataStore.request?.header("Accept-Language"))
            Assert.assertEquals("Basic abc123", TestDataStore.request?.header("Authorization"))
            Assert.assertNull(TestDataStore.request?.url?.query)
            Assert.assertTrue(result is JsonApiException.ClientError)
            Assert.assertEquals(404, (result as JsonApiException.ClientError).statusCode)
            Assert.assertEquals("GET", TestDataStore.request?.method)
            Assert.assertEquals("/get/${fooBarID}", TestDataStore.request?.url?.encodedPath)
        }
    }


    @Test
    fun testPatchRequest() {
        runBlocking {
            sampleApiProvider.performRequest<PostmanEchoResponse, PostmanEchoError>(PostmanEchoEndpoint.Patch(fooBarID, FooBar("Dolor", "Amet")))
            Assert.assertEquals("application/json", TestDataStore.request?.header("Content-Type"))
            Assert.assertEquals("application/json", TestDataStore.request?.header("Accept"))
            Assert.assertEquals("en", TestDataStore.request?.header("Accept-Language"))
            Assert.assertEquals("Basic abc123", TestDataStore.request?.header("Authorization"))
            Assert.assertEquals("PATCH", TestDataStore.request?.method)
            Assert.assertEquals("/patch/${fooBarID}", TestDataStore.request?.url?.encodedPath)
            Assert.assertNull(TestDataStore.request?.url?.query)
            Assert.assertNotNull(TestDataStore.response)
            Assert.assertFalse(TestDataStore.response?.isSuccessful!!)
        }
    }

    @Test
    fun testDeleteRequest() {
        runBlocking {
            sampleApiProvider.performRequest<PostmanEchoResponse, PostmanEchoError>(PostmanEchoEndpoint.Delete).get()
            Assert.assertEquals("application/json", TestDataStore.request?.header("Content-Type"))
            Assert.assertEquals("application/json", TestDataStore.request?.header("Accept"))
            Assert.assertEquals("en", TestDataStore.request?.header("Accept-Language"))
            Assert.assertEquals("Basic abc123", TestDataStore.request?.header("Authorization"))
            Assert.assertEquals("DELETE", TestDataStore.request?.method)
            Assert.assertEquals("/delete", TestDataStore.request?.url?.encodedPath)
            Assert.assertNull(TestDataStore.request?.url?.query)
            Assert.assertNotNull(TestDataStore.response)
            Assert.assertTrue(TestDataStore.response?.isSuccessful!!)
        }
    }

    @Test
    fun testMockedGetRequest() {
        runBlocking {
            val result = mockedImmediateApiProvider.performRequest<FooBar, PostmanEchoError>(PostmanEchoEndpoint.Get(fooBarID)).get()
            Assert.assertEquals("application/json", TestDataStore.request?.header("Content-Type"))
            Assert.assertEquals("application/json", TestDataStore.request?.header("Accept"))
            Assert.assertEquals("en", TestDataStore.request?.header("Accept-Language"))
            Assert.assertEquals("Basic abc123", TestDataStore.request?.header("Authorization"))
            Assert.assertEquals("foo${fooBarID}", result?.foo)
            Assert.assertEquals("bar${fooBarID}", result?.bar)
            Assert.assertEquals("GET", TestDataStore.request?.method)
            Assert.assertEquals("/get/${fooBarID}", TestDataStore.request?.url?.encodedPath)
            Assert.assertNull(TestDataStore.request?.url?.query)
        }
    }

    @Test
    fun testMockedDeleteRequest() {
        runBlocking {
            val result = mockedImmediateApiProvider.performRequest<EmptyBodyResponse, PostmanEchoError>(PostmanEchoEndpoint.Delete).get()
            Assert.assertEquals("application/json", TestDataStore.request?.header("Content-Type"))
            Assert.assertEquals("application/json", TestDataStore.request?.header("Accept"))
            Assert.assertEquals("en", TestDataStore.request?.header("Accept-Language"))
            Assert.assertEquals("Basic abc123", TestDataStore.request?.header("Authorization"))
            Assert.assertTrue(result is EmptyBodyResponse)
            Assert.assertEquals("DELETE", TestDataStore.request?.method)
            Assert.assertEquals("/delete", TestDataStore.request?.url?.encodedPath)
            Assert.assertNull(TestDataStore.request?.url?.query)
            Assert.assertNotNull(TestDataStore.response)
            Assert.assertTrue(TestDataStore.response?.isSuccessful!!)
        }
    }

    @Test
    fun testMockedWithCustomResponseGetRequest() {
        runBlocking {
            val result = mockedWithCustomResponseApiProvider.performRequest<FooBar, PostmanEchoError>(PostmanEchoEndpoint.Get(fooBarID)).get()
            Assert.assertEquals("application/json", TestDataStore.request?.header("Content-Type"))
            Assert.assertEquals("application/json", TestDataStore.request?.header("Accept"))
            Assert.assertEquals("en", TestDataStore.request?.header("Accept-Language"))
            Assert.assertEquals("Basic abc123", TestDataStore.request?.header("Authorization"))
            Assert.assertEquals("Sakata", result?.foo)
            Assert.assertEquals("Gintoki", result?.bar)
            Assert.assertEquals("GET", TestDataStore.request?.method)
            Assert.assertEquals("/get/${fooBarID}", TestDataStore.request?.url?.encodedPath)
            Assert.assertNull(TestDataStore.request?.url?.query)
        }
    }

    @Test
    fun testMockedWithCustomResponsePostRequest() {
        runBlocking {
            val result = mockedWithCustomResponseApiProvider.performRequest<FooBar, PostmanEchoError>(PostmanEchoEndpoint.Post(FooBar("Hinata", "Shoyo"))).getError()
            Assert.assertTrue(result is JsonApiException.EmptyMockedResponse)
        }
    }


    @Test(expected = IllegalArgumentException::class)
    fun testMockedWithCustomResponseDeleteRequest() {
        runBlocking {
            mockedWithCustomResponseApiProvider.performRequest<FooBar, PostmanEchoError>(PostmanEchoEndpoint.Delete).getError()
        }
    }
}