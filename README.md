HWBOT in-house benchmarks
========================

This repository holds in house benchmarks which work with the HWBOT Generic API. Their primary use is to provide a cross platform benchmark, as to be used as a sample application for writing your own benchmark or implementing the API in an existing benchmark.

More information:
http://static.hwbot.org/api/Generic_API_1.1.pdf

Latest binary of hwbotprime:
http://downloads.hwbot.org/downloads/hwbotprime.zip

Build a release:
```mvn versions:set -DnewVersion=1.0.0 clean install -DskipTests=true```