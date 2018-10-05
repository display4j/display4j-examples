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

public class DisplayTest {
    private static final Logger logger = LoggerFactory.getLogger(DisplayTest.class);
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

    public DisplayTest(SSDisplay display) {
        this.display = display;
        width = display.getWidth();
        height = display.getHeight();
    }

    public void run() throws Exception {
        setUp();
        testPatternFilling();
        testRowFilling();
        testDiagonalLines();
        testDrawing();
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

    public void testDrawing() throws IOException, InterruptedException {
        logger.debug(">> testDrawing");

        Graphics2D graphics = display.getGraphics2D();

        graphics.setColor(Color.darkGray);
        // rect around display area
        graphics.drawRect(0,0,width-1, height-1);

        Font font = new Font(Font.MONOSPACED, Font.BOLD, 25);
        Font fontMedium = new Font(Font.DIALOG, Font.BOLD, 16);
        Font fontSmall = new Font(Font.SANS_SERIF, Font.PLAIN, 12);

        graphics.setFont(fontSmall);
        graphics.setColor(Color.white);
        graphics.drawString(display.getClass().getSimpleName(), 5, 12);

        // line above time
        graphics.drawLine(1, display.getHeight() - 25, display.getWidth() - 2, display.getHeight() - 25);

        drawTime(graphics, font);


        Polygon check = new Polygon();
        int ox = width - 25, oy = 5;
        check.addPoint(ox + 0, oy + 9);
        check.addPoint(ox + 8, oy + 17);
        check.addPoint(ox + 22, oy + 3);
        check.addPoint(ox + 19, oy + 0);
        check.addPoint(ox + 8, oy + 11);
        check.addPoint(ox + 3, oy + 6);
        graphics.setColor(Color.white);

        graphics.fillPolygon(check);
/*
        ox = width - 30; oy = 0;
        Polygon cross = new Polygon();
        cross.addPoint(ox + 4, oy + 8);
        cross.addPoint(ox + 12, oy + 16);
        cross.addPoint(ox + 4, oy + 24);
        cross.addPoint(ox + 9, oy + 29);
        cross.addPoint(ox + 17, oy + 21);
        cross.addPoint(ox + 25, oy + 29);
        cross.addPoint(ox + 29, oy + 25);
        cross.addPoint(ox + 21, oy + 17);
        cross.addPoint(ox + 29, oy + 9);
        cross.addPoint(ox + 24, oy + 4);
        cross.addPoint(ox + 16, oy + 12);
        cross.addPoint(ox + 8, oy + 4);
        graphics.fillPolygon(cross);
*/
        // load PNG image

        if (display.getHeight() > 60) {
            BufferedImage img = ImageIO.read(
                    getClass().getClassLoader().getResourceAsStream(
                            "if_calendar_2561349.png"));
            graphics.drawImage(img, 4, display.getHeight()-50, 20, 20,null);

            // display date
            String date = dfDate.format(new Date());
            graphics.setFont(fontMedium);
            graphics.drawString(date, 30, display.getHeight()-30);
        }


        logger.debug("-- raster");
        display.rasterGraphics2DImage(true);

        long start = System.currentTimeMillis();
        display.display();
        display.display();
        display.display();
        long end = System.currentTimeMillis();
        long duration = end - start;
        long duration1 = duration / 3;

        String msg = "" + duration1 + " ms per frame";
        logger.info(msg);
        graphics.setFont(fontSmall);
        graphics.setColor(Color.darkGray);
        graphics.drawString(msg, 5, 30);
        display.rasterGraphics2DImage(true);



        boolean stayAlive = true;
        while (stayAlive) {
            drawTime(graphics, font);
            display.rasterGraphics2DImage(true);
        }

        logger.debug("<< testDrawing");
    }

    private void drawTime(Graphics2D graphics, Font font) throws IOException {
        // clear old time
        graphics.setColor(Color.black);
        graphics.fillRect(1, display.getHeight()-21, display.getWidth()-2, 20);
        String time = dfTime.format(new Date());
        graphics.setFont(font);
        graphics.setColor(Color.gray);
        graphics.drawString(time, 1, display.getHeight() - 3);
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
