package com.github.display4j.examples;

import com.github.display4j.core.SSDisplay;
import com.github.display4j.core.misc.HexConversionHelper;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.i2c.I2CBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DisplayTestLegacy {
    private static final Logger logger = LoggerFactory.getLogger(DisplayTestLegacy.class);
    public static final int INVERSION_FLIP_SLEEP = 300;

    public static final int I2C_BUS = I2CBus.BUS_1;
    public static final int I2C_ADDRESS = 0x3C;

    // public static final Pin RST_PIN = RaspiPin.GPIO_24;
    public static final Pin RST_PIN = null;


    static SSDisplay display;

    SimpleDateFormat dfTime = new SimpleDateFormat("HH:mm:ss");
    SimpleDateFormat dfDate = new SimpleDateFormat("dd.MM.yyyy");



    int width = 0;
    int height = 0;

    public DisplayTestLegacy(SSDisplay display) {
        this.display = display;
        width = display.getWidth();
        height = display.getHeight();
    }

    public void run() throws Exception {
        setUp();
        testPatternFilling();
        testRowFilling();
        testDiagonalLines();
        testInversionFlipping();
        //shutdown();
    }

    public void setUp() throws IOException {
        logger.info(">> setUp - startup display");
        logger.info("   type: {} -connected-via-> {}", display, display.getDspConn());

        display.startup(false);

        logger.info("-- headless awt setup");

        logger.debug("<< setUp");
    }

    public void shutdown() throws IOException {
        logger.info("-- shutdown");
        display.shutdown();
        logger.info("<< shutdown");
    }

    public void testPatternFilling() throws IOException, InterruptedException {
        logger.info("testRowFilling");
        for (byte i=0; i<16; i += 2) {
            byte b = (byte) (i << 4);
            b |= i;
            logger.info("pattern: {}", HexConversionHelper.byteToHex(b));
            display.fillBufferWithPattern(b);
            display.display();

            //Thread.sleep(50);
        }
        display.clearBuffer();
    }


    public void testDiagonalLines() throws InterruptedException, IOException {
        logger.info("testDiagonalLines");
        for (int r=0; r<height && r<width; r++) {
            display.setPixel(r, r, true);
            display.setPixel(width/2-1, r, true);
            display.setPixel(width/2, r, true);
            display.setPixel(width/2+1, r, true);

            display.setPixel(r, height/2-1, true);
            display.setPixel(r, height/2, true);
            display.setPixel(r, height/2+1, true);
            //display.setPixel(display.getWidth() - r -1, r, true);
        }
        int[] bufferAsInt = new int[display.getBuffer().length];
        int i = 0;
        for (byte b : display.getBuffer()) {
            bufferAsInt[i] = b;
            i++;
        }
        display.display();
        Thread.sleep(2000);
        display.clearBuffer();
    }

    public void testRowFilling() throws InterruptedException, IOException {
        logger.info("testRowFilling");
        int step = height / 16;
        if (step < 4) step = 4;
        logger.info("draw every {} line", step);

        for (int fillRow=0; fillRow<display.getHeight(); fillRow += 16) {
            logger.info("fill col: {}", fillRow);

            for (int c=0; c<display.getWidth(); c++) {
                display.setPixel(c, fillRow, true);
            }
            display.display();
        }
        //display.clearBuffer();
    }
    public void testInversionFlipping() throws Exception{
        // flip inversion some times
        // (helping with problem of bitmask interpretation)
        boolean displayInverted = false;

        for (int i = 0; i < 4; i++) {
            logger.info("{} - inverted: {}", i, displayInverted );
            display.setInverted(displayInverted);
            displayInverted = ! displayInverted;
            Thread.sleep(INVERSION_FLIP_SLEEP);
        }

        display.setInverted(false);
    }
}
