
# build + tests
gradle  build
gradle shadowjar

java -jar  build/libs/SaffiVertXBlackBox-3.3.3-fat.jar  -conf src/main/conf/json-blackbox.json


# Coverage by runing all tests
a. Main directory - low level tests
b. Test directory
dataevent	100% (7/7)	100% (18/18)	97% (77/79)
helper	100% (5/5)	100% (18/18)	93% (82/88)
verticles	85% (6/7)	100% (48/48)	94% (162/171)
JSonBlackBoxRestService	100% (1/1)	87% (7/8)	78% (37/47)
TestFuture	100% (1/1)	100% (4/4)	95% (21/22)