
# build + tests
gradle  build
gradle shadowjar

java -jar  build/libs/SaffiVertXBlackBox-3.3.3-fat.jar  -conf src/main/conf/json-blackbox.json


# Coverage by runing all tests
a. Main directory - low level tests
b. Test directory
dataevent	100% (7/7)	100% (18/18)	97% (77/79)
helper	100% (5/5)	100% (17/17)	93% (78/83)
verticles	85% (6/7)	100% (48/48)	96% (159/165)
JSonBlackBoxRestService	100% (1/1)	100% (7/7)	85% (36/42)
TestFuture	100% (1/1)	100% (4/4)	95% (21/22)