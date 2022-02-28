<p align="center">
    <img src="https://raw.githubusercontent.com/Flinesoft/Microya/main/Logo.png"
      width=396>
</p>

<p align="center">
  • <a href="#usage">Usage</a>
  • <a href="#donation">Donation</a>
  • <a href="https://github.com/Papershift/Microya-Kotlin/issues">Issues</a>
  • <a href="#contributing">Contributing</a>
  • <a href="#license">License</a>
</p>

# Microya

This is the network abstraction library for Kotlin

## Usage

### Step 1: Defining your Endpoints
Create an Api `sealed class` with all supported endpoints as `data class` with the request parameters/data specified as parameters.

For example, when writing a client for the [Microsoft Translator API](https://docs.microsoft.com/en-us/azure/cognitive-services/translator/reference/v3-0-languages):

```
sealed class PapershiftEndpoint : Endpoint() {
    object language : MicrosoftTranslatorApi()
    data class translate(val text: String, val body: ApiRequest<Request>) : MicrosoftTranslatorApi()
}
```

### Step 2: Making your Api `Endpoint` compliant



## Contributing

See the file [CONTRIBUTING.md](https://github.com/Flinesoft/Microya/blob/main/CONTRIBUTING.md).


## License
This library is released under the [MIT License](http://opensource.org/licenses/MIT). See LICENSE for details.