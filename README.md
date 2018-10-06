Examples and small test cases for usage of [display4j-core](https://github.com/display4j/display4j-core)

## Getting Started 
Please checkout and install [display4j-core](https://github.com/display4j/display4j-core) first.
```bash
git clone https://github.com/display4j/display4j-core
git clone https://github.com/display4j/display4j-examples
cd display4j-core
mvn install
cd ../display4j-examples
mvn compile
```

### Quick-Start display test on RPi or your DEV-machine (using Mock)
Compile and show valid options (you have to be root to use PI4J GPIO) 
```bash
# sudo su
# mvn compile
# ./run.sh 
```
will (/should) display
```
assume classes are already build (with f.e. maven in target/classes)
usage:
        run.{sh|bat} RoutineName DisplayType [ConnectionType]

example:        run.sh dspTest SSD1327
Routine:        dspTest | exampleFromReadme | dspTestStartStop
DisplayType:    SSD1306_128_64 | SSD1327 | SSD1327AwtMock
ConnectionType: I2C | SPI | Mock - default is: I2C
```
start for example with
```bash
# ./run.sh dspTest SSD1327 I2C
```

or run example on your dev computer (no PI4J required) with the mocking implementation
```bash
# ./run.sh dspTest SSD1327AwtMock Mock
```


| Device    | Mock |
|-----------|------|
|![example of SSD1327](https://raw.githubusercontent.com/display4j/display4j-docs/master/img/IMG_20181005_195320_cut.jpg)|![example of SSD1327 Awt Mock](https://raw.githubusercontent.com/display4j/display4j-docs/master/img/ssd1327_awt_mock_128_128.png)|


### Java
Have a look at the code ... here you find the implemented versions of the README.MD 
of [display4j-core](https://github.com/display4j/display4j-core)  

### Credits
Uses Icons from 
* [Feather Icon by Cole Bemis (MIT License)](https://www.iconfinder.com/colebemis)
