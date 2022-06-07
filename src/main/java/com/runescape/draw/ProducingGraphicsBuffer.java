package com.runescape.draw;

import com.runescape.Client;
import net.runelite.api.MainBufferProvider;
import net.runelite.rs.api.RSRasterProvider;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.util.Hashtable;

public final class ProducingGraphicsBuffer implements RSRasterProvider {

    public final int[] canvasRaster;
    public final int canvasWidth;
    public final int canvasHeight;
    private BufferedImage bufferedImage;

    public ProducingGraphicsBuffer(int canvasWidth, int canvasHeight) {
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        bufferedImage = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_RGB);
        canvasRaster = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();
        setRaster();
        if(Client.loggedIn) {
            init(canvasWidth,canvasHeight);
        }
    }

    public void init(int width, int height)
    {
        if (!Client.instance.isGpu())
        {
            return;
        }


        final int[] pixels = getPixels();

        // we need to make our own buffered image for the client with the alpha channel enabled in order to
        // have alphas for the overlays applied correctly
        DataBufferInt dataBufferInt = new DataBufferInt(pixels, pixels.length);
        DirectColorModel directColorModel = new DirectColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
                32, 0xff0000, 0xff00, 0xff, 0xff000000,
                true, DataBuffer.TYPE_INT);
        WritableRaster writableRaster = Raster.createWritableRaster(directColorModel.createCompatibleSampleModel(width, height), dataBufferInt, null);
        BufferedImage bufferedImage = new BufferedImage(directColorModel, writableRaster, true, new Hashtable());

        setImage(bufferedImage);
    }

    void drawGraphics(Graphics graphics, int x, int y) {
        if(Client.loggedIn) {
            Client.instance.getCallbacks().draw((MainBufferProvider) Client.instance.getBufferProvider(), graphics, x, y);
        } else {
            graphics.drawImage(bufferedImage, y, x, null);
        }

    }

    public void drawGraphics(int x, Graphics graphics, int y) {
        drawGraphics(graphics,x,y);
    }

    public void initDrawingArea() {
        Rasterizer2D.initDrawingArea(canvasHeight, canvasWidth, canvasRaster);
    }

    @Override
    public int[] getPixels() {
        return this.canvasRaster;
    }

    @Override
    public int getWidth() {
        return this.canvasWidth;
    }

    @Override
    public int getHeight() {
        return this.canvasHeight;
    }

    @Override
    public void setRaster() {
        initDrawingArea();
    }


    @Override
    public Image getImage() {
        return bufferedImage;
    }

    @Override
    public void setImage(Image image) {
        this.bufferedImage = toBufferedImage(image);
    }

    public static BufferedImage toBufferedImage(Image img)
    {
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    @Override
    public Component getCanvas() {
        return null;
    }
}