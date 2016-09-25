
# build + tests
gradle  build
gradle shadowjar

java -jar  build/libs/SaffiVertXBlackBox-3.3.3-fat.jar  -conf src/main/conf/json-blackbox.json


# Coverage
There are low level Unit Test in the source directory required for the full coverage.
 dataevent	100% (7/7)	100% (18/18)	98% (78/79)
 helper	100% (4/4)	100% (14/14)	97% (66/68)
 verticles	85% (6/7)	100% (51/51)	96% (162/168)
 JSonBlackBoxRestService	100% (1/1)	100% (9/9)	86% (46/53)
 TestFuture	100% (1/1)	100% (4/4)	95% (21/22)